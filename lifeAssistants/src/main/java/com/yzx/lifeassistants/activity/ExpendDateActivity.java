package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.baoyz.widget.PullRefreshLayout.OnRefreshListener;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.ExpendDateAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.ExpendInfo;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;
import com.yzx.lifeassistants.view.widget.MySwipeMenuListView;

/**
 * @Description: 某日支出
 * @author: yzx
 * @time: 2015-12-14 上午9:22:13
 */
public class ExpendDateActivity extends BaseActivity implements OnClickListener {
	private ImageButton backBtn;// 返回按钮
	private TextView titleTV;// 标题
	private PullRefreshLayout refreshLayout;// 下拉刷新
	private MySwipeMenuListView expendLV;// 支出列表

	private String date;// 月份
	private List<ExpendInfo> dataList;// 支出记录列表
	private ExpendDateAdapter adapter;// 适配器
	private CircularLoadingDialog dialog;
	private CircularLoadingDialog deleteDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expend_date);
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
			date = intent
					.getStringExtra(CommonConstant.TO_EXPEND_DATE_ACTIVITY_KEY);
		}
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.expend_date_back_btn);
		titleTV = (TextView) findViewById(R.id.expend_date_title_tv);
		refreshLayout = (PullRefreshLayout) findViewById(R.id.expend_date_expend_pull_refresh_layout);
		expendLV = (MySwipeMenuListView) findViewById(R.id.expend_date_expend_lv);
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
				ExpendInfo item = dataList.get(position);
				switch (index) {
				case 0:
					// open
					Intent intent = new Intent(ExpendDateActivity.this,
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
					deleteDialog.show();
					item.delete(ExpendDateActivity.this, new DeleteListener() {

						@Override
						public void onSuccess() {
							LogcatUtils.i("删除该支出记录成功");
							deleteDialog.dismiss();
							ToastUtils.showToast("删除成功~");
							dialog.show();
							queryExpend();
						}

						@Override
						public void onFailure(int code, String message) {
							LogcatUtils.e("删除该支出记录失败：" + code + " " + message);
							deleteDialog.dismiss();
							switch (code) {
							case 9010: {// 网络超时
								ToastUtils.showToast("网络超时，请检查您的手机网络~");
								break;
							}
							case 9016: {// 无网络连接，请检查您的手机网络
								ToastUtils.showToast("无网络连接，请检查您的手机网络~");
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
	 * @Description: 初始化能左划出打开/删除的ListView
	 */
	private void initSwipeMenuListView() {
		dataList = new ArrayList<ExpendInfo>();
		adapter = new ExpendDateAdapter(this, dataList);
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
				openItem.setWidth(DensityUtils.dp2px(ExpendDateActivity.this,
						90));
				// set item title
				openItem.setTitle("打开");
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
				deleteItem.setWidth(DensityUtils.dp2px(ExpendDateActivity.this,
						90));
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
	 * @Description: 初始化数据
	 */
	private void initData() {
		titleTV.setText(date.substring(date.lastIndexOf("-") + 1, date.length())
				+ "号支出");
		dialog = new CircularLoadingDialog(this);
		deleteDialog = new CircularLoadingDialog(this);
		dialog.show();
		queryExpend();
	}

	/**
	 * 
	 * @Description: 查询支出列表
	 */
	private void queryExpend() {
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
				refreshLayout.setRefreshing(false);
				dialog.dismiss();
				dataList.clear();
				if (null == expendInfos || 1 > expendInfos.size()) {
					expendLV.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
					String time = date.substring(date.indexOf("-") + 1,
							date.length()).replace("-", "月")
							+ "号";
					ToastUtils.showToast("暂无" + time + "的支出记录");
				} else {
					expendLV.setVisibility(View.VISIBLE);
					dataList.addAll(expendInfos);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onError(int code, String message) {
				LogcatUtils.e("查询该天的支出记录失败：" + code + " " + message);
				refreshLayout.setRefreshing(false);
				dialog.dismiss();
				switch (code) {
				case 101: {// 无数据
					dataList.clear();
					expendLV.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
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
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.expend_date_back_btn: {// 返回
			finish();
			break;
		}

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 修改账单成功
		if (CommonConstant.RESULTCODE_MODIFY_EXPEND_SUCCESS == resultCode) {
			if (CommonConstant.REQUESTCODE_MODIFY_EXPEND == requestCode) {
				queryExpend();// 查询
			}
		}
	}
}
