package com.hoosteen.schedule;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Keeps track of time. Has Day, Hour, Minute, and AM/PM
 * @author Justin
 *
 */
public class Time implements Serializable{

	/**
	 * A set of days. Su, M, Tu, W, Th, F, Sa
	 * @author Justin
	 *
	 */
	public enum Day{
		Su, M, Tu, W, Th, F, Sa
	}

	int hour;
	int min;
	boolean PM;
	
	/**
	 * Initializes a time with:
	 * @param Hour
	 * @param Minue
	 * @param Boolean PM (false for AM)
	 */
	public Time(int hour, int min, boolean PM){
		this.hour = hour;
		this.min = min;
		this.PM = PM;
	}
	
	/**
	 * Creates a time with an integer: W
	 * @param Minutes - The time from the beginning of the day
	 */
	public Time(int minutes){
		min = minutes % 60;
		minutes -= min;
		hour = minutes / 60;
		
		PM = (hour >= 12);
	}
	

	/**
	 * Creates a time from a string. The string must be in the specific format from the UMD Soc Website, or this will not work
	 * @param String to parse
	 */
	public Time(String timeString){	
		int colanPos = timeString.indexOf(':');
		String first = timeString.substring(0, colanPos);
		String second = timeString.substring(colanPos+1, colanPos+1 + 2);
		String end = timeString.substring(timeString.length()-2, timeString.length());
		
		hour = Integer.parseInt(first);
		min = Integer.parseInt(second);
		if(end.toLowerCase().equals("am")){
			PM = false;
		}else{
			PM = true;
		}
	}
	
	/**
	 * Returns a string representation of the time
	 */
	public String toString(){
		if(hour > 12){
			hour %= 12;
		}		
		
		String out = hour + ":";
		if(min < 10){
			out += "0" + min;
		}else{
			out += min;
		}
		if(PM){
			out += " PM";
		}else{
			out+= " AM";
		}
		return out;
	}
	
	
	/**
	 * Returns this time to: Minutes since 12:00 AM
	 */
	public int toMinutes(){
		int result = 0;
		if(PM && hour != 12){
			result += 12 * 60;
		}else if(!PM && hour == 12){
			result -= hour*60;
		}
		result += hour * 60;
		result += min;
		return result;
	}
	
	//I don't like this being here. That's why its not documented
	public static Time.Day[] getDays(String daysString){
		ArrayList<Time.Day> daysList = new ArrayList<Time.Day>();
		for(Time.Day d : Time.Day.values()){
			if(daysString.contains(d.toString())){
				daysList.add(d);
			}
		}
		
		Time.Day[] dayArr = new Time.Day[daysList.size()];
		for(int i = 0; i < dayArr.length; i++){
			dayArr[i] = daysList.get(i);
		}
		
		return dayArr;
	}
}