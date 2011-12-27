package bing.software.meminfomonitor;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * Update
 * 1. Set status of app (running/not running)
 * 2. Resort with App name(currently sorted with pkg_name)
*/
public class AppInfoActivity extends ListActivity{
	Context mContext = null;
	private ArrayList<ApplicationInfo> al_app_info;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appinfo_listview);
        
        initPackageInfo();
        
        MyListAdapter proc_listview_adapter = new MyListAdapter(this);
        setListAdapter(proc_listview_adapter);
	}
	
	private void initPackageInfo(){
		PackageManager pm = getApplicationContext().getPackageManager();
		al_app_info = (ArrayList<ApplicationInfo>) pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
	}
	
	class MyListAdapter extends BaseAdapter {
		public MyListAdapter(Context context) {
		    mContext = context;
		}
		
		public int getCount() {
		    return al_app_info.size();
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
		    if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
				R.layout.appinfo_item, null);
			holder = new ViewHolder();
			holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.app_name = (TextView) convertView.findViewById(R.id.app_name);
			holder.app_pkg_name = (TextView) convertView.findViewById(R.id.app_pkg_name);
			holder.app_cpu = (TextView) convertView.findViewById(R.id.app_cpu);
			holder.app_meminfo = (TextView) convertView.findViewById(R.id.app_meminfo);
			convertView.setTag(holder);
		    }else {
		        holder = (ViewHolder) convertView.getTag();
		    }
		
		    
		    holder.app_icon.setImageDrawable(al_app_info.get(position).loadIcon(getPackageManager()));
		    holder.app_name.setText(al_app_info.get(position).loadLabel(getPackageManager()));
		    holder.app_pkg_name.setText(al_app_info.get(position).packageName);
		    // Update later
		    holder.app_cpu.setText("cpu");
		    // Reference:
		    // http://stackoverflow.com/questions/2298208/how-to-discover-memory-usage-of-my-application-in-android
		    holder.app_meminfo.setText("mem");
			
			return convertView;
			}
		}
	    
	static class ViewHolder {
		ImageView app_icon;
		TextView app_name;
	    TextView app_pkg_name;
	    TextView app_cpu;
	    TextView app_meminfo;
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(AppInfoActivity.this, AppInfoMonitor.class);
		Bundle bl = new Bundle();
		bl.putString("PROC_NAME", al_app_info.get(position).processName);
		bl.putString("PKG_NAME", al_app_info.get(position).packageName);
		intent.putExtras(bl);
		startActivity(intent);
    }

		
}
