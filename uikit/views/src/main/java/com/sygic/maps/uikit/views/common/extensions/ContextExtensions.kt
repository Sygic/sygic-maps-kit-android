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

package com.sygic.maps.uikit.views.common.extensions

import android.content.*
import android.content.Context.*
import android.location.LocationManager
import android.net.Uri
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import com.sygic.maps.uikit.views.R

inline val Context.locationManager: LocationManager?
    get() = getSystemService(LOCATION_SERVICE) as? LocationManager

inline val Context.clipboardManager: ClipboardManager?
    get() = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager

inline val Context.inputMethodManager: InputMethodManager?
    get() = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

fun Context.applyStyle(@StyleRes resId: Int, force: Boolean = false) {
    theme.applyStyle(resId, force)
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes resId: Int, typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    typedValue.let {
        theme.resolveAttribute(resId, it, resolveRefs)
        return it.data
    }
}

fun Context.getStringFromAttr(
    @AttrRes resId: Int, typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): String {
    typedValue.let {
        theme.resolveAttribute(resId, it, resolveRefs)
        if (it.type == TypedValue.TYPE_STRING && it.string != null) {
            return it.string.toString()
        }
        return EMPTY_STRING
    }
}

fun Context.openActivity(targetClass: Class<out AppCompatActivity>) {
    startActivity(Intent(this, targetClass))
}

fun Context.isRtl(): Boolean {
    return this.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

fun Context.openUrl(url: String, vararg flags: Int = intArrayOf()) {
    if (!TextUtils.isEmpty(url)) {
        var parsedUrl = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            parsedUrl = "http://$url"
        }

        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(parsedUrl)).apply { flags.forEach { addFlags(it) } })
        } catch (e: ActivityNotFoundException) {
            longToast(R.string.no_browser_client)
        }
    }
}

fun Context.openEmail(mailto: String, vararg flags: Int = intArrayOf()) {
    if (!TextUtils.isEmpty(mailto)) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)

        emailIntent.data = Uri.parse("mailto:$mailto")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailto))
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)).apply { flags.forEach { addFlags(it) } })
        } catch (e: ActivityNotFoundException) {
            longToast(R.string.no_email_client)
        }
    }
}

fun Context.openPhone(phoneNumber: String, vararg flags: Int = intArrayOf()) {
    if (!TextUtils.isEmpty(phoneNumber)) {
        val phoneIntent = Intent(Intent.ACTION_DIAL)

        phoneIntent.data = Uri.parse("tel:$phoneNumber")

        try {
            startActivity(Intent.createChooser(phoneIntent, null).apply { flags.forEach { addFlags(it) } })
        } catch (e: ActivityNotFoundException) {
            longToast(R.string.no_phone_client)
        }
    }
}

fun Context.copyToClipboard(text: String) {
    clipboardManager?.let {
        it.primaryClip = ClipData.newPlainText(text, text)
        longToast(R.string.copied_to_clipboard)
    }
}

fun Context.shortToast(@StringRes text: Int) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
fun Context.longToast(@StringRes text: Int) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun Context.showKeyboard(view: View) = inputMethodManager?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
fun Context.toggleKeyboard() = inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
fun Context.hideKeyboard(view: View) = inputMethodManager?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)