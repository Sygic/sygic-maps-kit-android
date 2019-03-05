package com.sygic.samples

import android.Manifest
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.sygic.samples.idling.MapReadyIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class BaseMapTest(activityClass: Class<out CommonSampleActivity>) {

    @get:Rule
    val activityRule : ActivityTestRule<out CommonSampleActivity> = ActivityTestRule(activityClass)

    @get:Rule
    val grantPermissionRuleLocation: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    private val idlingResources = mutableListOf<IdlingResource>()

    protected val activity: CommonSampleActivity
        get() = activityRule.activity

    @Before
    fun registerIdlingResource() {
        idlingResources.add(MapReadyIdlingResource(activity))

        IdlingRegistry.getInstance().register(*idlingResources.toTypedArray())
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(*idlingResources.toTypedArray())
    }
}