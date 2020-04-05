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
class RepositoryNameExtractor {

    operator fun invoke(repositoryUrl: String?): String? {
        if (repositoryUrl.isNullOrBlank() || !repositoryUrl.contains("/")) {
            return null
        }

        val split = repositoryUrl.split("/")
        if (split.size < 5) {
            return null
        }
        return split[4]
    }
}