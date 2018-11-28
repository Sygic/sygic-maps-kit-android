package com.sygic.ui.view.poidetail

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sygic.ui.common.sdk.data.PoiData
import com.sygic.ui.view.poidetail.databinding.LayoutPoiDetailInternalBinding
import com.sygic.ui.view.poidetail.listener.PoiDetailStateListener
import com.sygic.ui.view.poidetail.viewmodel.PoiDetailInternalViewModel
import java.io.InvalidClassException
import java.lang.ref.WeakReference
import java.util.*

@BindingMethods(
    BindingMethod(
        type = PoiDetailView::class,
        attribute = "stateListener",
        method = "addStateListener"
    )
)
@Suppress("unused", "MemberVisibilityCanBePrivate")
class PoiDetailView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    LinearLayout(context, attrs, defStyle) {

    private val internalBinding: LayoutPoiDetailInternalBinding
    private val poiDetailInternalViewModel: PoiDetailInternalViewModel

    private val behavior: BottomSheetBehavior<PoiDetailView> = BottomSheetBehavior()
    private val stateListeners = LinkedHashSet<WeakReference<PoiDetailStateListener>>()

    private val poiData: MutableLiveData<PoiData> = MutableLiveData()
    private val onHeaderContainerClickObserver = Observer<Any> { behavior.state = BottomSheetBehavior.STATE_EXPANDED }

    init {
        isClickable = true
        isFocusable = true
        orientation = VERTICAL
        behavior.isHideable = true
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, @BottomSheetBehavior.State newState: Int) {
                val iterator = stateListeners.iterator()
                while (iterator.hasNext()) {
                    iterator.next().get()?.onPoiDetailStateChanged(newState) ?: iterator.remove()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        setBackgroundColor(context)

        if (context is FragmentActivity) {
            poiDetailInternalViewModel = ViewModelProviders.of(
                context,
                PoiDetailInternalViewModel.ViewModelFactory()
            ).get(PoiDetailInternalViewModel::class.java)
        } else {
            throw InvalidClassException("The PoiDetailView host must be the FragmentActivity")
        }
        poiDetailInternalViewModel.setOnHeaderContainerClickObserver(onHeaderContainerClickObserver)

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

        poiDetailInternalViewModel.addDataObservable(poiData)
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
        poiDetailInternalViewModel.removeDataObservable(poiData)

        super.onDetachedFromWindow()
    }

    fun setState(state: Int) {
        behavior.state = state
    }

    fun addStateListener(poiDetailStateListener: PoiDetailStateListener) {
        stateListeners.add(WeakReference(poiDetailStateListener))
    }

    fun setData(poiData: PoiData?) {
        if (poiData == null) {
            this.poiData.value = PoiData()
            return
        }

        this.poiData.value = poiData
    }
}
