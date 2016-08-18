package com.hoosteen.schedule.settings;

import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.JFrame;

import com.hoosteen.schedule.GenEdSubcat;
import com.hoosteen.settings.Settings;

/**
 * A Settings object which saves settings for the Schedule program
 * @author Justin
 *
 */
public class ScheduleSettings extends Settings<ScheduleSettingsWindow>{
	
	/**
	 * A set of all of the Courses which load when a new project is made
	 */
	TreeSet<String> defaultCourses = new TreeSet<String>();
	
	/**
	 * A List of all of the enabled GenEds
	 */
	ArrayList<GenEdSubcat> enabled = new ArrayList<GenEdSubcat>();
	
	public ScheduleSettings(){
		
		//Enable all GenEds
		for(GenEdSubcat subcat : GenEdSubcat.values()){
			enabled.add(subcat);
		}
	}
	
	/**
	 * Enables a GenEd
	 * @param genEd The GenEd to enable
	 */
	public void enableGenEd(GenEdSubcat genEd){
		if(!enabled.contains(genEd)){
			enabled.add(genEd);
		}		
	}
	
	/**
	 * Disables a GenEd
	 * @param genEd The GenEd to disable
	 */
	public void disableGenEd(GenEdSubcat genEd){
		if(enabled.contains(genEd)){
			enabled.remove(genEd);
		}
	}
	
	/**
	 * Gets the enabled GenEds
	 * @return An array of GenEdSubcats
	 */
	public GenEdSubcat[] getEnabledGenEds(){
		return enabled.toArray(new GenEdSubcat[0]);
	}
	
	/**
	 * Adds a Course to the list of default Courses
	 * @param course The CourseID
	 */
	public void addDefaultCourse(String course){
		if(!defaultCourses.contains(course)){
			defaultCourses.add(course);
		}		
	}
	
	/**
	 * Removes a Course from the list of default Courses
	 * @param course The CourseID
	 */
	public void removeDefaultCourse(String course){
		if(defaultCourses.contains(course)){
			defaultCourses.remove(course);
		}
	}
	
	/**
	 * Gets the default Courses
	 * @return An array of Strings (CourseIDs)
	 */
	public String[] getDefaultCourses(){
		return defaultCourses.toArray(new String[0]);
	}
	
	/**
	 * Gets a ScheduleSettingsWindow with the owner being the given JFrame
	 */
	@Override
	protected ScheduleSettingsWindow getSettingsWindow(JFrame owner) {
		return new ScheduleSettingsWindow(owner, this);
	}
}