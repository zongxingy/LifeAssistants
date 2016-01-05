package com.yzx.lifeassistants.view.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.yzx.lifeassistants.R;

/**
 * 
 * @Description: 圆弧计分
 * @author yzx
 * @date 2015-12-1
 */
public class CircularPoints extends View {

	public static final int COLOR_GREEN = 0;// 绿色
	public static final int COLOR_YELLOW = 1;// 黄色
	public static final int COLOR_ORANGE = 2;// 橘色
	public static final int COLOR_RED = 3;// 红色

	private int redColor = 0xddd31943; // 红色
	private int greenColor = 0xdd019587; // 绿色
	private int yellowColor = 0xffd3a247;// 黄色
	private int orangeColor = 0xffe77c59;// 橘色
	private int grayColor = 0x88cccccc;// 底灰色
	private int textColor;// 字体颜色
	private int loopColor;// 外环颜色
	private Paint paint_gray;// 外环底色
	private Paint paint_loop;// 外环显色
	private Paint paint_text;// 字体颜色
	private RectF rectf;
	private float tb;

	private int score;
	private float arc_y = 0f;
	private int score_text;

	public CircularPoints(Context context) {
		super(context);
	}

	public CircularPoints(Context context, int score, int textType) {
		super(context);
		setTextColor(textType);
		setLoopColor(textType);
		init(score);
	}

	/**
	 * 
	 * @Description: 设置字体颜色
	 */
	public void setTextColor(int type) {
		if (COLOR_RED == type) {
			this.textColor = redColor;
		} else if (COLOR_GREEN == type) {
			this.textColor = greenColor;
		} else if (COLOR_YELLOW == type) {
			this.textColor = yellowColor;
		} else if (COLOR_ORANGE == type) {
			this.textColor = orangeColor;
		}
	}

	/**
	 * 
	 * @Description: 设置外环颜色
	 */
	private void setLoopColor(int type) {
		if (COLOR_RED == type) {
			this.loopColor = redColor;
		} else if (COLOR_GREEN == type) {
			this.loopColor = greenColor;
		} else if (COLOR_YELLOW == type) {
			this.loopColor = yellowColor;
		} else if (COLOR_ORANGE == type) {
			this.loopColor = orangeColor;
		}
	}

	public void init(int score) {
		this.score = score;
		Resources res = getResources();
		tb = res.getDimension(R.dimen.historyscore_tb);

		paint_gray = new Paint();
		paint_gray.setAntiAlias(true);
		paint_gray.setColor(grayColor);
		paint_gray.setStrokeWidth(tb * 0.5f);
		paint_gray.setStyle(Style.STROKE);

		paint_loop = new Paint();
		paint_loop.setAntiAlias(true);
		paint_loop.setColor(loopColor);
		paint_loop.setStrokeWidth(tb * 0.5f);
		paint_loop.setStyle(Style.STROKE);

		paint_text = new Paint();
		paint_text.setAntiAlias(true);
		paint_text.setColor(textColor);
		paint_text.setTextSize(tb * 6.0f);
		paint_text.setStrokeWidth(tb * 0.2f);
		paint_text.setTextAlign(Align.CENTER);
		paint_text.setStyle(Style.STROKE);

		rectf = new RectF();
		rectf.set(tb * 0.5f, tb * 0.5f, tb * 18.5f, tb * 18.5f);

		setLayoutParams(new LayoutParams((int) (tb * 19.5f), (int) (tb * 19.5f)));

		this.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					public boolean onPreDraw() {
						new thread();
						getViewTreeObserver().removeOnPreDrawListener(this);
						return false;
					}
				});
	}

	protected void onDraw(Canvas c) {
		super.onDraw(c);
		c.drawArc(rectf, -90, 360, false, paint_gray);// 绘制圆环底线
		c.drawArc(rectf, -90, arc_y, false, paint_loop);// 绘制圆环
		c.drawText("" + score_text, tb * 9.7f, tb * 11.0f, paint_text);// 绘制中间分数
	}

	class thread implements Runnable {
		private Thread thread;
		private int statek;
		int count;

		public thread() {
			thread = new Thread(this);
			thread.start();
		}

		public void run() {
			while (true) {
				switch (statek) {
				case 0:
					try {
						Thread.sleep(200);
						statek = 1;
					} catch (InterruptedException e) {
					}
					break;
				case 1:
					try {
						Thread.sleep(25);
						arc_y += 3.6f;
						score_text++;
						count++;
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}
				if (count >= score)
					break;
			}
		}
	}

}
