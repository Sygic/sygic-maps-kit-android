package com.sygic.ui.view.poidetail.manager

import android.content.Context
import android.content.SharedPreferences
import com.sygic.ui.common.extensions.EMPTY_STRING
import com.sygic.ui.common.extensions.get
import com.sygic.ui.common.extensions.set
import com.sygic.ui.view.poidetail.R

const val PREFERENCES_NAME = "poi_detail_prefs"

internal class PreferencesManager(context: Context) {

    private val applicationContext = context.applicationContext
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var showcaseAllowed: Boolean
        get() = preferences[getKeyFromPrefKey(PrefKey.SHOWCASE_ALLOWED), true]
        set(value) {
            preferences[getKeyFromPrefKey(PrefKey.SHOWCASE_ALLOWED)] = value
        }

    private fun getKeyFromPrefKey(@PrefKey prefKey: Int): String {
        return when (prefKey) {
            PrefKey.SHOWCASE_ALLOWED -> applicationContext.getString(R.string.preferenceKey_poi_detail_showcase)
            else -> EMPTY_STRING
        }
    }
}