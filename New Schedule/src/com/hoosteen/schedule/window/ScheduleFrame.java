package com.hoosteen.schedule.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hoosteen.Tools;
import com.hoosteen.graphics.TaskWindow;
import com.hoosteen.schedule.GenEdSubcat;
import com.hoosteen.schedule.Project;
import com.hoosteen.schedule.Schedule;
import com.hoosteen.schedule.Time;
import com.hoosteen.schedule.settings.ScheduleSettings;
import com.hoosteen.schedule.settings.ScheduleSettingsWindow;
import com.hoosteen.settings.Settings;
import com.hoosteen.settings.SettingsLoader;
import com.hoosteen.tree.TreeComp;

/**
 * This is the main window of the program. The main method is here. 
 * @author justin
 *
 */
public class ScheduleFrame extends JFrame{
	
	private Project project;
	
	private TreeComp treeComp;
	private JSplitPane splitPane;
	
	private static ScheduleSettings settings;
	
	/**
	 *Default window width
	 */
	public static final int defaultWindowWidth = 800;
	
	/**
	 * Default window height
	 */
	public static final int defaultWindowHeight = 600;
	
	private static final ArrayList<ScheduleFrame> frameList = new ArrayList<ScheduleFrame>();
	
	/**
	 * Its the main function, what else can I say?
	 * @param args - Command line arguments
	 */
	public static void main(String[] args){
		com.hoosteen.Tools.setNativeUI();
		
		
		settings = Settings.loadSettings(ScheduleSettings.class, com.hoosteen.schedule.settings.Resources.programName,
				com.hoosteen.schedule.settings.Resources.version);
		
		//Load default project, and make frame
		Project project = Project.makeDefaultProject();
		newFrame(project);
	}
	
	public static ScheduleSettings getSettings(){
		return settings;
	}
	
	/**
	 * Creates an instance of a MainFrame, with a Tree tree. 
	 * Mainframe includes the ScheduleDisplay on the left, and the TreeComp on the right
	 * @param project - Project to manage.
	 */
	public ScheduleFrame(Project project){
		this.project = project;
	
		setJMenuBar(new ScheduleMenuBar(this));
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//Tree comp. Right side
		//Scroll pane holds tree comp
		treeComp = new TreeComp(this, project.getSchedule());
		
		//Schedule display. Left side
		ScheduleDisplay scheduleDisplay = new ScheduleDisplay(project.getSchedule(),treeComp);
		
		//Split pane separates the two
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,scheduleDisplay,treeComp);
		splitPane.setDividerLocation(defaultWindowWidth*3/4);			
		add(splitPane, BorderLayout.CENTER);
		
		setSize(defaultWindowWidth, defaultWindowHeight);
		setVisible(true);
	}

	/**
	 * Creates a new frame with a given project
	 * @param project Project to manage
	 */
	public static void newFrame(Project project) {
		frameList.add(new ScheduleFrame(project));
	}

	public Project getProject() {
		return project;
	}	
}