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
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.UnderlinePageIndicator;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.LostAndFindActivity;
import com.yzx.lifeassistants.activity.MainActivity;
import com.yzx.lifeassistants.adapter.CustomPagerAdapter;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.ToastUtils;

/**
 * @Description: 失物招领
 * @author: yzx
 * @time: 2015-9-15 上午8:49:50
 */

public class LostAndFindFragment extends Fragment implements OnClickListener,
		OnPageChangeListener {
	private ImageButton topAddBtn;// 顶部添加按钮
	private TextView topTitleTV;// 顶部标题
	private ViewPager viewPager;// 中间部分
	private UnderlinePageIndicator indicator;// 滑线
	private RadioButton lostRB;// 失物找寻
	private RadioButton findRB;// 拾取招领
	private CustomPagerAdapter adapter;// viewpage‘s adapter
	public static final int TAB_LOST = 0;// 失物找寻
	public static final int TAB_FIND = 1;// 拾取招领

	/**
	 * @Description: 在onCreateView加载布局
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lost_find, container,
				false);
		return view;
	}

	/**
	 * @Description: 在onActivityCreated加载控件
	 */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initTopView();
		initRadioGroup();
		initViewPage();
		initUnderLine();
	}

	/**
	 * @Description: 初始化顶部的标题与按钮
	 */
	private void initTopView() {
		topAddBtn = (ImageButton) getView().findViewById(R.id.top_add_btn);
		topAddBtn.setVisibility(View.VISIBLE);
		topAddBtn.setOnClickListener(this);
		topTitleTV = (TextView) getView().findViewById(R.id.top_title_tv);
		topTitleTV.setText(R.string.lost_find_title);
		topTitleTV.setTag("LOST");
	}

	/**
	 * @Description: 初始化RadioGoup
	 */
	private void initRadioGroup() {
		lostRB = (RadioButton) getView().findViewById(R.id.lost_rb);
		lostRB.setOnClickListener(this);
		findRB = (RadioButton) getView().findViewById(R.id.find_rb);
		findRB.setOnClickListener(this);

	}

	/**
	 * @Description: 初始化viewpage
	 */
	@SuppressWarnings("deprecation")
	private void initViewPage() {
		viewPager = (ViewPager) getView()
				.findViewById(R.id.lost_find_viewpager);
		// fragment嵌套viewpage里的多个fragment需要使用getChildFragmentManager
		FragmentManager fm = getChildFragmentManager();
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(new LostFragment());
		fragments.add(new FindFragment());
		adapter = new CustomPagerAdapter(fm, fragments);
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(TAB_LOST);
		viewPager.setOnPageChangeListener(this);
	}

	/**
	 * @Description: 初始化滑线
	 */
	private void initUnderLine() {
		indicator = (UnderlinePageIndicator) getView().findViewById(
				R.id.indicator);
		indicator.setViewPager(viewPager);
		indicator.setFades(false);
		indicator.setBackgroundColor(getResources().getColor(R.color.gray));
		indicator.setSelectedColor(getResources().getColor(
				R.color.text_green_color));
		indicator.setOnPageChangeListener(this);

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
		case TAB_LOST: {// 失物找寻
			// 设置触摸屏幕的模式,这里设置为全屏
			MainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			lostRB.setChecked(true);

			break;
		}
		case TAB_FIND: {// 拾取招领
			// 设置触摸屏幕的模式,这里设置为边缘
			MainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			findRB.setChecked(true);
			break;
		}
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lost_rb: {// 失物找寻
			viewPager.setCurrentItem(TAB_LOST);
			break;
		}
		case R.id.find_rb: {// 拾取招领
			viewPager.setCurrentItem(TAB_FIND);
			break;
		}
		case R.id.top_add_btn: {// 添加按钮
			Intent intent = new Intent(getActivity(), LostAndFindActivity.class);
			if (viewPager.getCurrentItem() == TAB_LOST) {
				intent.putExtra("from", CommonConstant.FROM_LOSTANDFRAGMENT);
			} else if (viewPager.getCurrentItem() == TAB_FIND) {
				intent.putExtra("from", CommonConstant.FROM_ANDFINDFRAGMENT);
			}
			startActivityForResult(intent,
					CommonConstant.REQUESTCODE_ADD_LOST_FIND);
			break;
		}
		default:
			break;
		}

	}

	/**
	 * @Description: 添加返回操作
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CommonConstant.REQUESTCODE_ADD_LOST_FIND) {
			if (resultCode == CommonConstant.RESULTCODE_NEW_LOST_OK) {
				ToastUtils.showToast("失物信息添加成功!");
			} else if (resultCode == CommonConstant.RESULTCODE_NEW_FIND_OK) {
				ToastUtils.showToast("招领信息添加成功!");
			}
		}
		// 嵌套的子Fragment发出的getParentFragment().startActivityForResult只能在父Fragment
		// 处理onActivityResult
		if (requestCode == CommonConstant.REQUESTCODE_SEE_LOST) {
			if (resultCode == CommonConstant.RESULTCODE_UPDATE_LOST_OK) {
				ToastUtils.showToast("失物信息修改成功!");
			} else if (resultCode == CommonConstant.RESULTCODE_DELETE_LOST_OK) {
				ToastUtils.showToast("失物信息删除成功!");
			}
		}
		if (requestCode == CommonConstant.REQUESTCODE_SEE_FIND) {
			if (resultCode == CommonConstant.RESULTCODE_UPDATE_FIND_OK) {
				ToastUtils.showToast("招领信息修改成功!");
			} else if (resultCode == CommonConstant.RESULTCODE_DELETE_FIND_OK) {
				ToastUtils.showToast("招领信息删除成功!");
			}
		}

	}
}
