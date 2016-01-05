package com.yzx.lifeassistants.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * @Description: 水平滚动
 * @author: yzx
 * @time: 2015-11-25 上午10:23:54
 */
public class CustomHScrollView extends HorizontalScrollView {
	private GestureDetector mGestureDetector;
	@SuppressWarnings("unused")
	private View.OnTouchListener mGestureListener;

	@SuppressWarnings("unused")
	private static final String TAG = "CustomHScrollView";

	/**
	 * @function CustomHScrollView constructor
	 * @param context
	 *            Interface to global information about an application
	 *            environment.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public CustomHScrollView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(new HScrollDetector());
		setFadingEdgeLength(0);
	}

	/**
	 * @function CustomHScrollView constructor
	 * @param context
	 *            Interface to global information about an application
	 *            environment.
	 * @param attrs
	 *            A collection of attributes, as found associated with a tag in
	 *            an XML document.
	 */
	@SuppressWarnings("deprecation")
	public CustomHScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(new HScrollDetector());
		setFadingEdgeLength(0);
	}

	/**
	 * @function CustomHScrollView constructor
	 * @param context
	 *            Interface to global information about an application
	 *            environment.
	 * @param attrs
	 *            A collection of attributes, as found associated with a tag in
	 *            an XML document.
	 * @param defStyle
	 *            style of view
	 */
	@SuppressWarnings("deprecation")
	public CustomHScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGestureDetector = new GestureDetector(new HScrollDetector());
		setFadingEdgeLength(0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev)
				&& mGestureDetector.onTouchEvent(ev);
	}

	// Return false if we're scrolling in the y direction
	class HScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (Math.abs(distanceX) > Math.abs(distanceY)) {
				return true;
			}

			return false;
		}
	}
}
