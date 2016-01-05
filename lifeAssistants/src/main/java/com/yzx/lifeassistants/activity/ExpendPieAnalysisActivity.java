package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.PullRefreshLayout.OnRefreshListener;
import com.viewpagerindicator.UnderlinePageIndicator;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.CustomPagerAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.ExpendByRange;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.fragment.PieMonthFragment;
import com.yzx.lifeassistants.fragment.PieYearFragment;
import com.yzx.lifeassistants.model.ISelectExpendByRange;
import com.yzx.lifeassistants.model.callback.ISelectExpendByRangeCB;
import com.yzx.lifeassistants.model.impl.SelectExpendByRangeImpl;
import com.yzx.lifeassistants.utils.CalendarUtil;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 支出饼图分析
 * @author: yzx
 * @time: 2015-11-30 下午3:32:28
 */
public class ExpendPieAnalysisActivity extends BaseActivity implements
		OnClickListener, OnPageChangeListener {
	private ImageButton backBtn;// 返回按钮
	private ImageButton barBtn;// 柱形图分析按钮
	private PullRefreshLayout refreshLayout;// 下拉刷新
	private RelativeLayout dayRL;// 今天
	private TextView todayTV;// 今天日期
	private TextView dayTV;// 今天日期
	private TextView dayExpendTV;// 今天支出总额
	private RelativeLayout monthRL;// 本月
	private TextView monthTV;// 本月日期
	private TextView monthExpendTV;// 本月支出总额
	private RelativeLayout yearRL;//
	private TextView yearTV;// 本年日期
	private TextView yearExpendTV;// 本年支出总额
	private ViewPager viewPager;// 中间部分
	private UnderlinePageIndicator indicator;// 滑线
	private RadioButton monthRB;// 本月
	private RadioButton yearRB;// 本年

	private List<Fragment> fragments;//
	private PieMonthFragment monthFragment;// 本月饼图
	private PieYearFragment yearFragment;// 本年饼图
	private CustomPagerAdapter adapter;// viewpage‘s adapter
	public static final int TAB_MONTH = 0;// 本月
	public static final int TAB_YEAR = 1;// 本年

	private Boolean isDay;// 今天
	private Boolean isMonth;// 本月
	private Boolean isYear;// 本年
	private Boolean isMonthPie;// 本月饼图
	private Boolean isYearPie;// 本年饼图
	private CircularLoadingDialog dialog;//
	private Handler handler;
	private ISelectExpendByRangeCB selectCallBack;
	private ISelectExpendByRange selectExpendByRange;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expend_pie_analysis);
		initView();
		setListener();
		initMaterialRipple();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.expend_analysis_back_btn);
		barBtn = (ImageButton) findViewById(R.id.expend_analysis_bar_btn);
		refreshLayout = (PullRefreshLayout) findViewById(R.id.expend_analysis_pull_refresh_layout);
		dayRL = (RelativeLayout) findViewById(R.id.expend_analysis_day_rl);
		todayTV = (TextView) findViewById(R.id.expend_analysis_todday_tv);
		dayTV = (TextView) findViewById(R.id.expend_analysis_day_tv);
		dayExpendTV = (TextView) findViewById(R.id.expend_analysis_day_expend_tv);
		monthRL = (RelativeLayout) findViewById(R.id.expend_analysis_month_rl);
		monthTV = (TextView) findViewById(R.id.expend_analysis_month_tv);
		monthExpendTV = (TextView) findViewById(R.id.expend_analysis_month_expend_tv);
		yearRL = (RelativeLayout) findViewById(R.id.expend_analysis_year_rl);
		yearTV = (TextView) findViewById(R.id.expend_analysis_year_tv);
		yearExpendTV = (TextView) findViewById(R.id.expend_analysis_year_expend_tv);
		viewPager = (ViewPager) findViewById(R.id.expend_analysis_viewpager);
		indicator = (UnderlinePageIndicator) findViewById(R.id.expend_analysis_indicator);
		monthRB = (RadioButton) findViewById(R.id.expend_analysis_month_rb);
		yearRB = (RadioButton) findViewById(R.id.expend_analysis_year_rb);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		barBtn.setOnClickListener(this);
		// 下拉刷新
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getDataFromService();
				monthFragment.getDataFromService();
				yearFragment.getDataFromService();
			}

		});
		dayRL.setOnClickListener(this);
		monthRL.setOnClickListener(this);
		yearRL.setOnClickListener(this);
		monthRB.setOnClickListener(this);
		yearRB.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		List<View> views = new ArrayList<View>();
		views.add(dayRL);
		views.add(monthRL);
		views.add(yearRL);
		for (View view : views) {
			// 动态特效
			MaterialRippleLayout
					.on(view)
					.rippleColor(
							Color.parseColor(CommonConstant.RIPPLE_COLOR_LITHT))
					.rippleAlpha(CommonConstant.RIPPLE_ALPHA).rippleHover(true)
					.create();
		}
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		isDay = false;
		isMonth = false;
		isYear = false;
		isMonthPie = false;
		isYearPie = false;
		dialog = new CircularLoadingDialog(this);
		todayTV.setText(CalendarUtil.getCurrentTime("dd"));
		dayTV.setText(CalendarUtil.getCurrentTime("dd号"));
		monthTV.setText(CalendarUtil.getCurrentTime("MM月"));
		yearTV.setText(CalendarUtil.getCurrentTime("yy年"));
		initViewPage();
		initUnderLine();
		handler = new Handler();
		selectCallBack = new ISelectExpendByRangeCB() {

			@Override
			public void selectSuccess(final List<ExpendByRange> expendList,
					final String range) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if ("date".equals(range)) {
							isDay = true;
							dayExpendTV.setText("总支出："
									+ expendList.get(0).getMoney() + "元");
							dayRL.setOnClickListener(ExpendPieAnalysisActivity.this);
						} else if ("month".equals(range)) {
							isMonth = true;
							monthExpendTV.setText("总支出："
									+ expendList.get(0).getMoney() + "元");
							monthRL.setOnClickListener(ExpendPieAnalysisActivity.this);
						} else if ("year".equals(range)) {
							isYear = true;
							yearExpendTV.setText("总支出："
									+ expendList.get(0).getMoney() + "元");
							yearRL.setOnClickListener(ExpendPieAnalysisActivity.this);
						}
						isRefreshOvew(2);
					}
				});
			}

			@Override
			public void selectError(final int code, final String range) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						switch (code) {
						case ISelectExpendByRangeCB.RESULR_NULL: {
							if ("date".equals(range)) {
								isDay = true;
								dayExpendTV.setText("今天暂无支出");
								dayRL.setOnClickListener(null);
								LogcatUtils.e("获取今天支出总金额为空");
							} else if ("month".equals(range)) {
								isMonth = true;
								monthExpendTV.setText("本月暂无支出");
								monthRL.setOnClickListener(null);
								LogcatUtils.e("获取本月支出总金额为空");
							} else if ("year".equals(range)) {
								isYear = true;
								yearExpendTV.setText("本年暂无支出");
								yearRL.setOnClickListener(null);
								LogcatUtils.e("获取本年支出总金额为空");
							}
							break;
						}
						case ISelectExpendByRangeCB.NET_ERROR: {
							if ("date".equals(range)) {
								isDay = true;
								dayExpendTV.setText("获取失败");
								dayRL.setOnClickListener(null);
							} else if ("month".equals(range)) {
								isMonth = true;
								monthExpendTV.setText("获取失败");
								monthRL.setOnClickListener(null);
							} else if ("year".equals(range)) {
								isYear = true;
								yearExpendTV.setText("获取失败");
								yearRL.setOnClickListener(null);
							}
							ToastUtils.showToast("网络超时，请检查您的手机网络~");
							break;
						}
						case ISelectExpendByRangeCB.SELECT_ERROR: {
							if ("date".equals(range)) {
								isDay = true;
								dayExpendTV.setText("获取失败");
								dayRL.setOnClickListener(null);
							} else if ("month".equals(range)) {
								isMonth = true;
								monthExpendTV.setText("获取失败");
								monthRL.setOnClickListener(null);
							} else if ("year".equals(range)) {
								isYear = true;
								yearExpendTV.setText("获取失败");
								yearRL.setOnClickListener(null);
							}
							ToastUtils.showToast("获取失败，请确认网络连接后刷新重试~");
							break;
						}
						default:
							break;
						}
						isRefreshOvew(2);
					}
				});
			}
		};
		selectExpendByRange = new SelectExpendByRangeImpl(this, selectCallBack);
		// 获取
		dialog.show();
		getDataFromService();
	}

	/**
	 * @Description: 初始化viewpage
	 */
	@SuppressWarnings("deprecation")
	private void initViewPage() {
		FragmentManager fm = getSupportFragmentManager();
		fragments = new ArrayList<Fragment>();
		monthFragment = new PieMonthFragment();
		yearFragment = new PieYearFragment();
		fragments.add(monthFragment);
		fragments.add(yearFragment);
		adapter = new CustomPagerAdapter(fm, fragments);
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(TAB_MONTH);
		viewPager.setOnPageChangeListener(this);
	}

	/**
	 * @Description: 初始化滑线
	 */
	private void initUnderLine() {
		indicator.setViewPager(viewPager);
		indicator.setFades(false);
		indicator.setBackgroundColor(getResources().getColor(R.color.gray));
		indicator.setSelectedColor(getResources().getColor(
				R.color.text_green_color));
		indicator.setOnPageChangeListener(this);

	}

	/**
	 * 
	 * @Description: 从服务器获取数据
	 */
	private void getDataFromService() {
		// 获取今天支出总额
		selectExpendByRange.selectExpendByRange(
				GlobalParams.userInfo.getUsername(),
				CalendarUtil.getCurrentTime("yyyy-MM-dd"), "date");
		// 获取本月支出总额
		selectExpendByRange.selectExpendByRange(
				GlobalParams.userInfo.getUsername(),
				CalendarUtil.getCurrentTime("yyyy-MM"), "month");
		// 获取本年支出总额
		selectExpendByRange.selectExpendByRange(
				GlobalParams.userInfo.getUsername(),
				CalendarUtil.getCurrentTime("yyyy"), "year");
	}

	/**
	 * 
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.expend_analysis_back_btn: {// 返回按钮
			finish();
			break;
		}
		case R.id.expend_analysis_bar_btn: {// 柱形图分析
			Intent intent = new Intent(this, ExpendBarAnalysisActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.expend_analysis_day_rl: {// 今天
			Intent intent = new Intent(this, ExpendDateActivity.class);
			intent.putExtra(CommonConstant.TO_EXPEND_DATE_ACTIVITY_KEY,
					CalendarUtil.getCurrentTime("yyyy-MM-dd"));
			startActivity(intent);
			break;
		}
		case R.id.expend_analysis_month_rl: {// 本月
			Intent intent = new Intent(this, ExpendMonthActivity.class);
			intent.putExtra(CommonConstant.TO_EXPEND_MONTH_ACTIVITY_KEY,
					CalendarUtil.getCurrentTime("yyyy-MM"));
			startActivity(intent);
			break;
		}
		case R.id.expend_analysis_year_rl: {// 本年
			Intent intent = new Intent(this, ExpendYearActivity.class);
			intent.putExtra(CommonConstant.TO_EXPEND_YEAR_ACTIVITY_KEY,
					CalendarUtil.getCurrentTime("yyyy"));
			startActivity(intent);
			break;
		}
		case R.id.expend_analysis_month_rb: {// 本月
			viewPager.setCurrentItem(TAB_MONTH);
			break;
		}
		case R.id.expend_analysis_year_rb: {// 本年
			viewPager.setCurrentItem(TAB_YEAR);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case TAB_MONTH: {// 本月
			monthRB.setChecked(true);
			break;
		}
		case TAB_YEAR: {// 本年
			yearRB.setChecked(true);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 
	 * @Description: 下拉刷新完成
	 */
	public void isRefreshOvew(final int chartType) {
		if (PieMonthFragment.CHART_PIE_MONTH == chartType) {
			isMonthPie = true;
		}
		if (PieYearFragment.CHART_PIE_YEAR == chartType) {
			isYearPie = true;
		}
		if (dialog.isShowing()) {
			if (isDay && isMonth && isYear) {
				isDay = false;
				isMonth = false;
				isYear = false;
				dialog.dismiss();
			}
		} else if (isMonthPie && isYearPie && isDay && isMonth && isYear) {
			isMonthPie = false;
			isYearPie = false;
			isDay = false;
			isMonth = false;
			isYear = false;
			refreshLayout.setRefreshing(false);
		}
	}
}
