package kr.nerdvana.nerdscribe.feature.export.service

/**
 * HTML -> PDF 변환 내보내기.
 * 플랫폼별 actual 구현이 필요하다.
 */
expect object PdfExporter {
    /**
     * HTML 문자열을 PDF 파일로 변환하여 저장한다.
     *
     * @param html     HTML 콘텐츠
     * @param filePath 출력 PDF 파일 경로
     * @return 성공 여부
     */
    suspend fun export(html: String, filePath: String): Boolean
}
