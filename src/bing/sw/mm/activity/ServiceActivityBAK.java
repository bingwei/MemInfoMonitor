package bing.sw.mm.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import bing.sw.mm.R;
import bing.sw.mm.constant.Constant;

/*Update
 * 1. Sort
 * 2. Highlight running service
 * 3. Formate time: problem, how to get boot time??
*/

public class ServiceActivityBAK extends ListActivity{
	private ArrayList<RunningServiceInfo> runningServiceInfo;
	private ActivityManager am;
	private ServiceListAdapter process_list_adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_service_listview);
	}
	
	@Override
	public void onStart(){
		updateService();
		super.onStart();
	}
	
	private void updateService(){
        am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        runningServiceInfo = (ArrayList<RunningServiceInfo>) am.getRunningServices(100);
        loadAdapter();
	}
	
	private void loadAdapter(){
		process_list_adapter = new ServiceListAdapter(this);
        setListAdapter(process_list_adapter);
	}
	
	
	private class ServiceListAdapter extends BaseAdapter{
		Context mContext = null;
		public ServiceListAdapter(Context context) {
		    mContext = context;
		}
		@Override
		public int getCount() {
			return runningServiceInfo.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		    ViewHolder holder;
		    RunningServiceInfo service;
		    if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
				R.layout.service_item_bak, null);
			holder = new ViewHolder();
			holder.service_name = (TextView) convertView.findViewById(R.id.service_service_name);
			holder.process_name = (TextView) convertView.findViewById(R.id.service_process_name);
			holder.active_since = (TextView) convertView.findViewById(R.id.service_active_since);
			holder.last_activity_time = (TextView) convertView.findViewById(R.id.service_last_activity_time);
			holder.started = (TextView) convertView.findViewById(R.id.service_started_status);
			convertView.setTag(holder);
		    }else {
		        holder = (ViewHolder) convertView.getTag();
		    }
		
		    service = runningServiceInfo.get(position);
		    
		    holder.service_name.setText(Html.fromHtml(String.format(getString(R.string.str_format_service_service_name), 
		    		service.service.toString())));
		    holder.process_name.setText(Html.fromHtml(String.format(getString(R.string.str_format_service_process_name), 
		    		service.process.toString(), service.pid)));
		    holder.active_since.setText(time2String(service.activeSince)); //Update format
		    holder.last_activity_time.setText(time2String(service.lastActivityTime));
		    holder.started.setText(String.valueOf(service.started));
			return convertView;
			}
	}
	
	private class ViewHolder{
		TextView service_name;
		TextView process_name;
		TextView active_since;
		TextView last_activity_time;
		TextView started;
	}
	
	private String time2String(long time){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time + Constant.getUpTime(ServiceActivityBAK.this));
		return cal.getTime().toLocaleString();
	}

}
