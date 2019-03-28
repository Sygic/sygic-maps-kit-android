package com.sygic.modules.common.theme


interface ThemeManager {

    sealed class SkinLayer(val position: Int) {
        object DayNight : SkinLayer(0)
        object Vehicle : SkinLayer(1)
    }

    fun setSkinAtLayer(skinLayer: SkinLayer, desiredSkin: String)
}