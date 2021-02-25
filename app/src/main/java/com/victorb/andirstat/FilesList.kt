package com.victorb.andirstat

import java.io.File

class FilesList() {
    private val filesList: MutableList<File> = mutableListOf()

    fun populateList() {
        File("/sdcard").walk().forEach { file: File ->
            filesList.add(file)
        }
    }

    fun getDirectoryFiles(directory: File): MutableList<File> {
        val result: MutableList<File> = mutableListOf()
        for (file in filesList) {
            if (file.parentFile == directory) {
                result.add(file)
            }
        }
        return result
    }
}