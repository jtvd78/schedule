package com.hoosteen.other;


/**
 * Makes URL Strings for purpouses of connecting to UMD Schedule of Classes. 
 * @author justi
 *
 */
public class URLMaker {
	
	public static final String mainPath = "https://ntst.umd.edu/soc/";
	public static final String timePeriod = "201601";

	
	public static String[] getGenEdURLs(){
		String[] urlList = new String[GenEdSubcat.values().length];
		int num = 0;
		for(GenEdSubcat ges : GenEdSubcat.values()){
			urlList[num] = mainPath + "gen-ed/" + timePeriod + "/" + ges.toString();
			num++;
		}
		
		return urlList;
	}
	
	public static String getGenEdURL(GenEdSubcat ges){
		return mainPath + "gen-ed/" + timePeriod + "/" + ges.toString();
	}
	
	public static String getCourseTimeURL(String courseId){
		return mainPath + timePeriod + "/sections?courseIds=" + courseId;
	}

	public static String getCourseByIdURL(String id) {
		return mainPath + timePeriod + "/" + id.substring(0, 4) + "/" + id;
	}
	
}
