package com.yzx.lifeassistants.view.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CycleViewPager extends ViewPager {

	private Context context;
	private List<ImageView> dotViewList;// 表示当前页面的小点图片集合
	private int selectResId;// 选中的点的图片资源
	private int unselectResId;// 未选中的点的图片资源
	private int position;// 当前页数

	private ScheduledExecutorService scheduledExecutorService;// 定时任务
	private Handler handler;
	private long initialDelay = 5;// 定时任务启动延时(单位秒)
	private long period = 10;// 定时任务间隔时间(单位秒)

	public CycleViewPager(Context context) {
		super(context);
		this.context = context;
		setOnPageChangeListener(null);
	}

	public CycleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOnPageChangeListener(null);
	}

	@Override
	public void setAdapter(PagerAdapter arg0) {
		super.setAdapter(arg0);
		handler = new Handler(context.getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				setCurrentItem(position);
			}

		};
		setCurrentItem(1);
		startPlay();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = 0;
		// 下面遍历所有child的高度
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			if (h > height) // 采用最大的view的高度。
				height = h;
		}
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
				MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * @reListSize:页面数
	 * @selectResId:选中的图片资源
	 * @unselectResId:未选中的图片资源
	 */
	public void setDotViewList(int resListSize, int selectResId,
			int unselectResId, LinearLayout dotLL) {
		this.selectResId = selectResId;
		this.unselectResId = unselectResId;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = 4;
		params.rightMargin = 4;
		dotViewList = new ArrayList<ImageView>();
		for (int i = 0; i < resListSize; i++) {
			ImageView dotImg = new ImageView(context);
			if (i == 0) {
				dotImg.setImageResource(selectResId);
			} else {
				dotImg.setImageResource(unselectResId);
			}
			dotLL.addView(dotImg, params);
			dotViewList.add(dotImg);
		}
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * 开始轮播图切换
	 */
	private void startPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(),
				initialDelay, period, TimeUnit.SECONDS);
	}

	/**
	 * 执行轮播图切换任务
	 * 
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			synchronized (CycleViewPager.class) {
				position = (position + 1) % getAdapter().getCount();
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		super.setOnPageChangeListener(new CyclePageChangeListener(listener));
	}

	private class CyclePageChangeListener implements OnPageChangeListener {

		private OnPageChangeListener listener;

		public CyclePageChangeListener(OnPageChangeListener listener) {
			this.listener = listener;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (null != listener) {
				listener.onPageScrollStateChanged(arg0);
			}
			if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
				if (position == getAdapter().getCount() - 1) {
					setCurrentItem(1, false);
				} else if (position == 0) {
					setCurrentItem(getAdapter().getCount() - 2, false);
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			if (null != listener) {
				listener.onPageScrolled(arg0, arg1, arg2);
			}
		}

		@Override
		public void onPageSelected(int arg0) {
			position = arg0;
			if (null != listener) {
				listener.onPageSelected(arg0);
			}
			for (int i = 0; i < dotViewList.size(); i++) {
				if (position == getAdapter().getCount() - 1) {// 滑动到最后一页时
					if (i == 0) {// 第一个点设为选中
						dotViewList.get(i).setImageResource(selectResId);
					} else {// 其他点未选中
						dotViewList.get(i).setImageResource(unselectResId);
					}
				} else if (position == 0) {// 滑动到第一页时
					if (i == dotViewList.size() - 1) {// 最后一个点设为选中
						dotViewList.get(i).setImageResource(selectResId);
					} else {// 其他点未选中
						dotViewList.get(i).setImageResource(unselectResId);
					}
				} else {// 没有滑动到前、后所添加的页面时
					if (i == position - 1) {
						dotViewList.get(i).setImageResource(selectResId);
					} else {
						dotViewList.get(i).setImageResource(unselectResId);
					}
				}
			}
		}
	}

}