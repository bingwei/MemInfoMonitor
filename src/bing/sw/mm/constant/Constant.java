package bing.sw.mm.constant;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Debug.MemoryInfo;
import android.util.Log;

public class Constant {
	public static final String TAG = "meminfo_monitor";
	public static final int ALERT_ID = 1;
	public static final int NOT_EXIST = -1;
	public static final int ON = 0;
	public static final int OFF = 1;
	public static final String SP_STATUS = "status";
	public static final String KEY_MEM_STATUS = "MEM_STATUS";
	public static final String KEY_SERVICE_STATUS = "SERVICE_STATUS";
	public static final String KEY_PROC_NAME = "PROC_NAME";
	public static final String KEY_APP_NAME = "APP_NAME";
	public static final String KEY_PKG_NAME = "PKG_NAME";
	public static final String KEY_FLAG = "FLAG";
	public static final String KEY_PSS_TOTAL = "PSS_TOTAL";
	public static final String KEY_STATUS = "STATUS";
	public static final String ACTION_APPINFOSERVICE = "bing.sw.mm.service.AppInfoService";
	public static final String ACTION_APPINFOMONITOR = "bing.sw.mm.monitor.AppInfoMonitor";
	private static ArrayList<RunningAppProcessInfo> runningAppProcesses;
	private static MemoryInfo[] memoryInfoArray;
	
    // Reference:
    // http://stackoverflow.com/questions/2298208/how-to-discover-memory-usage-of-my-application-in-android
	public static int getPssTotal(String proc_name, ActivityManager am){
		int pid = Constant.NOT_EXIST;
		int pssTotal = 0;
		
		runningAppProcesses = (ArrayList<RunningAppProcessInfo>) am.getRunningAppProcesses();
		for(RunningAppProcessInfo runningAppProcess: runningAppProcesses){
			if(proc_name.equals(runningAppProcess.processName)){
				pid = runningAppProcess.pid;
				break;
			}
		}
		if(pid == Constant.NOT_EXIST){
			pssTotal = 0;
		}else{
			int pids[] = {pid};
			memoryInfoArray = am.getProcessMemoryInfo(pids);
			pssTotal = memoryInfoArray[0].getTotalPss();
		}
		Log.i(Constant.TAG, "Constant.getPssTotal-pid: " + pid);
		Log.i(Constant.TAG, "Constant.getPssTotal-getTotalPss: " + pssTotal);
		return pssTotal;
	}
}
