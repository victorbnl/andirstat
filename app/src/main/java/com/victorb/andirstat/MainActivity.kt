package com.victorb.andirstat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.absolutePath

class MainActivity : AppCompatActivity() {
    private lateinit var storageHelper: SimpleStorageHelper
    private var adapter = FilesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Defaults tasks
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup recyclerView
        val recyclerView: RecyclerView = findViewById(R.id.files_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        storageHelper = SimpleStorageHelper(this, savedInstanceState)
        initAdapterWithRootFolder()
    }

    private fun checkPermissionsAndInitRecyclerView(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(
                        this,
                        "Please allow the app to access your files",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    initAdapterWithRootFolder()
                }
            }
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            initAdapterWithRootFolder()
        }
    }

    private fun initAdapterWithRootFolder() {
        storageHelper.onFolderSelected = { _: Int, folder: DocumentFile ->
            Toast.makeText(this, "Scanning your files, this may take up to 5 mins", Toast.LENGTH_LONG).show()
            adapter.setRootFolder(folder)
        }
        storageHelper.openFolderPicker()
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        storageHelper.storage.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        storageHelper.storage.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        storageHelper.storage.onRestoreInstanceState(savedInstanceState)
    }
}