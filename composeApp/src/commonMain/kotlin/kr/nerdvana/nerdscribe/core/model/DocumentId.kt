package kr.nerdvana.nerdscribe.core.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * UUID 기반 문서 식별자.
 * value class로 런타임 오버헤드 없이 타입 안전성을 제공한다.
 */
@JvmInline
value class DocumentId @OptIn(ExperimentalUuidApi::class) constructor(
    val value: String = Uuid.random().toString()
)
