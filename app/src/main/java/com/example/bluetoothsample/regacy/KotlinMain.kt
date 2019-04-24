package com.example.bluetoothsample.regacy

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothsample.model.DateModel
import com.example.bluetoothsample.model.TimeModel
import com.ederdoski.simpleble.interfaces.BleCallback
import com.example.bluetoothsample.R
import com.example.bluetoothsample.viewmodel.BleViewModel
import kotlinx.android.synthetic.main.activity_ble.*
import kotlinx.android.synthetic.main.activity_ble.recycler
import java.util.*

class KotlinMain : AppCompatActivity() {

    private val TAG = "블루투스"
    private lateinit var ble: BleKotlin
    private lateinit var bleViewModel: BleViewModel
    private lateinit var bleAdapter: BleAdapter
    private val BLOOMENGINE_SERVICE = "F000CCC0-0451-4000-B000-000000000000"
    private val SETAUTOTIME_CHARACTERISTIC = "F000CCC2-0451-4000-B000-000000000000"
    private val SETPUMPTIME_CHARACTERISTIC = "F000CCC5-0451-4000-B000-000000000000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble)
        bleViewModel = ViewModelProviders.of(this).get(BleViewModel::class.java)

        ble = BleKotlin(this)
        ble.initBle()

        bleAdapter = BleAdapter()
        recycler.adapter = bleAdapter
        recycler.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        bleViewModel.listLiveData.observe(this, Observer { bleRooms -> bleAdapter.submitList(bleRooms) })

        search_start.setOnClickListener {
            if (ble.isReadyScan()) {
                ble.scan(bleCallback, bleViewModel)
            }
        }
        search_stop.setOnClickListener { ble.stopScan() }

        ble_read.setOnClickListener {
            if (ble.isConnected()) {
                ble.bleRead(BLOOMENGINE_SERVICE, SETAUTOTIME_CHARACTERISTIC)
            }
        }

        ble_pump.setOnClickListener {
            ble.bleWrite(BLOOMENGINE_SERVICE, SETPUMPTIME_CHARACTERISTIC, setPumpTime())
        }

        ble_write.setOnClickListener {
            val calendar = Calendar.getInstance()

            ble.bleWrite(BLOOMENGINE_SERVICE, SETAUTOTIME_CHARACTERISTIC,
                    setAutoTime(TimeModel(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND)),
                            TimeModel(14, 50, calendar.get(Calendar.SECOND)),
                            TimeModel(14, 55, calendar.get(Calendar.SECOND)),
                            TimeModel(14, 50, calendar.get(Calendar.SECOND)),
                            DateModel(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                    ))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bleViewModel.deleteAll()
    }

    private val bleCallback = object : BleCallback() {

        override fun onBleConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onBleConnectionStateChange(gatt, status, newState)

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread { Toast.makeText(this@KotlinMain, "Connected to GATT server.", Toast.LENGTH_SHORT).show() }
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                runOnUiThread { Toast.makeText(this@KotlinMain, "Disconnected from GATT server.", Toast.LENGTH_SHORT).show() }
            }
        }

        override fun onBleServiceDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onBleServiceDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG + "discovered", "onServicesDiscovered received: $status")
                for (bluetoothGattService in gatt!!.services) {

                    Log.d(TAG + "discovered", "서비스 확인 : " + bluetoothGattService.uuid.toString())

                    for (characteristic in bluetoothGattService.characteristics) {
                        Log.d(TAG + "discovered", "캐릭터 확인 : " + characteristic.uuid)
                    }
                }
            }
        }

        override fun onBleCharacteristicChange(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onBleCharacteristicChange(gatt, characteristic)
            Log.i(TAG, "onCharacteristicChanged Value: " + Arrays.toString(characteristic!!.value))
        }

        override fun onBleWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onBleWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "캐릭터 송신")
