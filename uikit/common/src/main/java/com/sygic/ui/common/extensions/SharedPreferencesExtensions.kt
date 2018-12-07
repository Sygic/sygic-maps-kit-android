package com.sygic.ui.common.extensions

import android.content.SharedPreferences
import android.text.TextUtils
import java.security.spec.InvalidParameterSpecException

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
@Suppress("UNCHECKED_CAST")
inline operator fun <reified T> SharedPreferences.set(key: String, value: T) {
    if (TextUtils.isEmpty(key)) {
        throw InvalidParameterSpecException("Invalid key")
    }

    val editor = this.edit()
    when (value) {
        is Boolean -> editor.putBoolean(key, value as Boolean)
        is Float -> editor.putFloat(key, value as Float)
        is Int -> editor.putInt(key, value as Int)
        is Long -> editor.putLong(key, value as Long)
        is String -> editor.putString(key, value as String)
        is Set<*> -> editor.putStringSet(key, value as Set<String>)
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
    editor.apply()
}