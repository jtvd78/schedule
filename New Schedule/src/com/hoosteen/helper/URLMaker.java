package com.hoosteen.helper;


/**
 * Makes URL Strings for purposes of connecting to UMD Schedule of Classes. 
 * @author Justin
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
	private static final String timePeriod = "201601";
	
	/**
	 * Gets the URL of the specified GenEd.
	 * @param ges
	 * @return
	 */
	public static String getGenEdURL(GenEdSubcat ges){
		return mainPath + "gen-ed/" + timePeriod + "/" + ges.toString();
	}
	
	/**
	 * Gets the URL for the course times URL for a specified course
	 * @param courseId The ID of a course
	 * @return
	 */
	public static String getCourseTimeURL(String courseId){
		return mainPath + timePeriod + "/sections?courseIds=" + courseId;
	}

	/**
	 * Gets the URL for the course info URL for a specified course
	 * @param courseId the ID of a course
	 * @return
	 */
	public static String getCourseURL(String courseId) {
		return mainPath + timePeriod + "/" + courseId.substring(0, 4) + "/" + courseId;
	}
	
}
