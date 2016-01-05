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
import com.yzx.lifeassistants.activity.BorrowAndLendActivity;
import com.yzx.lifeassistants.activity.MainActivity;
import com.yzx.lifeassistants.adapter.CustomPagerAdapter;
import com.yzx.lifeassistants.common.CommonConstant;
import com.yzx.lifeassistants.utils.ToastUtils;

/**
 * @Description: 借入借出
 * @author: yzx
 * @time: 2015-9-23 下午4:21:30
 */
public class BorrowAndLendFragment extends Fragment implements OnClickListener,
		OnPageChangeListener {
	private ImageButton topAddBtn;// 顶部添加按钮
	private TextView topTitleTV;// 顶部标题
	private ViewPager viewPager;// 中间部分
	private UnderlinePageIndicator indicator;// 滑线
	private RadioButton borrowRB;// 借入明细
	private RadioButton lendRB;// 借出明细
	private CustomPagerAdapter adapter;// viewpage‘s adapter
	public static final int TAB_BORROW = 0;// 借入明细
	public static final int TAB_LEND = 1;// 借出明细

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_borrow_lend, container,
				false);
		return view;
	}

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
		topTitleTV.setText(R.string.borrow_lend_title);
		topTitleTV.setTag("Borrow");
	}

	/**
	 * @Description: 初始化RadioGoup
	 */
	private void initRadioGroup() {
		borrowRB = (RadioButton) getView().findViewById(R.id.borrow_rb);
		borrowRB.setOnClickListener(this);
		lendRB = (RadioButton) getView().findViewById(R.id.lend_rb);
		lendRB.setOnClickListener(this);
	}

	/**
	 * @Description: 初始化viewpage
	 */
	@SuppressWarnings("deprecation")
	private void initViewPage() {
		viewPager = (ViewPager) getView().findViewById(
				R.id.borrow_lend_viewpager);
		// fragment嵌套viewpage里的多个fragment需要使用getChildFragmentManager
		FragmentManager fm = getChildFragmentManager();
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(new BorrowFragment());
		fragments.add(new LendFragment());
		adapter = new CustomPagerAdapter(fm, fragments);
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(TAB_BORROW);
		viewPager.setOnPageChangeListener(this);
	}

	/**
	 * @Description: 初始化滑线
	 */
	private void initUnderLine() {
		indicator = (UnderlinePageIndicator) getView().findViewById(
				R.id.borrow_lend_indicator);
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
		case TAB_BORROW: {// 借入明细
			// 设置触摸屏幕的模式,这里设置为全屏
			MainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			borrowRB.setChecked(true);

			break;
		}
		case TAB_LEND: {// 借出明细
			// 设置触摸屏幕的模式,这里设置为边缘
			MainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			lendRB.setChecked(true);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.borrow_rb: {// 借入明细
			viewPager.setCurrentItem(TAB_BORROW);
			break;
		}
		case R.id.lend_rb: {// 借出明细
			viewPager.setCurrentItem(TAB_LEND);
			break;
		}
		case R.id.top_add_btn: {// 添加按钮
			Intent intent = new Intent(getActivity(),
					BorrowAndLendActivity.class);
			if (viewPager.getCurrentItem() == TAB_BORROW) {
				intent.putExtra("from", CommonConstant.FROM_BORROWANDFRAGMENT);
			} else if (viewPager.getCurrentItem() == TAB_LEND) {
				intent.putExtra("from", CommonConstant.FROM_ANDLENDFRAGMENT);
			}
			startActivityForResult(intent,
					CommonConstant.REQUESTCODE_ADD_BORROW_LEND);
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
		if (requestCode == CommonConstant.REQUESTCODE_ADD_BORROW_LEND) {
			if (resultCode == CommonConstant.RESULTCODE_NEW_BORROW_OK) {
				ToastUtils.showToast("借入信息添加成功!");
			} else if (resultCode == CommonConstant.RESULTCODE_NEW_LEND_OK) {
				ToastUtils.showToast("借出信息添加成功!");
			}
		}
		// 嵌套的子Fragment发出的getParentFragment().startActivityForResult只能在父Fragment
		// 处理onActivityResult
		if (requestCode == CommonConstant.REQUESTCODE_SEE_BORROW) {
			if (resultCode == CommonConstant.RESULTCODE_UPDATE_BORROW_OK) {
				ToastUtils.showToast("借入信息修改成功!");
			} else if (resultCode == CommonConstant.RESULTCODE_DELETE_BORROW_OK) {
				ToastUtils.showToast("借出信息删除成功!");
			}
		}
		if (requestCode == CommonConstant.REQUESTCODE_SEE_LEND) {
			if (resultCode == CommonConstant.RESULTCODE_UPDATE_LEND_OK) {
				ToastUtils.showToast("借入信息修改成功!");
			} else if (resultCode == CommonConstant.RESULTCODE_DELETE_LEND_OK) {
				ToastUtils.showToast("借出信息删除成功!");
			}
		}

	}
}
