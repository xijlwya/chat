package chat.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogFileWriter {
	
	private File logFile;
	private FileWriter writer;
	
	public LogFileWriter(String fileName) {
		logFile = new File(fileName);
		writer = null;
	}
	
	public void logLine(String msg) {
		try {
			logFile.createNewFile();
			writer = new FileWriter(logFile, true);
			writer.write(msg);
			writer.append(System.getProperty("line.separator"));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
