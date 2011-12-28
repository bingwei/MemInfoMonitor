package bing.software.meminfomonitor;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Update
 * 1. Add thread/service to record pssTotal into file (Half)
 * 2. Update current view with gotten pssTotal (DONE)
 * 3. Add tip in notice bar for user to remember to stop it (DONE)
 * Bug
 * 1. After second onStop, the mem_status become to OFF (Fixed)
*/
public class AppInfoMonitor extends Activity{
	private String procName;
	private String pkgName;
	private String appName;
	private TextView tv_monitor_app_name;
	private TextView tv_monitor_app_pkg_name;
	private TextView tmp_monitor_pss_total; //tmp, will be deleted later
	private Button btn_meminfo_start;
	private Button btn_meminfo_stop;
	private ImageView img_monitor_app_icon;
	private int MEM_STATUS;
	private DataReceiver dataReceiver;
	private boolean flag;
//	private ActivityManager activityManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfo_monitor);

		tv_monitor_app_pkg_name = (TextView)findViewById(R.id.tv_monitor_app_pkg_name);
		tv_monitor_app_name = (TextView)findViewById(R.id.tv_monitor_app_name);
		tmp_monitor_pss_total = (TextView)findViewById(R.id.tmp_monitor_pss_total);
		btn_meminfo_start = (Button)findViewById(R.id.btn_meminfo_start);
		btn_meminfo_stop = (Button)findViewById(R.id.btn_meminfo_stop);
		img_monitor_app_icon = (ImageView)findViewById(R.id.img_monitor_app_icon);
		
		// Get data from AppInfoActivity
		Intent intent = getIntent();
		procName = intent.getExtras().getString("PROC_NAME");
		pkgName = intent.getExtras().getString("PKG_NAME");
		
		
		// Set image, app name and pkg name
		PackageManager pm = getApplicationContext().getPackageManager();
		ArrayList<ApplicationInfo> al_app_info = 
				(ArrayList<ApplicationInfo>) pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(ApplicationInfo app: al_app_info){
			if(app.packageName.equals(pkgName)){
				appName = app.loadLabel(getPackageManager()).toString();
				tv_monitor_app_name.setText(appName);
				img_monitor_app_icon.setImageDrawable(app.loadIcon(getPackageManager()));
				break;
			}
		}
		tv_monitor_app_pkg_name.setText(pkgName);
		
		
		SharedPreferences settings = getSharedPreferences(Constant.STATUS, MODE_PRIVATE);
		MEM_STATUS = settings.getInt(Constant._MEM_STATUS, Constant.OFF);
        buttonMonitor(MEM_STATUS);
        Log.e(Constant.TAG, "AppInfoMonitor-onCreate-retrieved mem_status: " + MEM_STATUS);
		
		btn_meminfo_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent;
				MEM_STATUS = Constant.ON;
				buttonMonitor(MEM_STATUS);

				myIntent = new Intent(AppInfoMonitor.this, AppInfoService.class);
				myIntent.putExtra("PROC_NAME", procName);
				myIntent.putExtra("APP_NAME", appName);
				myIntent.putExtra("PKG_NAME", pkgName);
                startService(myIntent);
			}
		});
		btn_meminfo_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent;
				MEM_STATUS = Constant.OFF;
				buttonMonitor(MEM_STATUS);
                myIntent = new Intent();
                myIntent.setAction("bing.software.meminfomonitor.AppInfoService");
                myIntent.putExtra("STATUS", Constant.OFF);
                sendBroadcast(myIntent);
                // To be converted chart
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		SharedPreferences settings = getSharedPreferences(Constant.STATUS, MODE_PRIVATE);
		int mem_status = settings.getInt(Constant._MEM_STATUS, Constant.OFF);
		Log.e(Constant.TAG, "AppInfoMonitor-onStart-retrieved mem_status: " + MEM_STATUS);
        buttonMonitor(mem_status);
        Log.e(Constant.TAG, "onStart: " + mem_status);

		// Set broadcast
    	dataReceiver = new DataReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction("bing.software.meminfomonitor.AppInfoMonitor");
    	registerReceiver(dataReceiver, filter);
        super.onStart();
	}
	@Override
	protected void onStop() {
	    unregisterReceiver(dataReceiver);
		SharedPreferences settings = getSharedPreferences(Constant.STATUS, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.putInt(Constant._MEM_STATUS, MEM_STATUS);
		editor.putBoolean(Constant._SERVICE_STATUS, flag);
		Log.e(Constant.TAG, "AppInfoMonitor-onStop-save mem_status: " + MEM_STATUS);
	    // Commit the edits!
		editor.commit();

	    super.onStop();
	}

	
	
	private void buttonMonitor(int status){
		if(status == Constant.OFF){
			btn_meminfo_start.setEnabled(true);
			btn_meminfo_stop.setEnabled(false);
		}else{
			btn_meminfo_start.setEnabled(false);
			btn_meminfo_stop.setEnabled(true);
		}
	}
	class DataReceiver extends BroadcastReceiver{
		private int pss_total;
		private String pkg_name;
		private String app_name;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			pss_total = intent.getIntExtra("PSS_TOTAL", 0);
			pkg_name = intent.getStringExtra("PKG_NAME");
			app_name = intent.getStringExtra("APP_NAME");
			flag = intent.getBooleanExtra("FLAG", false);
			if(pkg_name.equals(pkgName)){
				tmp_monitor_pss_total.setText(String.valueOf(pss_total));  
			}else{
				tmp_monitor_pss_total.setText(String.format("%s(%s) is being recorded.\n" +
						"You could click \"Stop\" button to stop it", app_name, pkg_name));
			}
			Log.d(Constant.TAG, "AppInfoMonitor-DataReceiver-received pss_total: " + pss_total);
		}
		public int getPssTotal(){
			return pss_total;
		}
		public String getPkgName(){
			return pkg_name;
		}
		public String getAppName(){
			return app_name;
		}
	}
}
