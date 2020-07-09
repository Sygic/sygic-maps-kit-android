/*
 * Copyright (c) 2020 Sygic a.s. All rights reserved.
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

package com.sygic.samples.offlinemaps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sygic.maps.offlinemaps.installed.InstalledMapsFragment
import com.sygic.maps.offlinemaps.loader.MapLoaderGlobal
import com.sygic.maps.uikit.viewmodels.common.services.ServicesManager
import com.sygic.samples.R
import dagger.android.AndroidInjection
import javax.inject.Inject

class OfflineMapsActivity : AppCompatActivity() {
    @Inject
    internal lateinit var servicesManager: ServicesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_maps)

        servicesManager.initializeSdk()

        MapLoaderGlobal.mapLoaderExceptionObservable.observe(this, Observer {
            MaterialAlertDialogBuilder(this@OfflineMapsActivity)
                .setTitle("Error occurred")
                .setMessage(it)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->  }
                .show()
        })

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, InstalledMapsFragment.newInstance())
            commit()
        }
    }
}
