package com.sygic.ui.common.extensions

import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.sygic.ui.common.R

fun Context.isRtl(): Boolean {
    return this.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
}

fun Context.openUrl(url: String) {
    if (!TextUtils.isEmpty(url)) {
        var parsedUrl = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            parsedUrl = "http://$url"
        }

        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(parsedUrl)))
        } catch (e: ActivityNotFoundException) {
            longToast(R.string.no_browser_client)
        }
    }
}

fun Context.openEmail(mailto: String) {
    if (!TextUtils.isEmpty(mailto)) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)

        emailIntent.data = Uri.parse("mailto:$mailto")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailto))
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
        } catch (e: ActivityNotFoundException) {
            longToast(R.string.no_email_client)
        }
    }
}

fun Context.openPhone(phoneNumber: String) {
    if (!TextUtils.isEmpty(phoneNumber)) {
        val phoneIntent = Intent(Intent.ACTION_DIAL)
        phoneIntent.data = Uri.parse("tel:$phoneNumber")
        try {
            phoneIntent.resolveActivity(packageManager)?.let { startActivity(phoneIntent) }
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

fun Context.shortToast(@StringRes text: Int) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(@StringRes text: Int) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

inline val Context.locationManager: LocationManager?
    get() = getSystemService(LOCATION_SERVICE) as? LocationManager

inline val Context.clipboardManager: ClipboardManager?
    get() = getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager