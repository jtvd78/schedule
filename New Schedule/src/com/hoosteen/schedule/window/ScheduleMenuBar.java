package com.hoosteen.schedule.window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.hoosteen.Tools;
import com.hoosteen.graphics.TaskWindow;
import com.hoosteen.graphics.TaskWindow.Task;
import com.hoosteen.schedule.GenEdSubcat;
import com.hoosteen.schedule.ScheduleProject;
import com.hoosteen.schedule.ScheduleStart;
import com.hoosteen.schedule.Time;
import com.hoosteen.schedule.node.Schedule;
import com.hoosteen.schedule.settings.ScheduleSettingsWindow;

public class ScheduleMenuBar extends JMenuBar{	
	
	/**
	 * The owning ScheduleFrame
	 */
	private ScheduleFrame frame;
	
	/**
	 * The JMenuBar for a ScheduleFrame
	 * @param frame The owning ScheduleFrame
	 */
	public ScheduleMenuBar(ScheduleFrame frame){
		this.frame = frame;
		
		//Init the MenuBar's options
		
		JMenu file = new JMenu("File");
			JMenuItem newProject = new JMenuItem("New");
			newProject.addActionListener(new NewProjectActionListener());
			newProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			JMenuItem openProject = new JMenuItem("Open...");
			openProject.addActionListener(new OpenProjectActionListener());
			openProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			JMenuItem saveProject = new JMenuItem("Save");
			saveProject.addActionListener(new SaveProjectActionListener());
			saveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			JMenuItem saveProjectAs = new JMenuItem("Save As...");
			saveProjectAs.addActionListener(new SaveProjectAsActionListener());
		file.add(newProject);
		file.add(openProject);
		file.add(saveProject);
		file.add(saveProjectAs);
		
		JMenu courses = new JMenu("Courses");
			JMenuItem addCourse = new JMenuItem("Add Course...");
			addCourse.addActionListener(new AddCourseActionListener());
			addCourse.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
			JMenuItem addGenEds = new JMenuItem("Add GenEds");
			addGenEds.addActionListener(new AddGenEdsActionListener());
			JMenuItem refreshCourses = new JMenuItem("Refresh Courses");
			refreshCourses.addActionListener(new RefreshCoursesActionListener());
		courses.add(addCourse);
		courses.add(addGenEds);
		courses.add(refreshCourses);
		
		JMenu edit = new JMenu("Edit");
			JMenuItem pref = new JMenuItem("Preferences") ;
			pref.addActionListener(new PreferencesActionListener());
		edit.add(pref);
		
		JMenu help = new JMenu("Help");
			JMenuItem about = new JMenuItem("About");
			about.addActionListener(new HelpActionListener());
		help.add(about);
		
		add(file);
		add(courses);
		add(edit);
		add(help);
	}
	
