package com.sygic.samples

import android.Manifest
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.sygic.samples.idling.MapReadyIdlingResource
import com.sygic.samples.rules.DisableAnimationsRule
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class BaseMapTest(activityClass: Class<out CommonSampleActivity>) {

    @get:Rule
    val disableAnimationsRule = DisableAnimationsRule()

    @get:Rule
    val activityRule = ActivityTestRule(activityClass)

    @get:Rule
    val grantLocationPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    private var mapReadyIdlingResource: IdlingResource? = null

    protected val activity: CommonSampleActivity
        get() = activityRule.activity

    @Before
    fun registerIdlingResource() {
        mapReadyIdlingResource = MapReadyIdlingResource(activity)

        IdlingRegistry.getInstance().register(mapReadyIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        mapReadyIdlingResource?.let { IdlingRegistry.getInstance().unregister(it) }
    }
}