package com.hoosteen.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JPopupMenu;

/**
 * Abstract class. A Node is a member of a figurative tree. There is
 * no tree object, but each node can function as a tree if it has no parent.
 * It can be expanded, selected, and hidden. 
 * Nodes can contain any number of other nodes
 * Nodes are Iterable, Serializable, and Comparable
 *  
 * @author justin
 *
 */
public abstract class Node implements Serializable, Iterable<Node>, Comparable<Node>{
	
	protected Node parent;	
	private boolean hidden = true;
	private boolean expanded = false;
	private boolean selected = false;	
	
	/**
	 * List of every node contained within this node
	 */
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	
	protected static JPopupMenu popupMenu = new JPopupMenu();
	
	public Node(){
		//Default constructor
		//Expanded is already false, don't have to set that here. 
	}
	
	public Node(boolean expanded){
		this.expanded = expanded;
	}
	
	/**
	 * Moves this node's index in its parent by the parameter
	 * @param adj - Number of indexes to move
	 */
	public void move(int adj){
		parent.moveChildNode(this, adj);
	}	
	
	/**
	 * Moves a child node a number of indexes
	 * @param n Node to move
	 * @param adj Number of indexes to move
	 */
	private void moveChildNode(Node n, int adj){
		if(nodeList.contains(n)){
			int newIndex = nodeList.indexOf(n) + adj;
			
			//Make sure that you are not moving outside of the nodeList's bounds
			if(newIndex >= 0 && newIndex < nodeList.size()){
				nodeList.remove(n);			
				nodeList.add(newIndex, n);	
			}
		}
	}
	
	/**
	 * Gets the index of this node relative to its parent node.
	 * @return The index
	 */
	public int index(){
		return parent.getIndex(this);
	}
	
	/**
	 * @return The node above this one within its parent
	 */
	public Node getNodeAbove(){
		int index = index();
		if(index == 0){
			return null;
		}
		return parent.getNode(index-1);
	}
	
	/**
	 * @return The node below this one within its parent
	 */
	public Node getNodeBelow(){
		int index = index();
		if(index+1 == parent.size()){
			return null;
		}
		return parent.getNode(index+1);
	}
	
	/**
	 * Allows this class to be used within an Advanced for loop. Very useful. 
	 */
	public Iterator<Node> iterator() {
		return nodeList.iterator();
	}
	
	/**
	 * @return If currently selected by the mouse. 
	 */
	public boolean isSelected(){
		return selected;
	}
	
	
	/**
	 * Alphabetically sorts the nodes in the this node by their toString
	 */
	public void sortAlphabetical(){
		Collections.sort(nodeList);
	}
	
	
	/**
	 * Sets the value of selected to the parameter
	 * Also sets the child nodes to the same value
	 * @param selected - True or false
	 */
	public void setSelected(boolean selected){
		this.selected = selected;
		for(Node n : this){
			n.setSelected(selected);
		}
	}	
	
	/**
	 * A node is transparent if its parent is transparent, or if it is currently selected. 
	 * @return boolean, transparent or not. 
	 */
	public boolean isTransparent(){
		
		//The top node is never transparent
		//I wouldn't say never, but for the purposes of this class, it wont
		//I will come back and change this if I need to. 
		if(getLevel() == 0){
			return false;
		}
		
		return selected || parent.isTransparent();
	}
	
	/**
	 * Sets hidden to the opposite of its current value
	 */
	public void toggleHidden() {
		hidden = !hidden;
		for(Node n : nodeList){
			n.setHidden(hidden);
		}
	}
	
	/**
	 * @return A boolean: weather this node is hidden or not. 
	 */
	public boolean isHidden(){
		return hidden;
	}
	
	/** 
	 * Sets this node and all sub-nodes as the parameter
	 * @param hidden - Value to set hidden of node to 
	 */
	public void setHidden(boolean hidden){
		this.hidden = hidden;
		for(Node n : nodeList){
			n.setHidden(hidden);
		}
	}
	
	/**
	 * @return The level of the node, in reference to the first node
	 * A Node with no parent will return 0
	 */
	public int getLevel(){
		if(parent == null){
			return 0;
		}
		return 1+parent.getLevel();
	}	
	
	/**
	 * @param expanded - Sets expanded to input boolean
	 */
	public void setExpanded(boolean expanded){
		this.expanded = expanded;
	}	
	
