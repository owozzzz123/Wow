package com.yourcompany.rootbypassmodule;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RootBypassModule implements IXposedHookLoadPackage {

    private static final String TARGET_PACKAGE = "com.yourcompany.yourapp"; // 대상 앱 패키지 이름

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(TARGET_PACKAGE)) {
            return;
        }

        // RootCheckManager.isRootCheckEnabled()을 후킹하여 항상 false 반환
        XposedHelpers.findAndHookMethod(
                "com.yourcompany.yourapp.util.RootCheckManager",
                lpparam.classLoader,
                "isRootCheckEnabled",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(false);
                    }
                }
        );

        // RootCheckManager.isDeviceRooted(Context)을 후킹하여 항상 false 반환
        XposedHelpers.findAndHookMethod(
                "com.yourcompany.yourapp.util.RootCheckManager",
                lpparam.classLoader,
                "isDeviceRooted",
                android.content.Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(false);
                    }
                }
        );

        // SystemPropertyWrapper.get(String)을 후킹하여 항상 빈 문자열 반환
        XposedHelpers.findAndHookMethod(
                "com.yourcompany.yourapp.util.SystemPropertyWrapper",
                lpparam.classLoader,
                "get",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult("");
                    }
                }
        );

        // 추가 검사 메서드 후킹 (필요 시 확장)
        // 예: checkSuBinary(), checkSuByRuntimeExec(), checkBuildTags()을 false로 후킹
        hookPrivateMethod(lpparam.classLoader, "checkSuBinary", false);
        hookPrivateMethod(lpparam.classLoader, "checkSuByRuntimeExec", false);
        hookPrivateMethod(lpparam.classLoader, "checkBuildTags", false);
    }

    private void hookPrivateMethod(ClassLoader classLoader, String methodName, final Object result) {
        XposedHelpers.findAndHookMethod(
                "com.yourcompany.yourapp.util.RootCheckManager",
                classLoader,
                methodName,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(result);
                    }
                }
        );
    }
}
