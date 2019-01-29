package com.sygic.ui.common.sdk.skin

fun isMapSkinValid(it: String): Boolean {
    if (it.isEmpty()) return false
    if (it == MapSkin.DEFAULT || it == MapSkin.DAY || it == MapSkin.NIGHT) return true

    return false
}