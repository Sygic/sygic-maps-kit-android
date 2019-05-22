package com.sygic.samples.benchmark

import android.Manifest
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.benchmark.BenchmarkRule
import androidx.benchmark.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.sygic.sdk.map.MapAnimation
import com.sygic.sdk.map.MapFragment
import kotlinx.coroutines.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class GlViewBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @get:Rule
    val activityRule = ActivityTestRule(BenchmarkActivity::class.java)

    @get:Rule
    val grantLocationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    private suspend fun getMapFragment(): MapFragment? {
        var fragment: MapFragment?

        while (findFragment().also { fragment = it } == null || fragment?.mapView == null) {
            delay(200L)
        }

        return fragment
    }

    private fun findFragment(): MapFragment? {
        activityRule.activity.supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is MapFragment) return fragment
        }

        return null
    }

    @Test
    fun benchmarkGlViews() {
        var i = 0
        var fragment: MapFragment?

        runBlocking {
            val job = async(Dispatchers.Main) {
                getMapFragment()
            }

            fragment = job.await()

            benchmarkRule.measureRepeated {
                launch(Dispatchers.Main) {
                    fragment?.mapView?.view?.run {
                        val xy = getCoordinates(this, i++)
                        val startTime = SystemClock.uptimeMillis()
                        dispatchTouchEvent(
                            MotionEvent.obtain(
                                startTime,
                                startTime,
                                MotionEvent.ACTION_DOWN,
                                xy[0],
                                xy[1],
                                0
                            )
                        )
                        dispatchTouchEvent(
                            MotionEvent.obtain(
                                startTime,
                                SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_UP,
                                xy[0],
                                xy[1],
                                0
                            )
                        )
                    }
                }

                fragment?.cameraDataModel?.setZoomLevel(Math.max(0f, 20f - (i / 4f)), MapAnimation(450, MapAnimation.InterpolationCurve.Linear))

                runWithTimingDisabled {
                    SystemClock.sleep(500L)
                }
            }
        }

        System.out.println("benchmark done with ${i-1} views laid out")
    }

    private fun getCoordinates(view: View, viewNo: Int): FloatArray {
        val xy = IntArray(2)
        view.getLocationOnScreen(xy)

        val sector = viewNo % 8

        val noXSectors: Int = if (view.width > view.height) 4 else 2
        val noYSectors: Int = if (view.width > view.height) 2 else 4

        val sectorXWidth = (view.width - xy[0]) / noXSectors
        val sectorYWidth = (view.height - xy[1]) / noYSectors

        val sectorX = (sectorXWidth / 2) + ((sector % noXSectors) * sectorXWidth)
        val sectorY = (sectorYWidth / 2) + ((sector / noXSectors) * sectorYWidth)

        return floatArrayOf(sectorX.toFloat(), sectorY.toFloat())
    }
}