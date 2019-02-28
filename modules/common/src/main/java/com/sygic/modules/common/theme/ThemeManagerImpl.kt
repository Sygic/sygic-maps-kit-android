package com.sygic.modules.common.theme

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewTreeObserver
import com.sygic.ui.common.sdk.model.ExtendedMapDataModel
import com.sygic.ui.common.sdk.skin.MapSkin


class ThemeManagerImpl(app: Application, private val mapDataModel: ExtendedMapDataModel) : ThemeManager {

    private var isDefaultSkin = true
    private var currentNightMode: Int = Configuration.UI_MODE_NIGHT_UNDEFINED

    init {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.window.decorView.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        activity.window.decorView.viewTreeObserver.removeOnPreDrawListener(this)

                        //set skin only if default(== auto) mode is set
                        if (isDefaultSkin) {
                            setSkinAtLayer(
                                ThemeManager.SkinLayer.DayNight,
                                getCurrentMapMode(activity.resources),
                                false
                            )
                        }
                        return true
                    }
                })
            }

        })
    }

    override fun setSkinAtLayer(skinLayer: ThemeManager.SkinLayer, @MapSkin desiredSkin: String) {
        setSkinAtLayer(skinLayer, desiredSkin, true)
    }

    private fun setSkinAtLayer(
        skinLayer: ThemeManager.SkinLayer, @MapSkin desiredSkin: String,
        replaceDefault: Boolean
    ) {
        isDefaultSkin = !(replaceDefault && MapSkin.DEFAULT != desiredSkin)

        @MapSkin val newSkin = if (MapSkin.DEFAULT == desiredSkin) {
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