package kr.nerdvana.nerdscribe.util

data class FileResult(
    val path: String,
    val content: String
)

expect suspend fun openFileDialog(): FileResult?

expect suspend fun saveFileDialog(content: String, suggestedFileName: String): String?

expect suspend fun saveFile(filePath: String, content: String): Boolean

expect suspend fun readFileContent(filePath: String): String

expect suspend fun listDirectory(path: String): List<kr.nerdvana.nerdscribe.feature.filetree.model.FileNode>

expect suspend fun openDirectoryDialog(): String?
