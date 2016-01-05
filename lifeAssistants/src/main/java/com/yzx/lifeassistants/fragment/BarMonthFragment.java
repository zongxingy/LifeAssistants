package com.yzx.lifeassistants.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

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
import com.yzx.lifeassistants.activity.ExpendMonthAnalysisActivity;
import com.yzx.lifeassistants.bean.ExpendByRange;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.model.ISelectExpendByRange;
import com.yzx.lifeassistants.model.callback.ISelectExpendByRangeCB;
import com.yzx.lifeassistants.model.impl.SelectExpendByRangeImpl;
import com.yzx.lifeassistants.utils.CalendarUtil;
import com.yzx.lifeassistants.utils.DisplayUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;
import com.yzx.lifeassistants.view.widget.CustomHScrollView;

public class BarMonthFragment extends Fragment {

	private CustomHScrollView cHScrollView;// 水平
	private BarChart mChart;// 柱形图

	public static final int CHART_BAR = 0;
	private String month;// 月份
	private CircularLoadingDialog dialog;//
	private Handler handler;//
	private List<ExpendByRange> dataList;// 支出记录列表
	private ISelectExpendByRangeCB selectCallBack;// 回调
	private ISelectExpendByRange selectExpendByRange;// 查询

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_month_bar, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		setListener();
		initChart();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化上个界面传来的值
	 */
	private void init() {
		Bundle bundle = getArguments();
		if (null != bundle) {
			month = bundle.getString(
					CommonConstant.FROM_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY,
					CalendarUtil.getCurrentTime("yyyy-MM"));
		} else {
			month = CalendarUtil.getCurrentTime("yyyy-MM");
		}
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		cHScrollView = (CustomHScrollView) getView().findViewById(
				R.id.expend_month_bar_horizontalscrollview);
		mChart = (BarChart) getView().findViewById(R.id.expend_month_bar_chart);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {

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
		dialog = new CircularLoadingDialog(getActivity());
		dataList = new ArrayList<ExpendByRange>();
		handler = new Handler();
		selectCallBack = new ISelectExpendByRangeCB() {

			@Override
			public void selectSuccess(final List<ExpendByRange> expendList,
					String range) {

				handler.post(new Runnable() {// 成功

					@Override
					public void run() {
						LogcatUtils.i("获取该月支出柱形图数据成功");
						if (getActivity() instanceof ExpendMonthAnalysisActivity) {
							((ExpendMonthAnalysisActivity) getActivity())
									.refreshOvew(CHART_BAR);
						}
						mChart.setVisibility(View.VISIBLE);
						dialog.dismiss();
						dataList.clear();
						dataList.addAll(expendList);
						sortData();// 排序
					}
				});
			}

			@Override
			public void selectError(final int code, final String range) {

				handler.post(new Runnable() {// 失败

					@Override
					public void run() {
						LogcatUtils.e("获取该月支出柱形图数据失败：" + code + " " + range);
						if (getActivity() instanceof ExpendMonthAnalysisActivity) {
							((ExpendMonthAnalysisActivity) getActivity())
									.refreshOvew(CHART_BAR);
						}
						mChart.setVisibility(View.GONE);
						dialog.dismiss();
						switch (code) {
						case ISelectExpendByRangeCB.RESULR_NULL: {
							LogcatUtils.e("获取该月支出数据为空");
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
		selectExpendByRange = new SelectExpendByRangeImpl(getActivity(),
				selectCallBack);
		dialog.show();
		queryExpend();
	}

	/**
	 * 
	 * @Description: 查询支出记录
	 */
	public void queryExpend() {
		selectExpendByRange.selectExpendByRange(
				GlobalParams.userInfo.getUsername(), month, "date");// 查询
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
			String month = expend.getRange();
			xVals.add(month.substring(month.lastIndexOf("-") + 1,
					month.length())
					+ "号");

		}
		BarDataSet set = new BarDataSet(yVals, month.substring(
				month.indexOf("-") + 1, month.length())
				+ "月支出记录");//
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
			params.width = DisplayUtils.getScreenWidth(getActivity())
					- DisplayUtils.dip2px(getActivity(), 15);
		} else {
			int perWide = DisplayUtils.getScreenWidth(getActivity()) / 8;
			params.width = perWide * xVals.size();
		}
		mChart.setLayoutParams(params);
		BarData data = new BarData(xVals, set);
		mChart.setData(data);// 设置图表数据
		cHScrollView.scrollTo(0, 0);
		mChart.invalidate();// 刷新界面
		mChart.animateY(800);// 立即执行的动画,x轴
	}
}
