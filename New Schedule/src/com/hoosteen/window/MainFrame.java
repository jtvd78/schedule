package com.hoosteen.window;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hoosteen.helper.Settings;
import com.hoosteen.schedule.Schedule;
import com.hoosteen.schedule.ScheduleLoader;

/**
 * This is the main window of the program. The main method is here. 
 * @author Justin
 *
 */
public class MainFrame extends JFrame{
	
	private Schedule schedule;
	
	private TreeComp treeComp;
	private JSplitPane splitPane;
	private JScrollPane scrollPane;
	
	private static final ArrayList<MainFrame> frameList = new ArrayList<MainFrame>();
	
	/**
	 * Its the main function, what else can I say?
	 * @param args
	 */
	public static void main(String[] args){
		setNativeUI();
		
		//Load default schedule, and make frame
		Schedule schedule = ScheduleLoader.getDefaultSchedule();
		newFrame(schedule);
	}
	
	/**
	 * Creates an instance of a MainFrame, with a Tree tree. 
	 * Mainframe includes the ScheduleDisplay on the left, and the TreeComp on the right
	 * @param Tree to manage.
	 */
	public MainFrame(Schedule schedule){
		this.schedule = schedule;
	
		setJMenuBar(new MainMenuBar(this));
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//Schedule display. Left side
		ScheduleDisplay scheduleDisplay = new ScheduleDisplay(schedule);
		
		//Tree comp. Right side
		//Scroll pane holds tree comp
		treeComp = new TreeComp(this, schedule);
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
	 * Creates a new frame with a given schedule
	 * @param schedule
	 */
	public static void newFrame(Schedule schedule) {
		frameList.add(new MainFrame(schedule));
	}	
	
	/** 
	 * @return The Frame's schedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}	
}