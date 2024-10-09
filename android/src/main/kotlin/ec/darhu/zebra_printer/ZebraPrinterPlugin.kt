package ec.darhu.zebra_printer

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import com.zebra.sdk.btleComm.BluetoothLeConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.ConnectionException
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/** ZebraPrinterPlugin */
class ZebraPrinterPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var logTag: String = "ZebraPrinter"
  private lateinit var context: Context
  private lateinit var activity: Activity
  var conn: Connection? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "zebra_printer")
    channel.setMethodCallHandler(this)         
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    val result = MethodResultWrapper(result)
    Thread(MethodRunner(call, result)).start()
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  inner class MethodRunner(call: MethodCall, result: Result) : Runnable {
    private val call: MethodCall = call
    private val result: Result = result

    override fun run() {
      when (call.method) {        
        "printZPLOverBluetooth" -> {
          onPrintZplDataOverBluetooth(call, result)
        }
        "onConnectBluetooth"->{
          onConnectBluetooth(call, result)
        }
        "onDisconnectBluetooth"->{
          onDisconnectBluetooth(call, result)
        }
        else -> result.notImplemented()
      }
    }
  }

  private fun onConnectBluetooth(@NonNull call: MethodCall, @NonNull result: Result){
    var macAddress: String? = call.argument("mac");
    try {
      conn = BluetoothLeConnection(macAddress,context)
      conn!!.open()
      result.success(true)
    }
    catch(e: Exception) {
      e.printStackTrace()
      result.error("Error", "onConnectBluetooth", e)
    }
  }

  private fun onDisconnectBluetooth(@NonNull call: MethodCall, @NonNull result: Result){
    if (null != conn) {
      if(!conn!!.isConnected){
        result.success(true);
        return
      }
      try {
        conn!!.close()
        result.success(true)
      } catch (e: ConnectionException) {
        e.printStackTrace()
        result.error("Error", "onDisconnectBluetooth", e)
      }
    }else{
      result.success(true)
    }

  }

  private fun  connectionBluetooth(macAddress: String){
    if(conn == null || !conn!!.isConnected){
      try {
        conn = BluetoothLeConnection(macAddress,context)
        conn!!.open()
        Log.e(logTag, "BluetoothLeConnection OPEN")
      } catch (ex: ConnectionException) {
        conn = BluetoothConnection(macAddress)
        conn!!.open()
        Log.e(logTag, "BluetoothConnection OPEN")
      }
    }else{
      Log.e(logTag, "CONNECTED")
      if(conn is BluetoothLeConnection){
        if ((conn as BluetoothLeConnection).macAddress != macAddress){
          // The connection is different
          // Close the previous connection, to open a new connection with the current macaddress
          conn!!.close()
          Thread.sleep(500)
          conn = BluetoothLeConnection(macAddress,context)
          conn!!.open()
        }
      }else if (conn is BluetoothConnection){
        if ((conn as BluetoothConnection).macAddress != macAddress){
          // The connection is different
          // Close the previous connection, to open a new connection with the current macaddress
          conn!!.close()
          Thread.sleep(500)
          conn = BluetoothConnection(macAddress)
          conn!!.open()
        }
      }
    }
  }

  private fun onPrintZplDataOverBluetooth(@NonNull call: MethodCall, @NonNull result: Result) {
    var data: String? = call.argument("data")
    var macAddress: String? = call.argument("mac");
    Log.d(logTag, "onPrintZplDataOverBluetooth $data $macAddress")
    if (data == null) {
      result.error("onPrintZplDataOverBluetooth", "Data is required", "Data Content")
      return
    }
    try {
      connectionBluetooth(macAddress!!)
      sendDataToPrinter(data, result)
    } catch (e: Exception) {
      if(e is ConnectionException){
        try {
          connectionBluetooth(macAddress!!)
          sendDataToPrinter(data, result)
        }catch(er: Exception){
          er.printStackTrace()
          result.error("CONNECTION_FAILURE_TO_WRITE", macAddress, er)
        }
      }else{
        // Handle communications error here.
        e.printStackTrace()
        result.error("onPrintZplDataOverBluetooth", macAddress, e)
      }
    }
  }

  private fun sendDataToPrinter(zplContent: String, result: Result ){
    conn!!.write(zplContent?.toByteArray())
    Thread.sleep(500)
    result.success(true)
    /*Log.e(logTag, "CONNECTED SUCCESsFULLY")
    val printer = ZebraPrinterFactory.getInstance(conn!!)
    Log.e(logTag, "STATUS PRINTER: "+ printer.currentStatus.isReadyToPrint.toString())
    val pl: PrinterLanguage = printer.printerControlLanguage
    if (pl === PrinterLanguage.ZPL) {
      Log.e(logTag, "ZPL")
      val filePath: String? = createZplFile(context, "test.LBL", zplContent)
      Log.e(logTag, filePath.toString())
      printer.sendFileContents(filePath)
      Log.e(logTag, "ZPL ENVIADO")
      Thread.sleep(500)
      result.success(true)
    } else {
      result.error("onPrintZplDataOverBluetooth", "CONFIGURE ZPL ON YOUR PRINTER", "Data Content")
      return
    }*/
  }

  private fun createZplFile(context: Context, fileName: String?, zplContent: String): String? {
    var fos: FileOutputStream? = null
    var filePath: String? = null
    try {
      // Crear el archivo en el almacenamiento interno
      fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
      // Escribir el contenido ZPL en el archivo
      fos.write(zplContent.toByteArray())
      // Obtener la ruta del archivo
      val file = File(context.filesDir, fileName)
      filePath = file.absolutePath
    } catch (e: IOException) {
      e.printStackTrace()
    } finally {
      if (fos != null) {
        try {
          fos.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
    }
    return filePath
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity;
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }
}

  class MethodResultWrapper(methodResult: Result) : Result {

    private val methodResult: Result = methodResult
    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun success(result: Any?) {
      handler.post { methodResult.success(result) }
    }

    override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
      handler.post { methodResult.error(errorCode, errorMessage, errorDetails) }
    }

    override fun notImplemented() {
      handler.post { methodResult.notImplemented() }
    }
  }
