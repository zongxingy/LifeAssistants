package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

import com.balysv.materialripple.MaterialRippleLayout;
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
import com.squareup.timessquare.CalendarPickerView;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.ExpendDateAdapter;
import com.yzx.lifeassistants.adapter.MyPoupWindowAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.ExpendByRange;
import com.yzx.lifeassistants.bean.ExpendInfo;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.model.ISelectExpendByRange;
import com.yzx.lifeassistants.model.callback.ISelectExpendByRangeCB;
import com.yzx.lifeassistants.model.impl.SelectExpendByRangeImpl;
import com.yzx.lifeassistants.utils.CalendarUtil;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.DisplayUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;
import com.yzx.lifeassistants.view.widget.CustomHScrollView;
import com.yzx.lifeassistants.view.widget.MySwipeMenuListView;

/**
 * @Description: 支出柱形图分析
 * @author: yzx
 * @time: 2015-12-10 上午9:04:00
 */
@SuppressLint("InflateParams")
public class ExpendBarAnalysisActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private ImageButton backBtn;// 返回按钮
	private TextView typeTV;// 类型选择
	private ImageButton calendarBtn;// 日历按钮
	private PullRefreshLayout refreshLayout;// 下拉刷新
	private MySwipeMenuListView expendLV;// 支出列表
	private CustomHScrollView cHScrollView;// 水平
	private BarChart mChart;// 柱形图

	private PopupWindow popupWindow;
	private View contentView;// 日历弹框
	private AlertDialog theDialog;// 日历弹框
	private CalendarPickerView calendarPV;// 日历选择
	private CircularLoadingDialog dialog;// 加载数据对话框
	private Boolean isDate;// 获取该天数据
	private Boolean isBar;// 获取柱形图数据
	private String date;// 日期
	private String type;// 类型
	private List<ExpendInfo> expendList;// 支出记录列表
	private ExpendDateAdapter adapter;// 适配器
	private Handler handler;
	private ISelectExpendByRangeCB selectCallBack;// 回调
	private ISelectExpendByRange selectExpendByRange;// 查询
	private List<ExpendByRange> dataList;// 返回结果

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expend_bar_analysis);
		initView();
		setListener();
		initPoupWindow();
		initCalendarPickerView();
		initSwipeMenuListView();
		initChart();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.expend_bar_analysis_back_btn);
		typeTV = (TextView) findViewById(R.id.expend_bar_analysis_top_type_tv);
		calendarBtn = (ImageButton) findViewById(R.id.expend_bar_analysis_calendar_btn);
		refreshLayout = (PullRefreshLayout) findViewById(R.id.expend_bar_analysis_pull_refresh_layout);
		expendLV = (MySwipeMenuListView) findViewById(R.id.expend_bar_analysis_lv);
		cHScrollView = (CustomHScrollView) findViewById(R.id.expend_bar_analysis_horizontalscrollview);
		mChart = (BarChart) findViewById(R.id.expend_bar_analysis_chart);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		typeTV.setOnClickListener(this);
		calendarBtn.setOnClickListener(this);
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
				ExpendInfo item = expendList.get(position);
				switch (index) {
				case 0:
					// open
					Intent intent = new Intent(ExpendBarAnalysisActivity.this,
							AddExpendActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(
							CommonConstant.TO_ADD_EXPEND_ACTIVITY_IS_MODIFY,
							item);
					intent.putExtras(bundle);
					startActivityForResult(intent,
							CommonConstant.REQUESTCODE_MODIFY_EXPEND);
					break;
				case 1:
					// delete
					dialog.show();
					item.delete(ExpendBarAnalysisActivity.this,
							new DeleteListener() {

								@Override
								public void onSuccess() {
									LogcatUtils.i("删除该支出记录成功");
									ToastUtils.showToast("删除成功~");
									queryExpend();
								}

								@Override
								public void onFailure(int code, String message) {
									LogcatUtils.e("删除该支出记录失败：" + code + " "
											+ message);
									dialog.dismiss();
									switch (code) {
									case 9010: {// 网络超时
										ToastUtils.showToast("网络超时，请检查您的手机网络~");
										break;
									}
									case 9016: {// 无网络连接，请检查您的手机网络
										ToastUtils
												.showToast("无网络连接，请检查您的手机网络~");
										break;
									}
									default: {
										ToastUtils.showToast("删除失败，请重试~");
										break;
									}
									}
								}
							});
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
	 * @Description: 初始化PoupWindow弹框
	 */
	@SuppressWarnings("deprecation")
	private void initPoupWindow() {
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.poupwindow, null);
		ListView groupLV = (ListView) view.findViewById(R.id.poupwindow_lv);
		// 加载数据
		List<String> groupList = new ArrayList<String>();
		groupList.add("月支出");
		groupList.add("年支出");
		MyPoupWindowAdapter groupAdapter = new MyPoupWindowAdapter(this,
				groupList);
		groupLV.setAdapter(groupAdapter);
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		// 创建一个PopuWidow对象
		popupWindow = new PopupWindow(view, windowManager.getDefaultDisplay()
				.getWidth() / 2,
				windowManager.getDefaultDisplay().getHeight() / 4);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		groupLV.setOnItemClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化日历控件
	 */
	private void initCalendarPickerView() {
		// 弹框
		theDialog = new AlertDialog.Builder(this).create();
		theDialog.setCanceledOnTouchOutside(true);
		contentView = LayoutInflater.from(this).inflate(
				R.layout.dialog_calendarpicker, null);
		// 日历控件
		calendarPV = (CalendarPickerView) contentView
				.findViewById(R.id.calendar_view);
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);
		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -1);
		calendarPV.init(lastYear.getTime(), nextYear.getTime()) // 设置显示时间范围
				.withSelectedDate(new Date());
		// 取消按钮
		Button cancleBtn = (Button) contentView
				.findViewById(R.id.dialog_cancle_btn);
		// 确定按钮
		Button confirmBtn = (Button) contentView
				.findViewById(R.id.dialog_confirm_btn);
		// 动态特效
		MaterialRippleLayout
				.on(cancleBtn)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();
		MaterialRippleLayout
				.on(confirmBtn)
				.rippleColor(Color.parseColor(CommonConstant.RIPPLE_COLOR_DARK))
				.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
				.create();
		// 取消事件
		cancleBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				theDialog.dismiss();
			}
		});
		// 确定事件
		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long time = calendarPV.getSelectedDate().getTime();
				date = CalendarUtil.longToString("yyyy-MM-dd", time);
				theDialog.dismiss();
				dialog.show();
				queryExpend();// 查询

			}
		});
		theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialogInterface) {
				calendarPV.fixDialogDimens();
			}
		});
	}

	/**
	 * 
	 * @Description: 初始化能左划出打开/删除的ListView
	 */
	private void initSwipeMenuListView() {
		expendList = new ArrayList<ExpendInfo>();
		adapter = new ExpendDateAdapter(ExpendBarAnalysisActivity.this,
				expendList);
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
				openItem.setWidth(DensityUtils.dp2px(
						ExpendBarAnalysisActivity.this, 90));
				// set item title
				openItem.setTitle("Open");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(DensityUtils.dp2px(
						ExpendBarAnalysisActivity.this, 90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
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
		isBar = false;
		isDate = false;
		dialog = new CircularLoadingDialog(this);
		date = CalendarUtil.getCurrentTime("yyyy-MM-dd");
		dataList = new ArrayList<ExpendByRange>();
		handler = new Handler();
		selectCallBack = new ISelectExpendByRangeCB() {

			@Override
			public void selectSuccess(final List<ExpendByRange> expendList,
					final String range) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						LogcatUtils.i("获取该月/年支出柱形图数据成功");
						isBar = true;
						isLoaded();
						mChart.setVisibility(View.VISIBLE);
						dataList.clear();
						dataList.addAll(expendList);
						sortData();// 排序
					}

				});
			}

			@Override
			public void selectError(final int code, final String range) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						LogcatUtils.e("获取该月/年支出柱形图数据失败：" + code + " " + range);
						isBar = true;
						isLoaded();
						mChart.setVisibility(View.GONE);
						switch (code) {
						case ISelectExpendByRangeCB.RESULR_NULL: {
							if ("date".equals(range)) {
								ToastUtils.showToast("该月无支出记录~");
							} else if ("month".equals(range)) {
								ToastUtils.showToast("该年无支出记录~");
							}
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

		type = "月";
		typeTV.setText("月支出");
		dialog.show();
		queryExpend();// 刚进入时查询月支出

	}

	/**
	 * 
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.expend_bar_analysis_back_btn: {// 返回
			finish();
			break;
		}
		case R.id.expend_bar_analysis_top_type_tv: {// 类型选择
			popupWindow.showAsDropDown(v,
					-typeTV.getWidth() + DensityUtils.dp2px(this, 20)
							+ DensityUtils.sp2px(this, 17), 0);
			break;
		}
		case R.id.expend_bar_analysis_calendar_btn: {// 日历
			theDialog.show();
			theDialog.setContentView(contentView);
			theDialog.getWindow().setGravity(Gravity.CENTER);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 展示类型选择
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (0 == position) {
			type = "月";
			typeTV.setText("月支出");
		} else {
			type = "年";
			typeTV.setText("年支出");
		}
		dialog.show();
		queryExpend();// 查询
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}

	/**
	 * 
	 * @Description: 查询支出记录
	 */
	private void queryExpend() {
		// 查询柱形图数据
		if (-1 != type.indexOf("月")) {
			String month = date.substring(0, date.lastIndexOf("-"));
			selectExpendByRange.selectExpendByRange(
					GlobalParams.userInfo.getUsername(), month, "date");
		} else if (-1 != type.indexOf("年")) {
			String year = date.substring(0, date.indexOf("-"));
			selectExpendByRange.selectExpendByRange(
					GlobalParams.userInfo.getUsername(), year, "month");
		}
		// 查询列表数据
		// 时间为选择的日期
		BmobQuery<ExpendInfo> dateQuery = new BmobQuery<ExpendInfo>();
		dateQuery.addWhereEqualTo("date", date);
		// 只查询自己账号的数据
		BmobQuery<ExpendInfo> userQuery = new BmobQuery<ExpendInfo>();
		userQuery.addWhereEqualTo("username",
				GlobalParams.userInfo.getUsername());
		// 总体复合与查询
		List<BmobQuery<ExpendInfo>> queries = new ArrayList<BmobQuery<ExpendInfo>>();
		queries.add(userQuery);
		queries.add(dateQuery);
		BmobQuery<ExpendInfo> query = new BmobQuery<ExpendInfo>();
		query.and(queries);
		query.order("-money");// 按照支出金额降序
		query.findObjects(this, new FindListener<ExpendInfo>() {

			@Override
			public void onSuccess(List<ExpendInfo> expendInfos) {
				LogcatUtils.i("查询该天的支出记录成功");
				isDate = true;
				isLoaded();
				expendList.clear();
				if (null == expendInfos || 1 > expendInfos.size()) {
					expendLV.setVisibility(View.GONE);
					String time = date.substring(date.indexOf("-") + 1,
							date.length()).replace("-", "月")
							+ "号";
					ToastUtils.showToast("暂无" + time + "的支出记录~");
				} else {
					expendLV.setVisibility(View.VISIBLE);
					expendList.addAll(expendInfos);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onError(int code, String message) {
				LogcatUtils.e("查询该天的支出记录失败：" + code + " " + message);
				isDate = true;
				isLoaded();
				switch (code) {
				case 101: {// 无数据
					String time = date.substring(date.indexOf("-") + 1,
							date.length()).replace("-", "月")
							+ "号";
					ToastUtils.showToast("暂无" + time + "的支出记录~");
					LogcatUtils.e("查询" + time + "的支出记录为空");
					break;
				}
				case 9010: {// 网络超时
					ToastUtils.showToast("网络超时，请检查您的手机网络~");
					break;
				}
				case 9016: {// 无网络连接，请检查您的手机网络
					ToastUtils.showToast("无网络连接，请检查您的手机网络~");
					break;
				}
				default: {
					ToastUtils.showToast("获取失败，请刷新重试~");
					break;
				}
				}
			}

		});
	}

	/**
	 * 
	 * @Description: 排序处理
	 */
	private void sortData() {
		Collections.sort(dataList, new MyComparator());
		showChart();
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
			if (-1 != type.indexOf("月")) {
				String date = expend.getRange();
				xVals.add(date.substring(date.lastIndexOf("-") + 1,
						date.length())
						+ "号");
			} else if (-1 != type.indexOf("年")) {
				String year = expend.getRange();
				xVals.add(year.substring(year.lastIndexOf("-") + 1,
						year.length())
						+ "月");
			}

		}
		BarDataSet set = new BarDataSet(yVals, "");//
		if (-1 != type.indexOf("月")) {
			String month = date.substring(date.indexOf("-") + 1,
					date.lastIndexOf("-"))
					+ "月";
			set.setLabel(month + "支出记录");
		} else if (-1 != type.indexOf("年")) {
			String year = date.substring(0, date.indexOf("-")) + "年";
			set.setLabel(year + "支出记录");

		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 修改账单成功
		if (CommonConstant.RESULTCODE_MODIFY_EXPEND_SUCCESS == resultCode) {
			if (CommonConstant.REQUESTCODE_MODIFY_EXPEND == requestCode) {
				dialog.show();
				queryExpend();// 查询
			}
		}
	}

	/**
	 * 
	 * @Description: 判断是否加载完成
	 */
	private void isLoaded() {
		if (isBar && isDate) {
			isBar = false;
			isDate = false;
			if (dialog.isShowing()) {
				dialog.dismiss();
			} else {
				refreshLayout.setRefreshing(false);
			}
		}
	}
}
