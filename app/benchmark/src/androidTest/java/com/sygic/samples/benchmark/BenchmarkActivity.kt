package com.sygic.samples.benchmark

import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.gl.GlView
import com.sygic.sdk.map.listeners.OnMapInitListener
import com.sygic.sdk.map.mapgesturesdetector.listener.MapGestureAdapter
import kotlinx.android.synthetic.main.layout_bubble.view.*


class BenchmarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_benchmark)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        browseMapFragment.getMapAsync(object : OnMapInitListener {
            override fun onMapInitializationInterrupted() {
            }

            override fun onMapReady(mapView: MapView) {

                mapView.addMapGestureListener(object : MapGestureAdapter() {
                    override fun onMapClicked(e: MotionEvent, isTwoFingers: Boolean): Boolean {
                        mapView.requestObjectsAtPoint(e.x, e.y) { viewObjects, _, _, id ->
                            run {
                                val bubble = getBubbleView(id)
                                browseMapFragment.addGlView(
                                    GlView.of(
                                        bubble,
                                        viewObjects.first().position,
                                        PointF(0.2f, 0.3f)
                                    )
                                )
                            }
                        }
                        return true
                    }
                })
            }
        })
    }

    private fun getBubbleView(index: Int): ViewGroup {
        val bubble =
            LayoutInflater.from(this@BenchmarkActivity).inflate(R.layout.layout_bubble, null) as ViewGroup
        bubble.setOnClickListener { v ->
            run {
                TransitionManager.beginDelayedTransition(bubble)
                if (v.progressBar.visibility == View.VISIBLE) {
                    v.progressBar.visibility = View.GONE
                    v.progressBar2.visibility = View.GONE
                    v.ratingBar.visibility = View.GONE
                } else {
                    v.progressBar.visibility = View.VISIBLE
                    v.progressBar2.visibility = View.VISIBLE
                    v.ratingBar.visibility = View.VISIBLE
                }
            }
        }
        bubble.textView.text = "GlVIew $index"
        return bubble
    }
}