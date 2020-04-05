/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.repositories.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

/**
 * @author toastkidjp
 */
@Keep
@Fts4(contentEntity = Entry::class, tokenizer = FtsOptions.TOKENIZER_PORTER)
@Entity(tableName = "entryFts")
class EntryFts(
    var title: String = "",
    var textContent: String = ""
)