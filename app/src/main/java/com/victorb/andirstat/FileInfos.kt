package com.victorb.andirstat

import androidx.documentfile.provider.DocumentFile

data class FileInfos(
        val name: String,
        val path: String,
        val size: Long,
        val parentSize: Long,
        val parentPath: String,
        val isDirectory: Boolean
)