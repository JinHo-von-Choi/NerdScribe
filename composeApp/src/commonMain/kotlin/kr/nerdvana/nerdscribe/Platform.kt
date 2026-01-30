package kr.nerdvana.nerdscribe

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform