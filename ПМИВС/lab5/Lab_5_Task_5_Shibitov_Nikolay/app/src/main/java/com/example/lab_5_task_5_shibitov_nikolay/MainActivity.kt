package com.example.lab_5_task_5_shibitov_nikolay

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            drawingView.clearCanvas()
        }

        findViewById<Button>(R.id.btnUndo).setOnClickListener {
            drawingView.undo()
        }

        // Color buttons
        findViewById<Button>(R.id.btnBlack).setOnClickListener {
            drawingView.setColor(Color.BLACK)
        }
        findViewById<Button>(R.id.btnRed).setOnClickListener {
            drawingView.setColor(Color.RED)
        }
        findViewById<Button>(R.id.btnBlue).setOnClickListener {
            drawingView.setColor(Color.BLUE)
        }
        findViewById<Button>(R.id.btnGreen).setOnClickListener {
            drawingView.setColor(Color.GREEN)
        }
        // Eraser
        findViewById<Button>(R.id.btnEraser).setOnClickListener {
            drawingView.setColor(Color.WHITE)
        }

        // Stroke width seekbar
        val seekBar = findViewById<SeekBar>(R.id.seekBarWidth)
        seekBar.max = 50
        seekBar.progress = 6
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                drawingView.setStrokeWidth((progress + 1).toFloat())
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
    }
}
