package com.victorb.andirstat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {
    var filesList: MutableList<File> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Defaults tasks
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up the recycler view
        val recyclerView: RecyclerView = findViewById(R.id.files_recycler)
        val adapter: FilesAdapter = FilesAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        checkPermissions(this)

    }

    private fun checkPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(this, "Please allow the app to access your files", Toast.LENGTH_LONG).show()
                }
            }
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    @SuppressLint("SdCardPath")
    private fun startScan(context: Context, folder: String = "/sdcard") {
        val rootDir = File(folder)
        for (file in rootDir.listFiles()) {
            println(file)
        }
    }
}