package com.rebelo.lolistat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.rebelo.lolistat.R;
import com.rebelo.lolistat.ui.model.ActivityModel;
import com.rebelo.lolistat.ui.model.AppModel;

import static com.rebelo.lolistat.ui.utils.UiUtility.*;

public class ActivityAdapter extends BaseAdapter implements Filterable
{
	private List<ActivityModel> mList;
	private List<ActivityModel> mFullList;
	private LayoutInflater mInflater;

	public ActivityAdapter(Context context, List<ActivityModel> list) {
		mList = list;
		mFullList = list;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		if (position >= getCount())
			return convertView;

		View v = convertView;
		if (v == null)
			v = mInflater.inflate(R.layout.app, container, false);

		ActivityModel activity = mList.get(position);

		ImageView icon = $(v, R.id.app_icon);
		icon.setVisibility(View.GONE);

		TextView title = $(v, R.id.app_name);
		title.setText(activity.title);

		TextView pkg = $(v, R.id.app_pkg);
		pkg.setText(activity.className);

		return v;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				String query = constraint.toString().toLowerCase();
				List<ActivityModel> filtered = new ArrayList<ActivityModel>();

				if (query.equals("")) {
					filtered = mFullList;
				} else {
					for (ActivityModel act : mFullList) {
						if (act.title.toLowerCase().contains(query)
								|| act.className.toLowerCase().contains(query))

							filtered.add(act);
					}
				}

				FilterResults result = new FilterResults();
				result.count = filtered.size();
				result.values = filtered;

				return result;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint, FilterResults result) {
				mList = (List<ActivityModel>) result.values;
				notifyDataSetChanged();
			}
		};
	}
}
