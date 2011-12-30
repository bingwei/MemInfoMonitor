package bing.sw.mm.activity;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bing.sw.mm.R;
import bing.sw.mm.constant.Constant;
import bing.sw.mm.monitor.AppMonitor;

public class ProcessActivity extends ListActivity{
	private ArrayList<ApplicationInfo> runningAppInfo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_service_listview);
        
        runningAppInfo = Constant.getRunningAppInfoSortedWithAppName(
        		(ActivityManager)getSystemService(ACTIVITY_SERVICE),
        		getApplicationContext().getPackageManager());
        
        ProcessListAdapter process_list_adapter = new ProcessListAdapter(this);
        setListAdapter(process_list_adapter);
	}
	

	
	class ProcessListAdapter extends BaseAdapter {
		Context mContext = null;
		public ProcessListAdapter(Context context) {
		    mContext = context;
		}
		
		public int getCount() {
		    return runningAppInfo.size();
		}
		
		@Override
		public boolean areAllItemsEnabled() {
		    return false;
		}
		
		public Object getItem(int position) {
		    return position;
		}
		
		public long getItemId(int position) {
		    return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
		    ViewHolder holder;
		    ApplicationInfo appInfo;
		    if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
				R.layout.process_item, null);
			holder = new ViewHolder();
			holder.process_icon = (ImageView) convertView.findViewById(R.id.process_icon);
			holder.process_app_name = (TextView) convertView.findViewById(R.id.process_app_name);
			holder.process_pkg_name = (TextView) convertView.findViewById(R.id.process_pkg_name);
			convertView.setTag(holder);
		    }else {
		        holder = (ViewHolder) convertView.getTag();
		    }
		
		    appInfo = runningAppInfo.get(position);
		    
		    holder.process_icon.setImageDrawable(appInfo.loadIcon(getPackageManager()));
		    holder.process_app_name.setText(appInfo.loadLabel(getPackageManager()));
		    holder.process_pkg_name.setText(appInfo.packageName);
			return convertView;
			}
		}
	    
	static class ViewHolder {
		ImageView process_icon;
	    TextView process_app_name;
	    TextView process_pkg_name;
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(ProcessActivity.this, AppMonitor.class);
		Bundle bl = new Bundle();
		bl.putString(Constant.KEY_PROC_NAME, runningAppInfo.get(position).processName);
		bl.putString(Constant.KEY_PKG_NAME, runningAppInfo.get(position).packageName);
		intent.putExtras(bl);
		startActivity(intent);
    }

		
}
