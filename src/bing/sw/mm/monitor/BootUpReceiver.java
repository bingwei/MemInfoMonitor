package bing.sw.mm.monitor;

import java.sql.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import bing.sw.mm.constant.Constant;

public class BootUpReceiver extends BroadcastReceiver{ 
	 private SharedPreferences sharedPreferences;
	 private Editor editor;
	 @Override 
	 public void onReceive(Context context, Intent intent) { 
	 if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){ 
	 
	 sharedPreferences = context.getSharedPreferences(
			 Constant.SP_BOOT_UP_TIME, 
			 Context.MODE_PRIVATE); 
	 editor = sharedPreferences.edit(); 
	 
	 editor.putLong(Constant.KEY_BOOT_UP_TIME, new Date(System.currentTimeMillis()).getTime()); 
	 
	 editor.commit();
	 } 
	 
	 } 
} 
