package com.hoosteen.tree;

import com.hoosteen.window.TreeComp;

public class Tree extends Node{
	
	public Tree(){
		setExpanded(true);
	}
	
	public int getLevel(){
		return 0;
	}
	
	public int getNodeNumber(){
		return -1;
	}
	
	public boolean isTransparent(){
		return false;
	}
}