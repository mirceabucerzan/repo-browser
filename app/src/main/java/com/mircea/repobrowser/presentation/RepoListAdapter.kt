package com.mircea.repobrowser.presentation

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mircea.repobrowser.R

/**
 * Binds [RepoItem]s to views displayed in a RecyclerView.
 */
class RepoListAdapter : RecyclerView.Adapter<RepoViewHolder>() {
    var items: List<RepoItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RepoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.repo_list_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        if (position >= 0 && position < items.size) holder.bind(items[position])
    }
}

class RepoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val titleView: TextView = view.findViewById(R.id.repo_title)
    private val descriptionView: TextView = view.findViewById(R.id.repo_description)
    private val imageView: ImageView = view.findViewById(R.id.repo_image)

    fun bind(repoItem: RepoItem) {
        titleView.text = repoItem.name
        descriptionView.text = repoItem.description
        repoItem.imageUrl.takeIf { it.isNotBlank() }?.let {
            Glide.with(itemView).load(Uri.parse(it)).into(imageView)
        }
    }
}