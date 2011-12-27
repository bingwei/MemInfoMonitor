package bing.software.meminfomonitor;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MemInfoMonitor extends TabActivity implements TabHost.TabContentFactory{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, AppInfoActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("process").setIndicator("Process",
                          res.getDrawable(R.drawable.ic_tab_process))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, TaskActivity.class);
        spec = tabHost.newTabSpec("task").setIndicator("Task",
                          res.getDrawable(R.drawable.ic_tab_task))
                      .setContent(this);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ServiceActivity.class);
        spec = tabHost.newTabSpec("service").setIndicator("Service",
                          res.getDrawable(R.drawable.ic_tab_service))
                      .setContent(this);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
        
        // Adjust icon size
        View mView = tabHost.getTabWidget().getChildAt(0);//0是代表第一个Tab

        ImageView imageView = (ImageView)mView.findViewById(android.R.id.icon);//获取控件imageView
        setImageLayout(imageView, R.drawable.process);
        imageView = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.icon);
        setImageLayout(imageView, R.drawable.task);
        imageView  = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.icon);
        setImageLayout(imageView, R.drawable.service);
    }
    public void setImageLayout(ImageView iv, int rid){
    	iv.setImageDrawable(getResources().getDrawable(rid));
    	iv.setAdjustViewBounds(true);
    	iv.setMaxHeight(60);
    	iv.setMaxWidth(60);
    }
    
    public View createTabContent(String tag) {
        final TextView tv = new TextView(this);
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }
}