package com.sygic.modules.routeplanner

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sygic.modules.common.MapFragmentWrapper
import com.sygic.modules.routeplanner.databinding.LayoutRoutePlannerBinding
import com.sygic.modules.routeplanner.di.DaggerRoutePlannerComponent
import com.sygic.modules.routeplanner.di.RoutePlannerComponent
import com.sygic.modules.routeplanner.viewmodel.RoutePlannerFragmentViewModel

@Suppress("unused", "MemberVisibilityCanBePrivate")
class RoutePlannerFragment : MapFragmentWrapper() {

    private var attributesTypedArray: TypedArray? = null

    private lateinit var routePlannerFragmentViewModel: RoutePlannerFragmentViewModel

    override fun onInflate(context: Context, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        injector<RoutePlannerComponent, RoutePlannerComponent.Builder>(DaggerRoutePlannerComponent.builder()) { it.inject(this) }
        super.onInflate(context, attrs, savedInstanceState)

        attributesTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoutePlannerFragment)
    }

    override fun onAttach(context: Context) {
        injector<RoutePlannerComponent, RoutePlannerComponent.Builder>(DaggerRoutePlannerComponent.builder()) { it.inject(this) }
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        routePlannerFragmentViewModel = viewModelOf(RoutePlannerFragmentViewModel::class.java, attributesTypedArray)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutRoutePlannerBinding = LayoutRoutePlannerBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.routePlannerFragmentViewModel = routePlannerFragmentViewModel
        val root = binding.root as ViewGroup
        super.onCreateView(inflater, root, savedInstanceState)?.let {
            root.addView(it, 0)
        }
        return root
    }
}