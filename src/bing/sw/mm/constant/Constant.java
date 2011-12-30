package bing.sw.mm.constant;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
	
    // Reference:
    // http://stackoverflow.com/questions/2298208/how-to-discover-memory-usage-of-my-application-in-android
	public static int getPssTotal(String proc_name, ActivityManager am){
		int pid = Constant.NOT_EXIST;
		int pssTotal = 0;
		
		ArrayList<RunningAppProcessInfo> runningAppProcesses = (ArrayList<RunningAppProcessInfo>) am.getRunningAppProcesses();
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
			MemoryInfo[] memoryInfoArray = am.getProcessMemoryInfo(pids);
			pssTotal = memoryInfoArray[0].getTotalPss();
		}
		Log.i(Constant.TAG, "Constant.getPssTotal-pid: " + pid);
		Log.i(Constant.TAG, "Constant.getPssTotal-getTotalPss: " + pssTotal);
		return pssTotal;
	}
	
	public static ArrayList<ApplicationInfo> getAppInfoSortedWithAppName(PackageManager pm){
		ArrayList<ApplicationInfo> appAppInfo = (ArrayList<ApplicationInfo>) pm.getInstalledApplications(
													PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(appAppInfo, new ComparatorApplicationInfo(pm));
		return appAppInfo;
	}
	public static ArrayList<String> getRunningAppProcessesNames(ActivityManager am){
		ArrayList<RunningAppProcessInfo> runningAppProcesses = (ArrayList<RunningAppProcessInfo>) am.getRunningAppProcesses();
		ArrayList<String> runningAppProcessesNames = new ArrayList<String>();
		for(RunningAppProcessInfo runningAppProcess: runningAppProcesses){
			runningAppProcessesNames.add(runningAppProcess.processName);
		}
		return runningAppProcessesNames;
	}
	public static ArrayList<ApplicationInfo> getRunningAppInfoSortedWithAppName(ActivityManager am, PackageManager pm){
		ArrayList<ApplicationInfo> runningAppInfo = new ArrayList<ApplicationInfo>();;
		ArrayList<String> processes = getRunningAppProcessesNames(am);
		
		ArrayList<ApplicationInfo> all_app_info = getAppInfoSortedWithAppName(pm);
		for(ApplicationInfo app: all_app_info){
			if(processes.contains(app.processName)){
				runningAppInfo.add(app);
			}
		}
		
		return runningAppInfo;
	}
}


