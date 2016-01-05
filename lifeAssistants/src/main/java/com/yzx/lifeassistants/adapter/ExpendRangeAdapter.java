package com.yzx.lifeassistants.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yzx.lifeassistants.GlobalParams;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.bean.ExpendByRange;

/**
 * @Description: 某年/月的支出记录列表适配器
 * @author: yzx
 * @time: 2015-12-11 上午8:46:48
 */
public class ExpendRangeAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<ExpendByRange> dataList;
	private ExpendByRange data;
	private viewHolder holder;

	public ExpendRangeAdapter(Context context, List<ExpendByRange> dataList) {
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
			convertView = inflater.inflate(R.layout.item_expend_listview,
					parent, false);
			holder = new viewHolder();
			holder.iconIV = (ImageView) convertView
					.findViewById(R.id.item_expend_icon_money_iv);
			holder.rangeTV = (TextView) convertView
					.findViewById(R.id.item_expend_range_tv);
			holder.totalTV = (TextView) convertView
					.findViewById(R.id.item_expend_total_money_tv);
			convertView.setTag(holder);
		} else {
			holder = (viewHolder) convertView.getTag();
		}
		data = dataList.get(position);
		if (-1 != data.getRange().replaceFirst("-", "年").indexOf("-")) {// 月
			holder.rangeTV.setText(data
					.getRange()
					.substring(data.getRange().indexOf("-") + 1,
							data.getRange().length()).replace("-", "月")
					+ "号");
			if (data.getMoney() > 100) {
				holder.iconIV
						.setBackgroundResource(R.drawable.icon_expend_money_red);
			} else {
				holder.iconIV
						.setBackgroundResource(R.drawable.icon_expend_money_green);
			}
		} else {// 年
			holder.rangeTV.setText(data.getRange().replace("-", "年") + "月");
			Integer alimony = GlobalParams.userInfo.getAlimony();
			if (data.getMoney() > alimony) {
				holder.iconIV
						.setBackgroundResource(R.drawable.icon_expend_money_red);
			} else {
				holder.iconIV
						.setBackgroundResource(R.drawable.icon_expend_money_green);
			}
		}

		holder.totalTV.setText("总支出：" + data.getMoney() + "元");
		return convertView;
	}

	static class viewHolder {
		ImageView iconIV;// 支出类型
		TextView rangeTV;// 备注
		TextView totalTV;// 金额
	}
}
