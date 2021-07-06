package com.test.methodchannels

import android.app.Application
import com.test.methodchannels.main.util.FLUTTER_ENGINE_ID
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val flutterEngine = FlutterEngine(this)

        // Start executing Dart code in the FlutterEngine.
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )

        // Cache the pre-warmed FlutterEngine to be used later by FlutterFragment.
        FlutterEngineCache
            .getInstance()
            .put(FLUTTER_ENGINE_ID, flutterEngine)
    }
}