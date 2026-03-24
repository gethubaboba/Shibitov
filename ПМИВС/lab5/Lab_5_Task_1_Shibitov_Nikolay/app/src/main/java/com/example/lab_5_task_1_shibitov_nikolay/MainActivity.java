package com.example.lab_5_task_1_shibitov_nikolay;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

public class MainActivity extends AppCompatActivity
        implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private GestureDetectorCompat mDetector;
    private TextView tvOutput;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = findViewById(R.id.tvOutput);
        scrollView = findViewById(R.id.scrollView);
        View gestureArea = findViewById(R.id.gestureArea);

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);

        // Устанавливаем обработку жестов на конкретную область
        gestureArea.setOnTouchListener((v, event) -> {
            mDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return true;
        });

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> tvOutput.setText(""));
    }

    private void appendGesture(String name) {
        tvOutput.append(name + "\n");
        // Автопрокрутка вниз
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    // --- OnGestureListener methods ---

    @Override
    public boolean onDown(MotionEvent e) {
        appendGesture("onDown");
        return true; // Изменено на true, чтобы получать последующие события
    }

    @Override
    public void onShowPress(MotionEvent e) {
        appendGesture("onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        appendGesture("onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        appendGesture("onScroll: dX=" + String.format("%.1f", distanceX)
                + ", dY=" + String.format("%.1f", distanceY));
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        appendGesture("onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        appendGesture("onFling: vX=" + String.format("%.1f", velocityX)
                + ", vY=" + String.format("%.1f", velocityY));
        return true;
    }

    // --- OnDoubleTapListener methods ---

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        appendGesture("onSingleTapConfirmed");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        appendGesture("onDoubleTap");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        appendGesture("onDoubleTapEvent");
        return true;
    }
}
