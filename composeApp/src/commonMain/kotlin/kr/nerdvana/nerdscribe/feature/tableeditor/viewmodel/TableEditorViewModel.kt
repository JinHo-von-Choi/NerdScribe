package kr.nerdvana.nerdscribe.feature.tableeditor.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kr.nerdvana.nerdscribe.feature.tableeditor.model.ColumnAlignment
import kr.nerdvana.nerdscribe.feature.tableeditor.model.TableModel

/**
 * 테이블 편집 ViewModel.
 * 테이블 CRUD, 마크다운 파싱/시리얼라이즈 로직을 관리한다.
 */
class TableEditorViewModel {

    private val _table    = MutableStateFlow(TableModel())
    val table: StateFlow<TableModel> = _table.asStateFlow()

    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

    fun show(existingMarkdown: String? = null) {
        if (existingMarkdown != null) {
            val parsed = parseMarkdownTable(existingMarkdown)
            if (parsed != null) {
                _table.value = parsed
            }
        } else {
            _table.value = TableModel()
        }
        _isVisible.value = true
    }

    fun hide() {
        _isVisible.value = false
    }

    fun updateCell(row: Int, col: Int, value: String) {
        _table.update { t ->
            val newRows = t.rows.toMutableList().apply {
                val newRow = this[row].toMutableList().apply { this[col] = value }
                this[row] = newRow
            }
            t.copy(rows = newRows)
        }
    }

    fun addRow() {
        _table.update { t ->
            val emptyRow = List(t.columnCount) { "" }
            t.copy(rows = t.rows + listOf(emptyRow))
        }
    }

    fun addColumn() {
        _table.update { t ->
            val newRows = t.rows.map { it + "" }
            t.copy(
                rows       = newRows,
                alignments = t.alignments + ColumnAlignment.LEFT
            )
        }
    }

    fun removeRow(index: Int) {
        if (_table.value.rowCount <= 1) return
        _table.update { t ->
            t.copy(rows = t.rows.filterIndexed { i, _ -> i != index })
        }
    }

    fun removeColumn(index: Int) {
        if (_table.value.columnCount <= 1) return
        _table.update { t ->
            val newRows = t.rows.map { row -> row.filterIndexed { i, _ -> i != index } }
            val newAlignments = t.alignments.filterIndexed { i, _ -> i != index }
            t.copy(rows = newRows, alignments = newAlignments)
        }
    }

    fun setAlignment(col: Int, alignment: ColumnAlignment) {
        _table.update { t ->
            val newAlignments = t.alignments.toMutableList().apply { this[col] = alignment }
            t.copy(alignments = newAlignments)
        }
    }

    /**
     * 현재 테이블을 마크다운 문자열로 변환한다.
     */
    fun toMarkdown(): String {
        val t = _table.value
        if (t.rows.isEmpty()) return ""

        val sb = StringBuilder()

        // 헤더 행
        sb.append("| ")
        sb.append(t.headerRow.joinToString(" | ") { it.ifEmpty { " " } })
        sb.appendLine(" |")

        // 구분선
        sb.append("| ")
        sb.append(t.alignments.joinToString(" | ") { align ->
            when (align) {
                ColumnAlignment.LEFT   -> "---"
                ColumnAlignment.CENTER -> ":---:"
                ColumnAlignment.RIGHT  -> "---:"
            }
        })
        sb.appendLine(" |")

        // 데이터 행
        for (row in t.dataRows) {
            sb.append("| ")
            sb.append(row.joinToString(" | ") { it.ifEmpty { " " } })
            sb.appendLine(" |")
        }

        return sb.toString()
    }

    /**
     * 마크다운 테이블 문자열을 파싱하여 TableModel로 변환한다.
     */
    private fun parseMarkdownTable(markdown: String): TableModel? {
        val lines = markdown.trim().lines()
            .filter { it.contains("|") }
            .map { it.trim().removePrefix("|").removeSuffix("|") }

        if (lines.size < 2) return null

        val headerCells = lines[0].split("|").map { it.trim() }
        val separatorCells = lines[1].split("|").map { it.trim() }

        val alignments = separatorCells.map { cell ->
            when {
                cell.startsWith(":") && cell.endsWith(":") -> ColumnAlignment.CENTER
                cell.endsWith(":")                           -> ColumnAlignment.RIGHT
                else                                         -> ColumnAlignment.LEFT
            }
        }

        val dataRows = lines.drop(2).map { line ->
            val cells = line.split("|").map { it.trim() }
            cells + List((headerCells.size - cells.size).coerceAtLeast(0)) { "" }
        }

        val allRows = listOf(headerCells) + dataRows

        return TableModel(rows = allRows, alignments = alignments)
    }
}
