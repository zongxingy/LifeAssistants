package com.yzx.lifeassistants.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yzx.lifeassistants.fragment.ImageDetailFragment;

/**
 * @Description: 图片列表适配器
 * @author: yzx
 * @time: 2015-11-23 上午11:24:05
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter {
	public List<String> picList;

	public ImagePagerAdapter(FragmentManager fm, List<String> picList) {
		super(fm);
		this.picList = picList;
	}

	@Override
	public Fragment getItem(int position) {
		return ImageDetailFragment.newInstance(picList.get(position));
	}

	@Override
	public int getCount() {
		return picList == null ? 0 : picList.size();
	}

}
