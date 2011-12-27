package bing.software.meminfomonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
*/
public class AppInfoMonitor extends Activity{
	private String proc_name;
	private TextView monitor_proc_name;
	private TextView monitor_pkg_name;
	private TextView tmp_monitor_pss_total; //tmp, will be deleted later
	private Button btn_meminfo_monitor;
	private int MEM_STATUS = Constant.MEM_STOP;
	private DataReceiver dataReceiver;
//	private ActivityManager activityManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfo_monitor);
		
		monitor_pkg_name = (TextView)findViewById(R.id.monitor_pkg_name);
		monitor_proc_name = (TextView)findViewById(R.id.monitor_proc_name);
		tmp_monitor_pss_total = (TextView)findViewById(R.id.tmp_monitor_pss_total);
		btn_meminfo_monitor = (Button)findViewById(R.id.btn_meminfo_monitor);
		Intent intent = getIntent();
		proc_name = intent.getExtras().getString("PROC_NAME");
		monitor_proc_name.setText(proc_name);
		monitor_pkg_name.setText(intent.getExtras().getString("PKG_NAME"));
		Log.i(Constant.TAG, "proc_name: " + proc_name);

		btn_meminfo_monitor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent;
				if(MEM_STATUS == Constant.MEM_STOP){
					MEM_STATUS = Constant.MEM_RECORDING;
					// To be converted chart
					btn_meminfo_monitor.setText(getString(R.string.stop_record_meminfo));
					myIntent = new Intent(AppInfoMonitor.this, AppInfoService.class);
					myIntent.putExtra("PROC_NAME", proc_name);
                    startService(myIntent);
				}else{
					MEM_STATUS = Constant.MEM_STOP;
					// To be converted chart
					btn_meminfo_monitor.setText(getString(R.string.start_record_meminfo));
                    myIntent = new Intent();
                    myIntent.setAction("bing.software.meminfomonitor.AppInfoService");
                    myIntent.putExtra("STATUS", Constant.MEM_STOP);
                    sendBroadcast(myIntent);
                    tmp_monitor_pss_total.setText("Stop now");
				}
			}
		});
		
	}
	
	@Override
	protected void onStart() {
	        dataReceiver = new DataReceiver();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("bing.software.meminfomonitor.AppInfoMonitor");
	        registerReceiver(dataReceiver, filter);
	        super.onStart();
	}
	@Override
	protected void onStop() {
	        unregisterReceiver(dataReceiver);
	        super.onStop();
	}
	
	class DataReceiver extends BroadcastReceiver{
		private int pss_total;
		@Override
		public void onReceive(Context context, Intent intent) {
			pss_total = intent.getIntExtra("PSS_TOTAL", 0);
			tmp_monitor_pss_total.setText(String.valueOf(pss_total));  
		}
		public int getPssTotal(){
			return pss_total;
		}
	}
}
