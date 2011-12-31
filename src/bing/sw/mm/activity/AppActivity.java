package bing.sw.mm.activity;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
 * 3. Add search bar for user to search app (DONE)
 * http://liangruijun.blog.51cto.com/3061169/729505
 * 6. Merge initPackage into common method, return ArrayList<ApplicationInfo> and things like that (DONE)
 * 5. Add thread for loading appinfo (DONE)
 * 7. Add cpu info into recorded file
*/
public class AppActivity extends ListActivity{
	private ArrayList<ApplicationInfo> appAppInfo;
	private ArrayList<String> runningAppProcessesNames;
	private EditText etAppSearchBar;
//	private String strRegexp = Constant.DEFAULT_REG_EXPRESSION;
	private AppListAdapter app_list_adapter;
	private ArrayList<ApplicationInfo> mAppInfo;
	private ProgressDialog mProgressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_listview);
        
        etAppSearchBar = (EditText)findViewById(R.id.app_search_bar);
        etAppSearchBar.addTextChangedListener(appSearchBarTextWatcher);
	}
	
	@Override
	protected void onStart() {
		loadProgressDialog();
		new mReloadProcessThread(etAppSearchBar.getText().toString()).start();
		super.onStart();
	}

	
	@Override
	protected void onDestroy() {
		SharedPreferences settings = getSharedPreferences(Constant.SP_STATUS, MODE_PRIVATE);
		boolean flag = settings.getBoolean(Constant.KEY_SERVICE_STATUS, false);
		SharedPreferences.Editor editor = settings.edit();
//		editor.clear();
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
	
	//Reference:
	//http://liangruijun.blog.51cto.com/3061169/729505
	private TextWatcher appSearchBarTextWatcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable arg0) {// Do nothing
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {// Do nothing
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			loadProgressDialog();
			new mReloadProcessesWhileSearchingThread(etAppSearchBar.getText().toString()).start();
		}
	 };
	 
	class mReloadProcessThread extends Thread{
		String searchText;
		public mReloadProcessThread(String searchText){
			this.searchText = searchText;
		}
		public void run(){
			reloadProcess(searchText);
			Message msg = new Message();
			msg.what = 0;
			handler.sendEmptyMessage(msg.what);
		}
	}
	
	class mReloadProcessesWhileSearchingThread extends Thread{
		String searchText;
		public mReloadProcessesWhileSearchingThread(String searchText){
			this.searchText = searchText;
		}
		public void run(){
			reloadProcessesWhileSearching(searchText);
			handler.sendEmptyMessage(0);
		}
	}
	 
	private void loadProgressDialog(){
		mProgressDialog = new ProgressDialog(AppActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(getResources().getString(R.string.app_progressbar_title));
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	@SuppressWarnings("unchecked")
	private void reloadProcess(String searchText){
		appAppInfo = Constant.getAppInfoSortedWithAppName(getApplicationContext().getPackageManager());
		runningAppProcessesNames = Constant.getRunningAppProcessesNames(
				(ActivityManager)getSystemService(ACTIVITY_SERVICE));
		mAppInfo = (ArrayList<ApplicationInfo>) appAppInfo.clone();
		if(searchText.length() != 0){
			reloadProcessesWhileSearching(searchText);
		}
//		Log.d(Constant.TAG, "RP: appAppInfo size: "+ appAppInfo.size());
//		Log.d(Constant.TAG, "RP: runningAppProcessesNames size: "+ runningAppProcessesNames.size());
//		Log.d(Constant.TAG, "RP: mAppInfo size: "+ mAppInfo.size());
	}
	
	private void reloadProcessesWhileSearching(String searchText){
		mAppInfo.clear();
		String strRegexp = searchText.toLowerCase();
		for(ApplicationInfo app : appAppInfo){
			if(app.loadLabel(getPackageManager()).toString().toLowerCase().contains(strRegexp)
					|| app.packageName.toLowerCase().contains(strRegexp)){
				mAppInfo.add(app);
			}
		}
		Log.d(Constant.TAG, "RPW: appAppInfo size: "+ appAppInfo.size());
		Log.d(Constant.TAG, "RPW: mAppInfo size: "+ mAppInfo.size());
	}
	
	
	private void loadAdapter(){
		app_list_adapter = new AppListAdapter(this);
        AppActivity.this.setListAdapter(app_list_adapter);
	}
	

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mProgressDialog.dismiss();
				loadAdapter();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	
	class AppListAdapter extends BaseAdapter {
		Context mContext = null;
		public AppListAdapter(Context context) {
		    mContext = context;
		}
		
		public int getCount() {
		    return mAppInfo.size();
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
		
		    appInfo = mAppInfo.get(position);
		    
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
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bl = new Bundle();
		bl.putString(Constant.KEY_PROC_NAME, mAppInfo.get(position).processName);
		bl.putString(Constant.KEY_PKG_NAME, mAppInfo.get(position).packageName);
		intent.putExtras(bl);
		startActivity(intent);
    }
    
}
