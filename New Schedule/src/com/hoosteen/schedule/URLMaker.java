package com.hoosteen.schedule;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Makes URL Strings for purposes of connecting to UMD Schedule of Classes. 
 * @author justin
 *
 */
public class URLMaker {
	
	/**
	 * Base URL
	 */
	private static final String mainPath = "https://ntst.umd.edu/soc/";
	
	/**
	 * Used within the URL to specify which time period to pull from the SoC server
	 */
	private static final String timePeriod = "201701";
	//private static final String timePeriod = "201608";
	/**
	 * Gets the URL of the specified GenEd.
	 * @param ges - GenEdSubcat to get URL of
	 * @return The String of the URL for a specified GenEd
	 */
	public static String getGenEdURL(GenEdSubcat ges){
		return mainPath + "gen-ed/" + timePeriod + "/" + ges.toString();
	}
	
	/**
	 * Gets the URL for the course times URL for a specified course
	 * @param courseId - The ID of a course
	 * @return the String of the URL of a specific course's course times. 
	 */
	public static String getCourseTimeURL(String courseId){
		return mainPath + timePeriod + "/sections?courseIds=" + courseId;
	}

	/**
	 * Gets the URL for the course info URL for a specified course
	 * @param courseId the ID of a course
	 * @return the String of the URL of a given course
	 */
	public static String getCourseURL(String courseId) {
		
		//The courseID must be as least 4 letters long to not throw an error
		//Crude check but this stops errors from occurring when a user inputs an incorrect courseId
		if(courseId.length() < 4){
			return null;
		}	
		
		return mainPath + timePeriod + "/" + courseId.substring(0, 4) + "/" + courseId;
	}

	
	/**
	 * Confirms weather a course URL will load without throwing an error
	 * @param course The CourseID of the course to test
	 * @return True for a successful loading, False for otherwise
	 */
	public static boolean confirmCourse(String course) {
		
		//There's gotta be a 4 letter beginning portion
		if(course.length() < 4){
			return false;
		}		
		
		//URL String to text
		String urlString = getCourseURL(course);
		
		//Test it
		try {
			new URL(urlString).openStream();
		} catch (MalformedURLException e1) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		//Success
		return true;		
	}	
}