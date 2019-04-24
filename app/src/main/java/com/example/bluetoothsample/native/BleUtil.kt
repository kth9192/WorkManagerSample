package com.example.bluetoothsample.native

import android.app.Activity
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import androidx.core.app.ActivityCompat
import java.util.*
import android.content.IntentFilter
import android.util.Log
import com.example.bluetoothsample.MyApp
import com.example.bluetoothsample.repository.ble.BleRoom
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Deferred
import kotlin.collections.ArrayList

object BleUtil {

    private var PERMISSION_ALL: Int = 100
    private lateinit var gatt: BluetoothGatt
    private var SCAN_PERIOD: Long = 10000
    const val REQUEST_ENABLE_BT = 1000

    //서비스 및 캐릭터 목록
    const val DEVICEINFO_SERVICE = "AA00"
    const val WATERINFO_SERVICE = "AA20"
    const val DEVICEWORK_SERVICE = "CCC0"

    const val DEVICEMODE_READ_CHARACTERISTIC = "AA01"
    const val WATERLEVEL_READ_CHARACTERISTIC = "AA21"
    const val AUTOMODESET_WRITE_CHARACTERISTIC ="CCC2"
    const val AUTOMODESET_READ_CHARACTERISTIC = "CCC3"
    const val MODESET_WRITE_CHARACTERISTIC = "CCC4"
    const val PUMPTIMER_WRITE_CHARACTERISTIC = "CCC5"

    const val LEDTIME_READ_CHARACTERISTIC = "CCC9"
    const val PUMPSET_READ_CHARACTERISTIC = "CCCA"
    const val SERIAL_READ_CHARACTERISTIC = "CCCD"
    const val DEVICENAME_READ_CHARACTERISTIC = "CCCE"

    //GATT 액션 인텐트
    const val ACTION_GATT_CONNECTED = "com.app.bloomengine.ACTION_GATT_CONNECTED"
    const val ACTION_GATT_DISCONNECTED = "com.app.bloomengine.ACTION_GATT_DISCONNECTED"
    const val ACTION_GATT_SERVICES_DISCOVERED = "com.app.bloomengine.ACTION_GATT_SERVICES_DISCOVERED"
    const val ACTION_DATA_AVAILABLE = "com.app.bloomengine.ACTION_DATA_AVAILABLE"
    const val EXTRA_DATA = "com.app.bloomengine.EXTRA_DATA"

    //연결상태
    const val STATE_DISCONNECTED = 0
    const val STATE_CONNECTING = 1
    const val STATE_CONNECTED = 2

    val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = MyApp.getGlobalApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    fun setScanPeriod(time: Long) {
        SCAN_PERIOD = time
    }

    fun checkPermissions(context: Context?, permissions: Array<String>) {

        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, permissions,
                            PERMISSION_ALL
                    )
                }
            }
        }
    }

    fun scanLeDevice(enable: Boolean, ble_scanner: BluetoothLeScanner?, leScanCallback: ScanCallback) {

        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                Handler().postDelayed({
                    ble_scanner?.stopScan(leScanCallback)
                }, SCAN_PERIOD)
                ble_scanner?.startScan(leScanCallback)
            }
            else -> {
                ble_scanner?.stopScan(leScanCallback)
            }
        }
    }

    fun connect(context: Context?, device: BluetoothDevice, callback: BluetoothGattCallback) {
        gatt = device.connectGatt(context, false, callback)
    }

    fun disConnect(){
        gatt.disconnect()
    }

    fun read(service: String, characteristic: String) {
        gatt.readCharacteristic(
                gatt.getService(UUID.fromString(service)).getCharacteristic(
                        UUID.fromString(characteristic)
                )
        )
    }

    fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_GATT_CONNECTED)
        intentFilter.addAction(ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(ACTION_DATA_AVAILABLE)
        return intentFilter
    }

    val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {


            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothProfile.STATE_CONNECTED) {
                for (service in gatt?.services!!) {
                    Logger.d(service.uuid.toString())
                    for (characteristic in service.characteristics) {
                        Logger.d(characteristic.uuid.toString())
                    }
                }
            }
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            if (status == BluetoothProfile.STATE_CONNECTED) {

            } else if (status == BluetoothProfile.STATE_DISCONNECTED) {

            }
        }

        override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
        }

        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)
        }
    }

}