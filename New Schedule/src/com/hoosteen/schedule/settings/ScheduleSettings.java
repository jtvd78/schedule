package com.hoosteen.schedule.settings;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.hoosteen.schedule.GenEdSubcat;
import com.hoosteen.settings.Settings;
import com.hoosteen.settings.SettingsWindow;

public class ScheduleSettings extends Settings<ScheduleSettingsWindow>{
	
	ArrayList<String> defaultCourses = new ArrayList<String>();
	ArrayList<GenEdSubcat> enabled = new ArrayList<GenEdSubcat>();
	
	public ScheduleSettings(){
		
		//Enable all GenEds
		for(GenEdSubcat subcat : GenEdSubcat.values()){
			enabled.add(subcat);
		}
	}
	
	public void enableGenEd(GenEdSubcat genEd){
		if(!enabled.contains(genEd)){
			enabled.add(genEd);
		}		
	}
	
	public void disableGenEd(GenEdSubcat genEd){
		if(enabled.contains(genEd)){
			enabled.remove(genEd);
		}
	}
	
	public GenEdSubcat[] getEnabledGenEds(){
		return enabled.toArray(new GenEdSubcat[0]);
	}
	
	public void addDefaultCourse(String course){
		if(!defaultCourses.contains(course)){
			defaultCourses.add(course);
		}		
	}
	
	public void removeDefaultCourse(String course){
		if(defaultCourses.contains(course)){
			defaultCourses.remove(course);
		}
	}
	
	public String[] getDefaultCourses(){
		return defaultCourses.toArray(new String[0]);
	}
	
	@Override
	protected ScheduleSettingsWindow getSettingsWindow(JFrame owner) {
		return new ScheduleSettingsWindow(owner, this);
	}
}