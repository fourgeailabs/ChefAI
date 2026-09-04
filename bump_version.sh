#!/bin/bash

# 1. Update build.gradle.kts
sed -i 's/versionCode = 10/versionCode = 11/' app/build.gradle.kts
sed -i 's/versionName = "1.09.00"/versionName = "1.10.00"/' app/build.gradle.kts

# 2. Update build.yml
sed -i 's/ChefAI-Studio-v1.06.00-debug.apk/ChefAI-Studio-v1.10.00-debug.apk/g' .github/workflows/build.yml
sed -i 's/ChefAI-Studio-v1.09.00-debug.apk/ChefAI-Studio-v1.10.00-debug.apk/g' .github/workflows/build.yml

