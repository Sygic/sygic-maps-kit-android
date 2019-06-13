/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 *
 * This project is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.module.search

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sygic.maps.module.common.delegate.ModulesComponentDelegate
import com.sygic.maps.module.search.callback.SearchResultCallback
import com.sygic.maps.module.search.databinding.LayoutSearchBinding
import com.sygic.maps.module.search.di.DaggerSearchComponent
import com.sygic.maps.module.search.viewmodel.SearchFragmentViewModel
import com.sygic.maps.tools.viewmodel.factory.ViewModelFactory
import com.sygic.maps.uikit.viewmodels.common.initialization.SdkInitializationManager
import com.sygic.maps.uikit.viewmodels.common.search.MAX_RESULTS_COUNT_DEFAULT_VALUE
import com.sygic.maps.uikit.viewmodels.searchresultlist.SearchResultListViewModel
import com.sygic.maps.uikit.viewmodels.searchtoolbar.SearchToolbarViewModel
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_INPUT
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_LOCATION
import com.sygic.maps.uikit.viewmodels.searchtoolbar.component.KEY_SEARCH_MAX_RESULTS_COUNT
import com.sygic.maps.uikit.views.common.extensions.*
import com.sygic.maps.uikit.views.searchresultlist.data.SearchResultItem
import com.sygic.maps.uikit.views.searchtoolbar.SearchToolbar
import com.sygic.sdk.online.OnlineManager
import com.sygic.sdk.position.GeoCoordinates
import com.sygic.sdk.search.SearchResult
import javax.inject.Inject

const val SEARCH_FRAGMENT_TAG = "search_fragment_tag"

