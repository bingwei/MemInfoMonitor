package bing.software.meminfomonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Update
 * 1. Add thread/service to record pssTotal into file
 * 2. Update current view with gotten pssTotal
 * 3. Add tip in notice bar for user to remember to stop it
 * Bug
 * 1. After second onStop, the mem_status become to OFF
*/
public class AppInfoMonitor extends Activity{
	private String proc_name;
	private TextView monitor_proc_name;
	private TextView monitor_pkg_name;
	private TextView tmp_monitor_pss_total; //tmp, will be deleted later
	private Button btn_meminfo_start;
	private Button btn_meminfo_stop;
	private int MEM_STATUS = Constant.OFF;
	private int REGISTER_STATUS = Constant.OFF;
	private DataReceiver dataReceiver;
	private final String STATUS = "status";
	private final String _MEM_STATUS = "MEM_STATUS";
	private final String _REGISTER_STATUS = "REGISTER_STATUS";
//	private ActivityManager activityManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfo_monitor);
		
		monitor_pkg_name = (TextView)findViewById(R.id.monitor_pkg_name);
		monitor_proc_name = (TextView)findViewById(R.id.monitor_proc_name);
		tmp_monitor_pss_total = (TextView)findViewById(R.id.tmp_monitor_pss_total);
		btn_meminfo_start = (Button)findViewById(R.id.btn_meminfo_start);
		btn_meminfo_stop = (Button)findViewById(R.id.btn_meminfo_stop);
		Intent intent = getIntent();
		proc_name = intent.getExtras().getString("PROC_NAME");
		monitor_proc_name.setText(proc_name);
		monitor_pkg_name.setText(intent.getExtras().getString("PKG_NAME"));
		Log.i(Constant.TAG, "proc_name: " + proc_name);
		
		btn_meminfo_start.setEnabled(true);
		btn_meminfo_stop.setEnabled(false);
		
		
        

		btn_meminfo_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent;
				MEM_STATUS = Constant.ON;
				buttonMonitor(MEM_STATUS);

				myIntent = new Intent(AppInfoMonitor.this, AppInfoService.class);
				myIntent.putExtra("PROC_NAME", proc_name);
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
                tmp_monitor_pss_total.setText("Stop now");
//                unregisterReceiver(dataReceiver);
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		SharedPreferences settings = getSharedPreferences(STATUS, MODE_PRIVATE);
		int mem_status = settings.getInt(_MEM_STATUS, Constant.OFF);
		Log.d(Constant.TAG, "retrieved mem_status: " + mem_status);
		int register_status = settings.getInt(_REGISTER_STATUS, Constant.OFF);
        buttonMonitor(mem_status);

		// Set broadcast
//        if(register_status == Constant.OFF){
        	dataReceiver = new DataReceiver();
        	IntentFilter filter = new IntentFilter();
        	filter.addAction("bing.software.meminfomonitor.AppInfoMonitor");
        	registerReceiver(dataReceiver, filter);
//        }
        super.onStart();
	}
	@Override
	protected void onStop() {
	    unregisterReceiver(dataReceiver);
		SharedPreferences settings = getSharedPreferences(STATUS, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(_MEM_STATUS, MEM_STATUS);
		editor.putInt(_REGISTER_STATUS, REGISTER_STATUS);
		Log.d(Constant.TAG, "saved mem_status: " + MEM_STATUS);
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
		@Override
		public void onReceive(Context context, Intent intent) {
			pss_total = intent.getIntExtra("PSS_TOTAL", 0);
			tmp_monitor_pss_total.setText(String.valueOf(pss_total));  
			Log.d(Constant.TAG, "received pss_total: " + pss_total);
		}
		public int getPssTotal(){
			return pss_total;
		}
	}
}
