/*
 * Copyright (c) 2020 Sygic a.s. All rights reserved.
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

package com.sygic.maps.module.common.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.sygic.maps.module.common.routingoptions.PersistentRoutingOptionsManager;
import com.sygic.maps.module.common.routingoptions.RoutingOptionsManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoutingOptionsModule {
    private static final String ROUTING_OPTIONS_PREFERENCES = "routing_options_preferences";

    @Singleton
    @Provides
    @Named(ROUTING_OPTIONS_PREFERENCES)
    public SharedPreferences provideRoutingOptionsPreferences(Application app) {
        return app.getSharedPreferences(ROUTING_OPTIONS_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    public RoutingOptionsManager provideRoutingOptionsManager(@Named(ROUTING_OPTIONS_PREFERENCES) SharedPreferences preferences) {
        return new PersistentRoutingOptionsManager(preferences);
    }
}