package bing.software.meminfomonitor;


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

// Reference
// http://www.eoeandroid.com/thread-45221-1-1.html
public class AppInfoService extends Service{
	private static final int ALERT_ID = 1;
	private boolean flag;
	private StatusReceiver statusReceiver;
	private ActivityManager mActivityManager;
	private NotificationManager mNotificationManager;
	private Notification notification;
	private String procName;
	private String appName;
	private String pkgName;

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
		 Toast.makeText(this, "My Service Started", Toast.LENGTH_SHORT).show();  
		 procName = intent.getStringExtra("PROC_NAME");
		 appName = intent.getStringExtra("APP_NAME");
		 pkgName = intent.getStringExtra("PKG_NAME");
		 IntentFilter filter = new IntentFilter();
		 filter.addAction("bing.software.meminfomonitor.AppInfoService");
		 registerReceiver(statusReceiver, filter);
		 Log.d(Constant.TAG, "AppInfoService-onStartCommand-intent.getStringExtra(\"PROC_NAME\"): " + procName); 
		 notificationMonitor();
		 // Start loop to get pss total value
		 new Thread(new mPssTotalThread(procName, mActivityManager)).start();
//		 return START_STICKY;
		 return START_REDELIVER_INTENT;
	 }  
  
    @Override  
    public void onDestroy() {  
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_SHORT).show();  
        unregisterReceiver(statusReceiver);
        mNotificationManager.cancel(ALERT_ID);
        super.onDestroy();
    }  
    
    public void notificationMonitor(){
		Intent notifyIntent = new Intent();
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		
		PendingIntent appIntent=PendingIntent.getActivity(this,0,notifyIntent,0);
		int icon = R.drawable.notification_alert;
		CharSequence tickerText = "MemoryInfo is recording " + appName +" into SD card!";
		long when = System.currentTimeMillis();
		notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(this,"Warning!",appName + " is still being recorded!",appIntent);
		mNotificationManager.notify(ALERT_ID, notification);
    }
      
    
	class mPssTotalThread extends Thread{
		String proc_name;
		ActivityManager am;
		int pssTotal;
		Intent intent;
		public mPssTotalThread(String proc_name, ActivityManager activityManager){
			this.proc_name = proc_name;
			this.am = activityManager;
		}
		// Refer 
		// http://stackoverflow.com/questions/2298208/how-to-discover-memory-usage-of-my-application-in-android
		public void run(){
			while(flag){
				//------------Keep this only for test----------------
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.e(Constant.TAG, "AppInfoService-mPssTotalThread-" + e.toString());
				}
				//----------------------------------------------------
				pssTotal = Constant.getPssTotal(proc_name, am);
				intent = new Intent();
                intent.setAction("bing.software.meminfomonitor.AppInfoMonitor");
                intent.putExtra("PSS_TOTAL",pssTotal);
                intent.putExtra("PKG_NAME",pkgName);
                intent.putExtra("APP_NAME",appName);
                intent.putExtra("FLAG",flag);
                sendBroadcast(intent);
			}
		}
	}
	
	
    private class StatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("STATUS", -1);
            if(status == Constant.OFF){                           
            	flag = false;
            	stopSelf();
            }
        }
    }

}
