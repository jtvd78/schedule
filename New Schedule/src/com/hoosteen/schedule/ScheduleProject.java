package com.hoosteen.schedule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.hoosteen.Tools;
import com.hoosteen.schedule.node.Schedule;

public class ScheduleProject implements Serializable{
	
	/**
	 * The location on the disk where the project is saved
	 * Allows the project to save itself
	 */
	private String location;
	
	/**
	 * THe Schedule (root node) which this project contains
	 */
	private Schedule schedule;
	
	/**
	 * Creates a Project object with a Schedule and a location String as input
	 * @param schedule the Schedule to save with the project
	 * @param location the location path where the project will be saved
	 */
	public ScheduleProject(Schedule schedule, String location){
		this.schedule = schedule;
		this.location = location;
	}	
	
	/**
	 * Gets the Schedule being saved with the project
	 * @return The project's Schedule
	 */
	public Schedule getSchedule(){
		return schedule;
	}

	/**
	 * Determines weather the project has a location or not
	 * @return A boolean weather or not the project has a location
	 */
	public boolean hasLocation(){
		return location != null;
	}
	
	/**
	 * Makes and returns a default project
	 * @return the new Project
	 */
	public static ScheduleProject makeDefaultProject(){
		
		//Create the containing Schedule
		Schedule schedule = new Schedule();
		
		//Add each of the user's pre-chosen default courses, each with a random color
		for(String s : ScheduleStart.getSettings().getDefaultCourses()){
			schedule.addCourseById(s, Tools.getRandomColor());
		}
		
		//Return a new project with the newly created schedule, and no location
		return new ScheduleProject(schedule, null);
	}	
	
	/**
	 * Refreshes the courses within the schedule
	 */
	public void refresh(){
		schedule.refreshCourses();
	}
	
	/**
	 * Loads a project
	 * @param file - File to load project from
	 * @return Project stored in the file
	 */
	public static ScheduleProject loadProject(File file){
		ScheduleProject out = null;
		if(file.exists()){
			
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			
			try {
				
				//Read it
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				out = (ScheduleProject) ois.readObject();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}finally{
				
				//Close the reading objects after success or error
				try {				
					if(ois != null){
						ois.close();
					}
					
					if(fis != null){
						fis.close();
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}			
			}
		}
		
		//Sets the location of the Project to the location of the loaded file
		out.location = file.getPath();
		
		//Return the loaded Project
		return out;
	}
	
	/**
	 * Saves the file in its last saved location
	 */
	public void save(){
		
		//Don't save unless the project has a location
		if(hasLocation()){
			saveAs(new File(location));
		}		
	}
	
	/**
	 * Saves a project in a specified file
	 * @param f The file to save to
	 */
	public void saveAs(File f){
		
		//Saves the location for future use
		location = f.getPath();
		
		//Makes the directory path if the file does not exist
		//Deletes the file if it does already exist
		if(!f.exists()){
			f.getParentFile().mkdirs();
		}else{
			f.delete();
		}
		
		//Save the project
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try{
			
			//Write it
			fos = new FileOutputStream(f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(this);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			//Close the writing objects after success or error
			try {				
				if(oos != null){
					oos.close();
				}
				
				if(fos != null){
					fos.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
}