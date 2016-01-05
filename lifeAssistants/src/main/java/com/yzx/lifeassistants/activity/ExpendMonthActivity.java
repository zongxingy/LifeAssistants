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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.PullRefreshLayout.OnRefreshListener;
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
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;
import com.yzx.lifeassistants.view.widget.MySwipeMenuListView;

/**
 * @Description: 某月支出
 * @author: yzx
 * @time: 2015-12-14 上午9:22:13
 */
public class ExpendMonthActivity extends BaseActivity implements
		OnClickListener {
	private ImageButton backBtn;// 返回按钮
	private TextView titleTV;// 标题
	private PullRefreshLayout refreshLayout;// 下拉刷新
	private MySwipeMenuListView expendLV;// 支出列表

	private String month;// 月份
	private List<ExpendByRange> dataList;// 支出记录列表
	private ExpendRangeAdapter adapter;// 适配器
	private CircularLoadingDialog dialog;
	private Handler handler;
	private ISelectExpendByRangeCB selectCallBack;// 回调
	private ISelectExpendByRange selectExpendByRange;// 查询

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expend_month);
		init();
		initView();
		setListener();
		initSwipeMenuListView();
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
					.getStringExtra(CommonConstant.TO_EXPEND_MONTH_ACTIVITY_KEY);
		}
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.expend_month_back_btn);
		titleTV = (TextView) findViewById(R.id.expend_month_title_tv);
		refreshLayout = (PullRefreshLayout) findViewById(R.id.expend_month_expend_pull_refresh_layout);
		expendLV = (MySwipeMenuListView) findViewById(R.id.expend_month_expend_lv);
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
					// open
					Intent intent = new Intent(ExpendMonthActivity.this,
							ExpendDateActivity.class);
					intent.putExtra(CommonConstant.TO_EXPEND_DATE_ACTIVITY_KEY,
							item.getRange());
					startActivity(intent);
					break;
				default:

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
				openItem.setWidth(DensityUtils.dp2px(ExpendMonthActivity.this,
						90));
				// set item title
				openItem.setTitle("打开");
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);

				// // create "delete" item
				// SwipeMenuItem deleteItem = new SwipeMenuItem(
				// getApplicationContext());
				// // set item background
				// deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
				// 0x3F, 0x25)));
				// // set item width
				// deleteItem.setWidth(DensityUtils.dp2px(
				// ExpendMonthActivity.this, 90));
				// // set a icon
				// deleteItem.setIcon(R.drawable.ic_delete);
				// // add to menu
				// menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		expendLV.setMenuCreator(creator);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		titleTV.setText(month.substring(month.indexOf("-") + 1, month.length())
				+ "月支出");
		dialog = new CircularLoadingDialog(this);
		handler = new Handler();
		selectCallBack = new ISelectExpendByRangeCB() {

			@Override
			public void selectSuccess(final List<ExpendByRange> expendList,
					String range) {

				handler.post(new Runnable() {// 成功

					@Override
					public void run() {
						LogcatUtils.i("查询该月的支出记录成功");
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
						dataList.clear();
						dataList.addAll(expendList);
						sortData();// 排序
						adapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void selectError(final int code, final String range) {

				handler.post(new Runnable() {// 失败

					@Override
					public void run() {
						LogcatUtils.e("查询该月的支出记录失败：" + code + " " + range);
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
						switch (code) {
						case ISelectExpendByRangeCB.RESULR_NULL: {
							dataList.clear();
							expendLV.setVisibility(View.VISIBLE);
							adapter.notifyDataSetChanged();
							ToastUtils.showToast("该月无支出记录~");
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
	 * @Description: 查询支出列表
	 */
	private void queryExpend() {
		selectExpendByRange.selectExpendByRange(
				GlobalParams.userInfo.getUsername(), month, "date");// 查询
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
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.expend_month_back_btn: {// 返回
			finish();
			break;
		}

		default:
			break;
		}
	}
}
