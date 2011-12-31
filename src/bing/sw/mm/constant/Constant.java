package bing.sw.mm.constant;

import java.io.File;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug.MemoryInfo;
import android.os.Environment;
import android.util.Log;

public class Constant {
	public static final String TAG = "meminfo_monitor";
	public static final int ALERT_ID = 1;
	public static final int NOT_EXIST = -1;
	public static final int ON = 0;
	public static final int OFF = 1;
	public static final String TAB_SPEC_APP = "application";
	public static final String TAB_SPEC_PROCESS = "process";
	public static final String TAB_SPEC_SERVICE = "service";
	public static final String DEFAULT_REG_EXPRESSION = "";
	public static final String SP_STATUS = "SP_STATUS";
	public static final String SP_BOOT_UP_TIME = "SP_BOOT_UP_TIME";
	public static final String KEY_BOOT_UP_TIME = "BOOT_UP_TIME";
	public static final String KEY_MEM_STATUS = "MEM_STATUS";
	public static final String KEY_SERVICE_STATUS = "SERVICE_STATUS";
	public static final String KEY_PROC_NAME = "PROC_NAME";
	public static final String KEY_APP_NAME = "APP_NAME";
	public static final String KEY_PKG_NAME = "PKG_NAME";
	public static final String KEY_FLAG = "FLAG";
	public static final String KEY_PSS_TOTAL = "PSS_TOTAL";
	public static final String KEY_STATUS = "STATUS";
	public static final String KEY_IS_SAVE_TO_FILE = "IS_SAVE_TO_FILE";
	public static final String ACTION_APPINFOSERVICE = "bing.sw.mm.service.AppInfoService";
	public static final String ACTION_APPINFOMONITOR = "bing.sw.mm.monitor.AppInfoMonitor";
	public static final String MEMINFO_SAVED_FOLDER_NAME = ".MemoryInfoMonitor";
	public static String mSDCardPath;
	public static String mMemInfoSavedFolderPath;
	
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
//		Log.i(Constant.TAG, "Constant.getPssTotal-pid: " + pid);
//		Log.i(Constant.TAG, "Constant.getPssTotal-getTotalPss: " + pssTotal);
		return pssTotal;
	}
	
	public static long getUpTime(Activity context) { 
		SharedPreferences sharedPreferences=context.getSharedPreferences(Constant.SP_BOOT_UP_TIME, Context.MODE_PRIVATE); 
		long seconds= sharedPreferences.getLong(Constant.KEY_BOOT_UP_TIME, new Date(System.currentTimeMillis()).getTime()); 
		return seconds; 
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
	
	public static ArrayList<RunningServiceInfo> getRunningServiceInfoSortedWithProcess(ActivityManager am){
		ArrayList<RunningServiceInfo> runningServiceInfo = (ArrayList<RunningServiceInfo>) am.getRunningServices(100);
		Collections.sort(runningServiceInfo, new ComparatorRunningServiceInfo(am));
		return runningServiceInfo;
	}
	
	public static boolean generateFolder(){
		String mSDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		mMemInfoSavedFolderPath = new File(mSDCardPath + File.separator + MEMINFO_SAVED_FOLDER_NAME).getAbsolutePath();
        Log.d(TAG, String.format("mMemInfoSavedFolderPath: %s", mMemInfoSavedFolderPath)); 
        File folder = new File(mMemInfoSavedFolderPath);
        
        boolean isFolderReady = false;
        if(!folder.exists()){
        	Log.d(Constant.TAG, "folder not exists.");
        	isFolderReady = folder.mkdir();
        	Log.d(Constant.TAG, "folder created.");
        	isFolderReady = true;
        }else{
        	isFolderReady = true;
        	Log.d(Constant.TAG, "folder already existed.");
        }
        return isFolderReady;
    }
    
	public static boolean isSDCardAvailable() {
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        
        if(false == mExternalStorageAvailable || false == mExternalStorageWriteable){
        	return false;
        }else{
        	return true;
        }
	}
	public static String time4csv(long time){
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(time);
//		return cal.getTime().toLocaleString();
		return MessageFormat.format("{0,date,MM/dd/yy HH:mm:ss:ms}" ,//12/27/11 10:18:13
                new Object[]  {
                new java.sql.Date(time)
            });
	}
	public static String time4FileName(long time){
		return MessageFormat.format("{0,date,yyyy_MM_dd_HHmmss}" ,//12/27/11 10:18:13
				new Object[]  {
				new java.sql.Date(time)
		});
	}
}


