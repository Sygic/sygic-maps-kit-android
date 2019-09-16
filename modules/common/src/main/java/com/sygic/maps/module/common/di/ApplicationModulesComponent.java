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

package com.sygic.maps.module.common.di;

import android.app.Application;

import com.sygic.maps.module.common.di.module.AppModule;
import com.sygic.maps.module.common.di.module.DateTimeManagerModule;
import com.sygic.maps.module.common.di.module.LocationModule;
import com.sygic.maps.module.common.di.module.NavigationManagerModule;
import com.sygic.maps.module.common.di.module.PermissionsModule;
import com.sygic.maps.module.common.di.module.PoiDataManagerModule;
import com.sygic.maps.module.common.di.module.PositionManagerModule;
import com.sygic.maps.module.common.di.module.RouteDemonstrationManagerModule;
import com.sygic.maps.module.common.di.module.SdkInitializationManagerModule;
import com.sygic.maps.module.common.di.module.SearchModule;
import com.sygic.maps.module.common.poi.manager.PoiDataManager;
import com.sygic.maps.uikit.viewmodels.common.datetime.DateTimeManager;
import com.sygic.maps.uikit.viewmodels.common.initialization.SdkInitializationManager;
import com.sygic.maps.uikit.viewmodels.common.location.LocationManager;
import com.sygic.maps.uikit.viewmodels.common.navigation.preview.RouteDemonstrationManager;
import com.sygic.maps.uikit.viewmodels.common.permission.PermissionsManager;
import com.sygic.maps.uikit.viewmodels.common.search.SearchManager;
import com.sygic.sdk.navigation.NavigationManager;
import com.sygic.sdk.position.PositionManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                DateTimeManagerModule.class,
                LocationModule.class,
                NavigationManagerModule.class,
                PermissionsModule.class,
                PoiDataManagerModule.class,
                PositionManagerModule.class,
                RouteDemonstrationManagerModule.class,
                SearchModule.class,
                SdkInitializationManagerModule.class
        }
)
public interface ApplicationModulesComponent {
    Application getApplication();
    DateTimeManager getDateTimeManager();
    LocationManager getLocationManager();
    NavigationManager getNavigationManager();
    PermissionsManager getPermissionsManager();
    PoiDataManager getPoiDataManager();
    PositionManager getPositionManager();
    RouteDemonstrationManager getRouteDemonstrationManager();
    SearchManager getSearchManager();
    SdkInitializationManager getSdkInitializationManager();
}