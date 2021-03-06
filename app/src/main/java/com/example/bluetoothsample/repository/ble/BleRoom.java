package com.example.bluetoothsample.repository.ble;

import com.ederdoski.simpleble.models.BluetoothLE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BleRoom {

    @NonNull
    @PrimaryKey
    private String mac;
    private String name;

    public BleRoom(@NonNull String mac, String name) {
        this.mac = mac;
        this.name = name;
    }

    @NonNull
    public String getMac() {
        return mac;
    }

    public void setMac(@NonNull String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
