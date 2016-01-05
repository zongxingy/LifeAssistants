package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.SecondHandListAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.SecondHandGoods;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 二手物品分类浏览界面
 * @author: yzx
 * @time: 2015-11-13 上午11:06:36
 */
public class SecondHandClassifyActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private String bigClass;// 大类名称
	private String classify;// 类别
	private String type;// 类型
	private ImageButton topBackBtn;// 顶部返回按钮
	private TextView topTitleTV;// 顶部标题

	private CircularLoadingDialog dialog;
	private ListView secondHandLV;// 闲置物品列表
	private PullToRefreshListView secondHandPTRLV;// 刷新
	private ILoadingLayout secondHandILL;// 加载

	private SecondHandListAdapter adapter;// 闲置物品列表适配器
	private List<SecondHandGoods> dataList;// 闲置物品数据列表
	private List<Boolean> isList;// 是否本人
	private static final int STATE_REFRESH = 0;// 下拉刷新
	private static final int STATE_MORE = 1;// 加载更多
	private int limit = 5; // 每页的数据是5条
	private int curPage = 0; // 当前页的编号，从0开始

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_hand_classify);
		init();
		initView();
		setListener();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化上个页面传来的数据
	 */
	private void init() {
		Intent intent = getIntent();
		if (null != intent) {
			bigClass = intent.getStringExtra("bigClass");
			classify = intent.getStringExtra("classify");
			type = bigClass + "/" + classify;
		}
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		topBackBtn = (ImageButton) findViewById(R.id.top_back_btn);
		topTitleTV = (TextView) findViewById(R.id.top_title_tv);

		dialog = new CircularLoadingDialog(this);
		secondHandPTRLV = (PullToRefreshListView) findViewById(R.id.second_hand_classify_listview);
		secondHandILL = secondHandPTRLV.getLoadingLayoutProxy();
		secondHandILL.setLastUpdatedLabel("");
		secondHandILL.setPullLabel("继续拖动");
		secondHandILL.setRefreshingLabel("正在装载数据···");
		secondHandILL.setReleaseLabel("放开装载更多");
		secondHandLV = secondHandPTRLV.getRefreshableView();
	}

	/**
	 * 
	 * @Description: 设置监听器
	 */
	private void setListener() {
		topBackBtn.setOnClickListener(this);

		// 滑动监听
		secondHandPTRLV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
					// lostILL.setLoadingDrawable(getResources().getDrawable(
					// R.drawable.rotate_circle_progress));
					secondHandILL.setLastUpdatedLabel("");
					secondHandILL.setPullLabel("下拉刷新");
					secondHandILL.setRefreshingLabel("加载中···");
					secondHandILL.setReleaseLabel("释放刷新");
				} else if (firstVisibleItem + visibleItemCount == totalItemCount) {
					secondHandILL.setLastUpdatedLabel("");
					secondHandILL.setPullLabel("继续拖动");
					secondHandILL.setRefreshingLabel("正在装载数据···");
					secondHandILL.setReleaseLabel("放开装载更多");
				}
			}
		});
		// 下拉刷新监听
		secondHandPTRLV
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 下拉刷新(从第一页开始装载数据)
						dialog.show();
						querySecondHands(0, STATE_REFRESH);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 上拉加载更多(加载下一页数据)
						dialog.show();
						querySecondHands(curPage, STATE_MORE);
					}

				});
		secondHandLV.setOnItemClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		topTitleTV.setText(type);

		dataList = new ArrayList<SecondHandGoods>();
		isList = new ArrayList<Boolean>();
		adapter = new SecondHandListAdapter(secondHandLV, this, dataList,
				isList);
		secondHandLV.setAdapter(adapter);
	}

	/**
	 * @Description: 从 Pause 状态转换到 Active 状态时被调用
	 */
	@Override
	public void onResume() {
		super.onResume();
		dialog.show();
		querySecondHands(0, STATE_REFRESH);

	}

	/**
	 * @Description: 查询出闲置物品列表
	 */
	private void querySecondHands(int page, final int actionType) {
		BmobQuery<SecondHandGoods> query = new BmobQuery<SecondHandGoods>();
		query.order("-updatedAt");// 按照修改时间升序
		query.addWhereEqualTo("type", type);
		query.setLimit(limit);
		query.setSkip(page * limit);
		query.findObjects(this, new FindListener<SecondHandGoods>() {
			@Override
			public void onSuccess(List<SecondHandGoods> list) {// 查询成功
				LogcatUtils.i("查询闲置列表信息成功");
				dialog.dismiss();
				// 若list.size()>0只剩一项时删不掉
				if (list.size() > -1) {
					if (actionType == STATE_REFRESH) {
						// 当是下拉刷新操作时，将当前页的编号重置为0，清空，重新添加
						curPage = 0;
						dataList.clear();
						isList.clear();
					}
					for (SecondHandGoods secondHandInfo : list) {
						dataList.add(secondHandInfo);
						if (GlobalParams.userInfo.getUsername().equals(
								secondHandInfo.getUsername())) {
							isList.add(true);
						} else {
							isList.add(false);
						}
					}
					// 这里在每次加载完数据后，将当前页码+1
					// 这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
					curPage++;
				}
				if (actionType == STATE_MORE && list.size() < 1) {
					ToastUtils.showToast("暂时还未有更多的闲置信息~");
				}
				if (actionType == STATE_REFRESH && list.size() < 1) {
					ToastUtils.showToast("暂时还未有闲置信息~");
				}
				secondHandPTRLV.onRefreshComplete();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int code, String message) {// 查询失败
				LogcatUtils.e("查询闲置列表信息失败：" + code + " " + message);
				dialog.dismiss();
				switch (code) {
				case 101: {
					ToastUtils.showToast("暂时还未有该分类的闲置信息~");
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
					ToastUtils.showToast("查询失败，请刷新重试~");
					break;
				}
				}
				secondHandPTRLV.onRefreshComplete();
				adapter.notifyDataSetChanged();

			}
		});
	}

	/**
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back_btn: {// 返回
			finish();
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		SecondHandGoods secondHandInfo = dataList.get(position - 1);
		Bundle bundle = new Bundle();
		bundle.putSerializable(
				CommonConstant.TO_SECOND_HAND_DETAIL_ACTIVITY_KEY,
				secondHandInfo);
		Intent intent = new Intent(this, SecondHandDetailActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
