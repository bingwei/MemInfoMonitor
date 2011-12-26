package bing.software.meminfomonitor;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AppInfoMonitor extends Activity{
	private final String TAG = "meminfo_monitor";
	private final int NOT_EXIST = -1;
	private Intent intent;
	private String proc_name;
	private TextView monitor_proc_name;
	private Button btn_meminfo_monitor;
	private ArrayList<RunningAppProcessInfo> runningAppProcesses;
	private ActivityManager activityManager;
	private Map<Integer, String> pidMap;
	private android.os.Debug.MemoryInfo[] memoryInfoArray;
	private int pssTotal = 0;
	private int pid = NOT_EXIST;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfo_monitor);
		
		monitor_proc_name = (TextView)findViewById(R.id.monitor_proc_name);
		btn_meminfo_monitor = (Button)findViewById(R.id.btn_meminfo_monitor);
		intent = getIntent();
		proc_name = intent.getExtras().getString("PROC_NAME");
		monitor_proc_name.setText(proc_name);
		Log.i(TAG, "proc_name: " + proc_name);

		btn_meminfo_monitor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBtnMeminfoMonitorClick();
			}
		});
		
	}
	
	private void onBtnMeminfoMonitorClick(){
		pid = NOT_EXIST;
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		runningAppProcesses = (ArrayList<RunningAppProcessInfo>) activityManager.getRunningAppProcesses();

		pidMap = new TreeMap<Integer, String>();
		for(int i = 0; i < runningAppProcesses.size(); i++){
			if(proc_name.equals(runningAppProcesses.get(i).processName)){
				pid = runningAppProcesses.get(i).pid;
				break;
			}
		}
		Log.i(TAG, "pid: " + pid);
		if(pid == NOT_EXIST){
			pssTotal = 0;
		}else{
			int pids[] = {pid};
			memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
			for(MemoryInfo pidMemoryInfo: memoryInfoArray)
			{
				pssTotal = pidMemoryInfo.getTotalPss();
			}
		}
		Log.i(TAG, "getTotalPss: " + pssTotal);
	}
	
	
}