	/**
	 * @return Returns boolean expanded
	 */
	public boolean isExpanded(){
		return expanded;
	}
	
	/**
	 * Sets expanded to the opposite of its current state
	 */
	public void toggleExpanded(){
		expanded = !expanded;
	}
	
	/**
	 * @param index - Index to retrieve
	 * @return Returns node at a specific index
	 */
	public Node getNode(int index){
		return nodeList.get(index);
	}	
	
	/**
	 * @param n - Node to get the index of
	 * @return The index of n, within the current node. Returns -1 if this node does not contain n.
	 */
	public int getIndex(Node n){
		int ctr = 0;
		for(Node n2 : nodeList){
			if(n == n2){
				return ctr;
			}
			ctr++;
		}
		return -1;
	}
	
	/**
	 * @return Returns color of Node. Defaults to WHITE. 
	 */
	public Color getColor(){
		return Color.WHITE;
	}
	
	/**
	 * Adds n to this node
	 * @param n Node to be added
	 */
	public synchronized void addNode(Node n){		
		n.setParent(this);
		nodeList.add(n);
	}
	
	/**
	 * Private method. Used in conjunction with addNode(). 
	 * Used to set the parent of the node being added to this. 
	 * @param n The parent node
	 */
	private void setParent(Node n){
		this.parent = n;
	}	
	
	/**
	 * Removes n from this current node
	 * @param n Node to be removed
	 * @return Returns true for success, false for not. 
	 */
	public synchronized boolean removeNode(Node n){
		return nodeList.remove(n);
	}
	
	/** 
	 * @return Size of this node. The amount of nodes contained within it
	 */
	public int size(){
		return nodeList.size();
	}

	/**
	 * @return Returns the index of the node, relative to the first node.
	 *  Essentially, it is the number of nodes from the top that this node is
	 */
	public int getNodeNumber(){ 
		
		//This just means that the top node (which should not be visible),
		//has a visible node number of -1
		//so it is not displayed. 
		if(getLevel() == 0){
			return -1;
		}
		
		int out = 0;
		
		int index = parent.getIndex(this);
		for(int i = 0; i < index; i++){
			out += parent.getNode(i).getExpandedNodeCount();
		}		
		return parent.getIndex(this)+out+parent.getNodeNumber()+1;
	}
	
	/**
	 * Returns the visible node under this node, which corresponds to the argument nodeIndex. 
	 * Starts with 0
	 * @param nodeIndex - Node number to get
	 * @return The desired node
	 */
	public Node getVisibleNode(int nodeIndex){		
		for(Node n : nodeList){			
			//This is the desired node, return this node.
			if(nodeIndex == 0){
				return n;
			}
			
			//to account for current node
			nodeIndex -= 1; 
			
			//Total number of node visible within this node
			int count = n.getExpandedNodeCount();
			
			//If there are visible nodes within this node
			if(count > 0){
				if(nodeIndex >= count){
					nodeIndex -= count; // Skip to the next n since we need to go farther down
				}else{
					return n.getVisibleNode(nodeIndex); //Desired node is within this node
				}
			}			
		}
		
		//No Visible node found
		return null;
	}	
	
	/**
	 * @return number of nodes visible which are contained within this node.
	 */
	public int getExpandedNodeCount(){
		int ctr = 0;
		
		if(expanded && size() > 0){
			for(Node n : nodeList){
				ctr += n.getExpandedNodeCount()+1;
			}			
		}
		return ctr;
	}

	/**
	 * Removes this node from its parent, then removes the parent itself if it is empty. 
	 */	
	public void remove() {
		parent.removeNode(this);
		
		//After removing this node, if the parent node is empty, remove the parent node as well. 
		if(parent.nodeList.size() == 0){
			parent.remove();
		}
	}
	
	/**
	 * @return Description of the current node. Defaults to toString(), but can be overrided. 
	 */
	public String getDescription(){
		return toString();
	}
	
	/**
	 * @return The topmost node in the Node Hierarchy. Level of the returned node is 0
	 */
	public Node getTopNode(){
		Node topNode = this;
		while(topNode.getLevel() != 0){
			topNode = topNode.parent;
		}
		return topNode;
	}	
	
	@Override
	public int compareTo(Node o) {
		return toString().compareTo(o.toString());
	}
}