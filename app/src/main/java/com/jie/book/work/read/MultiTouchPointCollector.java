package com.jie.book.work.read;

import android.view.MotionEvent;

/**
 * 
 * @author Neevek
 * this class collects as many points as possible.
 * e.g. if we have 2 fingers touching the screen, we will fill the pts array as
 * the following:
 * pts[0]=x1, pts[1]=y1, pts[2]=x2, pts[3]=y2 
 */
public class MultiTouchPointCollector implements MotionEventPointCollector {
	public int collectPoints(MotionEvent event, float[] pts) {
		int pointCount = event.getPointerCount();
		if(pts.length / 2 < pointCount)
			pointCount = pts.length / 2;
		for (int i = 0, j = 0; j < pointCount; i+=2, ++j) {
			pts[i] = event.getX(j);
			pts[i+1] = event.getY(j);
		}
		return pointCount;
	}

}
