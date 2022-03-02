package me.exz.bilibilicomicnoteenager

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    private val TAG = "bilibilicomicnoteenager"

    private val packageName = "com.bilibili.comic"
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (packageName == lpparam.packageName) {
            hook(lpparam)
        }
    }

    private fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        val hookClass =
            lpparam.classLoader.loadClass("com.bilibili.comic.flutter.channel.method.FlutterTeenagerHandler")
                ?: return
        val callClass =
            XposedHelpers.findClass("io.flutter.plugin.common.MethodCall", lpparam.classLoader)
                ?: return
        val resultClass = XposedHelpers.findClass(
            "io.flutter.plugin.common.MethodChannel.Result",
            lpparam.classLoader
        ) ?: return
        val resultMethod =
            XposedHelpers.findMethodsByExactParameters(resultClass, null, Any().javaClass)[0]
                ?: return
        XposedHelpers.findAndHookMethod(
            hookClass,
            "onMethodCall",
            callClass,
            resultClass,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?) {
                    param?.let {
                        Log.i(TAG, "hooked teenager")
                        val result = param.args[1]
                        resultMethod.invoke(result, null)
                    }
                }
            })
    }

}