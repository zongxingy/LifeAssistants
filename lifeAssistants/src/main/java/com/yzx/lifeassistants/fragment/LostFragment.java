package com.yzx.lifeassistants.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.LostAndFindActivity;
import com.yzx.lifeassistants.adapter.LostListAdapter;
import com.yzx.lifeassistants.bean.LostThing;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

/**
 * @Description: 失物找寻
 * @author: yzx
 * @time: 2015-9-15 上午9:14:42
 */
public class LostFragment extends Fragment implements OnItemClickListener {
	private CircularLoadingDialog dialog;
	private ListView lostLV;//
	private List<LostThing> dataList;//
	private List<Boolean> isList;//
	private LostListAdapter adapter;//
	private PullToRefreshListView lostPTRLV;//
	private ILoadingLayout lostILL;//
	private static final int STATE_REFRESH = 0;// 下拉刷新
	private static final int STATE_MORE = 1;// 加载更多
	private int limit = 5; // 每页的数据是5条
	private int curPage = 0; // 当前页的编号，从0开始

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lost, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	/**
	 * @Description: 初始化控件
	 */
	private void initView() {
		dialog = new CircularLoadingDialog(getActivity());
		lostPTRLV = (PullToRefreshListView) getView().findViewById(
				R.id.lost_list_listview);
		lostILL = lostPTRLV.getLoadingLayoutProxy();
		lostILL.setLastUpdatedLabel("");
		lostILL.setPullLabel("继续拖动");
		lostILL.setRefreshingLabel("正在装载数据···");
		lostILL.setReleaseLabel("放开装载更多");
		// 滑动监听
		lostPTRLV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
					lostILL.setLastUpdatedLabel("");
					lostILL.setPullLabel("下拉刷新");
					lostILL.setRefreshingLabel("加载中···");
					lostILL.setReleaseLabel("释放刷新");
				} else if (firstVisibleItem + visibleItemCount == totalItemCount) {
					lostILL.setLastUpdatedLabel("");
					lostILL.setPullLabel("继续拖动");
					lostILL.setRefreshingLabel("正在装载数据···");
					lostILL.setReleaseLabel("放开装载更多");
				}
			}
		});
		// 下拉刷新监听
		lostPTRLV.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新(从第一页开始装载数据)
				dialog.show();
				queryLosts(0, STATE_REFRESH);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 上拉加载更多(加载下一页数据)
				dialog.show();
				queryLosts(curPage, STATE_MORE);
			}

		});
		lostLV = lostPTRLV.getRefreshableView();
		lostLV.setOnItemClickListener(this);

	}

	/**
	 * @Description: 初始化数据
	 */
	private void initData() {
		dataList = new ArrayList<LostThing>();
		isList = new ArrayList<Boolean>();
		adapter = new LostListAdapter(lostLV, getActivity(), dataList, isList);
		lostLV.setAdapter(adapter);
	}

	/**
	 * @Description: 从 Pause 状态转换到 Active 状态时被调用
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (getUserVisibleHint()) {// 当该Fragment显示时调用
			dialog.show();
			queryLosts(0, STATE_REFRESH);
		}

	}

	/**
	 * @Description: 查询出失物列表
	 */
	private void queryLosts(final int page, final int actionType) {
		BmobQuery<LostThing> query = new BmobQuery<LostThing>();
		query.order("-updatedAt");// 按照修改时间升序
		query.setLimit(limit);
		query.setSkip(page * limit);
		query.findObjects(getActivity(), new FindListener<LostThing>() {
			@Override
			public void onSuccess(List<LostThing> list) {// 查询成功
				LogcatUtils.i("查询失物列表信息成功");
				dialog.dismiss();
				// 若list.size()>0只剩一项时删不掉
				if (list.size() > -1) {
					if (actionType == STATE_REFRESH) {
						// 当是下拉刷新操作时，将当前页的编号重置为0，清空，重新添加
						curPage = 0;
						dataList.clear();
						isList.clear();
					}
					for (LostThing lostInfo : list) {
						dataList.add(lostInfo);
						if (GlobalParams.userInfo.getUsername().equals(
								lostInfo.getUsername())) {
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
					ToastUtils.showToast("暂时还未有更多的失物信息~");
				}
				if (actionType == STATE_REFRESH && list.size() < 1) {
					ToastUtils.showToast("暂时还未有失物信息~");
				}
				lostPTRLV.onRefreshComplete();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int code, String message) {// 查询失败
				LogcatUtils.e("查询失物列表信息失败：" + code + " " + message);
				dialog.dismiss();
				if (getUserVisibleHint()) {
					switch (code) {
					case 101: {
						ToastUtils.showToast("暂时还未有失物信息~");
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
				}
				lostPTRLV.onRefreshComplete();
				adapter.notifyDataSetChanged();

			}
		});

	}

	/**
	 * @Description: 点击后可查看详情
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = new Intent(getActivity(), LostAndFindActivity.class);
		intent.putExtra("from", CommonConstant.FROM_LOSTFRAGMENT);
		Bundle bundle = new Bundle();
		// 需要注意position - 1 不然会数组越界
		bundle.putSerializable("lostInfo", dataList.get(position - 1));
		intent.putExtras(bundle);
		// 嵌套的子Fragment需要使用getParentFragment().startActivityForResult
		// 并在父Fragment 处理onActivityResult返回结果
		getParentFragment().startActivityForResult(intent,
				CommonConstant.REQUESTCODE_SEE_LOST);
	}

}
