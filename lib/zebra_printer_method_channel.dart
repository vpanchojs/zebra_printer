import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'zebra_printer_platform_interface.dart';

/// An implementation of [ZebraPrinterPlatform] that uses method channels.
class MethodChannelZebraPrinter extends ZebraPrinterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('zebra_printer');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool?> onConnectBluetooth(String macAddress) async {
    final Map<String, dynamic> params = {"mac": macAddress};
    return await methodChannel.invokeMethod('onConnectBluetooth', params);
  }

  @override
  Future<bool?> onDisconnectBluetooth() async {
    final Map<String, dynamic> params = {};
    return await methodChannel.invokeMethod('onDisconnectBluetooth', params);
  }

  @override
  Future<bool?> printZPLOverBluetooth(
      {required String macAddress, required String data}) async {
    final Map<String, dynamic> params = {"mac": macAddress, "data": data};
    return await methodChannel.invokeMethod('printZPLOverBluetooth', params);
  }
}
