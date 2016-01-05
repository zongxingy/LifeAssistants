package com.yzx.lifeassistants.adapter;

import java.util.List;
import com.yzx.lifeassistants.R;
import com.yzx.lifeassistants.bean.BorrowDetail;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @Description: 借入明细列表适配器
 * @author: yzx
 * @time: 2015-9-24 下午3:45:40
 */
public class BorrowListAdapter extends BaseAdapter {
	private List<BorrowDetail> dataList;
	private BorrowDetail data;
	private LayoutInflater inflater;
	private ViewHolder holder;

	public BorrowListAdapter(Context context, List<BorrowDetail> dataList) {
		this.dataList = dataList;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_borrow_listview,
					parent, false);
			holder = new ViewHolder();
			holder.moneyTV = (TextView) convertView
					.findViewById(R.id.borrow_money_tv);
			holder.personTV = (TextView) convertView
					.findViewById(R.id.borrow_person_tv);
			holder.timeTV = (TextView) convertView
					.findViewById(R.id.borrow_time_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		data = dataList.get(position);
		holder.moneyTV.setText(data.getMoney());
		holder.personTV.setText(data.getPerson());
		holder.timeTV.setText(data.getTime());
		return convertView;
	}

	static class ViewHolder {
		TextView moneyTV;// 金额
		TextView personTV;// 姓名
		TextView timeTV;// 时间
	}
}
