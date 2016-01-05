package com.yzx.lifeassistants.fragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.BorrowAndLendActivity;
import com.yzx.lifeassistants.adapter.LendListAdapter;
import com.yzx.lifeassistants.bean.LendDetail;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CustomDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @Description: 借出明细
 * @author: yzx
 * @time: 2015-9-23 下午4:26:56
 */
public class LendFragment extends Fragment implements OnItemClickListener {
	private CustomDialog dialog;
	private ListView lendLV;//
	private List<LendDetail> dataList;//
	private LendListAdapter adapter;//
	private PullToRefreshListView lendPTRLV;//
	private ILoadingLayout lendILL;//
	private static final int STATE_REFRESH = 0;// 下拉刷新
	private static final int STATE_MORE = 1;// 加载更多
	private int limit = 5; // 每页的数据是5条
	private int curPage = 0; // 当前页的编号，从0开始

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lend, container, false);
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
		dialog = new CustomDialog(getActivity());
		lendPTRLV = (PullToRefreshListView) getView().findViewById(
				R.id.lend_list_listview);
		lendILL = lendPTRLV.getLoadingLayoutProxy();
		lendILL.setLastUpdatedLabel("");
		lendILL.setPullLabel("继续拖动");
		lendILL.setRefreshingLabel("正在装载数据···");
		lendILL.setReleaseLabel("放开装载更多");
		// 滑动监听
		lendPTRLV.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem == 0) {
					lendILL.setLastUpdatedLabel("");
					lendILL.setPullLabel("下拉刷新");
					lendILL.setRefreshingLabel("加载中···");
					lendILL.setReleaseLabel("释放刷新");
				} else if (firstVisibleItem + visibleItemCount == totalItemCount) {
					lendILL.setLastUpdatedLabel("");
					lendILL.setPullLabel("继续拖动");
					lendILL.setRefreshingLabel("正在装载数据···");
					lendILL.setReleaseLabel("放开装载更多");
				}
			}
		});
		// 下拉刷新监听
		lendPTRLV.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 下拉刷新(从第一页开始装载数据)
				dialog.show();
				queryLends(0, STATE_REFRESH);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// 上拉加载更多(加载下一页数据)
				dialog.show();
				queryLends(curPage, STATE_MORE);
			}

		});
		lendLV = lendPTRLV.getRefreshableView();
		lendLV.setOnItemClickListener(this);

	}

	/**
	 * @Description: 初始化数据
	 */
	private void initData() {
		dataList = new ArrayList<LendDetail>();
		adapter = new LendListAdapter(getActivity(), dataList);
		lendLV.setAdapter(adapter);
	}

	/**
	 * @Description: 从 Pause 状态转换到 Active 状态时被调用
	 */
	@Override
	public void onResume() {
		super.onResume();
		dialog.show();
		queryLends(0, STATE_REFRESH);
	}

	//
	// @Override
	// public void setUserVisibleHint(boolean isVisibleToUser) {
	// super.setUserVisibleHint(isVisibleToUser);
	// if (isVisibleToUser) {// 相当于onResume
	// dialog.show();
	// queryLends(0, STATE_REFRESH);
	// } else {// 相当于onPause
	//
	// }
	// }

	/**
	 * @Description: 查询出借出列表
	 */
	private void queryLends(final int page, final int actionType) {
		BmobQuery<LendDetail> query = new BmobQuery<LendDetail>();
		// 查询出该登陆用户的借入信息
		query.addWhereEqualTo("username", GlobalParams.userInfo.getUsername());
		query.order("time");// 按照时间降序
		query.setLimit(limit);
		query.setSkip(page * limit);
		query.findObjects(getActivity(), new FindListener<LendDetail>() {
			@Override
			public void onSuccess(List<LendDetail> list) {// 查询成功
				dialog.dismiss();
				// 若list.size()>0只剩一项时删不掉
				if (list.size() > -1) {
					if (actionType == STATE_REFRESH) {
						// 当是下拉刷新操作时，将当前页的编号重置为0，清空，重新添加
						curPage = 0;
						dataList.clear();
					}
					for (LendDetail lendInfo : list) {
						dataList.add(lendInfo);
					}
					// 这里在每次加载完数据后，将当前页码+1
					// 这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
					curPage++;
				}
				if (actionType == STATE_MORE && list.size() < 1) {
					ToastUtils.showToast("暂时还未有更多的借出信息");
				}
				if (actionType == STATE_REFRESH && list.size() < 1) {
					if (getUserVisibleHint()) {
						ToastUtils.showToast("暂时还未有借出信息");
					}
				}
				lendPTRLV.onRefreshComplete();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int code, String message) {// 查询失败
				dialog.dismiss();
				if (code == 101) {
					ToastUtils.showToast("暂时还未有借出信息");
				} else {
					ToastUtils.showToast("查询失败" + code + ":" + message);
				}
				lendPTRLV.onRefreshComplete();
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
		Intent intent = new Intent(getActivity(), BorrowAndLendActivity.class);
		intent.putExtra("from", CommonConstant.FROM_LENDFRAGMENT);
		Bundle bundle = new Bundle();
		// 需要注意position - 1 不然会数组越界
		bundle.putSerializable("lendInfo", dataList.get(position - 1));
		intent.putExtras(bundle);
		// 嵌套的子Fragment需要使用getParentFragment().startActivityForResult
		// 并在父Fragment 处理onActivityResult返回结果
		getParentFragment().startActivityForResult(intent,
				CommonConstant.REQUESTCODE_SEE_LEND);
	}

}
