package com.sygic.samples

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowseMapDefaultEspressoTest : BaseMapTest(BrowseMapDefaultActivity::class.java) {

    @Test
    fun browseMapDisplayed() {
        browseMap(activity) {}
    }
}