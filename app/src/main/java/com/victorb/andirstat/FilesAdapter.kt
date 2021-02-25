package com.victorb.andirstat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilesAdapter() : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {
    private val fileList: FileList = FileList()
    private var dataSet: MutableList<FileInfos> = mutableListOf()

    init {
        fileList.fillList("/sdcard")
        dataSet = fileList.getDirectoryFiles("/sdcard")
        for (file in fileList.getFileList()) {
            println(file.path)
        }
    }

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
        val file: FileInfos = dataSet[position]
        holder.fileName.text = file.name
        holder.fileSize.text = (file.size / 1000000).toString() + " MB"
        holder.sizeBar.progress = (100 * (file.size.toFloat() / file.parentSize.toFloat())).toInt()
        holder.iconView.setImageResource(if (file.isDirectory) R.drawable.ic_folder else R.drawable.ic_file)
        if (file.isDirectory) {
            holder.itemView.setOnClickListener {
                dataSet = fileList.getDirectoryFiles(file.path)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size
}