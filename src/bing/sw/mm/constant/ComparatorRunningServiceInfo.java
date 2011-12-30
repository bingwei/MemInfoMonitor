package bing.sw.mm.constant;

import java.util.Comparator;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

public class ComparatorRunningServiceInfo implements Comparator<RunningServiceInfo>{
	ActivityManager am;
	public ComparatorRunningServiceInfo(ActivityManager am){
		this.am = am;
	}
	 public int compare(RunningServiceInfo arg0, RunningServiceInfo arg1) {
		 int flag=arg0.process.compareTo(
				 arg1.process);
		 	if(flag==0){
		 		return arg0.pid - arg1.pid;
		 	}else{
		 		return flag;
		 	}  
	 }
}
