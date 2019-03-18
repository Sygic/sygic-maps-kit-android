#!/usr/bin/env bash

set -e

echo no | android create avd --force -n test -t android-${ANDROID_API_LEVEL} --abi ${ANDROID_ABI} -c 400M
emulator -avd test -no-audio -no-window &
android-wait-for-emulator
adb shell input keyevent 82 &

exit 0