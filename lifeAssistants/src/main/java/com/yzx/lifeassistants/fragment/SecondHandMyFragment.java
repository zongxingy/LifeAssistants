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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.AddSecondHandActivity;
import com.yzx.lifeassistants.adapter.SecondHandListAdapter;
import com.yzx.lifeassistants.bean.SecondHandGoods;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.LogcatUtils;
import com.yzx.lifeassistants.utils.ToastUtils;
import com.yzx.lifeassistants.view.widget.CircularLoadingDialog;

public class SecondHandMyFragment extends Fragment implements
		OnItemClickListener, OnItemLongClickListener {
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
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_second_hand_my,
				container, false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		setListener();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */

	private void initView() {
		dialog = new CircularLoadingDialog(getActivity());
		secondHandPTRLV = (PullToRefreshListView) getView().findViewById(
				R.id.second_hand_my_listview);
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
						dialog.setTitle("加载中···");
						dialog.show();
						querySecondHands(0, STATE_REFRESH);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 上拉加载更多(加载下一页数据)
						dialog.setTitle("加载中···");
						dialog.show();
						querySecondHands(curPage, STATE_MORE);
					}

				});
		secondHandLV.setOnItemClickListener(this);
		secondHandLV.setOnItemLongClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		dataList = new ArrayList<SecondHandGoods>();
		isList = new ArrayList<Boolean>();
		adapter = new SecondHandListAdapter(secondHandLV, getActivity(),
				dataList, isList);
		secondHandLV.setAdapter(adapter);
	}

	/**
	 * @Description: 从 Pause 状态转换到 Active 状态时被调用
	 */
	@Override
	public void onResume() {
		super.onResume();
		// if (getUserVisibleHint()) {// 当该Fragment显示时调用
		dialog.setTitle("加载中···");
		dialog.show();
		querySecondHands(0, STATE_REFRESH);
		// }

	}

	/**
	 * @Description: 查询出闲置物品列表
	 */
	private void querySecondHands(int page, final int actionType) {
		BmobQuery<SecondHandGoods> query = new BmobQuery<SecondHandGoods>();
		query.order("-updatedAt");// 按照修改时间升序
		query.addWhereEqualTo("username", GlobalParams.userInfo.getUsername());
		query.setLimit(limit);
		query.setSkip(page * limit);
		query.findObjects(getActivity(), new FindListener<SecondHandGoods>() {
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
				// 当用户可见时才显示
				if (getUserVisibleHint()) {
					if (actionType == STATE_MORE && list.size() < 1) {
						ToastUtils.showToast("暂时还未有更多的闲置信息~");
					}
					if (actionType == STATE_REFRESH && list.size() < 1) {
						ToastUtils.showToast("暂时还未有闲置信息~");
					}
				}
				secondHandPTRLV.onRefreshComplete();
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onError(int code, String message) {// 查询失败
				LogcatUtils.e("查询闲置列表信息失败：" + code + " " + message);
				dialog.dismiss();
				if (getUserVisibleHint()) {
					switch (code) {
					case 101: {
						ToastUtils.showToast("暂时还未有闲置信息~");
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
				secondHandPTRLV.onRefreshComplete();
				adapter.notifyDataSetChanged();

			}
		});
	}

	/**
	 * @Description: 按键监听 点击后可编辑
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		Intent intent = new Intent(getActivity(), AddSecondHandActivity.class);
		intent.putExtra("from", CommonConstant.FROM_SECOND_HAND_MY_FRAGMENT);
		Bundle bundle = new Bundle();
		// 需要注意position - 1 不然会数组越界
		bundle.putSerializable("secondHandInfo", dataList.get(position - 1));
		intent.putExtras(bundle);
		// 嵌套的子Fragment需要使用getParentFragment().startActivityForResult
		// 并在父Fragment 处理onActivityResult返回结果
		getParentFragment().startActivityForResult(intent,
				CommonConstant.REQUESTCODE_MODIFY_SECOND_HAND);
	}

	/**
	 * @Description: 长按提示是否删除
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			int position, long arg3) {
		showDeleteDialog(position);
		return true;
	}

	/**
	 * @Description: 弹出是否删除提示
	 */
	private void showDeleteDialog(final int position) {
		new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
				.setTitleText("你确定删除该闲置物品吗？")
				.setCancelText("取消")
				.setConfirmText("删除")
				.showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(
						new SweetAlertDialog.OnSweetClickListener() {

							@Override
							public void onClick(

							SweetAlertDialog sweetAlertDialog) {// 删除图片
								deleteSecondHand(position);
								adapter.notifyDataSetChanged();
								sweetAlertDialog.dismiss();
							}

						}).show();
	}

	/**
	 * 
	 * @Title: deleteSecondHand
	 */
	private void deleteSecondHand(int position) {
		dialog.setTitle("删除中···");
		dialog.show();
		SecondHandGoods secondHandInfo = new SecondHandGoods();
		secondHandInfo.setObjectId(dataList.get(position - 1).getObjectId());
		secondHandInfo.delete(getActivity(), new DeleteListener() {

			@Override
			public void onSuccess() {
				LogcatUtils.i("删除闲置信息成功");
				dialog.dismiss();
				ToastUtils.showToast("删除成功");
				dialog.setTitle("加载中···");
				dialog.show();
				querySecondHands(0, STATE_REFRESH);
			}

			@Override
			public void onFailure(int code, String message) {
				LogcatUtils.e("删除闲置信息失败：" + code + " " + message);
				dialog.dismiss();
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
	}

}
