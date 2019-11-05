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

package com.sygic.maps.uikit.viewmodels.common.voice

import androidx.annotation.RestrictTo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sygic.maps.uikit.viewmodels.common.extensions.getFirstTtsVoiceForLanguage
import com.sygic.maps.uikit.views.common.extensions.observeOnce
import com.sygic.sdk.voice.VoiceEntry
import com.sygic.sdk.voice.VoiceManager
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object VoiceManagerClientImpl : VoiceManagerClient {

    private val managerProvider: LiveData<VoiceManager> = object : MutableLiveData<VoiceManager>() {
        init { value = VoiceManager.getInstance() }
    }

    override val currentVoice by lazy { MutableLiveData<VoiceEntry>() }

    init {
        currentVoice.observeForever { voice -> managerProvider.observeOnce { it.voice = voice } }
    }

    override fun getInstalledVoices(callback: (List<VoiceEntry>) -> Unit) =
        managerProvider.observeOnce { it.getInstalledVoices { voices, _ -> callback.invoke(voices)  } }

    override fun setDefaultVoice() {
        getInstalledVoices { voices ->

            managerProvider.observeOnce { voiceManger ->
                voiceManger.getDefaultTtsLocale { defaultTTS ->

                    voices.getFirstTtsVoiceForLanguage(defaultTTS)?.let {
                        currentVoice.value = it
                    } ?: run {
                        voices.getFirstTtsVoiceForLanguage(Locale.ENGLISH.language)?.let { currentVoice.value = it }
                    }
                }
            }
        }
    }
}