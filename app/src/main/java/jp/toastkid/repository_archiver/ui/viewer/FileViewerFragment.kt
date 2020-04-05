/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.ui.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.room.Room
import jp.toastkid.repository_archiver.R
import jp.toastkid.repository_archiver.databinding.FragmentFileViewerBinding
import jp.toastkid.repository_archiver.repositories.datasource.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @author toastkidjp
 */
class FileViewerFragment : Fragment() {

    private lateinit var binding: FragmentFileViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_file_viewer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val path = arguments?.getString("path") ?: return
        val dataBase = Room.databaseBuilder(
            view.context.applicationContext,
            AppDatabase::class.java,
            view.context.packageName
        ).build()

        val archiveRepository = dataBase.repository()
        CoroutineScope(Dispatchers.Main).launch {
            binding.textContent.text =
                CoroutineScope(Dispatchers.IO).async {
                    archiveRepository.findTextContentByPath(path)
                }.await()
        }

    }

}