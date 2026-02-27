package kr.nerdvana.nerdscribe.core.model

/**
 * 앱 전역 설정 데이터.
 *
 * @param isDarkTheme       다크 모드 활성화 여부
 * @param autoSaveEnabled   자동 저장 활성화 여부
 * @param autoSaveIntervalMs 자동 저장 주기 (밀리초)
 * @param syncScrollEnabled 동기 스크롤 활성화 여부
 * @param fontSize          에디터 폰트 크기 (sp)
 * @param showOutline       아웃라인 패널 표시 여부
 * @param showFileTree      파일 트리 패널 표시 여부
 */
data class AppSettings(
    val isDarkTheme: Boolean        = false,
    val autoSaveEnabled: Boolean    = true,
    val autoSaveIntervalMs: Long    = 30_000L,
    val syncScrollEnabled: Boolean  = true,
    val fontSize: Int               = 14,
    val showOutline: Boolean        = true,
    val showFileTree: Boolean       = true
)
