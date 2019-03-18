#!/usr/bin/env bash

set -e

target="system-images;android-${ANDROID_API_LEVEL};default;${ANDROID_ABI}"
# echo y | sdkmanager --update
# echo y | sdkmanager --install $target
# avdmanager create avd --force -n test -k $target --device "Nexus 4" -c 2048M
android create avd --force -n test -k $target --device "Nexus" -c 2048M
# QEMU_AUDIO_DRV=none $ANDROID_HOME/emulator/emulator -avd test -no-window -memory 2048 &
emulator -avd test -no-audio -no-window &
android-wait-for-emulator
adb shell input keyevent 82 &

# todo
# echo no | android create avd --force -n test -t android-${ANDROID_API_LEVEL} --abi ${ANDROID_ABI} -c 400M
# emulator -avd test -no-audio -no-window &
# android-wait-for-emulator
# adb shell input keyevent 82 &

exit 0