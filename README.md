# SygicMapsKit - Android

[![Build Status](https://travis-ci.com/Sygic/sygic-maps-kit-android.svg?branch=master)][8] [![GitHub release](https://img.shields.io/github/release/Sygic/sygic-maps-kit-android.svg)][1] [![License: MIT](https://img.shields.io/github/license/Sygic/sygic-maps-kit-android.svg)][12] ![Minimal API version level 21](https://img.shields.io/badge/API_level-21-green.svg)

A powerful open-source library based on [Sygic Maps SDK][5] which can be used to display rich map content and interact with it.
(if you are looking for a iOS version, you can find it [here][4])

<p align="center"><img src="assets/images/sygic_logo.png" alt="Sygic Logo"></p>

## Getting Started

To get familiar with all the features available, you can first try out our Sample App.

<p align="center"><a href="assets/images/screenshots_orig.png" target="_blank"><img src="assets/images/screenshots.png" alt="Screenshots"></a></p>
<p align="center"><a href="https://play.google.com/store/apps/details?id=com.sygic.samples" target="_blank"><img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="Get it on Google Play" height="80"></a></p>

### Prerequisites

1. **Request the Sygic API key**. To start developing with Sygic Maps SDK, please [fill this form][6] and get your API key.

2. Specify these Gradle properties:

    ```gradle
    android {
        dataBinding {
            enabled = true
        }
        compileOptions {
            sourceCompatibility 1.8
            targetCompatibility 1.8
        }
    }
    ```

3. Min API **21** (Android 5.0 Lollipop)

### Installing

Gradle:

```gradle
dependencies {
    implementation 'com.sygic.maps:module-browsemap:1.0.0'
    implementation 'com.sygic.maps:module-search:1.0.0' (coming soon)
    implementation 'com.sygic.maps:module-navigation:1.0.0' (coming soon)
    ...
}
```

Finally, you need to add your API key to the Android Manifest:

```xml
<manifest
    package="com.sygic.samples">
    <application>

    <meta-data
        android:name="@string/com_sygic_api_key"
        android:value="place your API key here" />
    </application>

</manifest>
```

### Basic Usage

Simply put the BrowseMapFragment to your layout container:

```xml
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <fragment
        android:id="@+id/browseMapFragment"
        class="com.sygic.maps.module.browsemap.BrowseMapFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</FrameLayout>
```

for more usage examples go to the [Wiki][2] page.

## Help

First read the [Wiki][2] page, then try to search on [Stackoverflow][9] or visit the GitHub [issues][3] page.

## Authors

* **Miroslav Kacera** - *Primary contributor & Team Leader* - [bio007][10]
* **Tomáš Valenta** - *Primary contributor* - [YAV][11]

## License

    This project is licensed under the MIT License

    Copyright (c) 2019 - Sygic a.s.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
[1]: https://github.com/Sygic/sygic-maps-kit-android/releases
[2]: https://github.com/Sygic/sygic-maps-kit-android/wiki
[3]: https://github.com/Sygic/sygic-maps-kit-android/issues
[4]: https://github.com/Sygic/sygic-maps-kit-ios/
[5]: https://www.sygic.com/enterprise/maps-navigation-sdk-api-developers
[6]: https://www.sygic.com/enterprise/get-api-key/
[7]: #
[8]: https://github.com/Sygic/sygic-maps-kit-android/
[9]: https://stackoverflow.com/questions/tagged/android+sygic
[10]: https://github.com/bio007
[11]: https://github.com/TomasValenta
[12]: https://github.com/Sygic/sygic-maps-kit-android/blob/master/LICENSE
