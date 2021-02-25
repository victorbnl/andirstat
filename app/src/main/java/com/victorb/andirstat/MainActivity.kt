package com.victorb.andirstat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.CharacterIterator
import java.text.StringCharacterIterator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Defaults tasks
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        checkPermissions(this)

        Toast.makeText(this, "Scanning your files...", Toast.LENGTH_LONG).show()

        // Set up the recycler view
        val recyclerView: RecyclerView = findViewById(R.id.files_recycler)
        val adapter = FilesAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun checkPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(
                        this,
                        "Please allow the app to access your files",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    class FilesAdapter() : RecyclerView.Adapter<FilesAdapter.ViewHolder>() {
        private val fileList: FileList = FileList()
        private var dataSet: MutableList<FileInfos> = mutableListOf()

        init {
            CoroutineScope(Dispatchers.IO).launch {
                fileList.fillList("/sdcard")
                updateDataSet("/sdcard")
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
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_file,
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val file: FileInfos = dataSet[position]
            holder.fileName.text = file.name
            holder.fileSize.text = fileSizeToHumanReadable(file.size)
            holder.sizeBar.progress = (100 * (file.size.toFloat() / file.parentSize.toFloat())).toInt()
            holder.sizeBar.max = 100
            println("FILE : " + file.path)
            println("SIZE : " + file.size.toFloat())
            println("PARENT SIZE : " + file.parentSize.toFloat())
            holder.iconView.setImageResource(if (file.isDirectory) R.drawable.ic_folder else R.drawable.ic_file)
            if (file.isDirectory) {
                holder.itemView.setOnClickListener {
                    updateDataSet(file.path)
                }
            }
        }

        override fun getItemCount(): Int = dataSet.size

        private fun updateDataSet(rootDirectory: String) {
            dataSet = fileList.getDirectoryFiles(rootDirectory)
            if (rootDirectory != "/sdcard") {
                dataSet.add(0, FileInfos(File(rootDirectory).parent, "..", 0, 0, "", true))
            }
            runOnMainThread { notifyDataSetChanged() }
        }

        // https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
        fun fileSizeToHumanReadable(bytes: Long): String? {
            var bytes = bytes
            if (-1000 < bytes && bytes < 1000) {
                return "$bytes B"
            }
            val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
            while (bytes <= -999950 || bytes >= 999950) {
                bytes /= 1000
                ci.next()
            }
            return java.lang.String.format("%.1f %cB", bytes / 1000.0, ci.current())
        }

        private fun runOnMainThread(callback: () -> Unit) {
            Handler(Looper.getMainLooper()).post {
                callback.invoke()
            }
        }
    }
}