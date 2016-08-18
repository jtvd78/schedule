package com.hoosteen.schedule.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.hoosteen.schedule.GenEdSubcat;
import com.hoosteen.schedule.URLMaker;
import com.hoosteen.settings.SettingsWindow;

/**
 * A SettingsWindow which displays a ScheduleSettings object
 * @author Justin
 *
 */
public class ScheduleSettingsWindow extends SettingsWindow{	
	
	/**
	 * Settings object to display
	 */
	ScheduleSettings settings;	
	
	/**
	 * List of enabled courses
	 */
	JList<String> courseList = new JList<String>();
	
	/**
	 * Map to correspond GenEds to CheckBoxes
	 */
	HashMap<GenEdSubcat, JCheckBox> checkBoxMap = new HashMap<GenEdSubcat, JCheckBox>();
	
	/**
	 * Courses to add when the settings are saved
	 */
	ArrayList<String> newCourses = new ArrayList<String>();
	
	/**
	 * Courses to remove when the settings are saved
	 */
	ArrayList<String> oldCourses = new ArrayList<String>();
	
	/**
	 * Model which contols the default course list
	 */
	DefaultListModel<String> model = new DefaultListModel<String>();
	
	/**
	 * Creates a ScheduleSettingsWindow with an owner and a ScheduleSettings to display
	 * @param owner
	 * @param settings
	 */
	public ScheduleSettingsWindow(JFrame owner, ScheduleSettings settings){
		super(owner, settings);		
		this.settings = settings;		
		
		//Add the GenEdPanel and the CoursePanel
		add(new GenEdPanel(), BorderLayout.WEST);
		add(new CoursePanel(), BorderLayout.CENTER);
	}
	
	/**
	 * Allows the user to select the GenEds that they want
	 * @author Justin
	 *
	 */
	private class GenEdPanel extends JPanel{	
		
		public GenEdPanel(){			
			setBorder(createBorder("Enabled Gen-Eds"));
			
			//Aligns the check boxes vertically
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));	
			
			//100px width is enough
			setPreferredSize(new Dimension(100,0));
			
			//Loop through each GenEd and add a checkbox
			for(GenEdSubcat subcat : GenEdSubcat.values()){
				
				JCheckBox box = new JCheckBox(subcat.toString());
				
				//Save the checkbox in the checkBoxMap
				checkBoxMap.put(subcat, box);
				
				//Add the box to the GenEdPanel
				add(box);				
			}		
		}		
	}
	
	/**
	 * Allows the user to select the courses which are loaded for a new project
	 * @author Justin
	 *
	 */
	private class CoursePanel extends JPanel{
		
		/**
		 * The button which removes a default course
		 */
		JButton remove;
		
		/**
		 * The index in the list which is selected
		 */
		int selectedIndex = -1;
		
		public CoursePanel(){
			
			setLayout(new BorderLayout());		
			setBorder(createBorder("Default Courses"));			
			
			//Add coursList selection listener, and make the list select one item at once
			courseList.addListSelectionListener(new CourseListListener());
			courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			courseList.setModel(model);		
			
			//Panel which holds the buttons on the right side
			JPanel rightButtonPanel = new JPanel();
			
			//Aligns the Buttons vertically
			rightButtonPanel.setLayout(new BoxLayout(rightButtonPanel,BoxLayout.Y_AXIS));
			
			//Add button
			JButton add = new JButton("Add...");
			add.addActionListener(new AddActionListener());
			
			//Remove button, starts out greyed-out
			remove = new JButton("Remove");
			remove.addActionListener(new RemoveListener());
			remove.setEnabled(false);
			
			//Add buttons to right panel and make 10px border to left of buttons
			rightButtonPanel.add(add);
			rightButtonPanel.add(remove);	
			rightButtonPanel.setBorder(new EmptyBorder(0,10,0,0));
			
			//Add components to CoursePanel
			add(courseList, BorderLayout.CENTER);
			add(rightButtonPanel, BorderLayout.EAST);			
		}		
		
		/**
		 * Detects when the user selected an index in the course list
		 * @author Justin
		 *
		 */
		private class CourseListListener implements ListSelectionListener{

			@Override
			public void valueChanged(ListSelectionEvent e) {				
				selectedIndex = e.getFirstIndex();
				
				remove.setEnabled(true);
			}
			
		}
		
		/**
		 * Detects when the user wants to remove a course
		 * @author Justin
		 *
		 */
		private class RemoveListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {	
				
				//Make sure an item is selected, and make sure it corresponds to an item in the model
				if(selectedIndex != -1 && selectedIndex < model.size()){
					
					//Get the selected course's ID
					String course = model.getElementAt(selectedIndex);
					
					//reset selectedIndex, disable button
					selectedIndex = -1;
					remove.setEnabled(false);
					
					//If the user previously selected to add the course, but is now removing it
					//remove the course from the new courses list
					if(newCourses.contains(course)){
						newCourses.remove(course);
					}
					
					//Set course to removed when saved
					oldCourses.add(course);
					
					//Remove course string from list model
					model.removeElement(course);					
				}				
			}			
		}
		
		/**
		 * Detects when a user wants to add a default Course
		 * @author Justin
		 *
		 */
		private class AddActionListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//Attempts to get the ID of a the course the user wants to add
				String id;
				if((id = JOptionPane.showInputDialog(CoursePanel.this, "Enter a class ID")) != null){
					
					//Make sure the Course string won't throw an error
					if(URLMaker.confirmCourse(id)){
						
						//You already have the course in the list
						if(model.contains(id)){
							return;
						}
						
						//If the user previously removed the course, and then wants to add it again, 
						//remove the course from the remove list
						if(oldCourses.contains(id)){ 
							oldCourses.remove(id);
						}					

						//Set course to be added when saved
						newCourses.add(id);
						
						//Add course to list model
						model.addElement(id);
					}					
				}
			}		
		}		
	}
	
	@Override
	public void refreshOptions() {		
		
		//Clear all old data
		model.clear();
		newCourses.clear();
		oldCourses.clear();	
		
		//Init model with default courses
		for(String s : settings.getDefaultCourses()){			
			model.addElement(s);
		}
		
		//De-select all boxes
		for(JCheckBox box : checkBoxMap.values()){
			box.setSelected(false);
		}
		
		//Select boxes for all enabled GenEds
		for(GenEdSubcat subcat : settings.getEnabledGenEds()){
			checkBoxMap.get(subcat).setSelected(true);
		}
	}
	
	@Override
	protected void updateSettings(){
		
		//Save the new courses
		for(String s : newCourses){
			settings.addDefaultCourse(s);
		}
		
		//Remove the old courses
		for(String s : oldCourses){
			settings.removeDefaultCourse(s);
		}		
		
		//Set GenEds to their selected values
		for(GenEdSubcat subcat : checkBoxMap.keySet()){
			JCheckBox box = checkBoxMap.get(subcat);
			
			if(box.isSelected()){
				settings.enableGenEd(subcat);
			}else{
				settings.disableGenEd(subcat);
			}			
		}	
	}
}