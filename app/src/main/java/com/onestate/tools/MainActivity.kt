package com.onestate.tools

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private val bg = 0xFF101010.toInt()
    private val red = 0xFFE53935.toInt()
    private val white = 0xFFF5F5F5.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(bg)
        }
        val title = TextView(this).apply {
            text = "ONESTATE TOOLS"
            textSize = 26f
            setTextColor(red)
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "MINI EDITION  •  ANDROID 14–16\n\nFloating developer test panel"
            textSize = 14f
            setTextColor(white)
            gravity = Gravity.CENTER
            setPadding(0, 14, 0, 28)
        }
        val start = Button(this).apply {
            text = "ENABLE FLOATING PANEL"
            setTextColor(white)
            setBackgroundColor(red)
            setOnClickListener { enablePanel() }
        }
        val note = TextView(this).apply {
            text = "This is a UI prototype. ESP and aim controls are demo switches until connected to your own game."
            textSize = 12f
            setTextColor(0xFFBDBDBD.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        }
        root.addView(title, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        root.addView(subtitle)
        root.addView(start, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        root.addView(note)
        setContentView(root)
    }

    private fun enablePanel() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
            return
        }
        val intent = Intent(this, FloatingPanelService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
    }

    override fun onResume() {
        super.onResume()
        // User can tap the enable button again after granting overlay permission.
    }
}
