package bing.sw.mm.constant;

import java.util.Comparator;

import android.app.ActivityManager.RunningAppProcessInfo;

public class ComparatorRunningAppProcessInfo implements Comparator<RunningAppProcessInfo>{
	public ComparatorRunningAppProcessInfo(){
		
	}
	 public int compare(RunningAppProcessInfo arg0, RunningAppProcessInfo arg1) {
		 int flag=arg0.processName.compareTo(arg1.processName);
		 	if(flag==0){
		 		return arg0.pid - arg1.pid;
		 	}else{
		 		return flag;
		 	}  
	 }
}