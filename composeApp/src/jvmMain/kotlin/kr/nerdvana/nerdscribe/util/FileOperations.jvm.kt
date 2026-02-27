package kr.nerdvana.nerdscribe.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual suspend fun openFileDialog(): FileResult? = withContext(Dispatchers.IO) {
    val fileChooser = JFileChooser().apply {
        dialogTitle = "파일 열기"
        addChoosableFileFilter(FileNameExtensionFilter("마크다운 파일 (*.md, *.markdown)", "md", "markdown"))
        addChoosableFileFilter(FileNameExtensionFilter("텍스트 파일 (*.txt)", "txt"))
        isAcceptAllFileFilterUsed = true
    }

    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        try {
            FileResult(
                path = file.absolutePath,
                content = file.readText()
            )
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}

actual suspend fun saveFileDialog(content: String, suggestedFileName: String): String? = withContext(Dispatchers.IO) {
    val fileChooser = JFileChooser().apply {
        dialogTitle = "마크다운 파일 저장"
        fileFilter = FileNameExtensionFilter("마크다운 파일 (*.md)", "md")
        selectedFile = File(suggestedFileName)
    }

    val result = fileChooser.showSaveDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        var file = fileChooser.selectedFile
        if (!file.name.endsWith(".md") && !file.name.endsWith(".markdown")) {
            file = File(file.absolutePath + ".md")
        }
        try {
            file.writeText(content)
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}

actual suspend fun saveFile(filePath: String, content: String): Boolean = withContext(Dispatchers.IO) {
    try {
        File(filePath).writeText(content)
        true
    } catch (e: Exception) {
        false
    }
}

actual suspend fun readFileContent(filePath: String): String = withContext(Dispatchers.IO) {
    File(filePath).readText()
}

actual suspend fun listDirectory(path: String): List<kr.nerdvana.nerdscribe.feature.filetree.model.FileNode> = withContext(Dispatchers.IO) {
    val dir = File(path)
    if (!dir.isDirectory) return@withContext emptyList()

    dir.listFiles()
        ?.filter { !it.name.startsWith(".") }
        ?.map { file ->
            kr.nerdvana.nerdscribe.feature.filetree.model.FileNode(
                name        = file.name,
                path        = file.absolutePath,
                isDirectory = file.isDirectory
            )
        }
        ?.sortedWith(compareByDescending<kr.nerdvana.nerdscribe.feature.filetree.model.FileNode> { it.isDirectory }.thenBy { it.name.lowercase() })
        ?: emptyList()
}

actual suspend fun openDirectoryDialog(): String? = withContext(Dispatchers.IO) {
    val fileChooser = JFileChooser().apply {
        dialogTitle = "폴더 열기"
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        isAcceptAllFileFilterUsed = false
    }

    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.absolutePath
    } else {
        null
    }
}
