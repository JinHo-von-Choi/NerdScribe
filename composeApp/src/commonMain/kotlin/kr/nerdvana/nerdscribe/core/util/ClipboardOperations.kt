package kr.nerdvana.nerdscribe.core.util

/**
 * 클립보드에서 이미지를 가져오는 expect 함수.
 * 이미지가 없으면 null을 반환한다.
 *
 * @return PNG 바이트 배열, 또는 null
 */
expect suspend fun getClipboardImage(): ByteArray?
