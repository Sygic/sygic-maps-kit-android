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

package com.sygic.maps.uikit.viewmodels.common.extensions

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import java.lang.System.identityHashCode

private const val COMPONENT_FRAGMENT_TAG = "FragmentsComponentFragmentTag"

fun LifecycleOwner.context(): Context? {
    return activity()
}

fun LifecycleOwner.activity(): Activity? {
    return when (this) {
        is Activity -> this
        is Fragment -> activity
        else -> throw  NotImplementedError("Unexpected LifecycleOwner ${this::class}! Only Activity and Fragment are supported as LifecycleOwner by now.")
    }
}

private fun Fragment.getScopeId() =
    if (id == 0) "$COMPONENT_FRAGMENT_TAG@${identityHashCode(this)}" else "$COMPONENT_FRAGMENT_TAG@$id"

val Fragment.extendedScope: Scope
    get() {
        val scopeId = getScopeId()
        return getKoin().getScopeOrNull(scopeId)
            ?: getKoin().createScope(scopeId, TypeQualifier(this::class)).also {
                bindExtendedScope(
                    it
                )
            }
    }

private fun Fragment.bindExtendedScope(scope: Scope) {
    Log.d("LifecycleExtensions", "bindExtendedScope ${scope.id} for fragment ${this.id}")

    if (this.id != 0) {
        this@bindExtendedScope.getKoin()
            .getScopeOrNull("$COMPONENT_FRAGMENT_TAG@${identityHashCode(this)}")?.let {
                copyAndDestroyScope(it, targetScope = extendedScope)
            }
    } else {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                val koin = this@bindExtendedScope.getKoin()

                if (koin.getScopeOrNull(this@bindExtendedScope.getScopeId()) == null) {
                    val finalScope = koin.createScope(
                        this@bindExtendedScope.getScopeId(),
                        TypeQualifier(this@bindExtendedScope::class)
                    )
                    copyAndDestroyScope(scope, targetScope = finalScope)
                }
            }
        })
    }
}

private fun Fragment.copyAndDestroyScope(originalScope: Scope, targetScope: Scope) {
    originalScope.beanRegistry.getAllDefinitions().forEach {
        targetScope.beanRegistry.saveDefinition(it.apply { options.override = true })
    }

    requireFragmentManager().also { fragmentManager ->
        ComponentHolderFragment(targetScope).also {
            fragmentManager.beginTransaction()
                .add(it, COMPONENT_FRAGMENT_TAG + this.id)
                .commit()
        }
    }

    Log.d("LifecycleExtensions", "closing scope ${originalScope.id}")
    originalScope.close()
    getKoin().scopeRegistry.deleteScopeInstance(originalScope.id)
}

inline fun <reified T> Fragment.injectExtendedOrDeclare(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
    noinline fallback: () -> T
): Lazy<T> =
    lazy {
        this@injectExtendedOrDeclare.extendedScope.getOrNull<T>(qualifier, parameters)
            ?: fallback().also { this@injectExtendedOrDeclare.extendedScope.declare(it, qualifier) }
    }

inline fun <reified T> Fragment.injectExtended(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = lazy {
    this.extendedScope.get<T>(T::class, qualifier, parameters)
}


@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ComponentHolderFragment(val scope: Scope) : Fragment() {

    init {
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()

        scope.close()
    }
}