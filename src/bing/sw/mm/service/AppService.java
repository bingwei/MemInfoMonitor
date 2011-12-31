package bing.sw.mm.service;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import bing.sw.mm.R;
import bing.sw.mm.main;
import bing.sw.mm.constant.Constant;

// Reference
// http://www.eoeandroid.com/thread-45221-1-1.html
public class AppService extends Service{
	private boolean flag;
	private StatusReceiver statusReceiver;
	private ActivityManager mActivityManager;
	private NotificationManager mNotificationManager;
	private Notification notification;
	private String procName;
	private String appName;
	private String pkgName;
	private int isSaveToFile;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	 @Override  
    public void onCreate() {  
        mActivityManager= (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        mNotificationManager= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        flag = true;
        statusReceiver = new StatusReceiver();
        super.onCreate();
    }  
	 @Override
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 Toast.makeText(this, getString(R.string.recording_started), Toast.LENGTH_SHORT).show();  
		 procName = intent.getStringExtra(Constant.KEY_PROC_NAME);
		 appName = intent.getStringExtra(Constant.KEY_APP_NAME);
		 pkgName = intent.getStringExtra(Constant.KEY_PKG_NAME);
		 isSaveToFile = intent.getIntExtra(Constant.KEY_IS_SAVE_TO_FILE, Constant.OFF);
		 IntentFilter filter = new IntentFilter();
		 filter.addAction(Constant.ACTION_APPINFOSERVICE);
		 registerReceiver(statusReceiver, filter);
		 Log.d(Constant.TAG, "AppInfoService-onStartCommand-intent.getStringExtra(\"PROC_NAME\"): " + procName); 
		 notificationMonitor();
		 // Start loop to get pss total value
		 new Thread(new mPssTotalThread(procName, mActivityManager, isSaveToFile)).start();
//		 return START_STICKY;
		 return START_REDELIVER_INTENT;
	 }  
  
    @Override  
    public void onDestroy() { 
        Toast.makeText(this, getString(R.string.recording_stopped), Toast.LENGTH_SHORT).show();  
        unregisterReceiver(statusReceiver);
        mNotificationManager.cancel(Constant.ALERT_ID);
        super.onDestroy();
    }  
    
    public void notificationMonitor(){
		Intent notifyIntent = new Intent(this, main.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		
		PendingIntent appIntent=PendingIntent.getActivity(this,0,notifyIntent,0);
		int icon = R.drawable.notification_alert;
		CharSequence tickerText = String.format(getString(R.string.notification), appName);
		long when = System.currentTimeMillis();
		notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(this,getString(R.string.notification_title),
				String.format(getString(R.string.notification_msg), appName),appIntent);
		mNotificationManager.notify(Constant.ALERT_ID, notification);
    }
    

      
    
	class mPssTotalThread extends Thread{
		String proc_name;
		ActivityManager am;
		int pssTotal;
		Intent intent;
		int isSaveToFile;
		public mPssTotalThread(String proc_name, ActivityManager activityManager, int isSaveToFile){
			this.proc_name = proc_name;
			this.am = activityManager;
			this.isSaveToFile = isSaveToFile;
		}

		
		public void sendMsg(){
			pssTotal = Constant.getPssTotal(proc_name, am);
			intent = new Intent();
            intent.setAction(Constant.ACTION_APPINFOMONITOR);
            intent.putExtra(Constant.KEY_PSS_TOTAL,pssTotal);
            intent.putExtra(Constant.KEY_PKG_NAME,pkgName);
            intent.putExtra(Constant.KEY_APP_NAME,appName);
            intent.putExtra(Constant.KEY_FLAG,flag);
            sendBroadcast(intent);
		}
		// Refer 
		// http://stackoverflow.com/questions/2298208/how-to-discover-memory-usage-of-my-application-in-android
		// http://blog.csdn.net/aerchi/article/details/5364056 (csv write & read)
		public void run(){
			if(isSaveToFile == Constant.ON){
				Log.d(Constant.TAG, "From service-mMemInfoSavedFolderPath: " + Constant.mMemInfoSavedFolderPath);
				File myFile = new File(Constant.mMemInfoSavedFolderPath + File.separator + pkgName 
						+ "." + Constant.time4FileName(System.currentTimeMillis()) + ".csv");
				try {
					BufferedWriter mBuffWriter = new BufferedWriter(new FileWriter(myFile, true));
					while(flag){
						sendMsg();
						mBuffWriter.newLine();
						mBuffWriter.write(Constant.time4csv(System.currentTimeMillis()) + "," + pssTotal);
					}
					mBuffWriter.flush();
					mBuffWriter.close();
				} catch (FileNotFoundException e) {
					Log.e(Constant.TAG, "FileNotFoundException: " + e.toString());
				} catch (IOException e) {
					Log.e(Constant.TAG, "IOException: " + e.toString());
				}
				
			}else{
				while(flag){
				//------------Keep this only for test----------------
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							Log.e(Constant.TAG, "AppInfoService-mPssTotalThread-" + e.toString());
//						}
				//----------------------------------------------------
				sendMsg();
				}
			}
		}
		

	}
	
	
    private class StatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(Constant.KEY_STATUS, -1);
            if(status == Constant.OFF){                           
            	flag = false;
            	stopSelf();
            }
        }
    }

}
