import 'package:flutter_test/flutter_test.dart';
import 'package:zebra_printer/zebra_printer.dart';
import 'package:zebra_printer/zebra_printer_platform_interface.dart';
import 'package:zebra_printer/zebra_printer_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockZebraPrinterPlatform
    with MockPlatformInterfaceMixin
    implements ZebraPrinterPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
  
  @override
  Future<bool?> onConnectBluetooth(String macAddress) {
    // TODO: implement onConnectBluetooth
    throw UnimplementedError();
  }
  
  @override
  Future<bool?> onDisconnectBluetooth() {
    // TODO: implement onDisconnectBluetooth
    throw UnimplementedError();
  }
  
  @override
  Future<bool?> printZPLOverBluetooth({required String macAddress, required String data}) {
    // TODO: implement printZPLOverBluetooth
    throw UnimplementedError();
  }
}

void main() {
  final ZebraPrinterPlatform initialPlatform = ZebraPrinterPlatform.instance;

  test('$MethodChannelZebraPrinter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelZebraPrinter>());
  });

  test('getPlatformVersion', () async {
    ZebraPrinter zebraPrinterPlugin = ZebraPrinter();
    MockZebraPrinterPlatform fakePlatform = MockZebraPrinterPlatform();
    ZebraPrinterPlatform.instance = fakePlatform;

    expect(await zebraPrinterPlugin.getPlatformVersion(), '42');
  });
}
