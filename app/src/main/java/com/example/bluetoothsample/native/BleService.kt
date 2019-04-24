package com.example.bluetoothsample.native

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.bluetoothsample.native.BleUtil.ACTION_GATT_CONNECTED
import com.example.bluetoothsample.native.BleUtil.ACTION_GATT_DISCONNECTED
import com.example.bluetoothsample.native.BleUtil.EXTRA_DATA
import com.example.bluetoothsample.native.BleUtil.STATE_CONNECTED
import com.example.bluetoothsample.native.BleUtil.STATE_DISCONNECTED
import com.example.bluetoothsample.native.BleUtil.gattCallback


class BleService : Service() {

    private val TAG = BleService::class.java.simpleName

    private lateinit var gatt: BluetoothGatt
    private val binder = MyBinder()
    private var mConnectionState: Int = STATE_CONNECTED

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    fun connect(mac: String): Boolean {

        if (BleUtil.bluetoothAdapter == null) {
            return false
        }

        BleUtil.connect(this, BleUtil.bluetoothAdapter!!.getRemoteDevice(mac), gattCallback)

        return true
    }

//    private fun broadcastUpdate(action: String) {
//        val intent = Intent(action)
//        sendBroadcast(intent)
//    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic?) {
        val intent = Intent(action)

        if (BleUtil.DEVICEMODE_READ_CHARACTERISTIC == characteristic?.uuid.toString()) {

            //TODO need custom

            val flag = characteristic?.properties
            var format = -1
            if ((flag?.and(0x01)) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16
                Log.d(TAG, "Heart rate format UINT16.")
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8
                Log.d(TAG, "Heart rate format UINT8.")
            }
            val heartRate = characteristic?.getIntValue(format, 1)!!
            Log.d(TAG, String.format("Received heart rate: %d", heartRate))
            intent.putExtra(EXTRA_DATA, heartRate.toString())
        } else {

            // For all other profiles, writes the data formatted in HEX.
            val data = characteristic?.value
            if (data != null && data.isNotEmpty()) {
                val stringBuilder = StringBuilder(data.size)
                for (byteChar in data)
                    stringBuilder.append(String.format("%02X ", byteChar))
                intent.putExtra(EXTRA_DATA, String(data) + "\n" + stringBuilder.toString())
            }
        }
        sendBroadcast(intent)
    }

    inner class MyBinder : Binder() {
        internal val service: BleService
            get() = this@BleService
    }
}
