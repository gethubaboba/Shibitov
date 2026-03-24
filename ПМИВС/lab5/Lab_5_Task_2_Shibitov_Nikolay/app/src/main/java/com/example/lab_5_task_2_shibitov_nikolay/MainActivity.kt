package com.example.lab_5_task_2_shibitov_nikolay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab_5_task_2_shibitov_nikolay.ui.theme.Lab_5_Task_2_Shibitov_NikolayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab_5_Task_2_Shibitov_NikolayTheme {
                GesturesDemoScreen()
            }
        }
    }
}

@Composable
fun GesturesDemoScreen() {
    var gestureLog by remember { mutableStateOf("") }

    fun log(msg: String) {
        gestureLog = "$msg\n$gestureLog"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Jetpack Compose — жесты",
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(8.dp))

        // Tap gestures zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFFE3F2FD))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { log("onTap: ${it.x.toInt()},${it.y.toInt()}") },
                        onDoubleTap = { log("onDoubleTap: ${it.x.toInt()},${it.y.toInt()}") },
                        onLongPress = { log("onLongPress: ${it.x.toInt()},${it.y.toInt()}") },
                        onPress = {
                            log("onPress (start)")
                            tryAwaitRelease()
                            log("onPress (release)")
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Нажмите / двойной тап / долгое нажатие", fontSize = 13.sp)
        }

        Spacer(Modifier.height(8.dp))

        // Drag gesture zone
        var dragOffset by remember { mutableStateOf("—") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFE8F5E9))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { log("onDragStart: ${it.x.toInt()},${it.y.toInt()}") },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffset = "dX=${dragAmount.x.toInt()}, dY=${dragAmount.y.toInt()}"
                        },
                        onDragEnd = { log("onDragEnd (last: $dragOffset)") }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Перетаскивание: $dragOffset", fontSize = 13.sp)
        }

        Spacer(Modifier.height(8.dp))

        // Transform (pinch/rotate) gesture zone
        var scale by remember { mutableStateOf(1f) }
        var rotation by remember { mutableStateOf(0f) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFFFF9C4))
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, rotationChange ->
                        scale *= zoom
                        rotation += rotationChange
                        log("Zoom: ${"%.2f".format(scale)}, Rotate: ${"%.1f".format(rotation)}°")
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Масштаб: ${"%.2f".format(scale)}x   Поворот: ${"%.1f".format(rotation)}°", fontSize = 13.sp)
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { gestureLog = "" }, modifier = Modifier.fillMaxWidth()) {
            Text("Очистить журнал")
        }

        Spacer(Modifier.height(8.dp))

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = gestureLog.ifEmpty { "Журнал жестов появится здесь..." },
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }
    }
}
