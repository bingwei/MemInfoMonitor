package bing.sw.mm.legacy;


import android.graphics.drawable.Drawable;

/**
 * 应用程序包的简要信息
 */
public class Z_BasicProgramUtil{
    
    /*
     * 定义应用程序的简要信息部分
     */
    private Drawable icon;                            // 程序图标
    private String programName;                        // 程序名称
    private String processName;
    
    private String cpuMemString;
    
    public Z_BasicProgramUtil() {
        icon = null;
        programName = "";    
        processName = "";    
        cpuMemString = "";
    }
    
    public Drawable getIcon() {
        return icon;
    }
    
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    
    public String getProgramName() {
        return programName;
    }
    
    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getCpuMemString() {
        return cpuMemString;
    }

    public void setCpuMemString(String cpuMemString) {
        this.cpuMemString = cpuMemString;
    }
    
    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}