package com.sygic.sample

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sample.*

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_browse_map -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_search -> {
                    Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bottom_nav_navigation -> {
                    Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}
