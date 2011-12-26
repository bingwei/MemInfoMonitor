package bing.software.meminfomonitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Z_CMDExecute {

public synchronized String run ( String [] cmd,String workdirectory)
throws IOException {
String result = "";
byte[] re;

	try {
		ProcessBuilder builder = new ProcessBuilder (cmd);
		//设置一个路径
		if ( workdirectory != null ){
			builder.directory(new File(workdirectory));
		}
		builder.redirectErrorStream(true);
		Process process = builder.start();
		InputStream in = process.getInputStream();
		re = new byte[128];
		while(in.read(re) != -1){
		}
		result = result + new String(re) ;
		in.close();
	
	}catch ( Exception ex ) {
		ex.printStackTrace ( ) ;
	}
	return result;
	}

}