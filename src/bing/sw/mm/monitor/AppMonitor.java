package bing.sw.mm.monitor;

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
import bing.sw.mm.R;
import bing.sw.mm.constant.Constant;
import bing.sw.mm.service.AppService;


/**
 * Update
 * 1. Add thread/service (DONE)
 * 2. Update current view with gotten pssTotal (DONE)
 * 3. Add tip in notice bar for user to remember to stop it (DONE)
 * 4. Record pssTotal into file (finish this later)
 * Bug
 * 1. After second onStop, the mem_status become to OFF (Fixed)
*/
public class AppMonitor extends Activity{
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_monitor);

		tv_monitor_app_pkg_name = (TextView)findViewById(R.id.tv_monitor_app_pkg_name);
		tv_monitor_app_name = (TextView)findViewById(R.id.tv_monitor_app_name);
		tmp_monitor_pss_total = (TextView)findViewById(R.id.tmp_monitor_pss_total);
		btn_meminfo_start = (Button)findViewById(R.id.btn_meminfo_start);
		btn_meminfo_stop = (Button)findViewById(R.id.btn_meminfo_stop);
		img_monitor_app_icon = (ImageView)findViewById(R.id.img_monitor_app_icon);
		
		// Get data from AppInfoActivity
		Intent intent = getIntent();
		procName = intent.getExtras().getString(Constant.KEY_PROC_NAME);
		pkgName = intent.getExtras().getString(Constant.KEY_PKG_NAME);
		
		
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
		
		
		SharedPreferences settings = getSharedPreferences(Constant.SP_STATUS, MODE_PRIVATE);
		MEM_STATUS = settings.getInt(Constant.KEY_MEM_STATUS, Constant.OFF);
        buttonMonitor(MEM_STATUS);
//        Log.d(Constant.TAG, "AppInfoMonitor-onCreate-retrieved mem_status: " + MEM_STATUS);
		
		btn_meminfo_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent;
				MEM_STATUS = Constant.ON;
				buttonMonitor(MEM_STATUS);

				myIntent = new Intent(AppMonitor.this, AppService.class);
				myIntent.putExtra(Constant.KEY_PROC_NAME, procName);
				myIntent.putExtra(Constant.KEY_APP_NAME, appName);
				myIntent.putExtra(Constant.KEY_PKG_NAME, pkgName);
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
                myIntent.setAction(Constant.ACTION_APPINFOSERVICE);
                myIntent.putExtra(Constant.KEY_STATUS, Constant.OFF);
                sendBroadcast(myIntent);
                // To be converted chart
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		SharedPreferences settings = getSharedPreferences(Constant.SP_STATUS, MODE_PRIVATE);
		int mem_status = settings.getInt(Constant.KEY_MEM_STATUS, Constant.OFF);
		Log.d(Constant.TAG, "AppInfoMonitor-onStart-retrieved mem_status: " + MEM_STATUS);
        buttonMonitor(mem_status);
        Log.d(Constant.TAG, "onStart: " + mem_status);

		// Set broadcast
    	dataReceiver = new DataReceiver();
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(Constant.ACTION_APPINFOMONITOR);
    	registerReceiver(dataReceiver, filter);
        super.onStart();
	}
	@Override
	protected void onStop() {
	    unregisterReceiver(dataReceiver);
		SharedPreferences settings = getSharedPreferences(Constant.SP_STATUS, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.putInt(Constant.KEY_MEM_STATUS, MEM_STATUS);
		editor.putBoolean(Constant.KEY_SERVICE_STATUS, flag);
		Log.d(Constant.TAG, "AppInfoMonitor-onStop-save mem_status: " + MEM_STATUS);
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
			pss_total = intent.getIntExtra(Constant.KEY_PSS_TOTAL, 0);
			pkg_name = intent.getStringExtra(Constant.KEY_PKG_NAME);
			app_name = intent.getStringExtra(Constant.KEY_APP_NAME);
			flag = intent.getBooleanExtra(Constant.KEY_FLAG, false);
			if(pkg_name.equals(pkgName)){
				tmp_monitor_pss_total.setText(String.valueOf(pss_total));  
			}else{
				tmp_monitor_pss_total.setText(String.format(getString(R.string.another_service_running), app_name, pkg_name));
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
