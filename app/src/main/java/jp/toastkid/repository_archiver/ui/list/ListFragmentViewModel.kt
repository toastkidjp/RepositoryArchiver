/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author toastkidjp
 */
class ListFragmentViewModel : ViewModel() {

    private val _loadRepositories = MutableLiveData<Unit>()

    val loadRepositories: LiveData<Unit> = _loadRepositories

    fun loadRepositories() {
        _loadRepositories.postValue(Unit)
    }

    private val _loadEntries = MutableLiveData<String>()

    val loadEntries: LiveData<String> = _loadEntries

    fun loadEntries(repositoryTitle: String) {
        _loadEntries.postValue(repositoryTitle)
    }

    private val _filterByText = MutableLiveData<Pair<String, String>>()

    val filterByText: LiveData<Pair<String, String>> = _filterByText

    fun filterByTest(repositoryTitle: String, text: String) {
        _filterByText.postValue(repositoryTitle to text)
    }
}