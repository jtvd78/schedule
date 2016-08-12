package com.hoosteen.schedule.settings;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hoosteen.schedule.GenEdSubcat;
import com.hoosteen.settings.SettingsWindow;

public class ScheduleSettingsWindow extends SettingsWindow{	
	
	ScheduleSettings settings;	
	HashMap<GenEdSubcat, JCheckBox> checkBoxMap = new HashMap<GenEdSubcat, JCheckBox>();
	
	public ScheduleSettingsWindow(JFrame owner, ScheduleSettings settings){
		super(owner, settings);		
		this.settings = settings;		
		
		add(new GenEdPanel(), BorderLayout.CENTER);
	}
	
	private class GenEdPanel extends JPanel{	
		
		public GenEdPanel(){
			
			setBorder(createBorder("Enabled Gen-Eds"));
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));			
			
			for(GenEdSubcat subcat : GenEdSubcat.values()){
				
				JCheckBox box = new JCheckBox(subcat.toString());
				checkBoxMap.put(subcat, box);
				
				add(box);				
			}		
		}		
	}
	
	@Override
	public void refreshOptions() {
		for(GenEdSubcat subcat : settings.getEnabledGenEds()){
			checkBoxMap.get(subcat).setSelected(true);
		}
	}
	
	@Override
	protected void updateSettings(){
		
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