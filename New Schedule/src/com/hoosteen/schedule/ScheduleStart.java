package com.hoosteen.schedule;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import com.hoosteen.Tools;
import com.hoosteen.schedule.settings.ScheduleResources;
import com.hoosteen.schedule.settings.ScheduleSettings;
import com.hoosteen.schedule.window.ScheduleFrame;
import com.hoosteen.settings.Settings;

public class ScheduleStart {
	
	private static ScheduleSettings settings;
	
	/**
	 * A list of all of the ScheduleFrames that are open
	 */
	private static final ArrayList<ScheduleFrame> frameList = new ArrayList<ScheduleFrame>();
	
	/**
	 * Its the main function, what else can I say?
	 * @param args - Command line arguments
	 */
	public static void main(String[] args){
		
		//Make it look nice
		Tools.setNativeUI();
		
		settings = Settings.loadSettings(ScheduleSettings.class, ScheduleResources.programName, ScheduleResources.version);
		
		//Load default project, and make frame
		newFrame(ScheduleProject.makeDefaultProject());
	}

	/**
	 * Gets the user's settings
	 * @return The program's settings
	 */
	public static ScheduleSettings getSettings(){
		return settings;
	}
	
	/**
	 * Creates a new frame with a given project
	 * @param project Project to manage
	 */
	public static void newFrame(ScheduleProject project) {
		
		ScheduleFrame newFrame = new ScheduleFrame(project);
		newFrame.addWindowListener(new CloseListener(newFrame));		
		frameList.add(newFrame);
	}
	
	/**
	 * Detects when a window is closed, and removes the window from the frameList
	 * @author Justin
	 *
	 */
	static class CloseListener implements WindowListener{
		
		/**
		 * The ScheduleFrame associated with the CloseListener
		 */
		ScheduleFrame frame;
		
		/**
		 * Takes a SchedulFrame which should be removed when closed
		 * @param frame The ScheduleFrame to be removed
		 */
		public CloseListener(ScheduleFrame frame){
			this.frame = frame;
		}	

		@Override
		public void windowClosed(WindowEvent e) {
			frameList.remove(frame);
		}
		
		@Override
		public void windowActivated(WindowEvent arg0) {}
		@Override
		public void windowClosing(WindowEvent arg0) {}
		@Override
		public void windowDeactivated(WindowEvent arg0) {}
		@Override
		public void windowDeiconified(WindowEvent arg0) {}
		@Override
		public void windowIconified(WindowEvent arg0) {}
		@Override
		public void windowOpened(WindowEvent arg0) {}
		
	}
}