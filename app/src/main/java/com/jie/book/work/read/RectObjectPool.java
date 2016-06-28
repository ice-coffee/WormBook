package com.jie.book.work.read;

import java.util.Stack;

import android.graphics.Rect;

public class RectObjectPool {
	private Stack<Rect> pool;
	private Byte readLock = 0;
	private int maxSize;
	private static RectObjectPool instance = null;
	
	private RectObjectPool(int maxSize) {
		pool = new Stack<Rect>();
		this.maxSize = maxSize;
	}
	
	public static Rect getObject() {
		if(instance == null) {
			instance = new RectObjectPool(100);
		}
		
		synchronized (instance.readLock) {
			if(instance.pool.size() > 0) {
				Rect rect = instance.pool.pop();
				rect.setEmpty();
				return rect;
			}
		}
		return new Rect();
	}

	public synchronized static void freeObject(Rect object) {
		if(instance == null) {
			instance = new RectObjectPool(100);
		}
		
		if(instance.pool.size() < instance.maxSize)
			instance.pool.push(object);
		else
			object = null;
	}

}
