package com.yzx.lifeassistants.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.baoyz.swipemenulistview.SwipeMenuListView;

/**
 * @Description: 嵌套ScrollView的ListView
 * @author: yzx
 * @time: 2015-11-24 上午9:25:23
 */
@SuppressLint("ClickableViewAccessibility")
public class MySwipeMenuListView extends SwipeMenuListView {
	private GestureDetector mGestureDetector;
	View.OnTouchListener mGestureListener;

	public MySwipeMenuListView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(context, onGestureListener);
	}

	public MySwipeMenuListView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		mGestureDetector = new GestureDetector(paramContext, onGestureListener);
	}

	public MySwipeMenuListView(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		mGestureDetector = new GestureDetector(paramContext, onGestureListener);
	}

	@SuppressWarnings("unused")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean b = mGestureDetector.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}

	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (distanceY != 0 && distanceX != 0) {
			}
			if (Math.abs(distanceY) >= Math.abs(distanceX)) {
				return true;
			}
			// 当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview 停住不能滚动
			setParentScrollAble(false);
			return false;
		}
	};

	/**
	 * 是否把滚动事件交给父scrollview
	 * 
	 * @param flag
	 */
	private void setParentScrollAble(boolean flag) {
		// 这里的parentScrollView就是listview外面的那个scrollview
		getParent().requestDisallowInterceptTouchEvent(!flag);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
