package com.sygic.modules.browsemap.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.AttributeSet
import android.util.Log
import com.sygic.modules.browsemap.R
import com.sygic.sdk.SygicEngine

class BrowseMapFragmentViewModel(application: Application, attrs: AttributeSet?) : AndroidViewModel(application) {

    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassRotation: MutableLiveData<Float> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    init {
        compassRotation.value = 45f //todo

        attrs?.let {
            val typedArray = application.obtainStyledAttributes(it, R.styleable.BrowseMapFragment)
            compassEnabled.value = typedArray.getBoolean(R.styleable.BrowseMapFragment_sygic_compassEnabled, false)
            positionLockFabEnabled.value = typedArray.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFabEnabled, false)
            zoomControlsEnabled.value = typedArray.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControlsEnabled, false)

            val key = typedArray.getString(R.styleable.BrowseMapFragment_sygic_secretKey)
            key?.let {
                SygicEngine.Builder("sdk-test", key, application).setInitListener(object : SygicEngine.OnInitListener {
                    override fun onSdkInitialized() {
                        Log.d("BrowseMapFragment", "onSdkInitialized()")
                    }

                    override fun onError(@SygicEngine.OnInitListener.InitError error: Int) {}
                }).init()
            }
            typedArray.recycle()
        }
    }

    internal class ViewModelFactory(private val application: Application, private val attrs: AttributeSet?) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BrowseMapFragmentViewModel(application, attrs) as T
        }
    }
}