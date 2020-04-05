/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.repositories.datasource.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jp.toastkid.repository_archiver.repositories.model.Entry
import jp.toastkid.repository_archiver.repositories.model.EntrySearchResult
import jp.toastkid.repository_archiver.repositories.model.Repository

/**
 * @author toastkidjp
 */
@Dao
interface ArchiveRepository {

    @Query("SELECT * FROM repositories")
    fun allRepository(): List<Repository>

    @Query("SELECT entries.title, entries.path, entries.lastModified FROM entries WHERE entries.directory = 0 AND entries.repositoryName = :repositoryName")
    fun findByRepositoryName(repositoryName: String): List<EntrySearchResult>

    @Query("SELECT entries.title, entries.path, entries.lastModified FROM entries WHERE entries.title LIKE :title AND entries.repositoryName = :repositoryName")
    fun filterByTitle(repositoryName: String, title: String): List<EntrySearchResult>

    @Query("SELECT entries.title, entries.path, entries.lastModified FROM entries JOIN entryFts ON (entries.id = entryFts.docid) WHERE entryFts MATCH :query")
    fun search(query: String): List<EntrySearchResult>

    @Query("SELECT entries.textContent FROM entries WHERE entries.path = :path LIMIT 1")
    fun findTextContentByPath(path: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: Entry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entries: List<Entry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repository: Repository)

    @Query("DELETE FROM entries")
    fun deleteAll()

}