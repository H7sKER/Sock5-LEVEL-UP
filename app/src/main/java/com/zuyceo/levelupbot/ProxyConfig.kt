package com.zuyceo.levelupbot

internal object ProxyConfig {
    internal val H = intArrayOf(50,48,51,46,49,55,53,46,49,50,53,46,49,53,49)
    internal val P = 10417

    internal fun host(): String = H.map { it.toChar() }.joinToString("")
    internal fun port(): Int = P
}
