/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.libs.receiver

import android.content.Context
import android.webkit.URLUtil
import androidx.core.net.toUri
import jp.toastkid.repository_archiver.repositories.datasource.zip.ZipLoaderService

/**
 * @author toastkidjp
 */
class RepositoryUrlReceiver {

    private val repositoryUrlConverter = RepositoryUrlConverter()

    private val repositoryNameExtractor = RepositoryNameExtractor()

    operator fun invoke(context: Context, url: String) {
        if (!URLUtil.isHttpsUrl(url)) {
            return
        }

        val repositoryName = repositoryNameExtractor(url) ?: return

        val zipUrl = repositoryUrlConverter.toZipUrl(url)
        if (!zipUrl.endsWith(".zip")) {
            return
        }

        ZipLoaderService.start(context, zipUrl.toUri(), repositoryName)
    }
}