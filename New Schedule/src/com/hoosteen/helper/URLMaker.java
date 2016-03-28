package com.hoosteen.helper;


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
	private static final String timePeriod = "201608";
	
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
		return mainPath + timePeriod + "/" + courseId.substring(0, 4) + "/" + courseId;
	}
	
}
