package com.yzx.lifeassistants.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.activity.ImagePagerActivity;
import com.yzx.lifeassistants.base.BaseApplication;
import com.yzx.lifeassistants.bean.ImageItem;
import com.yzx.lifeassistants.bean.LostThing;
import com.yzx.lifeassistants.bean.User;
import com.yzx.lifeassistants.utils.LoadLocalImageUtil;
import com.yzx.lifeassistants.view.RoundedImageView;
import com.yzx.lifeassistants.view.widget.NoScrollGridView;

/**
 * @Description: 失物找寻列表的适配器
 * @author: yzx
 * @time: 2015-9-15 下午1:54:14
 */
public class LostListAdapter extends BaseAdapter {
	private Context context;
	private List<LostThing> dataList;
	private LostThing data;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private List<Boolean> isList;
	private Boolean is;
	private ListView listView;

	public LostListAdapter(ListView listView, Context context,
			List<LostThing> dataList, List<Boolean> isList) {
		this.listView = listView;
		this.context = context;
		this.dataList = dataList;
		this.isList = isList;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lost_listview, parent,
					false);
			holder = new ViewHolder();
			holder.avatorIV = (RoundedImageView) convertView
					.findViewById(R.id.lost_user_avator_iv);
			holder.nickTV = (TextView) convertView
					.findViewById(R.id.lost_user_nick_tv);
			holder.titleTV = (TextView) convertView
					.findViewById(R.id.lost_title_tv);
			holder.describeTV = (TextView) convertView
					.findViewById(R.id.lost_describe_tv);
			holder.picGV = (NoScrollGridView) convertView
					.findViewById(R.id.lost_pic_gv);
			holder.timeTV = (TextView) convertView
					.findViewById(R.id.lost_time_tv);
			holder.phoneTV = (TextView) convertView
					.findViewById(R.id.lost_phone_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		data = dataList.get(position);
		is = isList.get(position);
		if (is) {
			holder.nickTV.setTextColor(Color.RED);
			holder.titleTV.setTextColor(Color.RED);
		} else {
			holder.nickTV.setTextColor(Color.BLACK);
			holder.titleTV.setTextColor(Color.BLACK);
		}
		holder.avatorIV.setTag(position + "avator");
		holder.nickTV.setTag(position + "nick");
		loadUserInfo(position);
		holder.titleTV.setText(data.getTitle());
		holder.describeTV.setText(data.getDescribe());
		if (null == data.getPicFileList() || 1 > data.getPicFileList().size()) {
			holder.picGV.setVisibility(View.GONE);
		} else {
			final List<ImageItem> picList = new ArrayList<ImageItem>();
			holder.picGV.setVisibility(View.VISIBLE);
			for (BmobFile pic : data.getPicFileList()) {
				ImageItem item = new ImageItem();
				item.setImagePath(pic.getFileUrl(context));
				picList.add(item);
			}
			UnEditGridAdapter adapter = new UnEditGridAdapter(context, picList);
			holder.picGV.setAdapter(adapter);
			holder.picGV.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					String[] imagePaths = new String[picList.size()];
					for (int i = 0; i < picList.size(); i++) {
						imagePaths[i] = picList.get(i).getImagePath();
					}
					imageBrower(position, imagePaths);
				}
			});
		}
		holder.timeTV.setText(data.getUpdatedAt().substring(0,
				data.getUpdatedAt().indexOf(" ")));
		holder.phoneTV.setText(data.getPhone());
		return convertView;
	}

	/**
	 * 
	 * @Description: 加载用户昵称和头像
	 */
	private void loadUserInfo(final int position) {
		BmobQuery<User> query = new BmobQuery<User>();
		query.getObject(context, data.getUser().getObjectId(),
				new GetListener<User>() {

					@Override
					public void onFailure(int code, String message) {
						ImageView avator = (ImageView) listView
								.findViewWithTag(position + "avator");
						TextView nick = (TextView) listView
								.findViewWithTag(position + "nick");
						if (null != avator) {
							LoadLocalImageUtil.getInstance()
									.displayFromDrawable(
											R.drawable.user_icon_default_main,
											avator);
						}
						if (null != nick) {
							nick.setText(dataList.get(position).getUsername());
						}
					}

					@Override
					public void onSuccess(User userInfo) {
						ImageView avator = (ImageView) listView
								.findViewWithTag(position + "avator");
						TextView nick = (TextView) listView
								.findViewWithTag(position + "nick");
						if (userInfo != null) {
							if (null != nick) {
								if (null == userInfo.getNick()
										&& "".equals(userInfo.getNick())) {
									nick.setText(userInfo.getUsername());
								} else {
									nick.setText(userInfo.getNick());
								}
							}
							if (null != avator) {
								if (null == userInfo.getAvatar()) {
									LoadLocalImageUtil
											.getInstance()
											.displayFromDrawable(
													R.drawable.user_icon_default_main,
													avator);
								} else {
									ImageLoader
											.getInstance()
											.displayImage(
													userInfo.getAvatar()
															.getFileUrl(context),
													avator,
													BaseApplication
															.getInstance()
															.getOptions(
																	R.drawable.user_icon_default_main),
													new SimpleImageLoadingListener() {
														@Override
														public void onLoadingComplete(
																String imageUri,
																View view,
																Bitmap loadedImage) {
															super.onLoadingComplete(
																	imageUri,
																	view,
																	loadedImage);
														}
													});
								}
							}

						}
					}
				});
	}

	/**
	 * 
	 * @Description: 加载网络图片
	 */
	private void imageBrower(int position, String[] imagePaths) {
		Intent intent = new Intent(context, ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.IMAGE_URLS, imagePaths);
		intent.putExtra(ImagePagerActivity.IMAGE_INDEX, position);
		context.startActivity(intent);
	}

	static class ViewHolder {
		RoundedImageView avatorIV;// 发布者头像
		TextView nickTV;// 发布者昵称
		TextView titleTV;// 失物标题
		TextView describeTV;// 失物描述
		NoScrollGridView picGV;// 图片列表
		TextView timeTV;// 最后更新的时间
		TextView phoneTV;// 联系号码
	}

}
