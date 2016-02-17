package com.hoosteen.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hoosteen.helper.Settings;
import com.hoosteen.helper.Tools;
import com.hoosteen.helper.GenEdSubcat;
import com.hoosteen.schedule.Project;
import com.hoosteen.schedule.Schedule;
import com.hoosteen.schedule.Time;

/**
 * This is the main window of the program. The main method is here. 
 * @author justin
 *
 */
public class MainFrame extends JFrame{
	
	private Project project;
	
	private TreeComp treeComp;
	private JSplitPane splitPane;
	private JScrollPane scrollPane;
	
	private static final ArrayList<MainFrame> frameList = new ArrayList<MainFrame>();
	
	/**
	 * Its the main function, what else can I say?
	 * @param args - Command line arguments
	 */
	public static void main(String[] args){
		setNativeUI();
		
		//Load default project, and make frame
		Project project = Project.makeDefaultProject();
		newFrame(project);
	}
	
	/**
	 * Creates an instance of a MainFrame, with a Tree tree. 
	 * Mainframe includes the ScheduleDisplay on the left, and the TreeComp on the right
	 * @param project - Project to manage.
	 */
	public MainFrame(Project project){
		this.project = project;
	
		setJMenuBar(new MainMenuBar());
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//Schedule display. Left side
		ScheduleDisplay scheduleDisplay = new ScheduleDisplay(project.getSchedule());
		
		//Tree comp. Right side
		//Scroll pane holds tree comp
		treeComp = new TreeComp(this, project.getSchedule());
		scrollPane = new JScrollPane(treeComp);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Settings.nodeHeight);
		disableArrowKeys(scrollPane);
		
		//Split pane separates the two
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scheduleDisplay,scrollPane);
		splitPane.setDividerLocation(Settings.defaultWindowWidth*3/4);			
		add(splitPane, BorderLayout.CENTER);
		
		setSize(Settings.defaultWindowWidth, Settings.defaultWindowHeight);
		setVisible(true);
	}	
	
	/**
	 * Disables the arrow keys for MainFrame's treeComp. Allows the scrollPane not to move up and down when arrow keys are pressed,
	 * but the selection will still go up and down
	 */
	private static void disableArrowKeys(JScrollPane scrollPane) {
		String[] keystrokeNames = {"UP","DOWN","LEFT","RIGHT"};
		for(int i=0; i<keystrokeNames.length; ++i){
			scrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(keystrokeNames[i]), "none");
		}
	}
	
	/**
	 * Makes the window look nice
	 */
	public static void setNativeUI(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new frame with a given project
	 * @param project - Project to manage
	 */
	public static void newFrame(Project project) {
		frameList.add(new MainFrame(project));
	}		
	
	public class MainMenuBar extends JMenuBar{		
		
		public MainMenuBar(){			
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
				
				project.getSchedule().merge(s);
			}

		}
		
		public class AddCourseActionListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				String id;
				if((id = JOptionPane.showInputDialog("Enter a class ID")) != null){
					project.getSchedule().addCourseById(id, Tools.getRandomColor());
					repaint();
				}
			}
		}
		
		public class NewProjectActionListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.newFrame(Project.makeDefaultProject());
			}

		}

		public class OpenProjectActionListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(MainMenuBar.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            MainFrame.newFrame(Project.loadProject(file));
		        }
			}
		}
		
		public class SaveProjectActionListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(project.getLocation() == null){
					new SaveProjectAsActionListener().actionPerformed(null);
				}
				project.save();
			}
		}
		
		public class SaveProjectAsActionListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(MainMenuBar.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            project.saveAs(file);
		        }
			}
		}
	}
}