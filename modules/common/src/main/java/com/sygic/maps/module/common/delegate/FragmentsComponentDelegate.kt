/*
 * Copyright (c) 2019 Sygic a.s. All rights reserved.
 * This project is licensed under the MIT License.
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sygic.maps.module.common.delegate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.sygic.maps.module.common.di.DaggerFragmentModulesComponent
import com.sygic.maps.module.common.di.FragmentModulesComponent

private const val COMPONENT_FRAGMENT_TAG = "FragmentsComponentFragmentTag"

object FragmentsComponentDelegate {

    fun getComponent(
        fragment: Fragment,
        delegate: ApplicationComponentDelegate
    ): FragmentModulesComponent {
        val fragmentManager = fragment.requireFragmentManager()

        val retainInstance =
            fragmentManager.findFragmentByTag(COMPONENT_FRAGMENT_TAG + fragment.id)

        val transaction: FragmentTransaction

        if (retainInstance != null) {
            if (retainInstance is ComponentHolderFragment && retainInstance.component != null) {
                return retainInstance.component
            } else {
                transaction = fragmentManager.beginTransaction().remove(retainInstance)
            }
        } else {
            transaction = fragmentManager.beginTransaction()
        }

        return ComponentHolderFragment(
            DaggerFragmentModulesComponent
                .builder()
                .applicationModulesComponent(delegate.getComponent(fragment))
                .build()
        ).also {
            transaction.add(it, COMPONENT_FRAGMENT_TAG + fragment.id).commit()
        }.component!!
    }

    class ComponentHolderFragment : Fragment {

        internal val component: FragmentModulesComponent?

        constructor() : super() {
            component = null
        }

        constructor(component: FragmentModulesComponent) : super() {
            this.component = component
        }

        init {
            retainInstance = true
        }
    }
}