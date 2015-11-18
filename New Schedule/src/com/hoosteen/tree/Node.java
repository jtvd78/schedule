package com.hoosteen.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Abstract class. A member of a tree. 
 * @author Justin
 *
 */
public abstract class Node implements Serializable, Iterable<Node>, Comparable{
	
	protected Node parent;	
	private boolean hidden = true;
	private boolean expanded = false;
	private boolean selected = false;	
	
	/**
	 * List of every node contained within this node
	 */
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	
	/**
	 * Moves a child node a number of indexes
	 * @param Node to move
	 * @param Number of indexes to move
	 */
	private void moveChildNode(Node n, int adj){
		System.out.println(adj);
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
	 * Moves this node's index in its parent by the parameter
	 * @param Number of indexes to move
	 */
	public void move(int adj){
		parent.moveChildNode(this, adj);
	}	
	
	/**
	 * @return The node above this one within its parent
	 */
	public Node getNodeAbove(){
		return parent.getNode(parent.getIndex(this)-1);
	}
	
	/**
	 * @return The node below this one within its parent
	 */
	public Node getNodeBelow(){
		return parent.getNode(parent.getIndex(this)+1);
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
	 * @param True or false
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
	 * Sets this node and all sub-nodes as the argument
	 * @param hidden
	 */
	public void setHidden(boolean hidden){
		this.hidden = hidden;
		for(Node n : nodeList){
			n.setHidden(hidden);
		}
	}
	
	/**
	 * @return The level of the node, in reference to the first node;
	 */
	public int getLevel(){
		return 1+parent.getLevel();
	}	
	
	/**
	 * @param Sets expanded to argument e
	 */
	public void setExpanded(boolean e){
		expanded = e;
	}	
	
	/**
	 * @return Returns expanded
	 */
	public boolean isExpanded(){
		return expanded;
	}
	
	/**
	 * Sets expanded to the opposite of expanded
	 */
	public void toggleExpanded(){
		expanded = !expanded;
	}
	
	/**
	 * @param Node to get the index of
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
	 * @return Returns color of Node. Defaults to GREEN. 
	 */
	public Color getColor(){
		return Color.GREEN;
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
	 * @return Size of this node
	 */
	public int size(){
		return nodeList.size();
	}
	
	/**
	 * @param Index to retrieve
	 * @return Returns node at a specific index
	 */
	public Node getNode(int index){
		return nodeList.get(index);
	}		

	/**
	 * @return Returns the index of the node, relative to the first node.
	 *  Essentially, it is the number of nodes from the top that this node is
	 */
	public int getNodeNumber(){
		int out = 0;
		
		int index = parent.getIndex(this);
		for(int i = 0; i < index; i++){
			out += parent.getNode(i).getExpandedNodeCount();
		}		
		return parent.getIndex(this)+out+parent.getNodeNumber()+1;
	}
	
	/**
	 * Returns the visible node under this node, which corresponds to the argument nodeIndex. 
	 * @param Node number to get
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
	 * Get number of nodes visible which are contained within this node.
	 * @return
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
	 * Removes this node from its parent, then removes itself if the parent is empty. 
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
	
	public Tree getTree(){
		Node topNode = this;
		while(topNode.getLevel() != 0){
			topNode = topNode.parent;
		}
		return (Tree)topNode;
	}	

	public class DescriptionAction extends AbstractAction{
		
		public DescriptionAction(){
			super("Show Descrition");
		}			
		
		public void actionPerformed(ActionEvent e) {				
			JTextArea jta = new JTextArea(getDescription());
			jta.setLineWrap(true);
			jta.setWrapStyleWord(true);
            JScrollPane jsp = new JScrollPane(jta);
            jsp.setPreferredSize(new Dimension(480, 320));
            JOptionPane.showMessageDialog( null, jsp, Node.this.toString(), JOptionPane.DEFAULT_OPTION);			
		}
	}

	public void showPopupMenu(Component comp, int x, int y) {
		JPopupMenu jpu = new JPopupMenu();
		jpu.add(new DescriptionAction());
		jpu.show(comp, x, y);
	}
	
	@Override
	public int compareTo(Object o) {
		return toString().compareTo(o.toString());
	}
}