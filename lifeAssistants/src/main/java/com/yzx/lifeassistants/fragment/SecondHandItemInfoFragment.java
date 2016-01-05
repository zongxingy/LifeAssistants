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
 * @Description: 闲置物品物品信息
 * @author: yzx
 * @time: 2015-11-25 上午11:08:02
 */
public class SecondHandItemInfoFragment extends Fragment {

	private SecondHandGoods secondHandInfo;// 闲置物品信息

	private MaterialEditText priceTV;// 价格
	private MaterialEditText typeTV;// 类型
	private MaterialEditText describeTV;// 描述

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
		View view = inflater.inflate(R.layout.fragment_second_hand_item_info,
				container, false);
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
		priceTV = (MaterialEditText) getView().findViewById(
				R.id.second_hand_item_info_price_tv);
		typeTV = (MaterialEditText) getView().findViewById(
				R.id.second_hand_item_info_type_tv);
		describeTV = (MaterialEditText) getView().findViewById(
				R.id.second_hand_item_info_describe_tv);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		priceTV.setText(secondHandInfo.getPrice() + "元");
		typeTV.setText(secondHandInfo.getType());
		describeTV.setText(secondHandInfo.getDescribe());
	}

	/**
	 * 
	 * @Description: 瓷砖特效
	 */
	private void initMaterialRipple() {
		// 设置Padding
		priceTV.setPaddings(DensityUtils.sp2px(getActivity(), 32), 0, 0, 0);
		typeTV.setPaddings(DensityUtils.sp2px(getActivity(), 32), 0, 0, 0);
		describeTV.setPaddings(DensityUtils.sp2px(getActivity(), 32), 0, 0, 0);
	}
}
