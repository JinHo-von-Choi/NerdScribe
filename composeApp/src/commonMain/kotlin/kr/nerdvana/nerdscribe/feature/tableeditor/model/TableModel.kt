package kr.nerdvana.nerdscribe.feature.tableeditor.model

/**
 * 테이블 열 정렬.
 */
enum class ColumnAlignment {
    LEFT, CENTER, RIGHT
}

/**
 * 편집 가능한 테이블 모델.
 *
 * @param rows       행 데이터 (각 행은 셀 문자열 리스트)
 * @param alignments 각 열의 정렬 방향
 */
data class TableModel(
    val rows: List<List<String>>          = listOf(listOf("", ""), listOf("", "")),
    val alignments: List<ColumnAlignment> = listOf(ColumnAlignment.LEFT, ColumnAlignment.LEFT)
) {
    val rowCount: Int get()    = rows.size
    val columnCount: Int get() = alignments.size

    /** 첫 행을 헤더로 간주 */
    val headerRow: List<String> get() = rows.firstOrNull() ?: emptyList()
    val dataRows: List<List<String>> get() = rows.drop(1)
}
