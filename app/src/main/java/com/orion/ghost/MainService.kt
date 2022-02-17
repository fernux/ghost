package com.orion.ghost

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.AnimationDrawable
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.ViewCompat
import java.util.*

class MainService : Service(), View.OnTouchListener {

    private lateinit var windowManager: WindowManager
    private var floatyView: View? = null
    lateinit var  params: WindowManager.LayoutParams

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager


        addOverlayView()
    }

    private fun addOverlayView() {


        val layoutParamsType: Int = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutParamsType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //params.gravity = Gravity.CENTER or Gravity.START
        params.x = 0
        params.y = 0

        val interceptorLayout = object : FrameLayout(this) {

            override fun dispatchKeyEvent(event: KeyEvent): Boolean {

                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                if (event.action == KeyEvent.ACTION_DOWN) {

                    // Check if the HOME button is pressed
                    if (event.keyCode == KeyEvent.KEYCODE_BACK) {

                        Log.v(TAG, "BACK Button Pressed")

                        // As we've taken action, we'll return true to prevent other apps from consuming the event as well
                        return true
                    }
                }

                // Otherwise don't intercept the event
                return super.dispatchKeyEvent(event)
            }
        }

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        floatyView = inflater.inflate(R.layout.activity_main, interceptorLayout)




        floatyView?.let {
            it.setOnTouchListener(this)
            windowManager.addView(floatyView, params)
        } ?: run {
            Log.e(TAG, "Layout Inflater Service is null; can't inflate and display R.layout.floating_view")
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        floatyView?.let {
            windowManager.removeView(it)
            floatyView = null
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {


        var startClickTime:Long = 0
        var initialX:Int =0
        var initialY:Int =0
        var initialTouchX:Float =0f
        var initialTouchY:Float =0f
        var lastAction:Int =0

        view.performClick()
        when (view) {
            floatyView -> {
                Log.d("next", "yeyy")
                when (motionEvent.action){
                    MotionEvent.ACTION_DOWN -> {
                        startClickTime = Calendar.getInstance().getTimeInMillis()
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = motionEvent.rawX
                        initialTouchY = motionEvent.rawY
                        lastAction = motionEvent.action
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.gravity = Gravity.TOP or Gravity.START
                        params.x= (initialX +  (motionEvent.rawX - initialTouchX)).toInt()
                        params.y= (initialY +  (motionEvent.rawY - initialTouchY)).toInt()
                        windowManager.updateViewLayout(floatyView, params)


                    }
                }
            }

        }
        return false


        Log.v(TAG, "onTouch...")

        // Kill service
        //onDestroy()

        //return true
    }
    


    companion object {
        private val TAG = MainService::class.java.simpleName
    }
}