//                Log.i(TAG, Arrays.toString(characteristic?.value))
//                characteristic.value = setAutoTime()
//                gatt?.writeCharacteristic(characteristic)
            }
        }

        override fun onBleRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onBleRead(gatt, characteristic, status)
            Log.i(TAG, "읽는 중 " + characteristic?.uuid.toString())

            runOnUiThread { Toast.makeText(this@KotlinMain, "onCharacteristicWrite Status : $status", Toast.LENGTH_SHORT).show() }
        }
    }

    //TODO 명령어에 필요한 바이트 배열 길이에 대해 동적으로 변수&배열 생성 및 byte배열 출력하는 함수를 기반으로 리팩토링.

    private fun setAutoTime(): ByteArray {

        val calendar = Calendar.getInstance()
        val hex = Integer.toHexString(calendar.get(Calendar.HOUR_OF_DAY))
        val hex1 = Integer.toHexString(calendar.get(Calendar.MINUTE))
        val hex2 = Integer.toHexString(calendar.get(Calendar.SECOND))
        val hex3 = Integer.toHexString(13)
        val hex4 = Integer.toHexString(calendar.get(Calendar.MINUTE))
        val hex5 = Integer.toHexString(13)
        val hex6 = Integer.toHexString(calendar.get(Calendar.MINUTE) + 5)
        val hex7 = Integer.toHexString(13)
        val hex8 = Integer.toHexString(calendar.get(Calendar.MINUTE))
        val hex11 = Integer.toHexString(7)
        val hex12 = Integer.toHexString(calendar.get(Calendar.MONTH) + 1)
        val hex13 = Integer.toHexString(calendar.get(Calendar.DAY_OF_MONTH))
        val reserve = Integer.toHexString(0)

        Log.d(TAG, "문자열 $hex $hex1 $hex2 $hex3 $hex4 $hex5 $hex6 $hex7 $hex8 $hex11 $hex12 $hex13 $reserve")

        val b0 = java.lang.Byte.parseByte(hex, 16)
        val b1 = java.lang.Byte.parseByte(hex1, 16)
        val b2 = java.lang.Byte.parseByte(hex2, 16)
        val b3 = java.lang.Byte.parseByte(hex3, 16)
        val b4 = java.lang.Byte.parseByte(hex4, 16)
        val b5 = java.lang.Byte.parseByte(hex5, 16)
        val b6 = java.lang.Byte.parseByte(hex6, 16)
        val b7 = java.lang.Byte.parseByte(hex7, 16)
        val b8 = java.lang.Byte.parseByte(hex8, 16)
        val b10 = 0xE3.toByte()
        val b11 = java.lang.Byte.parseByte(hex11, 16)
        val b12 = java.lang.Byte.parseByte(hex12, 16)
        val b13 = java.lang.Byte.parseByte(hex13, 16)
        val br = java.lang.Byte.parseByte(reserve, 16)

        val bytes = byteArrayOf(b0, b1, b2, b3, b4, b5, b6, b7, b8, br, b10, b11, b12, b13, br, br, br, br, br, br)

        return bytes
    }

    private fun setPumpTime(): ByteArray {

        val calendar = Calendar.getInstance()

        val hex = Integer.toHexString(calendar.get(Calendar.HOUR_OF_DAY))
        val hex1 = Integer.toHexString(calendar.get(Calendar.MINUTE) + 6)

        val b0 = java.lang.Byte.parseByte(hex, 16)
        val b1 = java.lang.Byte.parseByte(hex1, 16)

        var default = 0x00.toByte()

        val bytes = byteArrayOf(b0, b1, default, default, default, default, default, default, default, default,
                default, default, default, default, default, default, default, default, default, default)

        return bytes
    }

    /**CCC2 오토 세팅 명령어. Byte20자리
     * */
    fun setAutoTime(currentTimeModel: TimeModel, ledOnModel: TimeModel, ledOffModel: TimeModel, pumpOnModel: TimeModel, dayModel: DateModel): ByteArray {

        val sendBytes: ArrayList<Int> = ArrayList()
        sendBytes.add(currentTimeModel.hour)
        sendBytes.add(currentTimeModel.minute)
        sendBytes.add(currentTimeModel.second)
        sendBytes.add(ledOnModel.hour)
        sendBytes.add(ledOnModel.minute)
        sendBytes.add(ledOffModel.hour)
        sendBytes.add(ledOffModel.minute)
        sendBytes.add(pumpOnModel.hour)
        sendBytes.add(pumpOnModel.minute)
        sendBytes.add(0)// reserved
        sendBytes.add(0)// 년도의 뒷자리
        sendBytes.add(0)// 년도의 앞자리
        sendBytes.add(dayModel.month + 1)
        sendBytes.add(dayModel.day)
        sendBytes.add(0)//reserved
        sendBytes.add(0)//reserved
        sendBytes.add(0)//reserved
        sendBytes.add(0)//reserved
        sendBytes.add(0)//reserved
        sendBytes.add(0)//reserved

        Log.d(TAG, "사이즈 ${sendBytes.size}")
        val result = byteConverterForAuto(
                toIntegerHex(sendBytes), dayModel.year
        )

        for (bytes in result) {
            Log.d(TAG, "결과 바이트 $bytes")
        }

        return result
    }

    /**16진수 컨버터
     * */
    fun toIntegerHex(resource: ArrayList<Int>): ArrayList<String> {

        val result: ArrayList<String> = ArrayList()

        for (source in resource) {
            result.add(Integer.toHexString(source))
            Log.d(TAG, "정수에서 HEX ${Integer.toHexString(source)}")
        }

        return result
    }

    /**바이트 컨버터
     * */
    fun strToByteConverter(resource: ArrayList<String>): ByteArray {

        val result: ArrayList<Byte> = ArrayList()

        for (source in resource) {
            result.add(java.lang.Byte.parseByte(source, 16))
            Log.d(TAG, "HEX 에서 BYTE ${java.lang.Byte.parseByte(source, 16)}")
        }

        return result.toByteArray()
    }

    /**오토 세팅 전용 바이트 컨버터
     * 년도 바이트 코드를 반으로 나누어 보내야하기 때문에 연도관련 로직이 추가
     * */
    fun byteConverterForAuto(resource: ArrayList<String>, year: Int): ByteArray {

        val result = ByteArray(resource.size)

        val tmp = Integer.toHexString(year)

        val yearFront = tmp.substring(0, tmp.length / 2)
        val yearEnd = tmp.substring(tmp.length / 2, tmp.length)

        resource.forEachIndexed { index, source ->
            when (index) {
                10 -> result[10] = yearEnd.toInt(16).toByte()
                11 -> result[11] = yearFront.toInt(16).toByte()
                else -> result[index] = source.toByte(16)
            }
        }

        return result
    }
}