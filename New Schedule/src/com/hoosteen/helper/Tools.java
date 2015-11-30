package com.hoosteen.helper;

import java.awt.Color;
import java.util.Random;

/**
 * Some random stuff that I use. 
 * @author Justin
 *
 */
public class Tools {
	
	/**
	 * @param Array to print
	 * @param Spacer String
	 * @return Returns each element in the array, separated by a spacer. 
	 */
	public static String arrToString(Object[] arr, String spacer){
		if(arr == null){
			return null;
		}
		
		StringBuffer out = new StringBuffer("");
		for(int i = 0; i < arr.length; i++){
			out.append(arr[i].toString());
			if(i < arr.length -1){
				out.append(spacer);
			}
		}
		
		return out.toString();
	}
	
	/**
	 * @return Returns a random color
	 */
	public static Color getRandomColor(){
		Random random = new Random();		
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}
}