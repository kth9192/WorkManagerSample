package com.example.bluetoothsample

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.example.bluetoothsample.native.GattUpdateReceiver
import com.example.bluetoothsample.native.BleUtil


class MyApp: Application() {
    companion object {
        private var instance: MyApp? = null
        private val receiver = GattUpdateReceiver()
        lateinit var bleDevice:BluetoothDevice

        fun getGlobalApplicationContext(): MyApp {

            if (instance == null) {
                throw IllegalStateException("This Application does not inherit bluetoothsample")
            }

            return instance as MyApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        this.registerReceiver(receiver, BleUtil.makeGattUpdateIntentFilter())
    }

    fun context(): Context = applicationContext

    override fun onTerminate() {

        super.onTerminate()
        instance = null
        unregisterReceiver(receiver)
    }

    fun setDevice(device: BluetoothDevice){
        bleDevice = device
    }
}