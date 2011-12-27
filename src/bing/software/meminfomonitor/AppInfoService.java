package bing.software.meminfomonitor;

import android.app.ActivityManager;
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
	private boolean flag;
	private StatusReceiver statusReceiver;
	private ActivityManager mActivityManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	 @Override  
    public void onCreate() {  
        Log.d(Constant.TAG, "Service onCreate");  
        mActivityManager= (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        flag = true;
        statusReceiver = new StatusReceiver();
        super.onCreate();
    }  
	 @Override
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 Toast.makeText(this, "My Service Started", Toast.LENGTH_SHORT).show();  
		 Log.d(Constant.TAG, "Service onStart"); 
		 IntentFilter filter = new IntentFilter();
		 filter.addAction("bing.software.meminfomonitor.AppInfoService");
		 registerReceiver(statusReceiver, filter);
		 Log.d(Constant.TAG, "intent.getStringExtra(\"PROC_NAME\"): " + intent.getStringExtra("PROC_NAME")); 
		 // Start loop to get pss total value
		 new Thread(new mPssTotalThread(intent.getStringExtra("PROC_NAME"), mActivityManager)).start();
		 return START_STICKY;
	 }  
  
    @Override  
    public void onDestroy() {  
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_SHORT).show();  
        Log.d(Constant.TAG, "Service onDestroy");  
        unregisterReceiver(statusReceiver);
        super.onDestroy();
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
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Log.e(Constant.TAG, e.toString());
				}
				pssTotal = Constant.getPssTotal(proc_name, am);
				intent = new Intent();
                intent.setAction("bing.software.meminfomonitor.AppInfoMonitor");
                intent.putExtra("PSS_TOTAL",pssTotal);
                sendBroadcast(intent);
			}
		}
	}
	
	
    private class StatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("STATUS", -1);
            if(status == Constant.MEM_STOP){                           
            	Log.i(Constant.TAG, "in status: " + status);
            	flag = false;
            	stopSelf();
            }
        }
    }

}
