package kr.nerdvana.nerdscribe.core.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * JVM 클립보드 이미지 추출.
 * java.awt.Toolkit.systemClipboard에서 이미지를 가져온다.
 */
actual suspend fun getClipboardImage(): ByteArray? = withContext(Dispatchers.IO) {
    try {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        if (!clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) return@withContext null

        val image = clipboard.getData(DataFlavor.imageFlavor) as? Image ?: return@withContext null

        val buffered = if (image is BufferedImage) {
            image
        } else {
            val width  = image.getWidth(null)
            val height = image.getHeight(null)
            if (width <= 0 || height <= 0) return@withContext null
            BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
                createGraphics().apply {
                    drawImage(image, 0, 0, null)
                    dispose()
                }
            }
        }

        ByteArrayOutputStream().use { baos ->
            ImageIO.write(buffered, "png", baos)
            baos.toByteArray()
        }
    } catch (_: Exception) {
        null
    }
}
