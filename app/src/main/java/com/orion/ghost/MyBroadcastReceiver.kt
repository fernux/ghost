package com.orion.ghost

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.util.Log
import android.widget.Toast

private const val TAG = "MyBroadcastReceiver"

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
       //Iniciar Activity

        if (Intent.ACTION_BOOT_COMPLETED == intent!!.action) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(i)
        }
        //segun iniciar servicio
        /*val serviceIntent = Intent()
        serviceIntent.setAction("Service")
        context.startService(serviceIntent)*/

        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
    }
}
