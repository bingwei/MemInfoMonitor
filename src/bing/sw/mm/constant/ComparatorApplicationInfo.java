package bing.sw.mm.constant;

import java.util.Comparator;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ComparatorApplicationInfo implements Comparator<ApplicationInfo>{
	PackageManager pm;
	public ComparatorApplicationInfo(PackageManager pm){
		this.pm = pm;
	}
	 public int compare(ApplicationInfo arg0, ApplicationInfo arg1) {
		 int flag=arg0.loadLabel(pm).toString().compareTo(
				 arg1.loadLabel(pm).toString());
		 	if(flag==0){
		 		return arg0.packageName.compareTo(
		 				arg1.packageName);
		 	}else{
		 		return flag;
		 	}  
	 }
}
