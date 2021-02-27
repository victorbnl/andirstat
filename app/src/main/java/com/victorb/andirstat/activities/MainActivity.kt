package com.victorb.andirstat.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.SimpleStorageHelper
import com.victorb.andirstat.R
import com.victorb.andirstat.adapters.FilesAdapter

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

    private fun initAdapterWithRootFolder() {
        storageHelper.onFolderSelected = { _: Int, folder: DocumentFile ->
            Toast.makeText(this, "Tout vient au point à qui sait attendre", Toast.LENGTH_LONG).show()
            adapter.setRootFolder(folder)
        }
        storageHelper.openFolderPicker()
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