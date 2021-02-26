package com.victorb.andirstat

data class FileInfos(
        val name: String,
        val path: String,
        val size: Long,
        val parentSize: Long,
        val parentPath: String,
        val isDirectory: Boolean
)