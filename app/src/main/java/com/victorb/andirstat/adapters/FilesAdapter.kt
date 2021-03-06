package com.victorb.andirstat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.file.absolutePath
import com.victorb.andirstat.*
import com.victorb.andirstat.data.FileInfos

class FilesAdapter() : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {
    private lateinit var rootFolder: DocumentFile
    private lateinit var fileList: MutableList<FileInfos>
    private var filesToDisplay: MutableList<FileInfos> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.fileicon)
        val fileName: TextView = view.findViewById(R.id.filename)
        val fileSize: TextView = view.findViewById(R.id.filesize)
        val sizeBar: ProgressBar = view.findViewById(R.id.filesizebar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.item_file,
                            parent,
                            false
                    )
            )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file: FileInfos = filesToDisplay[position]
        holder.fileName.text = file.name
        holder.fileSize.text = fileSizeToHumanReadable(file.size)
        holder.iconView.setImageResource(if (file.isDirectory) R.drawable.ic_folder else R.drawable.ic_file)
        holder.sizeBar.progress = (100 * (file.size.toFloat() / file.parentSize.toFloat())).toInt()
        holder.sizeBar.max = 100
        if (file.isDirectory) {
            holder.itemView.setOnClickListener { updateFilesToDisplay(file.path, if (file.parentPath == rootFolder.absolutePath) file.parentPath else null) }
        } else {
            holder.itemView.setOnClickListener {  }
        }
    }

    override fun getItemCount(): Int = filesToDisplay.size

    private fun updateFilesToDisplay(folderPath: String, folderParentPath: String?) {
        runInCoroutine {
            filesToDisplay.clear()
            for (file in fileList) {
                if (file.parentPath == folderPath) {
                    filesToDisplay.add(file)
                }
            }
            filesToDisplay.sortByDescending { it.size }
            if (folderParentPath != null) {
                filesToDisplay.add(0, FileInfos("..", folderParentPath, 0, 0, "",true))
            }
            runOnMainThread { notifyDataSetChanged() }
        }
    }

    fun setRootFolder(folder: DocumentFile) {
        runInCoroutine {
            rootFolder = folder
            fileList = getAllChildren(rootFolder)
            updateFilesToDisplay(rootFolder.absolutePath, null)
            runOnMainThread { notifyDataSetChanged() }
        }
    }
}