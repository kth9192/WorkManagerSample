package com.example.bluetoothsample.regacy

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.ederdoski.simpleble.interfaces.BleCallback
import com.ederdoski.simpleble.models.BluetoothLE
import com.ederdoski.simpleble.utils.BluetoothLEHelper
import com.example.bluetoothsample.repository.ble.BleRoom
import com.example.bluetoothsample.viewmodel.BleViewModel
import java.util.*

class BleKotlin(targetContext: Context) {
    private val TAG = "BleUtil"

    lateinit var ble: BluetoothLEHelper
    private val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1
    private lateinit var locationManager: LocationManager
    private var context: Context = targetContext

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        permissionCheck(context)
    }

    fun initBle() {

        ble = BluetoothLEHelper(context as Activity?)

        //스캔을 위한 위치확인서비스 설정
    }

    private fun permissionCheck(context: Context) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            AlertDialog.Builder(context)
                    .setMessage("gps 권한이 필요합니다. 설정하시겠습니까?")
                    .setPositiveButton("설정") { dialog, which ->
                        // 설정 창을 띄운다
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                    .setNegativeButton("취소", null).show()
        }
    }

    fun scan(bleCallback: BleCallback, viewModel: BleViewModel) {
        if (ble.isReadyForScan) {
            val handler = Handler()
            ble.scanLeDevice(true)

            handler.postDelayed({

                for (bluetoothLE in ble.listDevices) {
                    Log.d(TAG, "맥주소" + bluetoothLE.device.address)
                    //todo remove dummy address
//                    if (bluetoothLE.device.address == "58:7A:62:50:EA:96") { //dummy macAddress 원래코드는
//                        viewModel.insert(BleRoom(bluetoothLE.macAddress, bluetoothLE.device.name))
//                        ble.connect(bluetoothLE.device, bleCallback)
//                    }

                    if (bluetoothLE.device.name == "Bloomengine") { //dummy macAddress 원래코드는 bluetoothLE.
                        viewModel.insert(BleRoom(bluetoothLE.macAddress, bluetoothLE.device.name))
                        ble.connect(bluetoothLE.device, bleCallback)
                    }
                }
            }, 5000)
        }
    }

    fun choiceDevice(bluetoothLE: BluetoothLE, bleCallback: BleCallback) {
        ble.connect(bluetoothLE.device, bleCallback)
    }

    fun bleRead(CUSTOMSERVICE: String, CUSTOMCHARACTERISTIC: String) {
        ble.read(CUSTOMSERVICE, CUSTOMCHARACTERISTIC)
    }

    fun bleWrite(CUSTOMSERVICE: String, CUSTOMCHARACTERISTIC: String, byteArray: ByteArray) {
        ble.write(CUSTOMSERVICE, CUSTOMCHARACTERISTIC, byteArray)
    }

    fun stopScan() {
        ble.disconnect()
    }

    fun isReadyScan(): Boolean {
        return ble.isReadyForScan
    }

    fun isConnected(): Boolean {
        return ble.isConnected
    }
}