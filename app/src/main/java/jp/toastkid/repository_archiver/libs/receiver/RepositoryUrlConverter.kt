/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.libs.receiver

/**
 * @author toastkidjp
 */
class RepositoryUrlConverter {

    fun toZipUrl(url: String): String {
        if (url.isBlank() || !url.startsWith("https://", ignoreCase = true)) {
            return url
        }

        val split = url.split("/")
        if (split.size < 4 || !split[2].endsWith("github.com")) {
            return url
        }

        val author = split.get(3)
        val repositoryName = split.get(4)
        val branchName = if (split.size >= 6 && url.contains("blob")) split[6] else "master"

        return "https://${split[2]}/$author/$repositoryName/archive/$branchName.zip"
    }
}