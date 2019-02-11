package com.sygic.samples.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sygic.samples.CommonSampleActivity

data class Sample(
    val target: Class<out CommonSampleActivity>, @DrawableRes val previewImage: Int, @StringRes val title: Int, @StringRes val subtitle: Int
)