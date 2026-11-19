package com.onestate.tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class FloatingPanelService : Service() {
    private lateinit var windowManager: WindowManager
    private var panel: View? = null
    private val red = 0xFFE53935.toInt()
    private val dark = 0xFF151515.toInt()
    private val white = 0xFFF5F5F5.toInt()

    override fun onCreate() {
        super.onCreate()
        startForeground(41, buildNotification())
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showPanel()
    }

    private fun buildNotification(): Notification {
        val channelId = "onestate_tools_panel"
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(channelId, "Floating panel", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, channelId) else Notification.Builder(this)
        return builder.setContentTitle("ONESTATE TOOLS")
            .setContentText("Floating developer panel is active")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()
    }

    private fun showPanel() {
        if (panel != null) return
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(14, 12, 14, 12)
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(dark)
                setStroke(2, red)
                cornerRadius = 22f
            }
        }
        val header = TextView(this).apply {
            text = "●  ONESTATE TOOLS   ×"
            textSize = 13f
            setTextColor(red)
            setPadding(2, 2, 2, 10)
            setOnClickListener { stopSelf() }
        }
        box.addView(header)
        addToggle(box, "ESP DEBUG", "Demo player overlay switch")
        addToggle(box, "AIM TEST", "Local aim-assist test switch")
        addToggle(box, "PLAYER BOXES", "Debug visualization switch")
        addToggle(box, "HEALTH / DISTANCE", "Debug data display switch")
        val footer = TextView(this).apply {
            text = "DRAG HEADER TO MOVE"
            textSize = 9f
            setTextColor(0xFF9E9E9E.toInt())
            setPadding(2, 10, 2, 0)
        }
        box.addView(footer)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 24
            y = 180
        }
        header.setOnTouchListener(object : View.OnTouchListener {
            var downX = 0f; var downY = 0f; var startX = 0; var startY = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { downX = event.rawX; downY = event.rawY; startX = params.x; startY = params.y; return true }
                    MotionEvent.ACTION_MOVE -> { params.x = startX + (event.rawX - downX).toInt(); params.y = startY + (event.rawY - downY).toInt(); windowManager.updateViewLayout(box, params); return true }
                }
                return false
            }
        })
        panel = box
        windowManager.addView(box, params)
    }

    private fun addToggle(parent: LinearLayout, label: String, hint: String) {
        var enabled = false
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(2, 7, 2, 7)
            setOnClickListener {
                enabled = !enabled
                (getChildAt(0) as TextView).text = "$label   ${if (enabled) "ON" else "OFF"}"
                (getChildAt(0) as TextView).setTextColor(if (enabled) red else white)
            }
        }
        val title = TextView(this).apply { text = "$label   OFF"; textSize = 12f; setTextColor(white) }
        val detail = TextView(this).apply { text = hint; textSize = 9f; setTextColor(0xFF9E9E9E.toInt()) }
        item.addView(title); item.addView(detail); parent.addView(item)
    }

    override fun onDestroy() {
        panel?.let { windowManager.removeView(it) }
        panel = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
