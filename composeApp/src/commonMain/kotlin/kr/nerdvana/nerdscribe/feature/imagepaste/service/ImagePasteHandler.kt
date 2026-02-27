package kr.nerdvana.nerdscribe.feature.imagepaste.service

import kr.nerdvana.nerdscribe.core.util.getClipboardImage
import kotlin.random.Random

/**
 * 이미지 붙여넣기 핸들러.
 * 클립보드의 이미지를 파일로 저장하고 마크다운 이미지 링크를 반환한다.
 *
 * @param saveImage 이미지 바이트를 파일로 저장하는 함수. (dirPath, fileName, bytes) -> 저장된 상대 경로
 */
class ImagePasteHandler(
    private val saveImage: suspend (dirPath: String, fileName: String, bytes: ByteArray) -> String?
) {
    private var counter = 0L

    /**
     * 클립보드에서 이미지를 추출하여 저장하고, 마크다운 이미지 링크를 반환한다.
     *
     * @param documentDir 문서가 위치한 디렉토리 경로
     * @return 마크다운 이미지 링크, 또는 이미지가 없으면 null
     */
    suspend fun handlePaste(documentDir: String): String? {
        val imageBytes = getClipboardImage() ?: return null
        val id         = "${++counter}-${Random.nextInt(10000)}"
        val fileName   = "paste-$id.png"
        val imagesDir  = "$documentDir/images"

        val relativePath = saveImage(imagesDir, fileName, imageBytes) ?: return null
        return "![]($relativePath)"
    }
}
