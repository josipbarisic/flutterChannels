import 'package:flutter/services.dart';
import 'package:method_channel_flutter_module/constants.dart';

const _methodChannel = MethodChannel(METHOD_CHANNEL);

Future<int> getAverageTemperature() async {
  try {
    return await _methodChannel.invokeMethod(AVERAGE_TEMPERATURE_METHOD);
  } on PlatformException catch (e) {
    print(e);
    return Future.value(null);
  }
}
