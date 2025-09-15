#!/bin/bash
set -e

cd ~/lsposed_build_project || { echo "Project folder not found"; exit 1; }

echo ">>> 1. settings.gradle 생성"
cp settings.gradle settings.gradle.bak 2>/dev/null || true
cat > settings.gradle <<'EOF'
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = 'lsposed_build_project'
EOF
echo "settings.gradle 생성 완료"

echo ">>> 2. build.gradle 패치 (plugins 블록 버전 삽입)"
if [ -f build.gradle ]; then
    cp build.gradle build.gradle.bak
    # plugins 블록 위치 찾기
    plugins_line=$(grep -n '^plugins {' build.gradle | cut -d: -f1 | head -n1)
    if [ -z "$plugins_line" ]; then
        echo "plugins 블록을 찾지 못했습니다. build.gradle 확인 필요"
    else
        # plugins 블록 수정: com.android.library 버전 명시
        awk -v pl="$plugins_line" 'NR==pl {print; getline; if ($0 ~ /id '"'"'com.android.library'"'"'/) print "    id '\''com.android.library'\'' version '\''7.4.2'\''"; else print; next} 1' build.gradle > build.gradle.tmp
        mv build.gradle.tmp build.gradle
        echo "build.gradle plugins 블록 패치 완료"
    fi
else
    echo "build.gradle 없음! 확인 필요"
    exit 1
fi

echo ">>> 3. Gradle wrapper 생성"
gradle wrapper
chmod +x ./gradlew

echo ">>> 4. Gradle 캐시 갱신 + Debug 빌드"
./gradlew --no-daemon --refresh-dependencies clean assembleDebug --stacktrace --info