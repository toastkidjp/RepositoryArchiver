/*
 * Copyright (c) 2019 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.repository_archiver.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import jp.toastkid.repository_archiver.R
import jp.toastkid.repository_archiver.databinding.ItemListBinding
import jp.toastkid.repository_archiver.main.MainActivityViewModel
import jp.toastkid.repository_archiver.repositories.model.EntrySearchResult
import jp.toastkid.repository_archiver.repositories.model.ListItem
import jp.toastkid.repository_archiver.repositories.model.Repository

/**
 * @author toastkidjp
 */
class Adapter(
    private val inflater: LayoutInflater,
    private val mainViewModel: MainActivityViewModel,
    private val listViewModel: ListFragmentViewModel
) : RecyclerView.Adapter<ViewHolder>() {

    private val items = mutableListOf<ListItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DataBindingUtil.inflate<ItemListBinding>(inflater, R.layout.item_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.setTitle(item.displayTitle())

        holder.setOnClick {
            when (item) {
                is Repository -> {
                    listViewModel.loadEntries(item.title)
                }
                is EntrySearchResult -> {
                    mainViewModel.open(item.path)
                }
                else -> Unit
            }
        }
    }

    override fun getItemCount() = items.size

    fun replace(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
    }

}