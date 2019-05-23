package com.sygic.samples.benchmark

import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.sygic.maps.module.browsemap.BrowseMapFragment
import com.sygic.sdk.map.MapView
import com.sygic.sdk.map.`object`.UiObject
import com.sygic.sdk.map.listeners.OnMapInitListener
import com.sygic.sdk.map.mapgesturesdetector.listener.MapGestureAdapter
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.layout_bubble.view.*


class BenchmarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_benchmark)

        val browseMapFragment = supportFragmentManager.findFragmentById(R.id.browseMapFragment) as BrowseMapFragment
        val dataModel = browseMapFragment.mapDataModel

        browseMapFragment.getMapAsync(object : OnMapInitListener {
            override fun onMapInitializationInterrupted() {
            }

            override fun onMapReady(mapView: MapView) {
                val viewFactory = ViewFactory()
                mapView.addMapGestureListener(object : MapGestureAdapter() {
                    override fun onMapClicked(e: MotionEvent, isTwoFingers: Boolean): Boolean {
                        mapView.requestObjectsAtPoint(e.x, e.y) { viewObjects, _, _, id ->
                            run {
                                dataModel.addMapObject(
                                    UiObject.create(
                                        viewObjects.first().position,
                                        viewFactory.withId(id)
                                    ).setAnchor(0.2f, 0.3f)
                                        .build()
                                )
                            }
                        }
                        return true
                    }
                })
            }
        })
    }

    @Parcelize
    class ViewFactory : UiObject.ViewFactory {

        @IgnoredOnParcel
        private var id: Int = 0

        override fun createView(inflater: LayoutInflater, container: ViewGroup?): View =
            getBubbleView(inflater, container, id)

        private fun getBubbleView(
            inflater: LayoutInflater,
            parent: ViewGroup?,
            index: Int
        ): ViewGroup {
            val bubble =
                inflater.inflate(R.layout.layout_bubble, parent, false) as ViewGroup
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

        fun withId(id: Int): UiObject.ViewFactory = this.apply { this.id = id }

        private companion object : Parceler<ViewFactory> {
            override fun ViewFactory.write(parcel: Parcel, flags: Int) {
                parcel.writeInt(id)
            }

            override fun create(parcel: Parcel): ViewFactory = ViewFactory().apply { withId(parcel.readInt()) }
        }
    }
}