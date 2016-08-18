package com.hoosteen.schedule.window;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.hoosteen.schedule.ScheduleProject;
import com.hoosteen.schedule.settings.ScheduleResources;
import com.hoosteen.tree.TreeComp;

/**
 * This is the main window of the program. The main method is here. 
 * @author justin
 *
 */
public class ScheduleFrame extends JFrame{
	
	private ScheduleProject project;
	
	/**
	 * Creates an instance of a MainFrame, with a Tree tree. 
	 * Mainframe includes the ScheduleDisplay on the left, and the TreeComp on the right
	 * @param project - Project to manage.
	 */
	public ScheduleFrame(ScheduleProject project){
		this.project = project;
	
		//Set the MenuBar
		setJMenuBar(new ScheduleMenuBar(this));
		
		//Set Layout to BorderLayout
		getContentPane().setLayout(new BorderLayout());
		
		//Dispose the Frame on close
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//Tree comp: Right side
		TreeComp treeComp = new TreeComp(this, project.getSchedule());
		
		//Schedule display: Left side
		ScheduleDisplay scheduleDisplay = new ScheduleDisplay(project.getSchedule(),treeComp);
		
		//Horizontal JSplitPane separates the two
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,scheduleDisplay,treeComp);
		
		//Make the ScheduleDisplay larger than the TreeComp
		splitPane.setDividerLocation(ScheduleResources.defaultWindowWidth*3/4);	
	
		//Set window size to default
		setSize(ScheduleResources.defaultWindowWidth, ScheduleResources.defaultWindowHeight);
		
		//Add SplitPane and set window to visible
		add(splitPane, BorderLayout.CENTER);
		setVisible(true);
	}
	
	/**
	 * Gets the project that is being displayed by this window
	 * @return The Project being viewed in this ScheduleFrame
	 */
	public ScheduleProject getProject() {
		return project;
	}	
}