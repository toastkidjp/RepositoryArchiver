/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.repositories.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.toastkid.repository_archiver.repositories.model.Entry
import jp.toastkid.repository_archiver.repositories.model.EntryFts
import jp.toastkid.repository_archiver.repositories.model.Repository

/**
 * @author toastkidjp
 */
@Database(entities = [Entry::class, EntryFts::class, Repository::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repository(): ArchiveRepository
}