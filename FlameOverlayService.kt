package com.flameclient.android

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class FlameOverlayService : Service() {
    private lateinit var wm: WindowManager
    private var root: LinearLayout? = null
    private var expanded = false
    private lateinit var params: WindowManager.LayoutParams
    private val dark = Color.rgb(19, 13, 15)
    private val red = Color.rgb(255, 54, 40)
    private val orange = Color.rgb(255, 125, 35)

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { if (root == null) showBubble(); return START_NOT_STICKY }

    @Suppress("DEPRECATION")
    private fun showBubble() {
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            if (android.os.Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = dp(12); y = dp(170) }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(dp(7), dp(7), dp(7), dp(7))
            background = rounded(dark, Color.rgb(111, 37, 25))
        }
        val badge = TextView(this).apply {
            text = "🔥 FLAME"; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; setTextColor(Color.WHITE)
            gravity = Gravity.CENTER; setPadding(dp(12), dp(10), dp(12), dp(10)); background = rounded(red, red)
            setOnClickListener { expanded = !expanded; refreshMenu(box) }
            setOnTouchListener(DragTouchListener())
        }
        box.addView(badge); root = box; wm.addView(box, params)
    }

    private fun refreshMenu(box: LinearLayout) {
        while (box.childCount > 1) box.removeViewAt(1)
        if (!expanded) return
        box.addView(item("OPEN FLAME CLICKGUI  ↗", orange, true) {
            val i = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            toast("Đã mở bảng FLAME ClickGUI. Các module chưa tích hợp vẫn chỉ lưu trạng thái trong app.")
        })
        box.addView(item("CHUNK BORDERS", Color.WHITE) {
            val i = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            toast("Mở FLAME CLIENT → Resource Packs → Xuất / mở ChunkFinder. Sau đó kích hoạt pack trong Minecraft PE 1.26.52.")
        })
        box.addView(item("FPS SETUP", Color.WHITE) { toast("Gợi ý: giảm Render Distance, tắt hiệu ứng nặng và dùng pack nhẹ.") })
        box.addView(item("CROSSHAIR PREVIEW", Color.WHITE) { toast("Tùy chọn giao diện mẫu; chưa thay tâm ngắm trong game.") })
        box.addView(item("THU GỌN MENU", Color.WHITE) { expanded = false; refreshMenu(box) })
        box.addView(item("ĐÓNG NÚT NỔI", red) { stopSelf() })
    }

    private fun item(text: String, color: Int, bold: Boolean = false, click: () -> Unit) = TextView(this).apply {
        this.text = text; textSize = 12f; setTextColor(color); setPadding(dp(10), dp(11), dp(10), dp(11))
        if (bold) typeface = Typeface.DEFAULT_BOLD
        setOnClickListener { click() }
    }
    private fun rounded(fill: Int, stroke: Int) = android.graphics.drawable.GradientDrawable().apply { setColor(fill); cornerRadius = dp(11).toFloat(); setStroke(dp(1), stroke) }
    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    override fun onDestroy() { root?.let { try { wm.removeView(it) } catch (_: Exception) {} }; root = null; super.onDestroy() }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private inner class DragTouchListener : View.OnTouchListener {
        private var startX = 0; private var startY = 0; private var touchX = 0f; private var touchY = 0f; private var dragged = false
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { startX = params.x; startY = params.y; touchX = event.rawX; touchY = event.rawY; dragged = false; return false }
                MotionEvent.ACTION_MOVE -> { val dx = (event.rawX-touchX).toInt(); val dy = (event.rawY-touchY).toInt(); if (kotlin.math.abs(dx)+kotlin.math.abs(dy)>dp(4)) dragged = true; params.x=startX+dx; params.y=startY+dy; try { root?.let { wm.updateViewLayout(it, params) } } catch (_: Exception) {}; return true }
                MotionEvent.ACTION_UP -> { if (dragged) return true; return false }
            }
            return false
        }
    }
}
