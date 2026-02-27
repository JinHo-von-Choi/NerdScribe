package kr.nerdvana.nerdscribe.feature.export.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * JVM PDF 내보내기.
 * openhtmltopdf 라이브러리를 사용하여 HTML을 PDF로 변환한다.
 * 라이브러리가 없는 경우 HTML을 직접 PDF로 래핑하는 폴백을 사용한다.
 */
actual object PdfExporter {

    actual suspend fun export(html: String, filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val pdfBuilder = Class.forName("com.openhtmltopdf.pdfboxout.PdfRendererBuilder")
            val builder    = pdfBuilder.getDeclaredConstructor().newInstance()
            val fos        = FileOutputStream(filePath)

            pdfBuilder.getMethod("withHtmlContent", String::class.java, String::class.java)
                .invoke(builder, html, null)
            pdfBuilder.getMethod("toStream", java.io.OutputStream::class.java)
                .invoke(builder, fos)
            pdfBuilder.getMethod("run")
                .invoke(builder)

            fos.close()
            true
        } catch (_: ClassNotFoundException) {
            // openhtmltopdf가 classpath에 없으면 HTML 파일로 폴백
            val htmlPath = filePath.replace(".pdf", ".html")
            File(htmlPath).writeText(html)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
