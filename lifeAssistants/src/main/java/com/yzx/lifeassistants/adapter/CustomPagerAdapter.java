package com.yzx.lifeassistants.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @Description: pageview's adapter
 * @author: yzx
 * @time: 2015-9-15 上午8:49:50
 */
public class CustomPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;

	public CustomPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public CustomPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
