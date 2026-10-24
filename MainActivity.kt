package com.flameclient.android

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray

/**
 * FLAME CLIENT mobile ClickGUI.
 * The screen uses a Flarial-inspired client layout pattern while keeping original FLAME styling.
 * Module switches in this companion app only persist local state unless a module is explicitly
 * integrated with a compatible in-game resource pack/runtime.
 */
class MainActivity : Activity() {
    private val bg = Color.rgb(10, 10, 13)
    private val panel = Color.rgb(23, 20, 23)
    private val panelRaised = Color.rgb(31, 25, 27)
    private val border = Color.rgb(74, 39, 35)
    private val red = Color.rgb(255, 59, 45)
    private val orange = Color.rgb(255, 129, 43)
    private val white = Color.rgb(247, 243, 242)
    private val muted = Color.rgb(166, 157, 159)
    private val greenOn = Color.rgb(255, 103, 43)
    private val createPackFile = 7201

    private var pendingPack = ""
    private var pendingMime = "application/x-mcpack"
    private var selectedCategory = "PVP"
    private var selectedSection = "MODULES"
    private var searchText = ""
    private lateinit var moduleList: LinearLayout
    private var activeCountBadge: TextView? = null

    private val categories = listOf("PVP", "SINH TỒN", "HUD", "VISUAL")
    private val modules = listOf(
        Module("Zoom", "Mở rộng tầm nhìn", "PVP", "ZO"),
        Module("Freelook", "Xoay góc nhìn độc lập", "PVP", "FL"),
        Module("Small Hand", "Tùy chọn tay nhỏ qua pack phù hợp", "PVP", "SH"),
        Module("FPS Booster", "Mẹo tối ưu và pack nhẹ", "PVP", "FB"),
        Module("FPS Counter", "Bộ đếm FPS — cần tích hợp runtime", "PVP", "FP"),
        Module("Ping Counter", "Ping máy chủ — cần tích hợp runtime", "PVP", "PI"),
        Module("CPS Counter", "Bộ đếm CPS — cần nguồn sự kiện trong game", "PVP", "CP"),
        Module("Motion Blur", "Hiệu ứng hình ảnh — chưa áp trực tiếp vào game", "PVP", "MB"),
        Module("Crosshair Customizer", "Tùy chỉnh tâm ngắm qua giao diện/pack phù hợp", "PVP", "CH"),
        Module("Totem Counter", "Đếm totem — cần đọc dữ liệu trong game", "PVP", "TC"),
        Module("Armor Durability", "Độ bền giáp — cần đọc dữ liệu trong game", "PVP", "AD"),
        Module("Auto Sprint HUD", "Bảng điều khiển mẫu; không tự chạy nhân vật", "PVP", "AS"),
        Module("Potion Status", "Trạng thái thuốc — cần đọc hiệu ứng trong game", "SINH TỒN", "PS"),
        Module("Minimap", "Bản đồ thời gian thực chưa được tích hợp", "SINH TỒN", "MM"),
        Module("Chunk Finder", "Xuất pack Chunk Finder trong Pack Manager", "SINH TỒN", "CF"),
        Module("Time Changer", "Thay đổi thời gian thế giới chưa tích hợp", "SINH TỒN", "TM"),
        Module("Backups Manager", "Hỗ trợ quản lý bản sao lưu thủ công", "SINH TỒN", "BK"),
        Module("Coordinates HUD", "Tọa độ trực tiếp cần tích hợp runtime", "HUD", "XY"),
        Module("Keystrokes", "Hiển thị phím cần nguồn sự kiện trong game", "HUD", "KS"),
        Module("Direction HUD", "Hướng nhìn trực tiếp chưa được tích hợp", "HUD", "DH"),
        Module("Clock HUD", "Đồng hồ của ứng dụng", "HUD", "CL"),
        Module("Crosshair Style", "Xem trước phong cách tâm ngắm", "VISUAL", "CS"),
        Module("Low Fire", "Xuất pack Low Fire qua Pack Manager", "VISUAL", "LF"),
        Module("Clear Glass", "Xuất pack Clear Glass qua Pack Manager", "VISUAL", "CG"),
        Module("Small Items", "Xuất pack Small Items qua Pack Manager", "VISUAL", "SI")
    )

    private data class Module(val name: String, val detail: String, val category: String, val icon: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        if (Build.VERSION.SDK_INT >= 23) window.decorView.systemUiVisibility = 0
        render()
    }

