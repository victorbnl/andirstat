package com.victorb.andirstat

import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.absolutePath
import java.text.CharacterIterator
import java.text.StringCharacterIterator

var fileSizes: MutableMap<String, Long> = mutableMapOf()

fun getAllChildren(rootFile: DocumentFile, fileList: MutableList<FileInfos> = mutableListOf()): MutableList<FileInfos> {
    for (file in rootFile.listFiles()) {
        if (file.isDirectory) {
            fileList.add(FileInfos(file.name!!, file.absolutePath, getFolderSize(file), getFolderSize(file.parentFile!!), file.parentFile!!.absolutePath, file.isDirectory))
            getAllChildren(file, fileList)
        } else {
            fileList.add(FileInfos(file.name!!, file.absolutePath, file.length(), getFolderSize(file.parentFile!!), file.parentFile!!.absolutePath, file.isDirectory))
        }
    }
    return fileList
}

fun getFolderSize(folder: DocumentFile): Long {
    fileSizes[folder.absolutePath]?.let { return it }
    var size: Long = 0
    for (file in folder.listFiles()) {
        if (file.isDirectory) {
            size += getFolderSize(file)
        } else {
            size += file.length()
        }
    }
    fileSizes[folder.absolutePath] = size
    return size
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