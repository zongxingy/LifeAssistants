package com.yzx.lifeassistants.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.SecondHandSearchActivity;
import com.yzx.lifeassistants.adapter.SecondHnadGridAdapter;
import com.yzx.lifeassistants.bean.ClassifyItem;
import com.yzx.lifeassistants.listener.ClassifyGridListener;
import com.yzx.lifeassistants.view.widget.CustomGridView;

/**
 * @Description: 二手交易广场
 * @author: yzx
 * @time: 2015-11-16 下午4:29:20
 */
public class SecondHandSquareFragment extends Fragment implements
		OnClickListener {
	private RelativeLayout searchRL;// 搜索栏
	private CustomGridView livingGV;// 生活用品
	private CustomGridView electronicsGV;// 电子产品
	private CustomGridView schoolGV;// 学习用品
	private SecondHnadGridAdapter livingAdapter;
	private SecondHnadGridAdapter electronicsAdapter;
	private SecondHnadGridAdapter schoolAdapter;
	private List<ClassifyItem> livingList;
	private List<ClassifyItem> electronicsList;
	private List<ClassifyItem> schoolList;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_second_hand_square,
				container, false);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
		initData();
		initGridView();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		searchRL = (RelativeLayout) getView().findViewById(
				R.id.second_hand_square_search_rl);
		livingGV = (CustomGridView) getView().findViewById(
				R.id.second_hand_living_things_gv);
		electronicsGV = (CustomGridView) getView().findViewById(
				R.id.second_hand_electronics_gv);
		schoolGV = (CustomGridView) getView().findViewById(
				R.id.second_hand_school_things_gv);
	}

	/**
	 * 
	 * @Description: 初始化监听
	 */
	private void initListener() {
		searchRL.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	private void initData() {
		livingList = new ArrayList<ClassifyItem>();
		electronicsList = new ArrayList<ClassifyItem>();
		schoolList = new ArrayList<ClassifyItem>();
		livingAdapter = new SecondHnadGridAdapter(getActivity(), livingList);
		electronicsAdapter = new SecondHnadGridAdapter(getActivity(),
				electronicsList);
		schoolAdapter = new SecondHnadGridAdapter(getActivity(), schoolList);
	}

	/**
	 * 
	 * @Description: 初始化分类布局
	 */
	private void initGridView() {
		// 生活用品
		ClassifyItem shoesItem = new ClassifyItem("生活", R.drawable.icon_shoes,
				"鞋子");
		ClassifyItem bikeItem = new ClassifyItem("生活", R.drawable.icon_bike,
				"车辆");
		ClassifyItem makeupItem = new ClassifyItem("生活",
				R.drawable.icon_makeup, "化妆");
		ClassifyItem livingOtherItem = new ClassifyItem("生活",
				R.drawable.icon_other, "其他");
		livingList.add(shoesItem);
		livingList.add(bikeItem);
		livingList.add(makeupItem);
		livingList.add(livingOtherItem);
		livingGV.setAdapter(livingAdapter);
		livingAdapter.notifyDataSetChanged();
		livingGV.setOnItemClickListener(new ClassifyGridListener(getActivity(),
				livingList));
		// 电子产品
		ClassifyItem phoneItem = new ClassifyItem("电子", R.drawable.icon_phone,
				"手机");
		ClassifyItem cameraItem = new ClassifyItem("电子",
				R.drawable.icon_camera, "相机");
		ClassifyItem computerItem = new ClassifyItem("电子",
				R.drawable.icon_computer, "电脑");
		ClassifyItem electronicsOtherItem = new ClassifyItem("电子",
				R.drawable.icon_other, "其他");
		electronicsList.add(phoneItem);
		electronicsList.add(cameraItem);
		electronicsList.add(computerItem);
		electronicsList.add(electronicsOtherItem);
		electronicsGV.setAdapter(electronicsAdapter);
		electronicsAdapter.notifyDataSetChanged();
		electronicsGV.setOnItemClickListener(new ClassifyGridListener(
				getActivity(), electronicsList));
		// 学习用品
		ClassifyItem cetItem = new ClassifyItem("学习", R.drawable.icon_cet,
				"CET");
		ClassifyItem mbaItem = new ClassifyItem("学习", R.drawable.icon_mba, "考研");
		ClassifyItem civilItem = new ClassifyItem("学习", R.drawable.icon_civil,
				"考公");
		ClassifyItem schoolOtherItem = new ClassifyItem("学习",
				R.drawable.icon_other, "其他");
		schoolList.add(cetItem);
		schoolList.add(mbaItem);
		schoolList.add(civilItem);
		schoolList.add(schoolOtherItem);
		schoolGV.setAdapter(schoolAdapter);
		schoolAdapter.notifyDataSetChanged();
		schoolGV.setOnItemClickListener(new ClassifyGridListener(getActivity(),
				schoolList));
	}

	/**
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.second_hand_square_search_rl: {// 搜索
			Intent intent = new Intent(getActivity(),
					SecondHandSearchActivity.class);
			getActivity().startActivity(intent);
			break;
		}
		default:
			break;
		}
	}

}