	/**
	 * ActionListener which is activated when the user hits the Help MenuOption
	 * Shows the help window
	 * @author Justin
	 *
	 */
	public class HelpActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {			
			JOptionPane.showMessageDialog(frame, "Made by Justin Van Dort");
		}
	}	

	/**
	 * ActionListener which is activated when the user wants to add enabled gen-eds to the Schedule
	 * Adds the gen-eds which are enabled in the settings
	 * @author Justin
	 *
	 */
	public class AddGenEdsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {			
			
			//Creates a task window to show the progress of the operation
			TaskWindow taskWindow = new TaskWindow(frame, "Loading Gen-Eds");
			
			//Creates the task to run in the TaskWindow
			TaskWindow.Task task = taskWindow.new Task(){

				@Override
				public void begin() {
					
					//Creates a temporary schedule to add the gen-eds to
					Schedule s = new Schedule();
					
					//All of the user's enabled gen-eds
					GenEdSubcat[] enabled = ScheduleStart.getSettings().getEnabledGenEds();
					
					//The number of gen-eds which are being added
					double genEdCount = enabled.length;
					
					//Loops through the enabled gen-eds
					for(int ctr = 0; ctr < genEdCount; ctr++){
						GenEdSubcat ges = enabled[ctr];
						 
						//If the user interrupts the task (AKA tries to close the window), stop the task and return
						if(interrupted){
							return;
						}
						
						//Update the window progress along with the counter
						window.updateProgress(ges.toString(), ctr / ( 2.0 * genEdCount) );
						
						//Actually ad the gen-ed to the temporary schedule
						s.addGenEdSubcat(ges);
						
					}
					
					//Remove any duplicate courses that were added due to gen-ed overlap
					window.updateProgress("Removing Duplicate Courses", 0.5f + 0.1);
					s.removeDuplicateCourses();
					
					//Remove any class that starts before 10
					window.updateProgress("Removing Early Class Times", 0.5f + 0.2);
					s.removeSectionsBetween(new Time(8,0,false),new Time(9,59,false));
					
					//Remove gen-eds that count for less than two subcategories
					window.updateProgress("Removing Low Gen-Ed Courses", 0.5f + 0.3);
					s.removeCoursesWithLowerGenEdSubcats(2);
					
					//Sort the list alphabetically
					window.updateProgress("Sorting", 0.5f + 0.4);
					s.sortAlphabetical();
					
					//Add the temporary Schedule's courses to the main schedule
					window.updateProgress("Merging", 0.5f + 0.5);
					frame.getProject().getSchedule().merge(s);
				}				
			};			
			
			//Run the task
			taskWindow.runTask(task);				
		}
	}
	
	/**
	 * ActionListener which is activated when the user wants to show the settings window
	 * Shows the settings window
	 * @author Justin
	 *
	 */
	public class PreferencesActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e){			
			ScheduleStart.getSettings().showSettingsWindow(frame);
		}		
	}
	
	/**
	 * ActionListener which is activated when the user wants to refresh the courses in the current project
	 * Refreshes the project associated with the ScheduleFrame 
	 * @author Justin
	 *
	 */
	public class RefreshCoursesActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			frame.getProject().refresh();
		}
	}
	
	/**
	 *  ActionListener which is activated when the user wants to add a new course to the project
	 *  It records the user input and attempts to add a new course to the project with the given course ID
	 * 
	 * @author Justin
	 *
	 */
	public class AddCourseActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {			
			String id;
			if((id = JOptionPane.showInputDialog(frame, "Enter a class ID")) != null){
				frame.getProject().getSchedule().addCourseById(id, Tools.getRandomColor());
			}
		}
	}
	
	/**
	 *  ActionListener which is activated when the user wants to create a new project
	 *  It creates a default project and opens a ScheduleFrame with it
	 * 
	 * @author Justin
	 *
	 */
	public class NewProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ScheduleStart.newFrame(ScheduleProject.makeDefaultProject());
		}

	}

	/**
	 *  ActionListener which is activated when the user wants to open the project
	 *  It loads the project and opens a ScheduleFrame with it
	 * 
	 * @author Justin
	 *
	 */
	public class OpenProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Opens the file chooser and records the response
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(frame);
			
			//If the user doesn't cancel, get the file, load the associated project, and create a window with it
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            ScheduleStart.newFrame(ScheduleProject.loadProject(file));
	        }
		}
	}
	
	/**
	 * ActionListener which is activated when the user wants to save the project
	 * It saves the project if the project as a location, if not, it opens the save as window
	 * @author Justin
	 *
	 */
	public class SaveProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(frame.getProject().hasLocation()){
				
				//If there is a location, save the project
				frame.getProject().save();				
			}else{
				
				//If the project has no location, show the "save as" window
				new SaveProjectAsActionListener().actionPerformed(e);				
			}			
		}
	}
	
	
	/**
	 * ActionListener which is activated when the user wants to "save as" the project
	 * It opens a JFileChooser, and saves the project at the new location
	 * @author Justin
	 *
	 */
	public class SaveProjectAsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Opens the file chooser and records the response
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(frame);
			
			//If the user doesn't cancel, get the file and save the project
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            frame.getProject().saveAs(file);
	        }
		}
	}
}