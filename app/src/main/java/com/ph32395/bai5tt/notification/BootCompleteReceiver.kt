package com.ph32395.bai5tt.notification


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootCompleteReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
//            Xu ly cong viec khi thiet bi khoi dong lai o day
            Toast.makeText(context, "Boot completed", Toast.LENGTH_SHORT).show()
        }
    }
}