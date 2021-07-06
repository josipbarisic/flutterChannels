import 'dart:async';

import 'package:flutter/services.dart';
import 'package:method_channel_flutter_module/constants.dart';

const _channel = const EventChannel(EVENT_CHANNEL);

typedef void Listener(dynamic msg);

StreamSubscription<dynamic> startListening(Listener listener) {
  var subscription = _channel.receiveBroadcastStream().listen(
        listener,
        cancelOnError: false,
      );
  return subscription;
}
