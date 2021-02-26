package com.victorb.andirstat

import android.os.StatFs
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.absolutePath
import com.victorb.andirstat.data.FileInfos
import java.text.CharacterIterator
import java.text.StringCharacterIterator

var fileSizes: MutableMap<String, Long> = mutableMapOf()

fun getAllChildren(rootFile: DocumentFile, fileList: MutableList<FileInfos> = mutableListOf()): MutableList<FileInfos> {
    for (file in rootFile.listFiles()) {
        if (file.isDirectory) {
            fileList.add(FileInfos(file.name!!, file.absolutePath, getFolderSize(file), if (file.parentFile!!.absolutePath != "/") getFolderSize(file.parentFile!!) else StatFs(file.absolutePath).totalBytes, file.parentFile!!.absolutePath, file.isDirectory))
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
        size += if (file.isDirectory) getFolderSize(file) else file.length()
    }
    fileSizes[folder.absolutePath] = size
    return size
}

// https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
fun fileSizeToHumanReadable(bytes: Long): String? {
    var lbytes = bytes
    if (-1000 < lbytes && lbytes < 1000) {
        return "$lbytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (lbytes <= -999950 || lbytes >= 999950) {
        lbytes /= 1000
        ci.next()
    }
    return java.lang.String.format("%.1f %cB", lbytes / 1000.0, ci.current())
}