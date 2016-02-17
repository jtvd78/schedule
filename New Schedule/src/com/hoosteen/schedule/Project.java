package com.hoosteen.schedule;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Project implements Serializable{
	
	private String location;
	private Schedule schedule;
	
	public Project(Schedule schedule, String location){
		this.schedule = schedule;
		this.location = location;
	}	
	
	public Schedule getSchedule(){
		return schedule;
	}
	
	public String getLocation(){
		return location;
	}
	
	/**
	 * Saves the file in its last saved location
	 */
	public void save(){
		saveAs(new File(location));
	}
	
	/**
	 * Saves a project in a specified file
	 * @param f - The file to save to. 
	 */
	public void saveAs(File f){
		
		location = f.getPath();
		
		if(!f.exists()){
			f.getParentFile().mkdirs();
		}else{
			f.delete();
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
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
	 * Makes and returns a default project
	 * @return the new Project
	 */
	public static Project makeDefaultProject(){
		Schedule schedule = new Schedule();
		
		schedule.addCourseById("ENEE101", Color.BLUE);
		schedule.addCourseById("PHYS161", Color.MAGENTA);
		schedule.addCourseById("MATH141", Color.RED);
		schedule.addCourseById("CMSC132", Color.ORANGE);
		schedule.addCourseById("HIST289V", Color.GREEN);
		
		return new Project(schedule, null);
	}
	
	/**
	 * Loads a project
	 * @param file - File to load project from
	 * @return Project stored in the file
	 */
	public static Project loadProject(File file){
		Project out = null;
		if(file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				out = (Project) ois.readObject();
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
		out.location = file.getPath();
		return out;
	}
}