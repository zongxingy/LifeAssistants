package com.yzx.lifeassistants.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.PullRefreshLayout.OnRefreshListener;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.AddExpendActivity;
import com.yzx.lifeassistants.activity.ExpendPieAnalysisActivity;
import com.yzx.lifeassistants.activity.SetAlimonyActivity;
import com.yzx.lifeassistants.bean.ExpendByRange;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.model.ISelectExpendByRange;
import com.yzx.lifeassistants.model.callback.ISelectExpendByRangeCB;
import com.yzx.lifeassistants.model.impl.SelectExpendByRangeImpl;
import com.yzx.lifeassistants.utils.CalendarUtil;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;
import com.yzx.lifeassistants.view.widget.CircularPoints;

/**
 * @Description: 记账能手
 * @author: yzx
 * @time: 2015-11-27 下午2:33:16
 */
public class ChargeUpFragment extends Fragment implements OnClickListener {

	private TextView titleTV;// 标题
	private ImageButton analysisBtn;// 图表分析
	private PullRefreshLayout refreshLayout;// 下拉刷新
	private TextView monthTV;// 月份
	private TextView yearTV;// 年份
	private LinearLayout percentLL;//
	private RelativeLayout percentRL;//
	private CircularPoints circularPoints;// 百分比
	private LinearLayout alimonyLL;// 设置生活费
	private TextView alimonyTV;// 生活费
	private LinearLayout balanceLL;// 余额
	private ImageView balanceIV;// 余额图标
	private TextView balanceTV;// 余额
	private ImageButton addBtn;// 添加

	private CircularLoadingDialog dialog;//
	private int alimony;// 生活费
	private int balance;// 余额
	private int balanceLevel;// 余额等级

