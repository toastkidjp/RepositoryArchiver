/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.repositories.datasource.zip

import android.os.Build
import jp.toastkid.repository_archiver.repositories.model.Entry
import okio.BufferedSource
import okio.Okio
import timber.log.Timber
import java.io.InputStream
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author toastkidjp
 */
class ZipDataSource {

    operator fun invoke(repositoryName: String, inputStream: InputStream): List<Entry> {
        val entries = mutableListOf<Entry>()
        ZipInputStream(inputStream, CHARSET)
            .also { zipInputStream ->
                var nextEntry = zipInputStream.nextEntry
                while (nextEntry != null) {
                    Okio.buffer(Okio.source(zipInputStream))
                        .also {
                            val entry = extractEntry(it, nextEntry, repositoryName)
                            entries.add(entry)
                            Timber.i("tomaot entry ${entry.title}")
                        }
                    nextEntry = try {
                        zipInputStream.nextEntry
                    } catch (e: IllegalArgumentException) {
                        Timber.e("illegal: ${nextEntry.name}")
                        return@also
                    }
                }
                zipInputStream.closeEntry()
            }

        return entries
    }

    /**
     * Make article.
     *
     * @param bufferedSource [BufferedSource]
     * @param nextEntry [ZipEntry]
     */
    private fun extractEntry(
        bufferedSource: BufferedSource,
        nextEntry: ZipEntry,
        repositoryName: String
    ): Entry {
        val content = bufferedSource.readUtf8()
        return Entry(
            id = 0L,
            title = extractFileName(nextEntry.name),
            path = nextEntry.name,
            lastModified =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nextEntry.lastModifiedTime.to(TimeUnit.MILLISECONDS)
                } else {
                    nextEntry.time
                },
            textContent = content,
            directory = nextEntry.isDirectory,
            repositoryName = repositoryName
        )
    }

    private fun extractFileName(name: String) = name.substring(name.lastIndexOf("/") + 1, name.length)

    companion object {

        /**
         * Title character set.
         */
        private val CHARSET = Charset.forName("UTF-8")

    }
}