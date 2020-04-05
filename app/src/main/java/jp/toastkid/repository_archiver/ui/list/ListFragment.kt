/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import jp.toastkid.repository_archiver.R
import jp.toastkid.repository_archiver.databinding.FragmentListBinding
import jp.toastkid.repository_archiver.main.MainActivityViewModel
import jp.toastkid.repository_archiver.repositories.datasource.db.AppDatabase
import jp.toastkid.repository_archiver.repositories.datasource.db.ArchiveRepository
import jp.toastkid.repository_archiver.ui.OnBackPressed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author toastkidjp
 */
class ListFragment : Fragment(), OnBackPressed {

    private lateinit var binding: FragmentListBinding

    private lateinit var adapter: Adapter

    private lateinit var repository: ArchiveRepository

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private var currentRepositoryName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)

        repository = Room.databaseBuilder(
            binding.root.context.applicationContext,
            AppDatabase::class.java,
            activity?.packageName ?: ""
        )
            .build()
            .repository()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(ListFragmentViewModel::class.java)
        viewModel
            .loadRepositories
            .observe(viewLifecycleOwner, Observer {
                loadRepositories()
            })

        viewModel
            .loadEntries
            .observe(viewLifecycleOwner, Observer {
                loadEntries(it)
            })

        val requireActivity = requireActivity()

        adapter = Adapter(
            LayoutInflater.from(view.context),
            ViewModelProvider(requireActivity).get(MainActivityViewModel::class.java),
            viewModel
        )

        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val viewModelForActivity = ViewModelProvider(requireActivity).get(ListFragmentViewModel::class.java)

        viewModelForActivity
            .filterByText
            .observe(requireActivity, Observer(::filter))

        viewModelForActivity.loadRepositories.observe(requireActivity, Observer {
            loadRepositories()
        })
        viewModelForActivity.loadEntries.observe(requireActivity, Observer(::loadEntries))

        loadRepositories()
    }

    override fun onBackPressed(): Boolean {
        Log.i("tomato", "isVisible $isVisible $isHidden $isResumed")
        return when {
            currentRepositoryName.isBlank() -> false
            else -> {
                loadRepositories()
                true
            }
        }
    }

    private fun filter(filterQuery: Pair<String, String>) {
        CoroutineScope(Dispatchers.IO).launch {
            adapter.replace(repository.filterByTitle(filterQuery.first, "%${filterQuery.second}%"))
            CoroutineScope(Dispatchers.Main).launch {
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

    private fun loadRepositories() {
        CoroutineScope(Dispatchers.IO).launch {
            val repositories = repository.allRepository()
            ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
                .resetRepositoryName()
            currentRepositoryName = ""

            adapter.replace(repositories)
            CoroutineScope(Dispatchers.Main).launch {
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

    private fun loadEntries(repositoryName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val repositories = repository.findByRepositoryName(repositoryName)
            ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
                .nextRepository(repositoryName)
            currentRepositoryName = repositoryName

            adapter.replace(repositories)
            CoroutineScope(Dispatchers.Main).launch {
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

}