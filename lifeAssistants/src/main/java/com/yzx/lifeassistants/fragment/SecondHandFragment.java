package com.yzx.lifeassistants.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.AddSecondHandActivity;
import com.yzx.lifeassistants.activity.MainActivity;
import com.yzx.lifeassistants.adapter.CustomPagerAdapter;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.ToastUtils;

/**
 * @Description: 二手交易
 * @author: yzx
 * @time: 2015-9-23 下午4:29:20
 */
public class SecondHandFragment extends Fragment implements OnClickListener,
		OnPageChangeListener {
	private ImageButton topAddBtn;// 顶部添加按钮
	private RadioButton squareBtn;// 顶部广场按钮
	private RadioButton myBtn;// 顶部我的按钮
	private ViewPager viewPager;
	private CustomPagerAdapter adapter;// viewpage‘s adapter
	public static final int TAB_QUARE = 0;// 广场
	public static final int TAB_MY = 1;// 我的

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_second_hand, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
		initData();
	}

	/**
	 * 
	 * @Description: 初始化控件
	 */
	private void initView() {
		topAddBtn = (ImageButton) getView().findViewById(R.id.top_add_btn);
		squareBtn = (RadioButton) getView().findViewById(
				R.id.top_title_square_rb);
		myBtn = (RadioButton) getView().findViewById(R.id.top_title_my_rb);
		viewPager = (ViewPager) getView().findViewById(
				R.id.second_hand_viewpager);
	}

	/**
	 * 
	 * @Description: 初始化监听
	 */
	private void initListener() {
		topAddBtn.setOnClickListener(this);
		squareBtn.setOnClickListener(this);
		myBtn.setOnClickListener(this);
	}

	/**
	 * 
	 * @Description: 初始化数据
	 */
	@SuppressWarnings("deprecation")
	private void initData() {
		topAddBtn.setVisibility(View.VISIBLE);
		// fragment嵌套viewpage里的多个fragment需要使用getChildFragmentManager
		FragmentManager fm = getChildFragmentManager();
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(new SecondHandSquareFragment());
		fragments.add(new SecondHandMyFragment());
		adapter = new CustomPagerAdapter(fm, fragments);
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(TAB_QUARE);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case TAB_QUARE: {// 广场
			// 设置触摸屏幕的模式,这里设置为全屏
			MainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			squareBtn.setChecked(true);
			break;
		}
		case TAB_MY: {// 我的
			// 设置触摸屏幕的模式,这里设置为边缘
			MainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			myBtn.setChecked(true);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @Description: 按键监听
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_add_btn: {// 添加发布二手物品
			Intent intent = new Intent(getActivity(),
					AddSecondHandActivity.class);
			intent.putExtra("from", CommonConstant.FROM_SECOND_HAND_FRAGMENT);
			startActivityForResult(intent,
					CommonConstant.REQUESTCODE_ADD_SECOND_HAND);
			break;
		}
		case R.id.top_title_square_rb: {// 广场
			viewPager.setCurrentItem(TAB_QUARE);
			break;
		}
		case R.id.top_title_my_rb: {// 我的
			viewPager.setCurrentItem(TAB_MY);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * @Description: 回调处理
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (CommonConstant.REQUESTCODE_ADD_SECOND_HAND == requestCode) {
			if (CommonConstant.RESULTCODE_ADD_SECOND_HAND_OK == resultCode) {
				ToastUtils.showToast("闲置物品发布成功");
				// 页面跳到我的
				viewPager.setCurrentItem(TAB_MY);
			}
		}
		// 嵌套的子Fragment发出的getParentFragment().startActivityForResult只能在父Fragment
		// 处理onActivityResult
		if (requestCode == CommonConstant.REQUESTCODE_MODIFY_SECOND_HAND) {
			if (resultCode == CommonConstant.RESULTCODE_MODIFY_SECOND_HAND_OK) {
				ToastUtils.showToast("闲置物品修改成功!");
			}
		}
	}
}
