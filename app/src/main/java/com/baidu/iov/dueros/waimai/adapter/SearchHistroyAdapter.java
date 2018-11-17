package com.baidu.iov.dueros.waimai.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.utils.SharedPreferencesUtils;
import com.domain.multipltextview.MultiplTextView;

import java.util.List;

public class SearchHistroyAdapter extends BaseAdapter {

	private List<String> mHistorys;
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public SearchHistroyAdapter(List<String> historys, Context context) {
		this.mLayoutInflater = LayoutInflater.from(context);
		mContext = context;
		mHistorys = historys;
	}

	@Override
	public int getCount() {
		return mHistorys.size();
	}

	@Override
	public String getItem(int position) {
		return mHistorys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.layout_search_history_item, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.tvHistoryNum = convertView.findViewById(R.id.tv_history_num);
			viewHolder.tvHistoryName =  convertView.findViewById(R.id.tv_history_name);
			viewHolder.ivDelete = (AppCompatImageView) convertView.findViewById(R.id.iv_delete);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvHistoryNum.setText(position + 1 + "");
		viewHolder.tvHistoryName.setText(mHistorys.get(position));

		viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHistorys.remove(position);
				SharedPreferencesUtils.deleteSearchHistory(mHistorys);
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	public static class ViewHolder {
		private TextView tvHistoryNum;
		private TextView tvHistoryName;
		private AppCompatImageView ivDelete;

	}
}
