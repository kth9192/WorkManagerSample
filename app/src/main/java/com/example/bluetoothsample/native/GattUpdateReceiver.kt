package com.example.bluetoothsample.native

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.bluetoothsample.native.BleUtil

//서비스에서 알림 받는 리시버
class GattUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BleUtil.ACTION_GATT_CONNECTED -> {

            }
            BleUtil.ACTION_GATT_DISCONNECTED -> {

            }
            BleUtil.ACTION_GATT_SERVICES_DISCOVERED -> {

            }
            BleUtil.ACTION_DATA_AVAILABLE -> {

            }
        }
    }
}