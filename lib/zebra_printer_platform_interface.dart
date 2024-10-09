import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'zebra_printer_method_channel.dart';

abstract class ZebraPrinterPlatform extends PlatformInterface {
  /// Constructs a ZebraPrinterPlatform.
  ZebraPrinterPlatform() : super(token: _token);

  static final Object _token = Object();

  static ZebraPrinterPlatform _instance = MethodChannelZebraPrinter();

  /// The default instance of [ZebraPrinterPlatform] to use.
  ///
  /// Defaults to [MethodChannelZebraPrinter].
  static ZebraPrinterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ZebraPrinterPlatform] when
  /// they register themselves.
  static set instance(ZebraPrinterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool?> onConnectBluetooth(String macAddress) async {
    throw UnimplementedError('onConnectBluetooth() has not been implemented.');
  }

  Future<bool?> onDisconnectBluetooth() async {
    throw UnimplementedError(
        'onDisconnectBluetooth() has not been implemented.');
  }

  Future<bool?> printZPLOverBluetooth(
      {required String macAddress, required String data}) async {
    throw UnimplementedError(
        'printZPLOverBluetooth() has not been implemented.');
  }
}