/**
 * A *[SearchFragment]* is the core component for any search operation. It can be easily used to display search input
 * and search result list on the same screen. It can be modified with initial search input or coordinates. It comes with
 * several pre build-in elements such as [SearchToolbar] or [SearchResultList].
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class SearchFragment : Fragment(), SdkInitializationManager.Callback {

    private val modulesComponent = ModulesComponentDelegate()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    internal lateinit var sdkInitializationManager: SdkInitializationManager

    private lateinit var fragmentViewModel: SearchFragmentViewModel
    private lateinit var searchToolbarViewModel: SearchToolbarViewModel
    private lateinit var searchResultListViewModel: SearchResultListViewModel

    private var injected = false
    private fun inject() {
        if (!injected) {
            DaggerSearchComponent.builder().plus(modulesComponent.getInstance(this)).build().inject(this)
            injected = true
        }
    }

    /**
     * If *[searchInput]* is defined, then it will be used as input text.
     *
     * @param [String] text input to be processed.
     *
     * @return [String] the text input value.
     */
    var searchInput: String
        get() = if (::searchToolbarViewModel.isInitialized) {
            searchToolbarViewModel.inputText.value.toString()
        } else arguments.getString(KEY_SEARCH_INPUT, EMPTY_STRING)
        set(value) {
            arguments = Bundle(arguments).apply { putString(KEY_SEARCH_INPUT, value) }
            if (::searchToolbarViewModel.isInitialized) {
                searchToolbarViewModel.inputText.value = value
            }
        }

    /**
     * If *[searchLocation]* is defined, then it will be used to improve search accuracy.
     *
     * @param [GeoCoordinates] search position to be used, null otherwise.
     *
     * @return [GeoCoordinates] the search position value.
     */
    var searchLocation: GeoCoordinates?
        get() = if (::searchToolbarViewModel.isInitialized) {
            searchToolbarViewModel.searchLocation
        } else arguments.getParcelableValue(KEY_SEARCH_LOCATION)
        set(value) {
            arguments = Bundle(arguments).apply { putParcelable(KEY_SEARCH_LOCATION, value) }
            if (::searchToolbarViewModel.isInitialized) {
                searchToolbarViewModel.searchLocation = value
            }
        }

    /**
     * A *[maxResultsCount]* define the maximum number of search results.
     *
     * @param [Int] the maximum number of search results.
     *
     * @return [Int] the maximum number of search results.
     */
    var maxResultsCount: Int
        get() = if (::searchToolbarViewModel.isInitialized) {
            searchToolbarViewModel.maxResultsCount
        } else arguments.getInt(KEY_SEARCH_MAX_RESULTS_COUNT, MAX_RESULTS_COUNT_DEFAULT_VALUE)
        set(value) {
            arguments = Bundle(arguments).apply { putInt(KEY_SEARCH_MAX_RESULTS_COUNT, value) }
            if (::searchToolbarViewModel.isInitialized) {
                searchToolbarViewModel.maxResultsCount = value
            }
        }

    init {
        if (arguments == null) {
            arguments = Bundle.EMPTY
        }
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        inject()
        super.onInflate(context, attrs, savedInstanceState)
        resolveAttributes(attrs)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        inject()
        sdkInitializationManager.initialize(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentViewModel = ViewModelProviders.of(
            this,
            viewModelFactory/*.with(searchFragmentInitComponent)*/ //todo
        )[SearchFragmentViewModel::class.java].apply {
            this.hideKeyboardObservable.observe(
                this@SearchFragment,
                Observer<Any> { hideKeyboard() })
            this.popBackStackObservable.observe(
                this@SearchFragment,
                Observer<Any> { fragmentManager?.popBackStack() })
        }
        searchToolbarViewModel = ViewModelProviders.of(
            this,
            viewModelFactory.with(arguments)
        )[SearchToolbarViewModel::class.java].apply {
            this.onActionSearchClickObservable.observe(
                this@SearchFragment,
                Observer<Any> { fragmentViewModel.onActionSearchClick() })
        }
        searchResultListViewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        )[SearchResultListViewModel::class.java].apply {
            this.removeFocusAndHideKeyboardObservable.observe(
                this@SearchFragment,
                Observer<Any> {
                    searchToolbarViewModel.searchToolbarFocused.value = false
                    hideKeyboard()
                })
            this.onSearchResultItemClickObservable.observe(
                this@SearchFragment,
                Observer<SearchResultItem<out SearchResult>> { fragmentViewModel.onSearchResultItemClick(it) })
            this.searchResultListDataChangedObservable.observe(
                this@SearchFragment,
                Observer<List<SearchResultItem<out SearchResult>>> { fragmentViewModel.searchResultListDataChanged(it) })
        }

        lifecycle.addObserver(fragmentViewModel)
        lifecycle.addObserver(searchToolbarViewModel)
        lifecycle.addObserver(searchResultListViewModel)
    }

    @CallSuper
    override fun onSdkInitialized() {
        OnlineManager.getInstance().enableOnlineMapStreaming(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LayoutSearchBinding = LayoutSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.searchFragmentViewModel = fragmentViewModel
        binding.searchToolbarViewModel = searchToolbarViewModel
        binding.searchResultListViewModel = searchResultListViewModel
        return binding.root
    }

    /**
     * Register a custom callback to be invoked when a search process is done.
     *
     * @param callback [SearchResultCallback] callback to invoke when a search process is done.
     */
    fun setResultCallback(callback: SearchResultCallback?) {
        if (::fragmentViewModel.isInitialized) {
            fragmentViewModel.searchResultCallback = callback
        } else {
            //searchFragmentInitComponent.searchResultCallback = callback //todo
        }
    }

    /**
     * Register a custom callback to be invoked when a search process is done.
     *
     * @param callback [SearchResultCallback] callback to invoke when a search process is done.
     */
    fun setResultCallback(callback: (searchResultList: List<SearchResult>) -> Unit) {
        setResultCallback(object : SearchResultCallback {
            override fun onSearchResult(searchResultList: List<SearchResult>) = callback(searchResultList)
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(fragmentViewModel)
        lifecycle.removeObserver(searchToolbarViewModel)
        lifecycle.removeObserver(searchResultListViewModel)
    }

    fun resolveAttributes(attributes: AttributeSet) {
        with(requireContext().obtainStyledAttributes(attributes, R.styleable.SearchFragment)) {
            if (hasValue(R.styleable.SearchFragment_sygic_initial_search_input)) {
                searchInput = getString(R.styleable.SearchFragment_sygic_initial_search_input) ?: EMPTY_STRING
            }
            if (hasValue(R.styleable.SearchFragment_sygic_initial_latitude)
                && hasValue(R.styleable.SearchFragment_sygic_initial_longitude)
            ) {
                searchLocation = GeoCoordinates(
                    getFloat(R.styleable.SearchFragment_sygic_initial_latitude, Float.NaN).toDouble(),
                    getFloat(R.styleable.SearchFragment_sygic_initial_longitude, Float.NaN).toDouble()
                )
            }
            if (hasValue(R.styleable.SearchFragment_sygic_max_results_count)) {
                maxResultsCount =
                    getInt(R.styleable.SearchFragment_sygic_max_results_count, MAX_RESULTS_COUNT_DEFAULT_VALUE)
            }

            recycle()
        }
    }
}