    private fun render() {
        val shell = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(13), dp(15), dp(10))
            setBackgroundColor(bg)
        }

        val brandRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val brandStack = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        brandStack.addView(text("🔥 FLAME CLIENT", 24f, orange, true))
        brandStack.addView(text("BEDROCK / PE 1.26.52  •  MOBILE", 10f, muted, true).apply {
            setPadding(dp(1), dp(3), 0, 0)
        })
        brandRow.addView(brandStack, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        brandRow.addView(pill("FLOATING GUI", panelRaised, orange, 10f) { requestOverlay() })
        shell.addView(brandRow, margin(bottom = 13))

        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            background = rounded(panel, border, 13)
        }
        val sections = listOf("MODULES", "PACKS", "SETTINGS")
        sections.forEach { section ->
            nav.addView(pill(section, if (selectedSection == section) red else panel,
                if (selectedSection == section) white else muted, 11f, true) {
                selectedSection = section
                render()
            }, LinearLayout.LayoutParams(0, dp(40), 1f).apply { marginEnd = dp(3); marginStart = dp(3) })
        }
        shell.addView(nav, margin(bottom = 10))

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            setBackgroundColor(bg)
        }
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(2), 0, dp(12))
        }
        when (selectedSection) {
            "MODULES" -> renderClickGui(body)
            "PACKS" -> renderPacks(body)
            else -> renderSettings(body)
        }
        scroll.addView(body)
        shell.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        shell.addView(text("FLAME CLIENT  •  FIRE UI  •  ANDROID", 9f, muted, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(9), 0, dp(2))
        })
        setContentView(shell)
    }

    private fun renderClickGui(body: LinearLayout) {
        val summary = card()
        val summaryRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val summaryText = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        summaryText.addView(text("MODULE CENTER", 15f, white, true))
        summaryText.addView(text("Chọn nhóm • tìm module • bật/tắt", 11f, muted).apply { setPadding(0, dp(4), 0, 0) })
        summaryRow.addView(summaryText, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        val enabledCount = modules.count { preferences().getBoolean(prefKey(it), false) }
        val activeBadge = pill("$enabledCount ACTIVE", Color.rgb(61, 30, 24), orange, 10f) {}
        activeCountBadge = activeBadge
        summaryRow.addView(activeBadge)
        summary.addView(summaryRow)
        body.addView(summary, margin(bottom = 10))

        val search = EditText(this).apply {
            setSingleLine(true)
            textSize = 13f
            setTextColor(white)
            setHintTextColor(muted)
            hint = "⌕  Tìm module..."
            setPadding(dp(13), dp(10), dp(13), dp(10))
            background = rounded(panelRaised, border, 12)
            setText(searchText)
            setSelection(text.length)
        }
        body.addView(search, margin(bottom = 10))

        val categoriesScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            clipToPadding = false
        }
        val categoryRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        categories.forEach { category ->
            val count = modules.count { it.category == category }
            val label = when (category) {
                "PVP" -> "⚔  PVP  $count"
                "SINH TỒN" -> "◇  SINH TỒN  $count"
                "HUD" -> "◉  HUD  $count"
                else -> "✦  VISUAL  $count"
            }
            categoryRow.addView(pill(label,
                if (selectedCategory == category) red else panel,
                if (selectedCategory == category) white else muted,
                10f, true) {
                selectedCategory = category
                render()
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(39)).apply { marginEnd = dp(7) })
        }
        categoriesScroll.addView(categoryRow)
        body.addView(categoriesScroll, margin(bottom = 12))

        val categoryDescription = when (selectedCategory) {
            "PVP" -> "COMBAT MODULES"
            "SINH TỒN" -> "SURVIVAL MODULES"
            "HUD" -> "HUD & INFORMATION"
            else -> "VISUAL MODULES"
        }
        val sectionHead = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        sectionHead.addView(text(categoryDescription, 11f, orange, true), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        sectionHead.addView(text("${modules.count { it.category == selectedCategory }} MODULES", 9f, muted, true))
        body.addView(sectionHead, margin(bottom = 7))

        moduleList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        body.addView(moduleList)
        populateModuleList()
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString().orEmpty()
                populateModuleList()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        val note = card()
        note.addView(text("⚠  MODULE STATUS", 11f, orange, true))
        note.addView(text("Các công tắc hiện lưu trạng thái trong FLAME CLIENT. Chúng không tự bật tính năng bên trong Minecraft nếu chưa có phần tích hợp tương ứng. Những pack có sẵn có thể xuất ở mục PACKS.", 11f, muted).apply { setPadding(0, dp(7), 0, 0) })
        body.addView(note, margin(top = 10))
    }

    private fun populateModuleList() {
        if (!::moduleList.isInitialized) return
        val enabledTotal = modules.count { preferences().getBoolean(prefKey(it), false) }
        activeCountBadge?.text = "$enabledTotal ACTIVE"
        moduleList.removeAllViews()
        val query = searchText.trim().lowercase()
        val filtered = modules.filter {
            it.category == selectedCategory && (query.isEmpty() || it.name.lowercase().contains(query) || it.detail.lowercase().contains(query))
        }
        if (filtered.isEmpty()) {
            val empty = card()
            empty.addView(text("Không tìm thấy module", 14f, white, true))
            empty.addView(text("Thử tên khác hoặc chọn nhóm khác.", 11f, muted).apply { setPadding(0, dp(5), 0, 0) })
            moduleList.addView(empty)
            return
        }
        filtered.forEach { module ->
            val enabled = preferences().getBoolean(prefKey(module), false)
            val item = card()
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            val icon = TextView(this).apply {
                text = module.icon
                textSize = 10f
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setTextColor(if (enabled) white else orange)
                background = rounded(if (enabled) Color.rgb(105, 42, 28) else Color.rgb(43, 28, 27), border, 10)
                layoutParams = LinearLayout.LayoutParams(dp(42), dp(42)).apply { marginEnd = dp(10) }
            }
            row.addView(icon)

            val labels = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_VERTICAL }
            labels.addView(text(module.name, 13f, white, true))
            labels.addView(text(module.detail, 10f, muted).apply { setPadding(0, dp(4), 0, 0) })
            row.addView(labels, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

            val settingsButton = pill("⚙", panelRaised, orange, 15f, true) {
                showModuleInfo(module)
            }
            row.addView(settingsButton, LinearLayout.LayoutParams(dp(34), dp(36)).apply { marginStart = dp(5) })

            val toggle = pill(if (enabled) "ON" else "OFF",
                if (enabled) greenOn else Color.rgb(54, 48, 50),
                if (enabled) Color.WHITE else muted, 11f, true) {
                val next = !preferences().getBoolean(prefKey(module), false)
                preferences().edit().putBoolean(prefKey(module), next).apply()
                populateModuleList()
            }
            row.addView(toggle, LinearLayout.LayoutParams(dp(52), dp(36)).apply { marginStart = dp(6) })
            item.addView(row)
            item.setOnClickListener { showModuleInfo(module) }
            // Keep the switch click independent of the card's information dialog.
            toggle.setOnClickListener {
                val next = !preferences().getBoolean(prefKey(module), false)
                preferences().edit().putBoolean(prefKey(module), next).apply()
                populateModuleList()
            }
            moduleList.addView(item, margin(bottom = 7))
        }
    }

    private fun showModuleInfo(module: Module) {
        val enabled = preferences().getBoolean(prefKey(module), false)
        val state = if (enabled) "ON" else "OFF"
        AlertDialog.Builder(this)
            .setTitle(module.name)
            .setMessage("Nhóm: ${module.category}\nTrạng thái: $state\n\n${module.detail}\n\nLưu ý: công tắc trong app chỉ lưu trạng thái. Chức năng cần tích hợp Minecraft hoặc resource pack tương thích mới tác động trong game.")
            .setPositiveButton("ĐÓNG", null)
            .show()
    }

    private fun renderPacks(body: LinearLayout) {
        body.addView(text("RESOURCE PACKS", 18f, white, true))
        body.addView(text("Xuất pack rồi mở bằng Minecraft. Sau khi nhập, bật pack trong Global Resources hoặc cài đặt thế giới.", 11f, muted).apply {
            setPadding(0, dp(5), 0, dp(12))
        })
        try {
            val catalog = JSONArray(assets.open("packs/catalog.json").bufferedReader().use { it.readText() })
            for (i in 0 until catalog.length()) {
                val item = catalog.getJSONObject(i)
                val file = item.getString("file")
                val p = card()
                p.addView(text(item.getString("title"), 14f, white, true))
                p.addView(text(item.getString("description"), 11f, muted).apply { setPadding(0, dp(5), 0, dp(10)) })
                p.addView(pill("XUẤT / MỞ PACK  ↗", red, white, 11f, true) {
                    exportPack(file, item.optString("mime", "application/x-mcpack"))
                })
                body.addView(p, margin(bottom = 9))
            }
        } catch (e: Exception) {
            val p = card()
            p.addView(text("Không đọc được danh sách pack", 13f, white, true))
            p.addView(text(e.message ?: "catalog.json không đọc được.", 11f, muted).apply { setPadding(0, dp(5), 0, 0) })
            body.addView(p)
        }
    }

    private fun renderSettings(body: LinearLayout) {
        body.addView(text("CLIENT SETTINGS", 18f, white, true))
        body.addView(text("Tùy chọn của ứng dụng FLAME CLIENT.", 11f, muted).apply {
            setPadding(0, dp(5), 0, dp(12))
        })
        val appSettings = card()
        appSettings.addView(text("APP & CLICKGUI", 11f, orange, true))
        appSettings.addView(text("• Giao diện module có tìm kiếm, phân loại và nút cấu hình.\n• Trạng thái ON/OFF được lưu trong ứng dụng.\n• Resource pack nằm ở trang PACKS.", 12f, white).apply { setPadding(0, dp(8), 0, dp(10)) })
        appSettings.addView(pill("MỞ FLOATING GUI  ↗", red, white, 11f, true) { requestOverlay() })
        body.addView(appSettings, margin(bottom = 9))
        val limits = card()
        limits.addView(text("!  CHƯA TÍCH HỢP TRỰC TIẾP", 11f, orange, true))
        limits.addView(text("Freelook điều khiển camera, FPS/Ping/CPS thật, Motion Blur, Minimap thời gian thực và các module đọc dữ liệu trong Minecraft cần runtime tương thích. Ứng dụng Android độc lập không thể tự điều khiển bộ nhớ hay giao diện Minecraft chỉ bằng công tắc.", 11f, muted).apply { setPadding(0, dp(8), 0, 0) })
        body.addView(limits, margin(bottom = 9))
        val target = card()
        target.addView(text("BUILD TARGET", 11f, orange, true))
        target.addView(text("Minecraft Bedrock / PE 1.26.52\nAndroid • Java 17 • Gradle 8.9\nTên gói: com.flameclient.android", 11f, muted).apply { setPadding(0, dp(7), 0, 0) })
        body.addView(target)
    }

    private fun exportPack(assetName: String, mime: String) {
        pendingPack = assetName
        pendingMime = mime
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mime
            putExtra(Intent.EXTRA_TITLE, assetName)
        }
        startActivityForResult(intent, createPackFile)
    }

    @Deprecated("Using startActivityForResult for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != createPackFile || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            assets.open("packs/$pendingPack").use { input ->
                contentResolver.openOutputStream(uri, "w").use { output ->
                    requireNotNull(output) { "Không mở được file đích" }
                    input.copyTo(output)
                }
            }
            Toast.makeText(this, "Đã lưu $pendingPack. Đang mở bằng Minecraft…", Toast.LENGTH_LONG).show()
            val open = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, pendingMime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                startActivity(open)
            } catch (_: Exception) {
                Toast.makeText(this, "Mở file vừa lưu trong ứng dụng Tệp nếu Minecraft chưa tự mở.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể xuất pack: ${e.message ?: "lỗi file"}", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Cho phép FLAME CLIENT hiển thị trên ứng dụng khác để dùng ClickGUI nổi.", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            return
        }
        startService(Intent(this, FlameOverlayService::class.java))
        Toast.makeText(this, "Đã mở nút FLAME nổi.", Toast.LENGTH_SHORT).show()
    }

    private fun preferences() = getPreferences(MODE_PRIVATE)
    private fun prefKey(module: Module) = "module_${module.name}"

    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(12), dp(11), dp(12), dp(11))
        background = rounded(panel, border, 13)
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean = false) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
        gravity = Gravity.CENTER_VERTICAL
    }

    private fun pill(value: String, fill: Int, color: Int, size: Float, bold: Boolean = false, click: () -> Unit) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        gravity = Gravity.CENTER
        setPadding(dp(10), dp(8), dp(10), dp(8))
        if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
        background = rounded(fill, if (fill == red) red else border, 10)
        isClickable = true
        isFocusable = true
        setOnClickListener { click() }
    }

    private fun rounded(fill: Int, stroke: Int, radius: Int) = GradientDrawable().apply {
        setColor(fill)
        cornerRadius = dp(radius).toFloat()
        setStroke(dp(1), stroke)
    }

    private fun margin(top: Int = 0, bottom: Int = 0) = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        topMargin = dp(top)
        bottomMargin = dp(bottom)
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
