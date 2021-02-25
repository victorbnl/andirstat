package com.victorb.andirstat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.nio.file.Files

class FilesAdapter() : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {
    private val filesList: FilesList = FilesList()
    private var dataSet: MutableList<File> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.fileicon)
        val fileName: TextView = view.findViewById(R.id.filename)
        val fileSize: TextView = view.findViewById(R.id.filesize)
        val sizeBar: ProgressBar = view.findViewById(R.id.filesizebar)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_file,
                parent,
                false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.fileName.text = dataSet[position].name
            holder.sizeBar.progress = dataSet[position].length().toInt()
            holder.sizeBar.max = 8000
            holder.iconView.setImageResource(if (dataSet[position].isDirectory) R.drawable.ic_baseline_folder_24 else R.drawable.ic_baseline_insert_drive_file_24)
            if (dataSet[position].isDirectory) {
                holder.itemView.setOnClickListener {
                    dataSet = filesList.getDirectoryFiles(dataSet[position])
                    notifyDataSetChanged()
                }
            }
    }

    override fun getItemCount(): Int = dataSet.size

    @SuppressLint("SdCardPath")
    fun populateFilesList() {
        filesList.populateList()
        dataSet = filesList.getDirectoryFiles(File("/sdcard"))
        notifyDataSetChanged()
    }
}