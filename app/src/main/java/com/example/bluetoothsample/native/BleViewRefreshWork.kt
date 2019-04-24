package com.example.bluetoothsample.native

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class BleViewRefreshWork(context :Context, workerParameters: WorkerParameters) : Worker(context, workerParameters){

    override fun doWork(): Result {

        return Result.success()
    }
}