	private Handler handler;//
	private ISelectExpendByRangeCB selectCallBack;// 回调
	private ISelectExpendByRange selectExpendByRange;// 查询
	private List<ExpendByRange> dateList;//

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_charge_up, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		setListenter();
		initData();
		initMaterialRipple();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		titleTV = (TextView) getView().findViewById(R.id.top_title_tv);
		analysisBtn = (ImageButton) getView().findViewById(
				R.id.top_analysis_btn);
		refreshLayout = (PullRefreshLayout) getView().findViewById(
				R.id.charge_up_pull_refresh_layout);
		monthTV = (TextView) getView().findViewById(R.id.charge_up_month_tv);
		yearTV = (TextView) getView().findViewById(R.id.charge_up_year_tv);
		percentRL = (RelativeLayout) getView().findViewById(
				R.id.charge_up_percent_rl);
		percentLL = (LinearLayout) getView().findViewById(
				R.id.charge_up_percent_ll);
		alimonyLL = (LinearLayout) getView().findViewById(
				R.id.charge_up_alimony_ll);
		alimonyTV = (TextView) getView()
				.findViewById(R.id.charge_up_alimony_tv);
		balanceLL = (LinearLayout) getView().findViewById(
				R.id.charge_up_balance_ll);
		balanceIV = (ImageView) getView().findViewById(
				R.id.charge_up_balance_iv);
		balanceTV = (TextView) getView()
				.findViewById(R.id.charge_up_balance_tv);
		addBtn = (ImageButton) getView().findViewById(R.id.charge_up_add_btn);
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListenter() {
		analysisBtn.setOnClickListener(this);
		// 下拉刷新
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getDataFromService();
			}
		});
		alimonyLL.setOnClickListener(this);
		addBtn.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		titleTV.setText("记账能手");
		analysisBtn.setVisibility(View.VISIBLE);
		String time = CalendarUtil.getCurrentTime("yyyy-MM");
		monthTV.setText(time.substring(time.indexOf("-") + 1, time.length()));
		yearTV.setText("/" + time.substring(0, time.indexOf("-")));

		dialog = new CircularLoadingDialog(getActivity());

		setAlimony();

		dateList = new ArrayList<ExpendByRange>();
		handler = new Handler();
		selectCallBack = new ISelectExpendByRangeCB() {

			@Override
			public void selectSuccess(final List<ExpendByRange> expendList,
					String range) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						LogcatUtils.i("查询本月总支出金额成功");
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
						if (null == expendList || 1 > expendList.size()) {

						} else {
							dateList.clear();
							dateList.add(expendList.get(0));
						}
						setBalance();
					}
				});
			}

			@Override
			public void selectError(final int error, final String range) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						LogcatUtils.e("查询本月总支出金额失败：" + error + " " + range);
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
						switch (error) {
						case ISelectExpendByRangeCB.NET_ERROR: {
							ToastUtils.showToast("网络超时，请检查您的手机网络~");
							break;
						}
						case ISelectExpendByRangeCB.SELECT_ERROR: {
							ToastUtils.showToast("获取失败，请检查您的手机网络~");
							break;
						}
						case ISelectExpendByRangeCB.RESULR_NULL: {
							LogcatUtils.e("获取本月支出为空");
							dateList.clear();
							setBalance();// 设置余额
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
		getDataFromService();
	}

	/**
	 * 
	 * @Description: 设置生活费指标
	 */
	private void setAlimony() {
		if (null == GlobalParams.userInfo.getAlimony()) {
			alimony = 1500;
		} else {
			alimony = GlobalParams.userInfo.getAlimony();
		}
		alimonyTV.setText(alimony + "元");
	}

	/**
	 * 
	 * @Description: 从服务器获取数据
	 */
	private void getDataFromService() {
		// 获取本月支出总额
		selectExpendByRange.selectExpendByRange(
				GlobalParams.userInfo.getUsername(),
				CalendarUtil.getCurrentTime("yyyy-MM"), "month");
	}

	/**
	 * 
	 * @Description: 设置余额
	 */
	private void setBalance() {
		balance = alimony;
		if (dateList.size() > 0) {
			balance = alimony - dateList.get(0).getMoney();
		}
		// 先移除该百分比控件再添加
		if (null != circularPoints) {
			percentLL.removeView(circularPoints);
		}
		balanceTV.setText(balance + "元");
		if (0 == alimony) {
			balanceLevel = 1;
			balanceIV.setBackgroundResource(R.drawable.icon_balance_4);
			circularPoints = new CircularPoints(getActivity(), 0,
					CircularPoints.COLOR_RED);
		} else {
			int percent = (int) (balance * 100 / alimony);
			int scale = (balance * 4) / alimony;
			if (scale >= 3) {
				balanceLevel = 4;
				balanceIV.setBackgroundResource(R.drawable.icon_balance_1);
				circularPoints = new CircularPoints(getActivity(), percent,
						CircularPoints.COLOR_GREEN);
			} else if (scale >= 2) {
				balanceLevel = 3;
				balanceIV.setBackgroundResource(R.drawable.icon_balance_2);
				circularPoints = new CircularPoints(getActivity(), percent,
						CircularPoints.COLOR_YELLOW);
			} else if (scale >= 1) {
				balanceLevel = 2;
				balanceIV.setBackgroundResource(R.drawable.icon_balance_3);
				circularPoints = new CircularPoints(getActivity(), percent,
						CircularPoints.COLOR_ORANGE);
			} else {
				balanceLevel = 1;
				balanceIV.setBackgroundResource(R.drawable.icon_balance_4);
				circularPoints = new CircularPoints(getActivity(), percent,
						CircularPoints.COLOR_RED);
			}
		}
		percentLL.addView(circularPoints);

	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		List<View> views = new ArrayList<View>();
		views.add(percentRL);
		views.add(alimonyLL);
		views.add(balanceLL);
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
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.charge_up_alimony_ll: {// 设置生活费指标
			Intent intent = new Intent(getActivity(), SetAlimonyActivity.class);
			intent.putExtra("alimony", alimony);
			startActivityForResult(intent,
					CommonConstant.REQUESTCODE_SET_ALIMONY);
			break;
		}
		case R.id.charge_up_add_btn: {// 添加支出记录
			Intent intent = new Intent(getActivity(), AddExpendActivity.class);
			intent.putExtra(
					CommonConstant.TO_ADD_EXPEND_ACTIVITY_BALANCE_LEVEL,
					balanceLevel);
			intent.putExtra(CommonConstant.TO_ADD_EXPEND_ACTIVITY_IS_NEW, true);
			startActivityForResult(intent,
					CommonConstant.REQUESTCODE_ADD_EXPEND);
			break;
		}
		case R.id.top_analysis_btn: {// 支出图表分析
			Intent intent = new Intent(getActivity(),
					ExpendPieAnalysisActivity.class);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 设置生活费指标
		if (CommonConstant.REQUESTCODE_SET_ALIMONY == requestCode) {
			if (CommonConstant.RESULTCODE_SET_ALIMONY_SUCCESS == resultCode) {
				setAlimony();
				setBalance();
				ToastUtils.showToast("设置成功");
			}
		}
		// 添加支出记录
		if (CommonConstant.REQUESTCODE_ADD_EXPEND == requestCode) {
			if (CommonConstant.RESULTCODE_ADD_EXPEND_SUCCESS == resultCode) {
				dialog.show();
				getDataFromService();
				ToastUtils.showToast("添加成功");
			}
		}
	}

}
