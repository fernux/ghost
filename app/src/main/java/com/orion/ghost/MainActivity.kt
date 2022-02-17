package com.orion.ghost

import android.app.Application
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()

        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(this)
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {

            //rocketAnimation.start()
            // Launch service right away - the user has already previously granted permission
            launchMainService()
        } else {

            // Check that the user has granted permission, and prompt them if not
            checkDrawOverlayPermission()
        }
    }

    private fun launchMainService() {


        val svc = Intent(this, MainService::class.java)

        stopService(svc)
        startService(svc)

        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkDrawOverlayPermission() {

        // Checks if app already has permission to draw overlays
        if (!Settings.canDrawOverlays(this)) {

            // If not, form up an Intent to launch the permission request
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))


            // Launch Intent, with the supplied request code
            startActivityForResult(intent, REQUEST_CODE)

            //val i = Intent(ACTION_BOOT_COMPLETED, Uri.parse("package:$packageName"))
           //startActivityForResult(i, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if a request code is received that matches that which we provided for the overlay draw request
        if (requestCode == REQUEST_CODE) {

            // Double-check that the user granted it, and didn't just dismiss the request
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Settings.canDrawOverlays(this)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            ) {

                // Launch the service
                launchMainService()
            } else {

                Toast.makeText(this, "Sorry. Can't draw overlays without permission...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        lateinit var rocketAnimation: AnimationDrawable
        val rocketImage = findViewById<ImageView>(R.id.ivBatman).apply {
            setBackgroundResource(R.drawable.walk_item)
            rocketAnimation = background as AnimationDrawable
        }
        rocketImage.setOnClickListener({ rocketAnimation.start() })

    }


    companion object {
        private const val REQUEST_CODE = 10101
        private const val REQUEST_CODEII = 10100
    }
}
