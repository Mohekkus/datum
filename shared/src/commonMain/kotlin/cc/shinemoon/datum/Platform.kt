package cc.shinemoon.datum

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform