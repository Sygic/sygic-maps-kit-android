package com.sygic.modules.common.theme

import com.sygic.ui.common.sdk.skin.MapSkin


interface ThemeManager {

    sealed class SkinLayer(val position: Int) {
        object DayNight : SkinLayer(0)
        object Vehicle : SkinLayer(1)
    }

    fun setSkinAtLayer(skinLayer: SkinLayer, @MapSkin desiredSkin: String)
}