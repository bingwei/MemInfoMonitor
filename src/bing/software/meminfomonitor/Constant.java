package bing.software.meminfomonitor;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Debug.MemoryInfo;
import android.util.Log;

public class Constant {
	public static final String TAG = "meminfo_monitor";
	public static final int NOT_EXIST = -1;
	public static final int ON = 0;
	public static final int OFF = 1;
	public static final String STATUS = "status";
	public static final String _MEM_STATUS = "MEM_STATUS";
	public static final String _SERVICE_STATUS = "SERVICE_STATUS";
	
	public static int getPssTotal(String proc_name, ActivityManager am){
		int pid = Constant.NOT_EXIST;
		int pssTotal = 0;
		ArrayList<RunningAppProcessInfo> runningAppProcesses;
		MemoryInfo[] memoryInfoArray;
		
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
