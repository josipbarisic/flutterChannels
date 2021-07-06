package com.test.methodchannels.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.test.methodchannels.R
import com.test.methodchannels.databinding.ActivityMainBinding
import com.test.methodchannels.main.repo.MainRepo
import com.test.methodchannels.main.util.AVERAGE_TEMPERATURE_METHOD
import com.test.methodchannels.main.util.EVENT_CHANNEL
import com.test.methodchannels.main.util.FLUTTER_ENGINE_ID
import com.test.methodchannels.main.util.METHOD_CHANNEL
import io.flutter.embedding.android.FlutterFragment
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity : FragmentActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val mainViewModel = MainViewModel(MainRepo())


    private var methodChannel: MethodChannel? = null
    private var eventChannel: EventChannel? = null
    private var eventSink: EventChannel.EventSink? = null

    companion object {
        private const val TAG_FLUTTER_FRAGMENT = "flutter_fragment"
    }

    private var flutterFragment: FlutterFragment? = null

    private val realtimeWeatherObserver = Observer<String> {
        viewBinding.tvRealtimeTemperature.textSize = if (it.toIntOrNull() == null) 30f else 75f
        viewBinding.realtimeTempIcon.visibility =
            if (it.toIntOrNull() == null) View.GONE else View.VISIBLE
        viewBinding.tvRealtimeTemperature.text = it

        eventSink?.let { sink ->
            if (it.toIntOrNull() != null) sink.success(it)
            else sink.error("404", it, it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val fragmentManager: FragmentManager = supportFragmentManager

        flutterFragment = fragmentManager
            .findFragmentByTag(TAG_FLUTTER_FRAGMENT) as FlutterFragment?

        if (flutterFragment == null) {
            val newFlutterFragment =
                FlutterFragment.withCachedEngine(FLUTTER_ENGINE_ID).build<FlutterFragment>()

            flutterFragment = newFlutterFragment
            fragmentManager
                .beginTransaction()
                .add(
                    R.id.fragment_container,
                    newFlutterFragment,
                    TAG_FLUTTER_FRAGMENT
                )
                .commit()
        }
        setUpObservers()
    }

    override fun onPostResume() {
        super.onPostResume()
        flutterFragment!!.onPostResume()
        flutterFragment?.flutterEngine?.let {
            setUpEventChannel(it.dartExecutor.binaryMessenger)
            setUpMethodChannel(it.dartExecutor.binaryMessenger)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        methodChannel?.setMethodCallHandler(null)
        eventChannel?.setStreamHandler(null)
    }

    private fun setUpEventChannel(
        binaryMessenger: BinaryMessenger,
        eventChannelName: String = EVENT_CHANNEL
    ) {
        eventChannel = EventChannel(binaryMessenger, eventChannelName).apply {
            setStreamHandler(
                object : EventChannel.StreamHandler {
                    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                        eventSink = events
                    }

                    override fun onCancel(arguments: Any?) {
//                        Log.d("MAIN_ACTIVITY", "onCancel: EventChannelAlpha")
                    }
                })
        }
    }

    private fun setUpMethodChannel(
        binaryMessenger: BinaryMessenger,
        methodChannelName: String = METHOD_CHANNEL
    ) {
        methodChannel = MethodChannel(binaryMessenger, methodChannelName).apply {
            setMethodCallHandler { call, result ->
                if (call.method == AVERAGE_TEMPERATURE_METHOD) result.success(
                    (10..35).random()
                ) else result.notImplemented()
            }
        }
    }

    private fun setUpObservers() {
        mainViewModel.realtimeWeather.observe(this, realtimeWeatherObserver)
    }
}