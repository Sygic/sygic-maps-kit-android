package com.sygic.modules.browsemap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.util.AttributeSet
import com.sygic.modules.browsemap.R
import android.content.pm.PackageManager
import com.sygic.sdk.SygicEngine
import com.sygic.sdk.online.OnlineManager

class BrowseMapFragmentViewModel(application: Application, attrs: AttributeSet?) : AndroidViewModel(application) {

    val compassEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val compassHideIfNorthUp: MutableLiveData<Boolean> = MutableLiveData()
    val positionLockFabEnabled: MutableLiveData<Boolean> = MutableLiveData()
    val zoomControlsEnabled: MutableLiveData<Boolean> = MutableLiveData()

    init {
        attrs?.let {
            val typedArray = application.obtainStyledAttributes(it, R.styleable.BrowseMapFragment)
            compassEnabled.value = typedArray.getBoolean(R.styleable.BrowseMapFragment_sygic_compassEnabled, false)
            compassHideIfNorthUp.value = typedArray.getBoolean(R.styleable.BrowseMapFragment_sygic_compassHideIfNorthUp, false)
            positionLockFabEnabled.value = typedArray.getBoolean(R.styleable.BrowseMapFragment_sygic_positionLockFabEnabled, false)
            zoomControlsEnabled.value = typedArray.getBoolean(R.styleable.BrowseMapFragment_sygic_zoomControlsEnabled, false)
            typedArray.recycle()

            //ToDO MS-4508
            getApiKey()?.let { key ->
                SygicEngine.Builder("sdk-test", key, application).setInitListener(object : SygicEngine.OnInitListener {
                    override fun onSdkInitialized() {
                        OnlineManager.getInstance().enableOnlineMapStreaming(true)
                    }

                    override fun onError(@SygicEngine.OnInitListener.InitError error: Int) {}
                }).init()
            } ?: run {
                //ToDO MS-4508
            }
        }
    }

    //ToDO MS-4508
    private fun getApiKey(): String? {
        return try {
            val applicationInfo = getApplication<Application>().packageManager.getApplicationInfo(
                getApplication<Application>().packageName,
                PackageManager.GET_META_DATA
            )
            applicationInfo.metaData.getString("com.sygic.ApiKey") //ToDO MS-4508
        } catch (e: PackageManager.NameNotFoundException) {
            null
        } catch (e: NullPointerException) {
            null
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