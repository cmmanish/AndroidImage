#!/usr/bin/env bash

gradle clean build;
adb install app/build/outputs/apk/app-debug.apk