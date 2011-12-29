package bing.sw.mm.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

public class ProcessActivity extends ListActivity{
	Context mContext = null;
	private ArrayList<ApplicationInfo> runningAppInfo;
	private ArrayList<RunningAppProcessInfo> runningAppProcesses;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_listview);
        
        initPackageInfo();
        
        ProcessListAdapter process_list_adapter = new ProcessListAdapter(this);
        setListAdapter(process_list_adapter);
	}
	
	
	private void initPackageInfo(){
		ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		runningAppProcesses = (ArrayList<RunningAppProcessInfo>) am.getRunningAppProcesses();
		Collections.sort(runningAppProcesses, new ComparatorRunningAppProcessInfo());
		ArrayList<String> processes = new ArrayList<String>();
		for(RunningAppProcessInfo runningAppProcess: runningAppProcesses){
			processes.add(runningAppProcess.processName);
			Log.d(Constant.TAG, "runningAppProcess.processName:" + runningAppProcess.processName);
		}
		
		PackageManager pm = getApplicationContext().getPackageManager();
		ArrayList<ApplicationInfo> all_app_info = (ArrayList<ApplicationInfo>) pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		runningAppInfo = new ArrayList<ApplicationInfo>();
		for(ApplicationInfo app: all_app_info){
			if(processes.contains(app.processName)){
				runningAppInfo.add(app);
			}
		}
		Collections.sort(runningAppInfo, new ComparatorApplicationInfo());
		Log.d(Constant.TAG, "runningAppInfo.size(): "+ runningAppInfo.size());
		Log.d(Constant.TAG, "runningAppProcesses.size(): "+ runningAppProcesses.size());
		Log.i(Constant.TAG, "init finished");
	}
	
	class ComparatorRunningAppProcessInfo implements Comparator<RunningAppProcessInfo>{
		 public int compare(RunningAppProcessInfo arg0, RunningAppProcessInfo arg1) {
			 int flag=arg0.processName.compareTo(arg1.processName);
			 	if(flag==0){
			 		return arg0.pid - arg1.pid;
			 	}else{
			 		return flag;
			 	}  
		 }
	}
	
	class LegacyComparatorApplicationInfo implements Comparator<ApplicationInfo>{
		 public int compare(ApplicationInfo arg0, ApplicationInfo arg1) {
			 int flag=arg0.processName.compareTo(arg1.processName);
			 if(flag==0){
			 		return arg0.packageName.compareTo(arg1.packageName);
			 	}else{
			 		return flag;
			 	} 
		 }
	}
	
	class ComparatorApplicationInfo implements Comparator<ApplicationInfo>{
		 public int compare(ApplicationInfo arg0, ApplicationInfo arg1) {
			 int flag=arg0.loadLabel(getPackageManager()).toString().compareTo(
					 arg1.loadLabel(getPackageManager()).toString());
			 	if(flag==0){
			 		return arg0.packageName.compareTo(
			 				arg1.packageName);
			 	}else{
			 		return flag;
			 	}  
		 }
	}
	
	class ProcessListAdapter extends BaseAdapter {
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
