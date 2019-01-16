package com.sygic.samples.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity

data class Sample(
    val target: Class<out AppCompatActivity>, @DrawableRes val previewImage: Int, @StringRes val title: Int, @StringRes val subtitle: Int
)