package io.github.khalil212002.unmockgps

import android.location.Location
import android.os.Bundle
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class UnMockGPS : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val locationClass = Location::class.java.name

            val methods = listOf(
                "isMock",
                "isFromMockProvider",
                "getProvider",
                "getLatitude",
                "getLongitude",
                "getAccuracy",
                "hasAccuracy",
                "getAltitude",
                "hasAltitude",
                "getSpeed",
                "hasSpeed",
                "getBearing",
                "hasBearing",
                "getTime",
                "getElapsedRealtimeNanos",
                "getExtras"
            )

            for (method in methods) {
                XposedHelpers.findAndHookMethod(
                    locationClass,
                    lpparam.classLoader,
                    method,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val value = when (val result = param.result) {
                                null -> "null"
                                is Bundle -> "Bundle(size=${result.size()})"
                                else -> result.toString()
                            }

                            XposedBridge.log(
                                "UnMockDiagnostic: " +
                                    "pkg=${lpparam.packageName} " +
                                    "method=$method " +
                                    "result=$value"
                            )
                        }
                    }
                )
            }

            XposedBridge.log(
                "UnMockDiagnostic: modul read-only aktif di ${lpparam.packageName}; " +
                    "tidak mengubah hasil method Location."
            )
        } catch (e: Throwable) {
            XposedBridge.log(
                "UnMockDiagnostic error: ${e.javaClass.name}: ${e.message}"
            )
        }
    }
}
