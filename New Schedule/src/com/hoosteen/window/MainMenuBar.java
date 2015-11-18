package com.hoosteen.window;

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

import com.hoosteen.other.GenEdSubcat;
import com.hoosteen.other.Time;
import com.hoosteen.other.Tools;
import com.hoosteen.schedule.Schedule;
import com.hoosteen.tree.ScheduleLoader;

public class MainMenuBar extends JMenuBar{
	
	MainFrame mainFrame;
	
	public MainMenuBar(MainFrame mainFrame){
		
		this.mainFrame = mainFrame;
		
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
		courses.add(addCourse);
		courses.add(addGenEds);
		
		add(file);
		add(courses);
	}
	
	public class AddGenEdsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Schedule s = new Schedule();
			
			for(GenEdSubcat ges : GenEdSubcat.values()){
				
				//I already have these requirements done;
				if(ges == GenEdSubcat.FSMA || ges == GenEdSubcat.FSOC ||
						ges == GenEdSubcat.FSAW || ges == GenEdSubcat.FSAR ||
						ges == GenEdSubcat.DSNS || ges == GenEdSubcat.DSNL || ges == GenEdSubcat.FSPW)
				{
					continue;
				}			
				
				System.out.println(ges);
				s.addGenEdSubcat(ges);
			}
			
			s.removeDuplicateCourses();
			
			//Remove 8 AMs
			s.removeSectionsBetween(new Time(8,0,false),new Time(9,59,false));
			
			s.removeCoursesWithLowerGenEdSubcats(2);
			s.sortAlphabetical();
			s.removeEmptySections();
			
			mainFrame.getSchedule().merge(s);
		}

	}
	
	public class AddCourseActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			String id;
			if((id = JOptionPane.showInputDialog("Enter a class ID")) != null){
				mainFrame.getSchedule().addCourseById(id, Tools.getRandomColor());
				mainFrame.repaint();
			}			
		}
	}
	
	public class NewProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			MainFrame.newFrame(ScheduleLoader.getDefaultSchedule());
		}

	}

	public class OpenProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(MainMenuBar.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            MainFrame.newFrame(ScheduleLoader.loadSchedule(file));
	        }
		}
	}
	
	public class SaveProjectActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
		}
	}
	
	public class SaveProjectAsActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(MainMenuBar.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            ScheduleLoader.saveSchedule(mainFrame.getSchedule(), file);
	        }
		}
	}
}