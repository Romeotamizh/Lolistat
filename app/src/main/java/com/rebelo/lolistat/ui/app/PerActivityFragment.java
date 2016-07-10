package com.rebelo.lolistat.ui.app;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.SearchView;

import com.rebelo.lolistat.R;
import com.rebelo.lolistat.ui.adapter.ActivityAdapter;
import com.rebelo.lolistat.ui.base.BaseListFragment;
import com.rebelo.lolistat.ui.model.ActivityModel;

import java.util.ArrayList;
import java.util.List;

import static com.rebelo.lolistat.ui.utils.UiUtility.$;

public class PerActivityFragment extends BaseListFragment<ActivityModel>
{
	private ActivityAdapter mAdapter;
	private String mTitle = "", mDefaultClassName, mPkgName;
	private int mDefaultClassPosition = -1;

	private MenuItem mSearchItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected BaseAdapter buildAdapter() {
		mAdapter = new ActivityAdapter(getActivity(), getItemList());
		return mAdapter;
	}

	@Override
	protected List<ActivityModel> loadData(ProgressCallback callback) {
		List<ActivityModel> list = new ArrayList<ActivityModel>();
		PackageManager pm = getActivity().getPackageManager();
		
		if (getExtraPass().contains(",")) {
			String[] s = getExtraPass().split(",");
			mPkgName = s[0];
			mDefaultClassName = s[1];
		} else {
			mPkgName = getExtraPass();
		}
		
		ActivityInfo[] ai;
		
		try {
			PackageInfo pkg = pm.getPackageInfo(mPkgName, PackageManager.GET_ACTIVITIES);
			ai = pkg.activities;
			mTitle = pm.getApplicationLabel(pkg.applicationInfo).toString();
		} catch (Exception e) {
			ai = new ActivityInfo[0];
			mTitle = getString(R.string.global_short);
		}
		
		// Add the default one
		ActivityModel global = new ActivityModel();
		global.className = "global";
		global.title = getString(R.string.global);
		list.add(global);
		
		if (ai != null) {
			for (ActivityInfo info : ai) {
				ActivityModel activity = new ActivityModel();
				activity.className = info.name;
				activity.title = info.loadLabel(pm).toString();
				list.add(activity);
				callback.updateProgress(list.size() - 1, ai.length);
				
				if (activity.className.equals(mDefaultClassName)) {
					mDefaultClassPosition = list.size() - 1;
				}
			}
		}
		
		return list;
	}

	@Override
	protected void onDataLoaded(List<ActivityModel> data) {
		super.onDataLoaded(data);
		if (!mTitle.equals("")) {
			showHomeAsUp();
			setTitle(mTitle + " - " + getString(R.string.app_name));
		}
		
		if (mDefaultClassPosition > 0) {
			onItemClick(mDefaultClassPosition);
		}
		// If size is smaller than 2, which means the app has only one or no activity
		// Then we can skip this fragment and go to settings directly
		else if (data.size() <= 2) {
			onItemClick(0);
			getActivity().finish();
		}
	}

	@Override
	protected void onItemClick(int pos) {
		ActivityModel activity = getItemList().get(pos);
		startFragment("settings", mPkgName + "," + activity.className + "," + activity.title + "," + mTitle);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.app, menu);

		menu.findItem(R.id.about).setVisible(false);
		menu.findItem(R.id.show_launcher).setVisible(false);

		mSearchItem = menu.findItem(R.id.search);
		final SearchView search = $(mSearchItem.getActionView());
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String query) {
				mAdapter.getFilter().filter(query);
				return true;
			}
		});

		search.setIconified(true);

		mSearchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				search.setIconified(false);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem p1) {
				search.setQuery("", false);
				search.setIconified(true);
				return true;
			}
		});
	}
}
