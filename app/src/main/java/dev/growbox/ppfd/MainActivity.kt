package dev.growbox.ppfd

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

/** Typy światła i przybliżone PPFD na luks. */
private val LIGHTS = listOf(
    "Biała LED, pełne spektrum (3500–5000 K)" to 0.0170,
    "Biała LED, ciepła (2700–3000 K)" to 0.0150,
    "Biała LED, zimna (6000 K+)" to 0.0160,
    "Światło dzienne (słońce)" to 0.0185,
    "Własna kalibracja" to 0.0,
)

private const val INK = "#1F2A22"
private const val MUTED = "#6B776E"
private const val BG = "#F6F7F4"

class MainActivity : Activity(), SensorEventListener {
    private lateinit var sm: SensorManager
    private var sensor: Sensor? = null
    private lateinit var prefs: android.content.SharedPreferences
    private var smoothed = 0f
    private var lightIdx = 0
    private var hours = 12
    private var plant: Plant = PLANTS[0]

    private lateinit var ppfdView: TextView
    private lateinit var verdictView: TextView
    private lateinit var targetView: TextView
    private lateinit var bar: RangeBar
    private lateinit var careBox: LinearLayout
    private lateinit var infoView: TextView
    private lateinit var refInput: EditText

    private fun factor(): Double =
        if (lightIdx == LIGHTS.lastIndex) prefs.getFloat("custom", 0.017f).toDouble() else LIGHTS[lightIdx].second

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun text(s: String = "", size: Float = 16f, bold: Boolean = false, color: String = INK) = TextView(this).apply {
        text = s; textSize = size; setTextColor(Color.parseColor(color))
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    private fun card(vararg views: View) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(18), dp(20), dp(18))
        background = GradientDrawable().apply { setColor(Color.WHITE); cornerRadius = dp(20).toFloat() }
        views.forEach { addView(it) }
        layoutParams = LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(14) }
    }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        prefs = getSharedPreferences("ppfd", Context.MODE_PRIVATE)
        lightIdx = prefs.getInt("light", 0)
        hours = prefs.getInt("hours", 12)
        plant = PLANTS.getOrElse(prefs.getInt("plant", 0)) { PLANTS[0] }
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(8), dp(16), dp(24))
        }
        root.addView(text("Światło dla roślin", 26f, true))
        root.addView(text("Połóż telefon płasko przy liściach, górną krawędzią do lampy.", 13f, color = MUTED).apply { setPadding(0, dp(2), 0, dp(10)) })

        val plantSpinner = Spinner(this).apply {
            adapter = adapter(PLANTS.map { it.name })
            setSelection(PLANTS.indexOf(plant).coerceAtLeast(0))
            onItemSelectedListener = sel { plant = PLANTS[it]; prefs.edit().putInt("plant", it).apply(); refresh() }
        }
        root.addView(card(text("Twoja roślina", 12f, color = MUTED), plantSpinner))

        ppfdView = text("--", 60f, true).apply { gravity = Gravity.CENTER }
        verdictView = text("", 20f, true).apply { gravity = Gravity.CENTER }
        targetView = text("", 13f, color = MUTED).apply { gravity = Gravity.CENTER; setPadding(0, dp(6), 0, 0) }
        bar = RangeBar(this)
        root.addView(card(
            ppfdView,
            text("PPFD (µmol/m²/s)", 12f, color = MUTED).apply { gravity = Gravity.CENTER },
            verdictView, bar.also { it.setPadding(0, dp(8), 0, 0) }, targetView))

        careBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(card(text("Jak dbać", 18f, true), careBox))

        // Ustawienia schowane domyślnie
        val settings = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; visibility = View.GONE }
        settings.addView(text("Rodzaj światła", 12f, color = MUTED).apply { setPadding(0, dp(8), 0, 0) })
        settings.addView(Spinner(this).apply {
            adapter = adapter(LIGHTS.map { it.first }); setSelection(lightIdx)
            onItemSelectedListener = sel { lightIdx = it; prefs.edit().putInt("light", it).apply(); refresh() }
        })
        settings.addView(text("Godziny światła na dobę", 12f, color = MUTED).apply { setPadding(0, dp(8), 0, 0) })
        settings.addView(EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER; setText(hours.toString()); setTextColor(Color.parseColor(INK))
            addTextChangedListener(object : android.text.TextWatcher {
                override fun afterTextChanged(s: android.text.Editable?) {
                    s?.toString()?.toIntOrNull()?.let { hours = it.coerceIn(1, 24); prefs.edit().putInt("hours", hours).apply(); refresh() }
                }
                override fun beforeTextChanged(a: CharSequence?, b: Int, c: Int, d: Int) {}
                override fun onTextChanged(a: CharSequence?, b: Int, c: Int, d: Int) {}
            })
        })
        settings.addView(text("Kalibracja: wpisz PPFD z miernika wzorcowego, mierząc w tym samym miejscu", 12f, color = MUTED).apply { setPadding(0, dp(8), 0, 0) })
        val cal = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        refInput = EditText(this).apply {
            hint = "wzorzec PPFD"; setTextColor(Color.parseColor(INK))
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        cal.addView(refInput, LinearLayout.LayoutParams(0, -2, 1f))
        cal.addView(Button(this).apply {
            text = "Kalibruj"
            setOnClickListener {
                val ref = refInput.text.toString().toDoubleOrNull()
                if (ref == null || ref <= 0 || smoothed < 1f) {
                    Toast.makeText(context, "Wpisz wartość wzorca i oświetl czujnik", Toast.LENGTH_SHORT).show()
                } else {
                    prefs.edit().putFloat("custom", (ref / smoothed).toFloat()).putInt("light", LIGHTS.lastIndex).apply()
                    lightIdx = LIGHTS.lastIndex; refresh()
                    Toast.makeText(context, "Skalibrowano", Toast.LENGTH_SHORT).show()
                }
            }
        })
        settings.addView(cal)
        infoView = text("", 13f, color = MUTED).apply { setPadding(0, dp(8), 0, 0) }
        settings.addView(infoView)
        val toggle = text("Ustawienia  ▾", 15f, true, "#3F7D4F").apply {
            setOnClickListener {
                val open = settings.visibility == View.GONE
                settings.visibility = if (open) View.VISIBLE else View.GONE
                text = if (open) "Ustawienia  ▴" else "Ustawienia  ▾"
            }
        }
        root.addView(card(toggle, settings))

        root.addView(text("Czujnik telefonu mierzy luksy, a PPFD jest z nich przeliczane. Dokładność to ok. ±20–30% dla białych LED. " +
            "Zakresy dla roślin są orientacyjne.", 11f, color = MUTED).apply { setPadding(dp(4), dp(14), dp(4), 0) })

        val scroll = ScrollView(this).apply { setBackgroundColor(Color.parseColor(BG)); addView(root) }
        scroll.setOnApplyWindowInsetsListener { v, ins ->
            v.setPadding(0, ins.systemWindowInsetTop, 0, ins.systemWindowInsetBottom); ins
        }
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        setContentView(scroll)
        refresh()
    }

    private fun adapter(items: List<String>) =
        object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items) {
            override fun getView(p: Int, v: View?, g: ViewGroup): View =
                (super.getView(p, v, g) as TextView).apply { setTextColor(Color.parseColor(INK)); textSize = 17f }
        }

    private fun sel(f: (Int) -> Unit) = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) = f(pos)
        override fun onNothingSelected(p: AdapterView<*>?) {}
    }

    private fun refresh() {
        val ppfd = smoothed * factor()
        ppfdView.text = if (sensor == null) "brak czujnika" else "%.0f".format(ppfd)
        bar.min = plant.minPpfd.toFloat(); bar.max = plant.maxPpfd.toFloat(); bar.value = ppfd.toFloat()
        targetView.text = "Cel dla tej rośliny: ${plant.minPpfd}–${plant.maxPpfd}, ok. ${plant.hours} h dziennie"
        val dli = ppfd * 3600 * hours / 1e6
        infoView.text = "Współczynnik: %.4f PPFD/lx · %.0f lx · DLI %.1f mol/m²/dobę".format(factor(), smoothed, dli)
        val (msg, color) = when {
            ppfd < plant.minPpfd * 0.5 -> "Za ciemno" to "#C62828"
            ppfd < plant.minPpfd -> "Trochę za mało światła" to "#E08A00"
            ppfd <= plant.maxPpfd -> "W sam raz" to "#2E7D32"
            ppfd <= plant.maxPpfd * 1.3 -> "Trochę za jasno" to "#E08A00"
            else -> "Za jasno" to "#C62828"
        }
        verdictView.text = msg; verdictView.setTextColor(Color.parseColor(color))

        careBox.removeAllViews()
        careBox.addView(text(plant.latin, 13f, color = MUTED).apply { setTypeface(null, Typeface.ITALIC) })
        plant.care.forEach { (h, t) ->
            careBox.addView(text(h, 13f, true, "#3F7D4F").apply { setPadding(0, dp(12), 0, 0) })
            careBox.addView(text(t, 15f))
        }
    }

    override fun onResume() { super.onResume(); sensor?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }; refresh() }
    override fun onPause() { super.onPause(); sm.unregisterListener(this) }
    override fun onSensorChanged(e: SensorEvent) {
        val lux = e.values[0]
        smoothed = if (smoothed == 0f) lux else smoothed * 0.8f + lux * 0.2f
        refresh()
    }
    override fun onAccuracyChanged(s: Sensor?, a: Int) {}
}
