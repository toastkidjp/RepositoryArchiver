/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.repositories.datasource.zip

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.core.app.JobIntentService
import androidx.core.net.toUri
import androidx.room.Room
import jp.toastkid.repository_archiver.libs.PreferenceApplier
import jp.toastkid.repository_archiver.repositories.datasource.db.AppDatabase
import jp.toastkid.repository_archiver.repositories.model.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import java.io.File
import java.io.InputStream

/**
 * @author toastkidjp
 */
class ZipLoaderService : JobIntentService() {

    @SuppressLint("CheckResult")
    override fun onHandleWork(intent: Intent) {
        when {
            intent.hasExtra(KEY_URI) -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val uri = intent.getParcelableExtra<Uri>(KEY_URI) ?: return@launch
                    val response = OkHttpClient.Builder().build()
                        .newCall(Request.Builder().url(uri.toString()).get().build())
                        .execute()
                    val inputStream = response.body()?.byteStream() ?: return@launch

                    val repositoryName = intent.getStringExtra(KEY_REPOSITORY_NAME) ?: return@launch
                    loadFromStream(repositoryName, inputStream)
                }.start()
            }
            intent.hasExtra(KEY_TARGET) -> {
                val fileUrl = intent.getStringExtra(KEY_TARGET) ?: return

                val fileExtractorFromUri =
                    FileExtractorFromUri(this, fileUrl.toUri()) ?: return

                val file = File(fileExtractorFromUri)
                PreferenceApplier(this@ZipLoaderService).setLastUpdated(file.lastModified())

                CoroutineScope(Dispatchers.IO).launch {
                    val repositoryName =
                        if (file.name.contains(".")) file.name.substring(0, file.name.lastIndexOf("."))
                        else file.name
                    loadFromStream(repositoryName, Okio.buffer(Okio.source(file)).inputStream())
                }.start()
            }
        }
    }

    private fun loadFromStream(
        repositoryName: String,
        inputStream: InputStream
    ) {
        val dataBase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            packageName
        ).build()

        val archiveRepository = dataBase.repository()

        val entries = ZipDataSource().invoke(repositoryName, inputStream)

        archiveRepository.insert(Repository(id = 0L, title = repositoryName))
        archiveRepository.insertAll(entries)

        CoroutineScope(Dispatchers.Main).launch {
            val progressIntent = Intent(ACTION_PROGRESS_BROADCAST)
            progressIntent.putExtra("progress", 100)
            sendBroadcast(progressIntent)
        }
    }

    companion object {

        /**
         * Broadcast action name.
         */
        private const val ACTION_PROGRESS_BROADCAST = "jp.toastkid.articles.importing.progress"

        /**
         * Make intent filter.
         */
        fun makeProgressBroadcastIntentFilter() = IntentFilter(ACTION_PROGRESS_BROADCAST)

        /**
         * Extra key of target.
         */
        private const val KEY_TARGET = "target"

        private const val KEY_URI = "uri"

        private const val KEY_REPOSITORY_NAME = "repository_name"

        /**
         * Start service.
         *
         * @param context [Context]
         * @param target target zip file
         */
        fun start(context: Context, target: String) {
            val intent = Intent(context, ZipLoaderService::class.java)
            intent.putExtra(KEY_TARGET, target)
            enqueueWork(context, ZipLoaderService::class.java, 20, intent)
        }

        /**
         * Start service.
         *
         * @param context [Context]
         * @param target target zip file
         */
        fun start(context: Context, uri: Uri, repositoryName: String) {
            val intent = Intent(context, ZipLoaderService::class.java)
            intent.putExtra(KEY_URI, uri)
            intent.putExtra(KEY_REPOSITORY_NAME, repositoryName)
            enqueueWork(context, ZipLoaderService::class.java, 21, intent)
        }
    }
}