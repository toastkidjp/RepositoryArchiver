/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.ui.list

import androidx.recyclerview.widget.RecyclerView
import jp.toastkid.repository_archiver.databinding.ItemListBinding

/**
 * @author toastkidjp
 */
class ViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setTitle(name: String) {
        binding.mainText.text = name
    }

    fun setOnClick(function: () -> Unit) {
        itemView.setOnClickListener { function() }
    }

}