package com.sygic.ui.view.poidetail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.view.poidetail.databinding.LayoutPoiDetailInternalBinding
import com.sygic.ui.view.poidetail.viewmodel.PoiDetailInternalViewModel
import android.util.TypedValue
import androidx.core.content.ContextCompat
import java.io.InvalidClassException

@Suppress("unused", "MemberVisibilityCanBePrivate")
class PoiDetailView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(context, attrs, defStyle) {

    private val internalBinding: LayoutPoiDetailInternalBinding
    private val poiDetailInternalViewModel: PoiDetailInternalViewModel
    private val behavior: BottomSheetBehavior<PoiDetailView> = BottomSheetBehavior()

    val poiData: MutableLiveData<PoiData> = MutableLiveData()

    init {
        isClickable = true
        isFocusable = true
        behavior.isHideable = true
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        setBackgroundColor(context)

        if (context is FragmentActivity) {
            poiDetailInternalViewModel = ViewModelProviders.of(
                context,
                PoiDetailInternalViewModel.ViewModelFactory()
            ).get(PoiDetailInternalViewModel::class.java)
        } else {
            throw InvalidClassException("The PoiDetailView host must be the FragmentActivity")
        }

        internalBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_poi_detail_internal, this, true)
        internalBinding.setLifecycleOwner(context)
        internalBinding.poiDetailInternalViewModel = poiDetailInternalViewModel
    }

    private fun setBackgroundColor(context: Context) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            setBackgroundColor(typedValue.data)
        } else {
            // windowBackground is not a color, probably a drawable
            background = ContextCompat.getDrawable(context, typedValue.resourceId)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        poiDetailInternalViewModel.addObservable(poiData)
        if (layoutParams is CoordinatorLayout.LayoutParams) {
            (layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
        } else {
            throw InvalidClassException("The PoiDetailView parent must be the CoordinatorLayout")
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        behavior.peekHeight = internalBinding.headerContainer.height
    }

    override fun onDetachedFromWindow() {
        poiDetailInternalViewModel.removeObservable(poiData)

        super.onDetachedFromWindow()
    }

    fun setState(state: Int) {
        behavior.state = state
    }

    fun setData(poiData: PoiData?) {
        if (poiData == null) {
            this.poiData.value = PoiData()
            return
        }

        this.poiData.value = poiData
    }
}
