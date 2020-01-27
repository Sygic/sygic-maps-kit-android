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

package com.sygic.drive.module.networking.di.module;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sygic.drive.module.networking.managers.auth.AuthManager;
import com.sygic.drive.module.networking.managers.auth.AuthManagerImpl;
import com.sygic.drive.module.networking.managers.networking.ApiRepository;
import com.sygic.drive.module.networking.managers.networking.ApiRepositoryImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkManagersModule {

    @Provides
    AuthManager provideAuthManager(@NonNull final Application application) {
        return AuthManagerImpl.getInstance(application);
    }

    @Provides
    ApiRepository provideApiRepository(@NonNull final Application application, @NonNull final AuthManager authManager) {
        return ApiRepositoryImpl.getInstance(application, authManager);
    }
}