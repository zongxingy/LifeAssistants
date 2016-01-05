package com.yzx.lifeassistants.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.adapter.MyDialogAdapter;
import com.yzx.lifeassistants.adapter.SecondHandListAdapter;
import com.yzx.lifeassistants.base.BaseActivity;
import com.yzx.lifeassistants.bean.SecondHandGoods;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 闲置物品搜索界面
 * @author: yzx
 * @time: 2015-11-26 下午3:20:58
 */
public class SecondHandSearchActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private ImageButton backBtn;// 返回按钮
	private ImageButton classifyBtn;// 分类按钮
	private MaterialEditText contentET;// 搜索内容
	private ImageView searchBtn;// 搜索按钮
	private ListView secondHandLV;// 闲置物品列表
	private PullToRefreshListView secondHandPTRLV;// 刷新
	private ILoadingLayout secondHandILL;// 加载

	private NormalListDialog classifyDialog;// 分类弹框
	private List<String> classifyList;// 分类列表
	private CircularLoadingDialog dialog;// 加载
	private SecondHandListAdapter adapter;// 闲置物品列表适配器
	private List<SecondHandGoods> dataList;// 闲置物品数据列表
	private List<Boolean> isList;// 是否本人
	private static final int STATE_REFRESH = 0;// 下拉刷新
	private static final int STATE_MORE = 1;// 加载更多
	private int limit = 5; // 每页的数据是5条
	private int curPage = 0; // 当前页的编号，从0开始
	private String type;// 搜索类型
	private String content;// 搜索内容

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_hand_search);
		initView();
		initDialog();
		setListener();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.second_hand_search_back_btn);
		classifyBtn = (ImageButton) findViewById(R.id.second_hand_search_classify_btn);
		contentET = (MaterialEditText) findViewById(R.id.second_hand_search_content_et);
		searchBtn = (ImageView) findViewById(R.id.second_hand_search_search_btn);
		secondHandPTRLV = (PullToRefreshListView) findViewById(R.id.second_hand_search_listview);
		secondHandILL = secondHandPTRLV.getLoadingLayoutProxy();
		secondHandILL.setLastUpdatedLabel("");
		secondHandILL.setPullLabel("继续拖动");
		secondHandILL.setRefreshingLabel("正在装载数据···");
		secondHandILL.setReleaseLabel("放开装载更多");
		secondHandLV = secondHandPTRLV.getRefreshableView();
	}

	/**
	 * 
	 * @Description: 初始化弹框
	 */
	private void initDialog() {
		BaseAnimatorSet mBasIn = new BounceTopEnter();
		BaseAnimatorSet mBasOut = new SlideBottomExit();
		classifyList = new ArrayList<String>();
		classifyList.add("生活/鞋子");
		classifyList.add("生活/车辆");
		classifyList.add("生活/化妆");
		classifyList.add("生活/其他");
		classifyList.add("电子/手机");
		classifyList.add("电子/相机");
		classifyList.add("电子/电脑");
		classifyList.add("电子/其他");
		classifyList.add("学习/CET");
		classifyList.add("学习/考研");
		classifyList.add("学习/考公");
		classifyList.add("学习/其他");
		MyDialogAdapter adapter = new MyDialogAdapter(this, classifyList);
		classifyDialog = new NormalListDialog(this, adapter);
		classifyDialog.title("请选择分类")//
				.showAnim(mBasIn)//
				.dismissAnim(mBasOut);//
		classifyDialog.setOnOperItemClickL(new OnOperItemClickL() {
			@Override
			public void onOperItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				classifyDialog.dismiss();
				type = classifyList.get(position);
				contentET.setHint(type);
				contentET.setFloatingLabelText(type);
			}
		});
	}

	/**
	 * 
	 * @Description: 设置监听
	 */
	private void setListener() {
		backBtn.setOnClickListener(this);
		classifyBtn.setOnClickListener(this);
		searchBtn.setOnClickListener(this);
		// 滑动监听
		secondHandPTRLV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
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
						if (null == content || "".equals(content)) {
							secondHandPTRLV.onRefreshComplete();
							ToastUtils.showToast("请输入要搜索的内容~");
							return;
						}
						// 下拉刷新(从第一页开始装载数据)
						dialog.show();
						querySecondHands(0, STATE_REFRESH);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (null == content || "".equals(content)) {
							secondHandPTRLV.onRefreshComplete();
							ToastUtils.showToast("请输入要搜索的内容~");
							return;
						}
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
		contentET.setPaddings(DensityUtils.sp2px(this, 32), 0, 0, 0);
		dialog = new CircularLoadingDialog(this);
		dataList = new ArrayList<SecondHandGoods>();
		isList = new ArrayList<Boolean>();
		adapter = new SecondHandListAdapter(secondHandLV, this, dataList,
				isList);
		secondHandLV.setAdapter(adapter);
	}

	/**
	 * @Description: 查询出闲置物品列表
	 */
	private void querySecondHands(int page, final int actionType) {
		// 查询条件1 类型
		BmobQuery<SecondHandGoods> typeQuery = new BmobQuery<SecondHandGoods>();
		typeQuery.addWhereEqualTo("type", type);
		// 查询条件2 标题
		BmobQuery<SecondHandGoods> titleQuery = new BmobQuery<SecondHandGoods>();
		titleQuery.addWhereContains("title", content);
		// 查询条件3 描述
		BmobQuery<SecondHandGoods> describeQuery = new BmobQuery<SecondHandGoods>();
		describeQuery.addWhereContains("describe", content);
		// or(条件2,条件3)
		List<BmobQuery<SecondHandGoods>> orQueries = new ArrayList<BmobQuery<SecondHandGoods>>();
		orQueries.add(titleQuery);
		orQueries.add(describeQuery);
		BmobQuery<SecondHandGoods> orQuery = new BmobQuery<SecondHandGoods>();
		BmobQuery<SecondHandGoods> contentQuery = orQuery.or(orQueries);
		// and(条件1,or(条件2,条件3))
		List<BmobQuery<SecondHandGoods>> andQueries = new ArrayList<BmobQuery<SecondHandGoods>>();
		andQueries.add(typeQuery);
		andQueries.add(contentQuery);
		BmobQuery<SecondHandGoods> query = new BmobQuery<SecondHandGoods>();
		query.order("-updatedAt");// 按照修改时间升序
		query.and(andQueries);
		query.setLimit(limit);
		query.setSkip(page * limit);
		query.findObjects(this, new FindListener<SecondHandGoods>() {
			@Override
			public void onSuccess(List<SecondHandGoods> list) {// 查询成功
				LogcatUtils.i("查询闲置列表信息成功");
				dialog.dismiss();
				secondHandPTRLV.setVisibility(View.VISIBLE);
				secondHandLV.setVisibility(View.VISIBLE);
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
				secondHandPTRLV.setVisibility(View.VISIBLE);
				secondHandLV.setVisibility(View.VISIBLE);
				switch (code) {
				case 101: {
					ToastUtils.showToast("查询结果为空~");
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
		case R.id.second_hand_search_back_btn: {// 返回
			finish();
			break;
		}
		case R.id.second_hand_search_classify_btn: {// 分类
			classifyDialog.show();
			classifyDialog.getWindow().setGravity(Gravity.CENTER);
			break;
		}
		case R.id.second_hand_search_search_btn: {// 搜索
			content = contentET.getText().toString().replace(" ", "");
			if ("".equals(content) || null == content) {
				ToastUtils.showToast("请输入要搜索的内容~");
			} else {
				dialog.show();
				querySecondHands(0, STATE_REFRESH);
			}
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
