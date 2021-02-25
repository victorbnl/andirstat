package com.victorb.andirstat

import java.io.File

data class FileInfos(
    val path: String,
    val name: String,
    val size: Long,
    val parentSize: Long,
    val parentPath: String,
    val isDirectory: Boolean
)