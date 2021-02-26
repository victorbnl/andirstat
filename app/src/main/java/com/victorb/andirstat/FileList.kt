package com.victorb.andirstat

import java.io.File

class FileList {
    private val fileList: MutableList<FileInfos> = mutableListOf()
    private val folderSizes: MutableMap<String, Long> = mutableMapOf()

    fun fillList(rootFolder: String) {
        File(rootFolder).walk().forEach { file: File ->
            fileList.add(FileInfos(file.path,
                file.name,
                getFileSize(file),
                if (file.parentFile != File("/")) getFileSize(file.parentFile) else 16000000000,
                file.parent,
                file.isDirectory))
        }
    }

    fun clearList() {
        fileList.clear()
    }

    private fun getFileSize(directory: File): Long {
        return if (folderSizes[directory.absolutePath] != null) {
            folderSizes[directory.absolutePath] as Long
        } else {
            var size: Long = 0
            directory.walk().forEach { file: File ->
                size += file.length()
            }
            folderSizes[directory.absolutePath] = size
            size
        }
    }

    fun getDirectoryFiles(directory: String): MutableList<FileInfos> {
        val results: MutableList<FileInfos> = mutableListOf()
        for (file in fileList) {
            if (file.parentPath == directory) {
                results.add(file)
            }
        }
        results.sortByDescending {
            it.size
        }
        return results
    }
}