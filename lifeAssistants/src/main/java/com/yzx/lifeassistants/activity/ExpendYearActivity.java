package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.PullRefreshLayout.OnRefreshListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.ExpendRangeAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.ExpendByRange;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.model.ISelectExpendByRange;
import com.yzx.lifeassistants.model.callback.ISelectExpendByRangeCB;
import com.yzx.lifeassistants.model.impl.SelectExpendByRangeImpl;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.DisplayUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;
import com.yzx.lifeassistants.view.widget.CustomHScrollView;
import com.yzx.lifeassistants.view.widget.MySwipeMenuListView;

/**
 * @Description: 某年支出
 * @author: yzx
 * @time: 2015-12-14 上午9:22:13
 */
public class ExpendYearActivity extends BaseActivity implements OnClickListener {
	private ImageButton backBtn;// 返回按钮
	private TextView titleTV;// 标题
	private PullRefreshLayout refreshLayout;// 下拉刷新
	private MySwipeMenuListView expendLV;// 支出列表
	private CustomHScrollView cHScrollView;// 水平
	private BarChart mChart;// 柱形图

	private String year;// 年份
	private List<ExpendByRange> dataList;// 支出记录列表
	private ExpendRangeAdapter adapter;// 适配器
	private CircularLoadingDialog dialog;
	private Handler handler;
	private ISelectExpendByRangeCB selectCallBack;// 回调
	private ISelectExpendByRange selectExpendByRange;// 查询

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expend_year);
		init();
		initView();
		setListener();
		initSwipeMenuListView();
		initChart();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化上个界面传来的值
	 */
	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			year = intent
					.getStringExtra(CommonConstant.TO_EXPEND_YEAR_ACTIVITY_KEY);
		}
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.expend_year_back_btn);
		titleTV = (TextView) findViewById(R.id.expend_year_title_tv);
		refreshLayout = (PullRefreshLayout) findViewById(R.id.expend_year_expend_pull_refresh_layout);
		expendLV = (MySwipeMenuListView) findViewById(R.id.expend_year_expend_lv);
		cHScrollView = (CustomHScrollView) findViewById(R.id.expend_year_horizontalscrollview);
		mChart = (BarChart) findViewById(R.id.expend_year_chart);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		// 下拉刷新
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				queryExpend();
			}

		});
		// step 2. listener item click event
		expendLV.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(final int position, SwipeMenu menu,
					int index) {
				ExpendByRange item = dataList.get(position);
				switch (index) {
				case 0:
					// 打开
					Intent intent = new Intent(ExpendYearActivity.this,
							ExpendMonthActivity.class);
					intent.putExtra(
							CommonConstant.TO_EXPEND_MONTH_ACTIVITY_KEY,
							item.getRange());
					startActivity(intent);
					break;
				case 1:
					// 图表分析
					Intent analysisIntent = new Intent(ExpendYearActivity.this,
							ExpendMonthAnalysisActivity.class);
					analysisIntent
							.putExtra(
									CommonConstant.TO_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY,
									item.getRange());
					startActivity(analysisIntent);
					break;
				}
				return false;
			}
		});

		// set SwipeListener
		expendLV.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

			@Override
			public void onSwipeStart(int position) {
				// swipe start
			}

			@Override
			public void onSwipeEnd(int position) {
				// swipe end
			}
		});

		// set MenuStateChangeListener
		expendLV.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
			@Override
			public void onMenuOpen(int position) {

			}

			@Override
			public void onMenuClose(int position) {

			}
		});

		// other setting
		// listView.setCloseInterpolator(new BounceInterpolator());

		// test item long click
		expendLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				return false;
			}
		});
	}

	/**
	 * 
	 * @Description: 初始化能左划出打开/删除的ListView
	 */
	private void initSwipeMenuListView() {
		dataList = new ArrayList<ExpendByRange>();
		adapter = new ExpendRangeAdapter(this, dataList);
		expendLV.setAdapter(adapter);
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				// set item width
				openItem.setWidth(DensityUtils.dp2px(ExpendYearActivity.this,
						90));
				// set item title
				openItem.setTitle("打开");
				// openItem.setIcon(R.drawable.icon_info);
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "分析" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(DensityUtils.dp2px(ExpendYearActivity.this,
						90));
				// set a icon
				deleteItem.setIcon(R.drawable.icon_bar_pie);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		expendLV.setMenuCreator(creator);
	}

	/**
	 * 
	 * @Description: 初始化柱形图
	 */
	private void initChart() {
		mChart.setDescription("");// 数据描述
		mChart.setPinchZoom(false);// 是否X,Y轴都缩放
		mChart.setTouchEnabled(true); // 设置是否可以触摸
		mChart.setDragEnabled(true);// 是否可以拖拽
		mChart.setScaleEnabled(true);// 是否可以缩放
		mChart.setDrawBarShadow(false);// 是否阴影显示未到最大值的部分
		mChart.setDrawGridBackground(false);// 是否显示网格背景
		mChart.setAutoScaleMinMaxEnabled(false);// 是否自动更改最大最小值
		XAxis xAxis = mChart.getXAxis();// x轴
		xAxis.setPosition(XAxisPosition.BOTTOM);// 设置X轴字体显示位置
		xAxis.setLabelRotationAngle(-0);// 设置X轴字体倾斜角度
		xAxis.setLabelsToSkip(0);// 设置X轴间隔不显示
		xAxis.setDrawGridLines(false);// 是否绘制网格线
		xAxis.setDrawAxisLine(false);// 是否绘制轴线
		YAxis leftYAxis = mChart.getAxisLeft();// 左Y轴
		leftYAxis.setDrawGridLines(true);// 是否绘制左边网格线
		leftYAxis.setStartAtZero(false);// 是否从0开始
		leftYAxis.setDrawAxisLine(false);// 是否绘制轴线
		// 设置数据格式
		leftYAxis.setValueFormatter(new YAxisValueFormatter() {

			@Override
			public String getFormattedValue(float value, YAxis yAxis) {
				return (int) value + "";
			}
		});
		YAxis rightYAxis = mChart.getAxisRight();// 右Y轴
		rightYAxis.setDrawGridLines(false);// 是否绘制右边网格线
		rightYAxis.setStartAtZero(false);// 是否从0开始
		rightYAxis.setDrawAxisLine(false);// 是否绘制轴线
		// 设置数据格式
		rightYAxis.setValueFormatter(new YAxisValueFormatter() {

			@Override
			public String getFormattedValue(float value, YAxis yAxis) {
				return (int) value + "";
			}
		});
		Legend mLegend = mChart.getLegend(); // 设置图标示图
		mLegend.setForm(LegendForm.SQUARE);// 样式
		mLegend.setFormSize(10f);// 样式大小
		mLegend.setTextColor(Color.BLACK);// 颜色
		mLegend.setTextSize(13f);// 字体大小
		mLegend.setPosition(LegendPosition.ABOVE_CHART_LEFT);// 图标显示位置
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		titleTV.setText(year + "年支出");
		dialog = new CircularLoadingDialog(this);
		handler = new Handler();
		selectCallBack = new ISelectExpendByRangeCB() {

			@Override
			public void selectSuccess(final List<ExpendByRange> expendList,
					String range) {

				handler.post(new Runnable() {// 成功

					@Override
					public void run() {
						LogcatUtils.i("查询该年的支出记录成功");
						refreshLayout.setRefreshing(false);
						expendLV.setVisibility(View.VISIBLE);
						mChart.setVisibility(View.VISIBLE);
						dialog.dismiss();
						dataList.clear();
						dataList.addAll(expendList);
						sortData();// 排序
						adapter.notifyDataSetChanged();
						showChart();
					}
				});
			}

			@Override
			public void selectError(final int code, final String range) {

				handler.post(new Runnable() {// 失败

					@Override
					public void run() {
						LogcatUtils.e("查询该年的支出记录失败：" + code + " " + range);
						refreshLayout.setRefreshing(false);
						expendLV.setVisibility(View.GONE);
						mChart.setVisibility(View.GONE);
						dialog.dismiss();
						switch (code) {
						case ISelectExpendByRangeCB.RESULR_NULL: {
							ToastUtils.showToast("该年无支出记录~");
							break;
						}
						case ISelectExpendByRangeCB.NET_ERROR: {
							ToastUtils.showToast("网络超时，请检查您的手机网络~");
							break;
						}
						case ISelectExpendByRangeCB.SELECT_ERROR: {
							ToastUtils.showToast("获取失败，请确认网络连接后刷新重试~");
							break;
						}
						default:
							break;
						}
					}
				});
			}
		};
		selectExpendByRange = new SelectExpendByRangeImpl(this, selectCallBack);
		dialog.show();
		queryExpend();
	}

	/**
	 * 
	 * @Description: 查询支出记录
	 */
	private void queryExpend() {
		selectExpendByRange.selectExpendByRange(
				GlobalParams.userInfo.getUsername(), year, "month");// 查询
	}

	/**
	 * 
	 * @Description: 排序处理
	 */
	private void sortData() {
		Collections.sort(dataList, new MyComparator());
	}

	/**
	 * 
	 * @Description: 比较器 Collections.sort(list, myCompatator);
	 */
	class MyComparator implements Comparator<Object> {
		public int compare(Object arg0, Object arg1) {
			ExpendByRange cop1 = (ExpendByRange) arg0;
			ExpendByRange cop2 = (ExpendByRange) arg1;
			int flag = cop1.getRange().compareTo(cop2.getRange());
			return flag;
		}

	}

	/**
	 * 
	 * @Description: 展示柱形图
	 */
	private void showChart() {
		ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();// Y轴数据
		ArrayList<String> xVals = new ArrayList<String>();// X轴数据
		int i = 0;
		for (ExpendByRange expend : dataList) {
			int val = expend.getMoney();
			yVals.add(new BarEntry((int) val, i++));
			String year = expend.getRange();
			xVals.add(year.substring(year.lastIndexOf("-") + 1, year.length())
					+ "月");

		}
		BarDataSet set = new BarDataSet(yVals, year + "年支出记录");//
		set.setColors(ColorTemplate.VORDIPLOM_COLORS);// 柱形颜色
		set.setDrawValues(true);// 是否显示数据
		set.setValueTextSize(10);// 显示数据字体大小
		// 设置数据格式 成int型数据
		set.setValueFormatter(new ValueFormatter() {

			@Override
			public String getFormattedValue(float value, Entry entry,
					int dataSetIndex, ViewPortHandler viewPortHandler) {

				return (int) value + "元";
			}
		});
		// 根据X轴数量动态更改宽度
		LayoutParams params = mChart.getLayoutParams();
		if (xVals.size() < 9) {
			params.width = DisplayUtils.getScreenWidth(this)
					- DisplayUtils.dip2px(this, 15);
		} else {
			int perWide = DisplayUtils.getScreenWidth(this) / 8;
			params.width = perWide * xVals.size();
		}
		mChart.setLayoutParams(params);
		BarData data = new BarData(xVals, set);
		mChart.setData(data);// 设置图表数据
		cHScrollView.scrollTo(0, 0);
		mChart.invalidate();// 刷新界面
		mChart.animateY(800);// 立即执行的动画,x轴
	}

	/**
	 * 
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.expend_year_back_btn: {// 返回
			finish();
			break;
		}

		default:
			break;
		}
	}
}
