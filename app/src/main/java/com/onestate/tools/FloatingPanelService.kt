package com.onestate.tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
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
    private var hideHandle: View? = null

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
            val channel = NotificationChannel(
                channelId,
                "Floating panel",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }

        return builder
            .setContentTitle("ONESTATE TOOLS")
            .setContentText("Floating developer panel is active")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()
    }

    private fun createParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= 26)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 24
            y = 180
        }
    }

    private fun hidePanel() {
        panel?.let { windowManager.removeView(it) }
        panel = null
        showHideHandle()
    }

    private fun showHideHandle() {
        if (hideHandle != null) return

        val handle = TextView(this).apply {
            text = "ON"
            textSize = 13f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(15, 12, 15, 12)
            background = GradientDrawable().apply {
                setColor(dark)
                setStroke(3, red)
                cornerRadius = 40f
            }

            setOnClickListener {
                hideHandle?.let { view ->
                    windowManager.removeView(view)
                }
                hideHandle = null
                showPanel()
            }
        }

        hideHandle = handle
        windowManager.addView(handle, createParams())
    }

    private fun showPanel() {
        if (panel != null) return

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(14, 12, 14, 12)
            background = GradientDrawable().apply {
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

        val params = createParams()

        header.setOnTouchListener(object : View.OnTouchListener {
            var downX = 0f
            var downY = 0f
            var startX = 0
            var startY = 0
            var moved = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.rawX
                        downY = event.rawY
                        startX = params.x
                        startY = params.y
                        moved = false
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = event.rawX - downX
                        val dy = event.rawY - downY

                        if (kotlin.math.abs(dx) > 8 ||
                            kotlin.math.abs(dy) > 8
                        ) {
                            moved = true
                        }

                        params.x = startX + dx.toInt()
                        params.y = startY + dy.toInt()

                        windowManager.updateViewLayout(box, params)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        if (!moved) {
                            hidePanel()
                        }
                        return true
                    }

                    MotionEvent.ACTION_CANCEL -> return true
                }
                return true
            }
        })

        windowManager.addView(box, params)
        panel = box
    }

    private fun addToggle(
        parent: LinearLayout,
        label: String,
        hint: String
    ) {
        var enabled = false

        val item = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(2, 7, 2, 7)

            setOnClickListener {
                enabled = !enabled
                val title = getChildAt(0) as TextView
                title.text = "$label   ${if (enabled) "ON" else "OFF"}"
                title.setTextColor(if (enabled) red else white)
            }
        }

        val title = TextView(this).apply {
            text = "$label   OFF"
            textSize = 12f
            setTextColor(white)
        }

        val detail = TextView(this).apply {
            text = hint
            textSize = 9f
            setTextColor(0xFF9E9E9E.toInt())
        }

        item.addView(title)
        item.addView(detail)
        parent.addView(item)
    }

    override fun onDestroy() {
        panel?.let { windowManager.removeView(it) }
        hideHandle?.let { windowManager.removeView(it) }

        panel = null
        hideHandle = null

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
