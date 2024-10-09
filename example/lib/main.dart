import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:zebra_printer/zebra_printer.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _zebraPrinterPlugin = ZebraPrinter();
  final TextEditingController controllerMac = TextEditingController();
  final TextEditingController controlleZpl = TextEditingController();

  @override
  void initState() {
    super.initState();
    controlleZpl.text = """^XA^FO50,50^A0N,50,50^FDHola Mundo^FS^XZ""";
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _zebraPrinterPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              children: [
                Text('MAC ADDRESS'),
                TextFormField(
                  controller: controllerMac,
                ),
                Padding(
                  padding: const EdgeInsets.only(top: 16),
                  child: Text('ZPL'),
                ),
                TextFormField(
                  controller: controlleZpl,
                ),
                TextButton(
                    onPressed: () {
                      _zebraPrinterPlugin.printZPLOverBluetooth(
                          macAddress: '', data: '');
                    },
                    child: const Text('Print Hello Word'))
              ],
            ),
          ),
        ),
      ),
    );
  }
}
