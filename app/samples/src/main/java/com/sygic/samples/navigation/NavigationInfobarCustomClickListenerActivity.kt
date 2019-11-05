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

package com.sygic.samples.navigation

import android.media.MediaPlayer
import android.os.Bundle
import com.sygic.maps.module.navigation.NavigationFragment
import com.sygic.maps.module.navigation.infobar.NavigationDefaultUnlockedLeftInfobarButton
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.InfobarButtonType
import com.sygic.maps.uikit.viewmodels.navigation.infobar.button.OnInfobarButtonClickListener
import com.sygic.maps.uikit.views.navigation.infobar.buttons.InfobarButton
import com.sygic.samples.R
import com.sygic.samples.app.activities.CommonSampleActivity
import com.sygic.samples.navigation.utils.SampleDemonstrationRoutePlan
import com.sygic.samples.utils.getPrimaryRoute
import com.sygic.sdk.map.Camera

class NavigationInfobarCustomClickListenerActivity : CommonSampleActivity() {

    override val wikiModulePath = "Module-Navigation#navigation---infobar-custom-click-listener"

    private var fanfareMediaPlayer: MediaPlayer? = null
    private lateinit var navigationFragment: NavigationFragment

    private val unlockedLeftInfobarButtonClickListener = object : OnInfobarButtonClickListener {
        override val button = NavigationDefaultUnlockedLeftInfobarButton()
        override fun onButtonClick() = lockVehicle()
    }

    private val fanfareLeftInfobarButtonClickListener = object : OnInfobarButtonClickListener {
        override val button = InfobarButton(
            R.drawable.ic_play,
            R.drawable.bg_infobar_button_rounded,
            R.color.white,
            R.color.colorAccent
        )

        override fun onButtonClick() = playFanfare()
    }

    private val cameraModeChangedListener = object : Camera.ModeChangedListener {
        override fun onRotationModeChanged(@Camera.RotationMode mode: Int) {}
        override fun onMovementModeChanged(@Camera.MovementMode mode: Int) {
            when (mode) {
                Camera.MovementMode.Free ->
                    setLeftInfobarButtonClickListener(unlockedLeftInfobarButtonClickListener)
                Camera.MovementMode.FollowGpsPosition, Camera.MovementMode.FollowGpsPositionWithAutozoom ->
                    setLeftInfobarButtonClickListener(fanfareLeftInfobarButtonClickListener)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_navigation_infobar_custom_click_listener)

        fanfareMediaPlayer = MediaPlayer.create(this, R.raw.fanfare)
        navigationFragment = (supportFragmentManager.findFragmentById(R.id.navigationFragment) as NavigationFragment)

        if (savedInstanceState == null) {
            setLeftInfobarButtonClickListener(fanfareLeftInfobarButtonClickListener)
            SampleDemonstrationRoutePlan().getPrimaryRoute { navigationFragment.route = it }
        }
    }

    override fun onResume() {
        super.onResume()

        navigationFragment.cameraDataModel.addModeChangedListener(cameraModeChangedListener)
    }

    private fun setLeftInfobarButtonClickListener(listener: OnInfobarButtonClickListener) =
        navigationFragment.setOnInfobarButtonClickListener(InfobarButtonType.LEFT, listener)

    private fun playFanfare() {
        fanfareMediaPlayer?.apply {
            if (isPlaying) {
                stop()
                prepare()
            }

            start()
        }
    }

    private fun lockVehicle() {
        with(navigationFragment) {
            cameraDataModel.rotationMode = Camera.RotationMode.Vehicle
            cameraDataModel.movementMode = Camera.MovementMode.FollowGpsPositionWithAutozoom
        }
    }

    override fun onPause() {
        super.onPause()

        navigationFragment.cameraDataModel.removeModeChangedListener(cameraModeChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        fanfareMediaPlayer?.apply {
            stop()
            if (isFinishing) release()
        }
    }
}