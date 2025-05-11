package com.example.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class CommandScheduler {
	private static final String COMMAND_FILE_PATH = System.getProperty("java.io.tmpdir") + "commands.txt";

	public static void main(String[] args) {
		
		try(BufferedReader br = new BufferedReader(new FileReader(COMMAND_FILE_PATH))){
			String line;
			if((line = br.readLine()) != null) {
				scheduleCommand(line);
				
			}
		}
		catch(FileNotFoundException fe) {
			fe.printStackTrace();
		} catch(IOException ie) {
			ie.printStackTrace();
		}
	}

	private static void scheduleCommand(String line) {
		// TODO Auto-generated method stub
		
		String[] parts = line.split(" ", 6);
		int minute = Integer.parseInt(parts[0]);
		int hour = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        int month = Integer.parseInt(parts[3]); // 1-based (January = 1)
        int year = Integer.parseInt(parts[4]);
        String command = parts[5];
        
        LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, minute);
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        System.out.println(zdt);
        long delayMillis = zdt.toInstant().toEpochMilli()-System.currentTimeMillis();
        
        if(delayMillis <= 0) {
        	System.out.println("Scheduled time already passed "+command);
        	return;
        }
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				try {
					System.out.println("Executing: " + command);
					ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
					pb.inheritIO();
					Process process = pb.start();
	                process.waitFor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, delayMillis);
	}
}
