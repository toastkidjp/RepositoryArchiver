package jp.toastkid.repository_archiver.main

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import jp.toastkid.repository_archiver.R
import jp.toastkid.repository_archiver.databinding.ActivityMainBinding
import jp.toastkid.repository_archiver.libs.PreferenceApplier
import jp.toastkid.repository_archiver.libs.receiver.RepositoryUrlReceiver
import jp.toastkid.repository_archiver.repositories.datasource.zip.FileExtractorFromUri
import jp.toastkid.repository_archiver.repositories.datasource.zip.ZipLoaderService
import jp.toastkid.repository_archiver.ui.OnBackPressed
import jp.toastkid.repository_archiver.ui.list.ListFragment
import jp.toastkid.repository_archiver.ui.list.ListFragmentViewModel
import jp.toastkid.repository_archiver.ui.viewer.FileViewerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private val progressBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, broadcastIntent: Intent?) {
            runOnUiThread {
                progressSnackbar?.dismiss()
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Done!",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.setAction("Refresh") {
                    ViewModelProvider(this@MainActivity).get(ListFragmentViewModel::class.java)
                        .loadRepositories()
                }
                snackbar.show()
            }
        }
    }

    private var currentRepositoryName = ""

    private var progressSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(LAYOUT_ID)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, LAYOUT_ID)
        binding.activity = this

        registerReceiver(
            progressBroadcastReceiver,
            ZipLoaderService.makeProgressBroadcastIntentFilter()
        )

        if (intent.action == Intent.ACTION_SEND) {
            intent.extras?.getCharSequence(Intent.EXTRA_TEXT)?.also {
                progressSnackbar = Snackbar.make(binding.root, "Loading...", Snackbar.LENGTH_INDEFINITE)
                (progressSnackbar?.view as? SnackbarContentLayout)?.addView(ProgressBar(this))
                progressSnackbar?.show()
                RepositoryUrlReceiver()(this, it.toString())
            }
        }

        binding.input.addTextChangedListener(object : TextWatcher {
            private val listFragmentViewModel =
                ViewModelProvider(this@MainActivity).get(ListFragmentViewModel::class.java)

            private var previousInput = ""

            override fun afterTextChanged(s: Editable?) = Unit

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s?.toString()?.trim()
                if (searchText == null || searchText == previousInput) {
                    return
                }

                previousInput = searchText

                CoroutineScope(Dispatchers.IO).launch {
                    delay(500)
                    if (searchText != previousInput) {
                        return@launch
                    }

                    if (searchText.isBlank() && currentRepositoryName.isNotBlank()) {
                        listFragmentViewModel.loadEntries(currentRepositoryName)
                        return@launch
                    }

                    listFragmentViewModel.filterByTest(currentRepositoryName, searchText)
                }
            }
        })

        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel
            .open
            .observe(this, Observer {
                binding.title.text = it
                val fileViewerFragment = obtainFragment(FileViewerFragment::class.java)
                (fileViewerFragment as? FileViewerFragment)?.arguments = bundleOf("path" to it)
                replaceFragment(fileViewerFragment)
            })

        viewModel
            .nextRepository
            .observe(this, Observer {
                binding.title.text = it
                currentRepositoryName = it
            })

        replaceFragment(obtainFragment(ListFragment::class.java))
    }

    fun open() {
        if (isGranted()) {
            selectTargetFile()
            return
        }
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
    }

    private fun selectTargetFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/zip"
        startActivityForResult(intent, 1)
    }

    private fun isGranted() =
        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun obtainFragment(fragmentClass: Class<out Fragment>): Fragment {
        return supportFragmentManager
            .findFragmentByTag(FileViewerFragment::class.java.canonicalName)
            ?: fragmentClass.newInstance()
    }

    private fun replaceFragment(fragment: Fragment?) {
        if (fragment == null) {
            return
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_right, 0, 0, android.R.anim.slide_out_right)
        transaction.add(R.id.content, fragment, fragment::class.java.canonicalName)
        transaction.addToBackStack(fragment::class.java.canonicalName)
        transaction.commitAllowingStateLoss()
    }

    private fun updateIfNeed() {
        val preferencesWrapper = PreferenceApplier(this)
        val target = preferencesWrapper.getTarget()
        if (target.isNullOrBlank()) {
            return
        }

        val fileExtractorFromUri = FileExtractorFromUri(this, target.toUri()) ?: return
        val file = File(fileExtractorFromUri)
        if (preferencesWrapper.getLastUpdated() == file.lastModified()) {
            //articleListFragment.all()
            return
        }

        ZipLoaderService.start(this, target)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            selectTargetFile()
            return
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            PreferenceApplier(this).setTarget(data?.data?.toString())
            updateIfNeed()
        }
    }

    override fun onBackPressed() {
        val currentFragmentHaveUsedEvent =
            (supportFragmentManager.findFragmentById(R.id.content) as? OnBackPressed)?.onBackPressed()
        if (currentFragmentHaveUsedEvent == true) {
            return
        }

        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
            return
        }

        super.onBackPressed()
    }

    override fun onDestroy() {
        unregisterReceiver(progressBroadcastReceiver)
        super.onDestroy()
    }

    companion object {
        @LayoutRes
        private const val LAYOUT_ID = R.layout.activity_main
    }
}