/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.module.common.theme

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import com.sygic.maps.uikit.viewmodels.common.sdk.model.ExtendedMapDataModel
import com.sygic.maps.uikit.viewmodels.common.sdk.skin.MapSkin

class ThemeManagerImpl(app: Application, private val mapDataModel: ExtendedMapDataModel) : ThemeManager {

    private var currentNightMode: Int = Configuration.UI_MODE_NIGHT_UNDEFINED

    init {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                setSkinAtLayer(ThemeManager.SkinLayer.DayNight, getCurrentMapMode(activity.resources))
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}
            override fun onActivityStopped(activity: Activity) {}
        })

        setSkinAtLayer(ThemeManager.SkinLayer.DayNight, getCurrentMapMode(app.resources))
    }

    override fun setSkinAtLayer(skinLayer: ThemeManager.SkinLayer, desiredSkin: String) {
        val newSkin = if (MapSkin.DEFAULT == desiredSkin) {
            getMapMode(currentNightMode)
        } else {
            desiredSkin
        }

        val skins: MutableList<String> = ArrayList(mapDataModel.skin)
        skins[skinLayer.position] = newSkin
        mapDataModel.skin = skins
    }

    @MapSkin
    private fun getCurrentMapMode(resources: Resources): String {
        currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return getMapMode(currentNightMode)
    }
}

@MapSkin
private fun getMapMode(currentNightMode: Int): String {
    return when (currentNightMode) {
        Configuration.UI_MODE_NIGHT_YES -> MapSkin.NIGHT
        Configuration.UI_MODE_NIGHT_UNDEFINED, Configuration.UI_MODE_NIGHT_NO -> MapSkin.DAY
        else -> MapSkin.DAY
    }
}
