package com.yzx.lifeassistants.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.bean.SecondHandGoods;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.DensityUtils;

/**
 * @Description: 二手物品联系信息
 * @author: yzx
 * @time: 2015-11-25 上午11:09:08
 */
public class SecondHandContactInfoFragment extends Fragment {

	private SecondHandGoods secondHandInfo;// 闲置物品信息

	private MaterialEditText bargainTV;// 是否议价
	private MaterialEditText qqTV;// QQ
	private MaterialEditText phoneTV;// 电话

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * 
	 * @Description: 初始化传递过来的闲置物品信息
	 */
	private void init() {
		Bundle bundle = getArguments();
		secondHandInfo = (SecondHandGoods) bundle
				.getSerializable(CommonConstant.FROM_SECOND_HAND_DETAIL_ACTIVITY_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_second_hand_contact_info, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
		initMaterialRipple();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		bargainTV = (MaterialEditText) getView().findViewById(
				R.id.second_hand_contact_info_bargain_tv);
		qqTV = (MaterialEditText) getView().findViewById(
				R.id.second_hand_contact_info_qq_tv);
		phoneTV = (MaterialEditText) getView().findViewById(
				R.id.second_hand_contact_info_phone_tv);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		if (secondHandInfo.getBargain()) {
			bargainTV.setText("可议价");
		} else {
			bargainTV.setText("不可议价");
		}
		if ("".equals(secondHandInfo.getQq()) || null == secondHandInfo.getQq()) {
			qqTV.setText("暂无");
		} else {
			qqTV.setText(secondHandInfo.getQq());
		}
		if ("".equals(secondHandInfo.getPhone())
				|| null == secondHandInfo.getPhone()) {
			phoneTV.setText("暂无");
		} else {
			phoneTV.setText(secondHandInfo.getPhone());
		}
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		// 设置Padding
		bargainTV.setPaddings(DensityUtils.sp2px(getActivity(), 32), 0, 0, 0);
		qqTV.setPaddings(DensityUtils.sp2px(getActivity(), 32), 0, 0, 0);
		phoneTV.setPaddings(DensityUtils.sp2px(getActivity(), 32), 0, 0, 0);
	}
}
