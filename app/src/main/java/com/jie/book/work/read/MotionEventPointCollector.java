package com.jie.book.work.read;

import android.view.MotionEvent;

public interface MotionEventPointCollector {
	int collectPoints(MotionEvent event, float[] pts);
}
