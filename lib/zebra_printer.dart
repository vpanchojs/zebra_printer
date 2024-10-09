import 'zebra_printer_platform_interface.dart';

class ZebraPrinter {
  Future<String?> getPlatformVersion() {
    return ZebraPrinterPlatform.instance.getPlatformVersion();
  }

  Future<bool?> onConnectBluetooth(String macAddress) async {
    return ZebraPrinterPlatform.instance.onConnectBluetooth(macAddress);
  }

  Future<bool?> onDisconnectBluetooth() async {
    return ZebraPrinterPlatform.instance.onDisconnectBluetooth();
  }

  Future<bool?> printZPLOverBluetooth(
      {required String macAddress, required String data}) async {
    return ZebraPrinterPlatform.instance
        .printZPLOverBluetooth(macAddress: macAddress, data: data);
  }
}
