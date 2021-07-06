import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:method_channel_flutter_module/method_channel.dart';

import 'event_channel.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Flutter Channels',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  var eventChannelWeather = 'N/A';
  var methodChannelWeather = 'Not Fetched';
  late StreamSubscription eventChannelSubscription;

  void startWeatherSubscription() {
    eventChannelSubscription = startListening((msg) {
      setState(() {
        eventChannelWeather = msg;
      });
    });

    eventChannelSubscription.onError((e, st) {
      final error = e as PlatformException;
      setState(() {
        eventChannelWeather = error.message.toString();
      });
    });
  }

  void cancelWeatherSubscription() => eventChannelSubscription.cancel();

  void getMethodChannelWeather() async {
    var average = await getAverageTemperature();
    setState(() {
      methodChannelWeather = average.toString();
    });
  }

  Widget _contentContainer(String title, Color color, String weather) {
    return Container(
      color: color,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Text(
            title,
            style: TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
              fontSize: 27,
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(20),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  weather,
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                    fontSize: int.tryParse(weather) != null ? 33 : 24,
                  ),
                ),
                if (int.tryParse(weather) != null)
                  Icon(
                    Icons.thermostat_sharp,
                    color: Colors.white,
                    size: 33,
                  )
              ],
            ),
          )
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    startWeatherSubscription();
    return Scaffold(
      backgroundColor: Colors.lightBlueAccent,
      body: Center(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 30),
              child: Text(
                'Flutter Fragment',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 30,
                ),
              ),
            ),
            Expanded(
              child: _contentContainer(
                  'Event Channel Weather', Colors.blue, eventChannelWeather),
            ),
            Expanded(
              child: _contentContainer('Method Channel Weather',
                  Colors.indigoAccent, methodChannelWeather),
            ),
            Padding(
              padding: const EdgeInsets.all(10),
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(primary: Colors.indigoAccent),
                onPressed: () => getMethodChannelWeather(),
                child: Padding(
                  padding: const EdgeInsets.all(15),
                  child: Text('Fetch Weather'),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
