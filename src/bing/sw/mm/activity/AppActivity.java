package bing.sw.mm.activity;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
/**
 * Update
 * 1. Set status of app and if not running set background as grey(running/not running) (DONE)
 * 2. Resort with App name(currently sorted with pkg_name)(DONE)
 * 4. Move all msg into values/string file(DONE)
 * 5. Add thread for loading appinfo (Not need now)
 * 3. Add search bar for user to search app
 * http://developer.android.com/guide/topics/search/search-dialog.html
 * 6. Merge initPackage into common method, return ArrayList<ApplicationInfo> and things like that (DONE)
 * 7. Add cpu info into recorded file
*/
public class AppActivity extends ListActivity{
	Context mContext = null;
	private ArrayList<ApplicationInfo> appAppInfo;
	ArrayList<String> runningAppProcessesNames;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_listview);
        
		appAppInfo = Constant.getAppInfoSortedWithAppName(getApplicationContext().getPackageManager());
		runningAppProcessesNames = Constant.getRunningAppProcessesNames(
				(ActivityManager)getSystemService(ACTIVITY_SERVICE));
        
        AppListAdapter app_list_adapter = new AppListAdapter(this);
        setListAdapter(app_list_adapter);
	}
	
	@Override
	protected void onDestroy() {
		SharedPreferences settings = getSharedPreferences(Constant.SP_STATUS, MODE_PRIVATE);
		boolean flag = settings.getBoolean(Constant.KEY_SERVICE_STATUS, false);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		if(flag){
			editor.putInt(Constant.KEY_MEM_STATUS, Constant.ON);
			Log.d(Constant.TAG, "AppInfoMonitor-onDestroy-save mem_status: " + Constant.ON);
		}else{
			editor.putInt(Constant.KEY_MEM_STATUS, Constant.OFF);
			Log.d(Constant.TAG, "AppInfoMonitor-onDestroy-save mem_status: " + Constant.OFF);
		}
		editor.commit();
	    super.onDestroy();
	}
	
	
	
	class AppListAdapter extends BaseAdapter {
		public AppListAdapter(Context context) {
		    mContext = context;
		}
		
		public int getCount() {
		    return appAppInfo.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.app_item, null);
			holder = new ViewHolder();
			holder.app_icon = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.app_name = (TextView) convertView.findViewById(R.id.app_name);
			holder.app_pkg_name = (TextView) convertView.findViewById(R.id.app_pkg_name);
			holder.app_status = (TextView) convertView.findViewById(R.id.app_status);
			convertView.setTag(holder);
		    }else {
		        holder = (ViewHolder) convertView.getTag();
		    }
		
		    appInfo = appAppInfo.get(position);
		    
		    holder.app_icon.setImageDrawable(appInfo.loadIcon(getPackageManager()));
		    holder.app_name.setText(appInfo.loadLabel(getPackageManager()));
		    holder.app_pkg_name.setText(appInfo.packageName);
		    
		    if(runningAppProcessesNames.contains(appInfo.processName)){
		    	holder.app_status.setText(getString(R.string.process_running));
		    	holder.app_status.setTypeface(null, Typeface.BOLD);
		    	convertView.setBackgroundColor(0x00000000);//black
		    	holder.app_name.setTextColor(0xB0FFFFFF);//white with transparent'B0'
		    	holder.app_pkg_name.setTextColor(0xB0FFFFFF);
		    	holder.app_status.setTextColor(0xB0FFFFFF);
		    }else{
		    	holder.app_status.setText(getString(R.string.process_not_running));
		    	holder.app_status.setTypeface(null, Typeface.NORMAL);
		    	convertView.setBackgroundColor(0xA0FFFFFF);//white with transparent'A0'
		    	holder.app_name.setTextColor(0xFF000000);//black with transparent'FF'
		    	holder.app_pkg_name.setTextColor(0xFF000000);
		    	holder.app_status.setTextColor(0xFF000000);
		    }
			
			return convertView;
			}
		}
	    
	static class ViewHolder {
		ImageView app_icon;
		TextView app_name;
	    TextView app_pkg_name;
	    TextView app_status;
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(AppActivity.this, AppMonitor.class);
		Bundle bl = new Bundle();
		bl.putString(Constant.KEY_PROC_NAME, appAppInfo.get(position).processName);
		bl.putString(Constant.KEY_PKG_NAME, appAppInfo.get(position).packageName);
		intent.putExtras(bl);
		startActivity(intent);
    }

		
}
