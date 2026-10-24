
package com.flameclient.android

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlin.math.abs

class FlameOverlayService : Service() {
    private lateinit var wm: WindowManager
    private var root: LinearLayout? = null
    private var expanded = false
    private lateinit var params: WindowManager.LayoutParams

    private val dark = Color.rgb(19, 13, 15)
    private val red = Color.rgb(255, 54, 40)
    private val orange = Color.rgb(255, 125, 35)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (root == null) showBubble()
        return START_NOT_STICKY
    }

    @Suppress("DEPRECATION")
    private fun showBubble() {
        wm = getSystemService(WINDOW_SERVICE) as WindowManager

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dp(12)
            y = dp(170)
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(7), dp(7), dp(7), dp(7))
            background = rounded(dark, Color.rgb(111, 37, 25))
        }

        val badge = TextView(this).apply {
            text = "🔥 FLAME"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = rounded(red, red)

            setOnClickListener {
                expanded = !expanded
                refreshMenu(box)
            }

            setOnTouchListener(DragTouchListener())
        }

        box.addView(badge)
        root = box

        try {
            wm.addView(box, params)
        } catch (e: Exception) {
            root = null
            toast("Không thể hiện nút nổi. Hãy kiểm tra quyền hiển thị trên ứng dụng khác.")
            stopSelf()
        }
    }

    private fun refreshMenu(box: LinearLayout) {
        while (box.childCount > 1) {
            box.removeViewAt(1)
        }

        if (!expanded) return

        box.addView(item("OPEN FLAME CLICKGUI ↗", orange, true) {
            openMainActivity()
        })

        box.addView(item("CHUNK BORDERS", Color.WHITE) {
            openMainActivity()
            toast("Vào Resource Packs để kiểm tra pack hỗ trợ Chunk Borders.")
        })

        box.addView(item("FPS SETUP", Color.WHITE) {
            toast("Giảm Render Distance và tắt hiệu ứng nặng để cải thiện FPS.")
        })

        box.addView(item("CROSSHAIR PREVIEW", Color.WHITE) {
            toast("Đây là tùy chọn xem trước; chưa thay đổi tâm ngắm trong Minecraft.")
        })

        box.addView(item("THU GỌN MENU", Color.WHITE) {
            expanded = false
            refreshMenu(box)
        })

        box.addView(item("ĐÓNG NÚT NỔI", red) {
            stopSelf()
        })
    }

    private fun openMainActivity() {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            toast("Không mở được FLAME CLIENT. Hãy kiểm tra MainActivity.")
        }
    }

    private fun item(
        text: String,
        color: Int,
        bold: Boolean = false,
        click: () -> Unit
    ) = TextView(this).apply {
        this.text = text
        textSize = 12f
        setTextColor(color)
        setPadding(dp(10), dp(11), dp(10), dp(11))
        isClickable = true
        isFocusable = false

        if (bold) typeface = Typeface.DEFAULT_BOLD

        setOnClickListener { click() }
    }

    private fun rounded(fill: Int, stroke: Int) =
        GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(11).toFloat()
            setStroke(dp(1), stroke)
        }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        root?.let {
            try {
                wm.removeView(it)
            } catch (_: Exception) {
            }
        }

        root = null
        super.onDestroy()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private inner class DragTouchListener : View.OnTouchListener {
        private var startX = 0
        private var startY = 0
        private var touchX = 0f
        private var touchY = 0f
        private var dragged = false

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x
                    startY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    dragged = false
                    return false
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).toInt()
                    val dy = (event.rawY - touchY).toInt()

                    if (abs(dx) + abs(dy) > dp(6)) {
                        dragged = true
                    }

                    if (dragged) {
                        params.x = (startX + dx).coerceAtLeast(0)
                        params.y = (startY + dy).coerceAtLeast(0)

                        try {
                            root?.let { wm.updateViewLayout(it, params) }
                        } catch (_: Exception) {
                        }

                        return true
                    }

                    return false
                }

                MotionEvent.ACTION_UP -> {
                    return dragged
                }

                MotionEvent.ACTION_CANCEL -> {
                    return dragged
                }
            }

            return false
        }
    }
}
