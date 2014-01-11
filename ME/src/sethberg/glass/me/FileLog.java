package sethberg.glass.me;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLog {
	public static void println(String message) {
		try {
			File logFile = new File("/mnt/sdcard/memora_logs/memora_log.log");
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
		    out.println(new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date(System.currentTimeMillis())) + ": " + message);
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
