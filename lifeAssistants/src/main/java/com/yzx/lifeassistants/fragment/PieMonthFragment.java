package com.yzx.lifeassistants.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.ExpendMonthAnalysisActivity;
import com.yzx.lifeassistants.activity.ExpendPieAnalysisActivity;
import com.yzx.lifeassistants.bean.ExpendByType;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.model.ISelectExpendByType;
import com.yzx.lifeassistants.model.callback.ISelectExpendByTypeCB;
import com.yzx.lifeassistants.model.impl.SelectExpendByTypeImpl;
import com.yzx.lifeassistants.utils.CalendarUtil;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.utils.TypefacesUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 月支出饼图分析
 * @author: yzx
 * @time: 2015-12-8 下午3:05:55
 */
public class PieMonthFragment extends Fragment implements
		OnChartValueSelectedListener {

	private PieChart mChart;// 饼图控件
	private TextView noDataTV;// 无支出提醒

	public static final int CHART_PIE_MONTH = 0;
	private String month;
	private CircularLoadingDialog dialog;
	private Handler handler;
	private ISelectExpendByTypeCB selectCallBack;// 回调
	private ISelectExpendByType selectExpendByType;// 查询
	private List<ExpendByType> dataList;// 数据

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_month_pie, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initChart();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化传来的值
	 */
	private void init() {
		Bundle bundle = getArguments();
		if (null == bundle) {
			month = CalendarUtil.getCurrentTime("yyyy-MM");
		} else {
			month = bundle.getString(
					CommonConstant.FROM_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY,
					CalendarUtil.getCurrentTime("yyyy-MM"));
		}

	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		mChart = (PieChart) getView().findViewById(R.id.pie_mhonth_chart);
		noDataTV = (TextView) getView().findViewById(R.id.pie_month_no_data_tv);
	}

	/**
	 * 
	 * @Description: 初始化饼图
	 */
	private void initChart() {
		mChart.setDescription("");
		// 设置中间文字
		mChart.setCenterTextTypeface(TypefacesUtils.get(getActivity(),
				"OpenSans-Light.ttf"));// 字体
		if (null == month) {
			mChart.setCenterText("本月支出");// 文字
		} else {
			mChart.setCenterText(month.substring(month.indexOf("-") + 1,
					month.length())
					+ "月支出");// 文字
		}
		mChart.setCenterTextSize(17);// 文字大小
		mChart.setDrawCenterText(true);// 是否显示文字

		mChart.setDrawHoleEnabled(true);// 是否饼图中心为空
		mChart.setHoleColorTransparent(true);// 设置中心是否透明

		mChart.setHoleRadius(55f);// 内圆半径
		mChart.setTransparentCircleRadius(60f);// 外圆半径
		mChart.setRotationAngle(0);// 初始旋转角度
		mChart.setTransparentCircleColor(Color.WHITE);// 设置透明的圆的颜色
		mChart.setTransparentCircleAlpha(110);// 透明度

		// enable rotation of the chart by touch
		mChart.setRotationEnabled(true);// 可以手动旋转
		mChart.setHighlightPerTapEnabled(true);// 是否点击变高

		// mChart.setUnit(" €");//设置单位
		// mChart.setDrawUnitsInChart(true);//是否显示单位

		// display percentage values
		mChart.setUsePercentValues(true); // 显示成百分比

		// add a selection listener
		mChart.setOnChartValueSelectedListener(this);// 设置选中监听

		// 设置比例图
		Legend legend = mChart.getLegend();
		legend.setPosition(LegendPosition.RIGHT_OF_CHART_CENTER);// 最右边显示
		legend.setForm(LegendForm.SQUARE); // 设置比例图的形状
		legend.setXEntrySpace(7f);// 设置X轴间隔
		legend.setYEntrySpace(0f);//
		legend.setTextSize(14);// 文字大小
		legend.setYOffset(0f);//
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		dialog = new CircularLoadingDialog(getActivity());
		dataList = new ArrayList<ExpendByType>();
		handler = new Handler();
		selectCallBack = new ISelectExpendByTypeCB() {

			@Override
			public void selectSuccess(final List<ExpendByType> expendList) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						LogcatUtils.i("获取该月支出饼图数据成功");
						if (getActivity() instanceof ExpendMonthAnalysisActivity) {
							((ExpendMonthAnalysisActivity) getActivity())
									.refreshOvew(CHART_PIE_MONTH);
						} else if (getActivity() instanceof ExpendPieAnalysisActivity) {
							((ExpendPieAnalysisActivity) getActivity())
									.isRefreshOvew(CHART_PIE_MONTH);
						}
						dialog.dismiss();
						if (null == expendList || 1 > expendList.size()) {

						} else {
							noDataTV.setVisibility(View.GONE);
							mChart.setVisibility(View.VISIBLE);
							dataList.clear();
							dataList.addAll(expendList);
							setChartData();
						}
					}
				});
			}

			@Override
			public void selectError(final int code) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						LogcatUtils.e("获取该月支出饼图数据失败：" + code);
						if (getActivity() instanceof ExpendMonthAnalysisActivity) {
							((ExpendMonthAnalysisActivity) getActivity())
									.refreshOvew(CHART_PIE_MONTH);
						} else if (getActivity() instanceof ExpendPieAnalysisActivity) {
							((ExpendPieAnalysisActivity) getActivity())
									.isRefreshOvew(CHART_PIE_MONTH);
						}
						dialog.dismiss();
						mChart.setVisibility(View.GONE);
						noDataTV.setVisibility(View.VISIBLE);
						switch (code) {
						case ISelectExpendByTypeCB.RESULR_NULL: {
							LogcatUtils.e("获取月饼图数据为空");
							break;
						}
						case ISelectExpendByTypeCB.SELECT_ERROR: {
							ToastUtils.showToast("获取失败，请确认网络连接后刷新重试~");
							break;
						}
						case ISelectExpendByTypeCB.NET_ERROR: {
							ToastUtils.showToast("网络超时，请检查您的手机网络~");
							break;
						}
						default:
							break;
						}
					}
				});
			}
		};
		selectExpendByType = new SelectExpendByTypeImpl(getActivity(),
				selectCallBack);
		dialog.show();
		getDataFromService();

	}

	/**
	 * 
	 * @Description: 获取本月各个类别的支出总额
	 */
	public void getDataFromService() {
		// 获取本月各个类别的支出总额
		if (null == month) {
			selectExpendByType.selectExpendByType(
					GlobalParams.userInfo.getUsername(),
					CalendarUtil.getCurrentTime("yyyy-MM"));
		} else {
			selectExpendByType.selectExpendByType(
					GlobalParams.userInfo.getUsername(), month);
		}
	}

	/**
	 * 
	 * @Description: 设置饼图数据
	 */
	private void setChartData() {
		// Y轴数据
		ArrayList<Entry> yVals = new ArrayList<Entry>();
		// X轴数据
		ArrayList<String> xVals = new ArrayList<String>();
		// 支出总额
		Integer total = 0;
		for (ExpendByType expendByType : dataList) {
			total += expendByType.getMoney();
			xVals.add(expendByType.getType());
		}
		// IMPORTANT: In a PieChart, no values (Entry) should have the same
		// xIndex (even if from different DataSets), since no values can be
		// drawn above each other.
		int i = 0;
		for (ExpendByType expendByType : dataList) {
			yVals.add(new Entry((float) expendByType.getMoney() * 100 / total,
					i++));
		}
		// 设置饼图的数据
		PieDataSet dataSet = new PieDataSet(yVals, "");
		dataSet.setSliceSpace(2f);// 间隔
		dataSet.setSelectionShift(5f);

		// // add a lot of colors
		ArrayList<Integer> colors = new ArrayList<Integer>();
		colors.add(Color.rgb(0xea, 0xf0, 0x42));// 黄色
		colors.add(Color.rgb(0xf2, 0x9a, 0xf1));// 紫色
		colors.add(Color.rgb(0x65, 0xd9, 0xfe));// 浅蓝
		colors.add(Color.rgb(0x98, 0xcb, 0x00));// 绿色
		colors.add(Color.rgb(0xf5, 0x93, 0x43));// 橙色
		colors.add(Color.rgb(0xf3, 0x24, 0x86));// 红色
		colors.add(Color.rgb(0x67, 0x8c, 0xff));// 蓝色
		colors.add(ColorTemplate.getHoloBlue());
		dataSet.setColors(colors);

		PieData data = new PieData(xVals, dataSet);
		data.setValueFormatter(new PercentFormatter());// 设置数据格式
		data.setValueTextSize(11f);// 设置字体大小
		data.setValueTextColor(Color.WHITE);// 设置字体颜色
		data.setValueTypeface(TypefacesUtils.get(getActivity(),
				"OpenSans-Regular.ttf"));// 设置字体
		mChart.setData(data);

		// undo all highlights
		mChart.highlightValues(null);
		// mChart.animateXY(1000, 1000); // 设置动画
		mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);// 设置动画
		mChart.invalidate();
	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

	}

	@Override
	public void onNothingSelected() {

	}
}
