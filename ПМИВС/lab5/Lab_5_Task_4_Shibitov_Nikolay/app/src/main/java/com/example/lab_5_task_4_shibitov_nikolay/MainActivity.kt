package com.example.lab_5_task_4_shibitov_nikolay

import android.content.res.Configuration
import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.gesture.Prediction
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : AppCompatActivity(),
        GestureOverlayView.OnGesturePerformedListener {

    private lateinit var tvDisplay: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvGestureHint: TextView
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var btnGestureMode: Button

    private var inputString: String = "0"
    private var gestureMode: Boolean = false
    private lateinit var gestureLibrary: GestureLibrary

    // Conversion factors from Lab 2
    private val fromFactors = mapOf(
        "m²"  to 1.0,
        "cm²" to 0.0001,
        "km²" to 1_000_000.0,
        "ha"  to 10_000.0
    )
    private val toFactors = mapOf(
        "in²" to 0.00064516,
        "ft²" to 0.09290304,
        "yd²" to 0.83612736,
        "acre" to 4046.85642,
        "mi²" to 2_589_988.11
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay      = findViewById(R.id.tvDisplay)
        tvResult       = findViewById(R.id.tvResult)
        tvGestureHint  = findViewById(R.id.tvGestureHint)
        spinnerFrom    = findViewById(R.id.spinnerFrom)
        spinnerTo      = findViewById(R.id.spinnerTo)
        btnGestureMode = findViewById(R.id.btnGestureMode)

        if (savedInstanceState != null) {
            inputString = savedInstanceState.getString("inputString", "0")
            tvResult.text = savedInstanceState.getString("resultString", "")
        }
        updateDisplay()

        // Load gesture library
        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures)
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Gesture library not found (create via Gesture Builder)",
                    Toast.LENGTH_LONG).show()
        }

        // Attach gesture listener
        val overlay = findViewById<GestureOverlayView>(R.id.gestureOverlay)
        overlay.addOnGesturePerformedListener(this)

        // Gesture mode toggle
        btnGestureMode.setOnClickListener {
            gestureMode = !gestureMode
            overlay.isEnabled = gestureMode
            if (gestureMode) {
                btnGestureMode.text = getString(R.string.gesture_mode_on)
                tvGestureHint.visibility = View.VISIBLE
            } else {
                btnGestureMode.text = getString(R.string.gesture_mode_off)
                tvGestureHint.visibility = View.GONE
            }
        }
        // Start with gesture overlay disabled (buttons are default input)
        overlay.isEnabled = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("inputString", inputString)
        outState.putString("resultString", tvResult.text.toString())
    }

    // --- Gesture input ---

    override fun onGesturePerformed(overlay: GestureOverlayView, gesture: Gesture) {
        val predictions: ArrayList<Prediction> = gestureLibrary.recognize(gesture)
        if (predictions.isEmpty() || predictions[0].score < 1.0) {
            Toast.makeText(this, "Gesture not recognized", Toast.LENGTH_SHORT).show()
            return
        }
        applyInput(predictions[0].name)
    }

    private fun applyInput(name: String) {
        when (name) {
            "0","1","2","3","4","5","6","7","8","9" -> {
                inputString = if (inputString == "0") name else inputString + name
            }
            "dot" -> {
                if (!inputString.contains(".")) inputString += "."
            }
            "clear" -> {
                inputString = "0"
                tvResult.text = ""
            }
            "backspace" -> {
                inputString = if (inputString.length > 1) {
                    val s = inputString.dropLast(1)
                    if (s == "-") "0" else s
                } else "0"
            }
            "plus_minus" -> {
                inputString = if (inputString.startsWith("-")) {
                    inputString.drop(1)
                } else {
                    if (inputString == "0") "0" else "-$inputString"
                }
            }
            "equals" -> calculateResult()
            else -> Toast.makeText(this, "Unknown gesture: $name", Toast.LENGTH_SHORT).show()
        }
        updateDisplay()
    }

    // --- Button input (original Lab 2 logic, unchanged) ---

    @Suppress("UNUSED_PARAMETER")
    fun onButtonClick(view: View) {
        val text = (view as Button).text.toString()
        when (text) {
            "C"  -> { inputString = "0"; tvResult.text = "" }
            "⌫"  -> {
                inputString = if (inputString.length > 1) {
                    val s = inputString.dropLast(1)
                    if (s == "-") "0" else s
                } else "0"
            }
            "±"  -> {
                inputString = if (inputString.startsWith("-")) {
                    inputString.drop(1)
                } else {
                    if (inputString == "0") "0" else "-$inputString"
                }
            }
            "."  -> { if (!inputString.contains(".")) inputString += "." }
            "="  -> calculateResult()
            else -> { // digits
                inputString = if (inputString == "0") text else inputString + text
            }
        }
        updateDisplay()
    }

    @Suppress("DEPRECATION", "UNUSED_PARAMETER")
    fun onLangClick(view: View) {
        val currentLang = resources.configuration.locales[0].language
        val newLang = if (currentLang == "ru") "en" else "ru"
        val locale = Locale(newLang)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    // --- Shared logic ---

    private fun updateDisplay() {
        tvDisplay.text = inputString
    }

    private fun calculateResult() {
        val value = inputString.toDoubleOrNull()
        if (value == null) {
            tvResult.text = getString(R.string.error_invalid)
            return
        }
        val fromUnit = spinnerFrom.selectedItem.toString()
        val toUnit   = spinnerTo.selectedItem.toString()
        val factorFrom = fromFactors[fromUnit] ?: 1.0
        val factorTo   = toFactors[toUnit]   ?: 1.0
        if (factorTo == 0.0) {
            tvResult.text = getString(R.string.error_div_zero)
            return
        }
        val result = (value * factorFrom) / factorTo
        val df = DecimalFormat("0.######", DecimalFormatSymbols(Locale.US))
        tvResult.text = df.format(result)
    }
}
