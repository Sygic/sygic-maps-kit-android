package com.sygic.ui.common.sdk.model

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.sygic.sdk.map.CameraState
import com.sygic.sdk.map.data.SimpleCameraDataModel


class ExtendedCameraModel : SimpleCameraDataModel(), DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        if (owner is Fragment) owner.activity?.run {
            if (isFinishing) {
                clear()
                cameraState = CameraState.Builder().build()
            }
        }
    }
}