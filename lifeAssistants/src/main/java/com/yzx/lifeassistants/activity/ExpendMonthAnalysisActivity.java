package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.PullRefreshLayout.OnRefreshListener;
import com.viewpagerindicator.UnderlinePageIndicator;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.CustomPagerAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.fragment.BarMonthFragment;
import com.yzx.lifeassistants.fragment.PieMonthFragment;

/**
 * 
 * @Description: 某月图表分析
 * @author yzx
 * @date 2015-12-14
 */
public class ExpendMonthAnalysisActivity extends BaseActivity implements
		OnClickListener, OnPageChangeListener {
	private ImageButton backBtn;// 返回按钮
	private TextView titleTV;// 标题
	private PullRefreshLayout refreshLayout;// 下拉刷新
	private ViewPager viewPager;// 中间部分
	private UnderlinePageIndicator indicator;// 滑线
	private RadioButton barRB;// 柱形图
	private RadioButton pieRB;// 饼状图

	private BarMonthFragment barFragment;// 柱状图
	private PieMonthFragment pieFragment;// 饼图
	private CustomPagerAdapter adapter;// viewpage‘s adapter
	public static final int TAB_BAR = 0;// 柱形图
	public static final int TAB_PIE = 1;// 饼状图

	private String month;// 月份
	private Handler handler;
	private Boolean isBar;// 柱形图
	private Boolean isPie;// 饼图

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expend_month_analysis);
		init();
		initView();
		setListener();
		initViewPage();
		initUnderLine();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化上个界面传来的值
	 */
	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			month = intent
					.getStringExtra(CommonConstant.TO_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY);
		}
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.expend_month_analysis_back_btn);
		titleTV = (TextView) findViewById(R.id.expend_month_analysis_title_tv);
		refreshLayout = (PullRefreshLayout) findViewById(R.id.expend_month_analysis_pull_refresh_layout);
		viewPager = (ViewPager) findViewById(R.id.expend_month_analysis_viewpager);
		indicator = (UnderlinePageIndicator) findViewById(R.id.expend_month_analysis_indicator);
		barRB = (RadioButton) findViewById(R.id.expend_month_analysis_bar_rb);
		pieRB = (RadioButton) findViewById(R.id.expend_month_analysis_pie_rb);
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
				barFragment.queryExpend();
				pieFragment.getDataFromService();
			}

		});
		barRB.setOnClickListener(this);
		pieRB.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		isBar = false;
		isPie = false;
		handler = new Handler();
		titleTV.setText(month.substring(month.indexOf("-") + 1, month.length())
				+ "月支出分析");
	}

	/**
	 * @Description: 初始化viewpage
	 */
	@SuppressWarnings("deprecation")
	private void initViewPage() {
		Bundle bundle = new Bundle();
		bundle.putString(
				CommonConstant.FROM_EXPEND_MONTH_ANALYSIS_ACTIVITY_KEY, month);
		FragmentManager fm = getSupportFragmentManager();
		List<Fragment> fragments = new ArrayList<Fragment>();
		// Activity传值到Fragment通过fragment.setArguments(bundle);
		barFragment = new BarMonthFragment();
		barFragment.setArguments(bundle);
		fragments.add(barFragment);
		pieFragment = new PieMonthFragment();
		pieFragment.setArguments(bundle);
		fragments.add(pieFragment);
		adapter = new CustomPagerAdapter(fm, fragments);
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(TAB_BAR);
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
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.expend_month_analysis_back_btn: {// 返回
			finish();
			break;
		}
		case R.id.expend_month_analysis_bar_rb: {// 柱形图
			viewPager.setCurrentItem(TAB_BAR);
			break;
		}
		case R.id.expend_month_analysis_pie_rb: {// 饼状图
			viewPager.setCurrentItem(TAB_PIE);
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
		case TAB_BAR: {// 柱形图
			barRB.setChecked(true);
			break;
		}
		case TAB_PIE: {// 饼状图
			pieRB.setChecked(true);
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
	public void refreshOvew(final int chartType) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (BarMonthFragment.CHART_BAR == chartType) {
					isBar = true;
				}
				if (PieMonthFragment.CHART_PIE_MONTH == chartType) {
					isPie = true;
				}
				if (isBar && isPie) {
					isBar = false;
					isPie = false;
					refreshLayout.setRefreshing(false);
				}
			}
		});
	}
}
