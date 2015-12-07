package com.hoosteen.schedule;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.hoosteen.helper.GenEdSubcat;

/**
 * Creates 
 * @author justi
 *
 */
public class ScheduleLoader {
	
	static String defaultSchedule = "C:\\Users\\justi\\.UMDSoCData\\schedule.sch";
	
	/**
	 * Loads a default schedule. Will always return a valid schedule
	 * @return
	 */
	public static Schedule getDefaultSchedule(){
		return makeSchedule();
	}
	
	/**
	 * Saves a given schedule to a give file
	 * @param schedule
	 * @param f
	 */
	public static void saveSchedule(Schedule schedule, File f){
		
		if(!f.exists()){
			f.getParentFile().mkdirs();
		}else{
			f.delete();
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(schedule);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Makes and returns a new default schedule
	 * @return
	 */
	public static Schedule makeSchedule(){
		Schedule schedule = new Schedule();
		
		schedule.addCourseById("ENEE101", Color.BLUE);
		schedule.addCourseById("PHYS161", Color.MAGENTA);
		schedule.addCourseById("MATH141", Color.RED);
		schedule.addCourseById("CMSC132", Color.ORANGE);
		
		return schedule;
	}

	/**
	 * Loads and returns a schedule from a give file
	 * @param file
	 * @return
	 */
	public static Schedule loadSchedule(File file) {
		Schedule out = null;
		if(file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				out = (Schedule) ois.readObject();
				ois.close();
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;
	}
}