package com.example.bluetoothsample.viewmodel

import android.app.Application
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.bluetoothsample.native.BleUtil
import com.example.bluetoothsample.repository.ble.BleRepo
import com.example.bluetoothsample.repository.ble.BleRoom
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NativeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = BleRepo(application)
    private var listLiveData: LiveData<List<BleRoom>>? = null

    fun getListLiveData(): LiveData<List<BleRoom>>? {

        if (listLiveData == null) {
            listLiveData = repo.all
        }

        return listLiveData
    }


    fun insert(BleRoom: BleRoom) {
        repo.insert(BleRoom)
    }

    fun deleteAll() {
        repo.deleteAll()
    }

}