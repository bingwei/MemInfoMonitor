package bing.sw.mm.activity;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Typeface;
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
 * 1. Sort (DONE, process -> pid)
 * 2. Highlight running service(DONE)
 * 3. Formate time: problem, how to get boot time??
*/

public class ServiceActivity extends ListActivity{
	private ArrayList<RunningServiceInfo> runningServiceInfo;
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
        runningServiceInfo = Constant.getRunningServiceInfoSortedWithProcess(
        		(ActivityManager)getSystemService(ACTIVITY_SERVICE));
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
				R.layout.service_item, null);
			holder = new ViewHolder();
			holder.service_name = (TextView) convertView.findViewById(R.id.service_service_name);
			holder.process_name = (TextView) convertView.findViewById(R.id.service_process_name);
			holder.started = (TextView) convertView.findViewById(R.id.service_started_status);
			convertView.setTag(holder);
		    }else {
		        holder = (ViewHolder) convertView.getTag();
		    }
		
		    service = runningServiceInfo.get(position);
		    boolean isStarted = service.started;
		    
		    holder.service_name.setText(Html.fromHtml(String.format(getString(R.string.str_format_service_service_name), 
		    		service.service.toString())));
		    holder.process_name.setText(Html.fromHtml(String.format(getString(R.string.str_format_service_process_name), 
		    		service.process.toString(), service.pid)));
		    holder.started.setText(Html.fromHtml(String.format(getString(R.string.str_format_service_started_status), isStarted)));
		    
		    
		    if(isStarted){
		    	holder.started.setTypeface(null, Typeface.BOLD);
		    	convertView.setBackgroundColor(0x00000000);//black
		    	holder.service_name.setTextColor(0xB0FFFFFF);//white with transparent'B0'
		    	holder.process_name.setTextColor(0xB0FFFFFF);
		    	holder.started.setTextColor(0xB0FFFFFF);
		    }else{
		    	holder.started.setTypeface(null, Typeface.NORMAL);
		    	convertView.setBackgroundColor(0xA0FFFFFF);//white with transparent'A0'
		    	holder.service_name.setTextColor(0xFF000000);//black with transparent'FF'
		    	holder.process_name.setTextColor(0xFF000000);
		    	holder.started.setTextColor(0xFF000000);
		    }
			return convertView;
			}
	}
	
	private class ViewHolder{
		TextView service_name;
		TextView process_name;
		TextView started;
	}
	

}
