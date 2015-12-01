package com.hoosteen.helper;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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

	public static void displayText(String description, String title) {
		JTextArea jta = new JTextArea(description);
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
        JScrollPane jsp = new JScrollPane(jta);
        jsp.setPreferredSize(new Dimension(480, 320));
        JOptionPane.showMessageDialog( null, jsp, title, JOptionPane.DEFAULT_OPTION);	
	}
}