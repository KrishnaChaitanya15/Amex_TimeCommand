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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandScheduler {
	private static final String COMMAND_FILE_PATH = System.getProperty("java.io.tmpdir") + "commands.txt";

	private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
	public static void main(String[] args) {
		
		try(BufferedReader br = new BufferedReader(new FileReader(COMMAND_FILE_PATH))){
			String line;
			 while ((line = br.readLine()) != null) {
	                line = line.trim();
				 if (line.isEmpty()) continue;
	
	             if (line.startsWith("*/")) {
	                 scheduleRecurringCommand(line);
	             } else {
	            	 scheduleCommand(line);
	             }
			 }
		}
		catch(FileNotFoundException fe) {
			fe.printStackTrace();
		} catch(IOException ie) {
			ie.printStackTrace();
		}
	}

	private static void scheduleRecurringCommand(String line) {
		// TODO Auto-generated method stub
		String[] parts = line.split(" ", 2);
        if (parts.length != 2 || !parts[0].startsWith("*/")) {
            System.err.println("Invalid recurring format: " + line);
            return;
        }
        try {
        	int interval = Integer.parseInt(parts[0].substring(2));
        	String command = parts[1];
        	Runnable task = () -> runCommand(command);
        	scheduledExecutorService.scheduleAtFixedRate(task, 0, interval, TimeUnit.MINUTES);
        } catch (NumberFormatException e) {
            System.err.println("Invalid interval in: " + line);
        }
	}

	private static void scheduleCommand(String line) {
		// TODO Auto-generated method stub
		
		String[] parts = line.split(" ", 6);
		 if (parts.length < 6) {
			 System.err.println("Invalid one-time format: " + line);
	         return;     
	    }

	    try {
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
				
				runCommand(command);
				
			}
		}, delayMillis);
	    } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public static void runCommand(String command) {
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
}
