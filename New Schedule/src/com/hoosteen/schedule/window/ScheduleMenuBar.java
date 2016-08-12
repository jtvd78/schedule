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
import com.hoosteen.schedule.Project;
import com.hoosteen.schedule.Schedule;
import com.hoosteen.schedule.Time;
import com.hoosteen.schedule.settings.ScheduleSettingsWindow;

public class ScheduleMenuBar extends JMenuBar{	
	
	ScheduleFrame frame;
	
	public ScheduleMenuBar(ScheduleFrame frame){
		
		this.frame = frame;
		
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
			about.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(frame, "Made by Justin Van Dort");
				}
				
			});
		help.add(about);
		
		add(file);
		add(courses);
		add(edit);
		add(help);
	}
	
	public class PreferencesActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0){
			
			ScheduleFrame.getSettings().showSettingsWindow(frame);
		}
		
	}
	
	public class AddGenEdsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			
			TaskWindow taskWindow = new TaskWindow(frame, "Loading Gen-Eds");
			
			TaskWindow.Task task = taskWindow.new Task(){

				@Override
				public void begin() {
					Schedule s = new Schedule();
					
					GenEdSubcat[] enabled = ScheduleFrame.getSettings().getEnabledGenEds();
					
					double num = enabled.length;
					double ctr = 0;
					for(GenEdSubcat ges : enabled){
						
						if(interrupted){
							return;
						}
						
						window.updateProgress(ges.toString(), ctr / ( 2.0 * num) );
						ctr++;		
						
						s.addGenEdSubcat(ges);
					}
					
					window.updateProgress("Removing Duplicate Courses", 0.5f + 0.1);
					s.removeDuplicateCourses();
					
					//Remove 8 AMs
					window.updateProgress("Removing Early Class Times", 0.5f + 0.2);
					s.removeSectionsBetween(new Time(8,0,false),new Time(9,59,false));
					
					window.updateProgress("Removing Low Gen-Ed Courses", 0.5f + 0.3);
					s.removeCoursesWithLowerGenEdSubcats(2);
					
					window.updateProgress("Sorting", 0.5f + 0.4);
					s.sortAlphabetical();
					
					window.updateProgress("Merging", 0.5f + 0.5);
					frame.getProject().getSchedule().merge(s);
				}
				
			};
			
			taskWindow.runTask(task);				
		}

	}
	
	public class RefreshCoursesActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			frame.getProject().refresh();
		}
	}
	
	public class AddCourseActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			String id;
			if((id = JOptionPane.showInputDialog(frame, "Enter a class ID")) != null){
				frame.getProject().getSchedule().addCourseById(id, Tools.getRandomColor());
				repaint();
			}
		}
	}
	
	public class NewProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ScheduleFrame.newFrame(Project.makeDefaultProject());
		}

	}

	public class OpenProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(ScheduleMenuBar.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            ScheduleFrame.newFrame(Project.loadProject(file));
	        }
		}
	}
	
	public class SaveProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(frame.getProject().getLocation() == null){
				new SaveProjectAsActionListener().actionPerformed(null);
			}
			frame.getProject().save();
		}
	}
	
	public class SaveProjectAsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(ScheduleMenuBar.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            frame.getProject().saveAs(file);
	        }
		}
	}
}