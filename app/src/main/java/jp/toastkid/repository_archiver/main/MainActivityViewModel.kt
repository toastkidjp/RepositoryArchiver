/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author toastkidjp
 */
class MainActivityViewModel : ViewModel() {

    private val _open = MutableLiveData<String>()

    val open: LiveData<String> = _open

    fun open(path: String) {
        _open.postValue(path)
    }

    private val _nextRepository =  MutableLiveData<String>()

    val nextRepository: LiveData<String> = _nextRepository

    fun nextRepository(next: String) {
        _nextRepository.postValue(next)
    }

    fun resetRepositoryName() {
        _nextRepository.postValue("")
    }
}