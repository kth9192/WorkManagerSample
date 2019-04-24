package com.example.bluetoothsample.native

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothsample.regacy.BleAdapter
import com.example.bluetoothsample.MyApp
import com.example.bluetoothsample.R
import com.example.bluetoothsample.native.BleUtil.REQUEST_ENABLE_BT
import com.example.bluetoothsample.native.BleUtil.gattCallback
import com.example.bluetoothsample.repository.ble.BleRoom
import com.example.bluetoothsample.viewmodel.NativeViewModel
import kotlinx.android.synthetic.main.activity_ble.recycler
import kotlinx.android.synthetic.main.activity_native.*


class NativeBleActivity : AppCompatActivity() {

    private var ble_scanner: BluetoothLeScanner? = null
    private val bluetoothAdapter = BleUtil.bluetoothAdapter
    private var bleService: BleService? = null
    private lateinit var bleViewModel: NativeViewModel
    private lateinit var bleAdapter: BleAdapter

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native)

        bleViewModel = ViewModelProviders.of(this).get(NativeViewModel::class.java)

        bleAdapter = BleAdapter()
        recycler.adapter = bleAdapter
        recycler.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        //통신 관련
        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        bleViewModel.getListLiveData()?.observe(this, Observer { bleRooms -> bleAdapter.submitList(bleRooms) })

        BleUtil.checkPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH))
        ble_scanner = bluetoothAdapter?.bluetoothLeScanner
        searchBloomEngine()

        connect.setOnClickListener {
            BleUtil.connect(this, MyApp.bleDevice, gattCallback )
        }

        read.setOnClickListener {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BleUtil.disConnect()
    }

    private fun searchBloomEngine() {
        BleUtil.scanLeDevice(true, ble_scanner,  leScanCallback)
    }

    private val leScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val roomModel = BleRoom(result.device.address, result.device?.name).let { bleViewModel.insert(it) }

            Log.d("DeviceScan", "onScanResult: ${result.device?.address} - ${result.device?.name}")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.d("DeviceScan", "onBatchScanResults:${results.toString()}")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("DeviceScan", "onScanFailed: $errorCode")
            val builder = AlertDialog.Builder(this@NativeBleActivity)

            builder.setTitle("스캔에러")
                    .setMessage("스캔 실패")
                    .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    })
            builder.create().show()
        }
    }
}