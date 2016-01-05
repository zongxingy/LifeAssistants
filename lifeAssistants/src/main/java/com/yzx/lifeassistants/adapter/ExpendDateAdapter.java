package com.yzx.lifeassistants.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.bean.ExpendInfo;

/**
 * @Description: 某天的支出记录列表适配器
 * @author: yzx
 * @time: 2015-12-11 上午8:46:48
 */
public class ExpendDateAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<ExpendInfo> dataList;
	private ExpendInfo data;
	private viewHolder holder;

	public ExpendDateAdapter(Context context, List<ExpendInfo> dataList) {
		this.context = context;
		this.dataList = dataList;
		inflater = (LayoutInflater) this.context
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			convertView = inflater.inflate(
					R.layout.item_expend_bar_analysis_listview, parent, false);
			holder = new viewHolder();
			holder.typeIV = (ImageView) convertView
					.findViewById(R.id.item_expend_type_iv);
			holder.remarkTV = (TextView) convertView
					.findViewById(R.id.item_expend_remark_tv);
			holder.moneyTV = (TextView) convertView
					.findViewById(R.id.item_expend_money_tv);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}
		data = dataList.get(position);
		if ("学习".equals(data.getType())) {
			holder.typeIV.setBackgroundResource(R.drawable.icon_study);
		} else if ("购物".equals(data.getType())) {
			holder.typeIV.setBackgroundResource(R.drawable.icon_shopping);
		} else if ("娱乐".equals(data.getType())) {
			holder.typeIV.setBackgroundResource(R.drawable.icon_entertainment);
		} else if ("医疗".equals(data.getType())) {
			holder.typeIV.setBackgroundResource(R.drawable.icon_medical);
		} else if ("交通".equals(data.getType())) {
			holder.typeIV.setBackgroundResource(R.drawable.icon_traffic);
		} else if ("饮食".equals(data.getType())) {
			holder.typeIV.setBackgroundResource(R.drawable.icon_eat);
		} else if ("爱人".equals(data.getType())) {
			holder.typeIV.setBackgroundResource(R.drawable.icon_lover);
		} else if ("其他".equals(data.getType())) {
			holder.typeIV.setBackgroundResource(R.drawable.icon_type_other);
		}
		if (null == data.getRemark() || "".equals(data.getRemark())) {
			holder.remarkTV.setText(data.getType());
		} else {
			holder.remarkTV.setText(data.getRemark());
		}
		holder.moneyTV.setText(data.getMoney() + "元");
		return convertView;
	}

	static class viewHolder {
		ImageView typeIV;// 支出类型
		TextView remarkTV;// 备注
		TextView moneyTV;// 金额
	}
}
