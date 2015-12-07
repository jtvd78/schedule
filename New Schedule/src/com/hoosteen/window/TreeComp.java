package com.hoosteen.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.hoosteen.graphics.Circle;
import com.hoosteen.graphics.GraphicsWrapper;
import com.hoosteen.graphics.Rect;
import com.hoosteen.helper.Settings;
import com.hoosteen.helper.Tools;
import com.hoosteen.schedule.ClassTime;
import com.hoosteen.schedule.Schedule;
import com.hoosteen.tree.Node;

//Join TreeComp and JScrollPane
//textArea.setCaretPosition(textArea.getDocument().getLength());

public class TreeComp extends JPanel {	
	
	private final int boxSize; //Width and height of expand boxes
	private final int nodeHeight; //Height of each node
	private final int circleRadius; //Radius of close circles
	private final int levelSpacing; // X-Spacing between each level of a tree. 
	
	//Start Settings
	private Color bgColor = Color.BLACK;
	private Color textColor = Color.BLACK;
	private Color otherColor = Color.WHITE;
	//End Settings
	
	Node tree;
	Frame parentFrame;
	
	//Node currently selected. If there is no node which is currently selected, this should be null
	Node selectedNode = null;	
	
	//Node currently being dragged. If there is no node being dragged currently, this should be null
	Node draggingNode = null;
	
	//These variables are used whenever a node is being dragged. 
	int draggingOffsetX = 0;
	int draggingOffsetY = 0;
	
	
	/**
	 * Main and only constructor. Takes 2 parameters
	 * @param ParentFrame
	 * @param Tree to draw
	 */
	public TreeComp(MainFrame parentFrame, Node tree){		
		
		circleRadius = Settings.circleRadius;
		boxSize = Settings.boxSize;
		nodeHeight = Settings.nodeHeight;
		levelSpacing = Settings.levelSpacing;		
		
		this.tree = tree;
		this.parentFrame = parentFrame;
		
		//Allows keyboard input
		setFocusable(true);
		
		//Configures input
		Listener l = new Listener();
		addMouseListener(l);
		addMouseMotionListener(l);
		addKeyListener(l);		
	}	
	
	/**
	 *Draws the tree to the Graphics object g
	 */
	public void paintComponent(Graphics gOld){		
		TreeGraphics g = new TreeGraphics(gOld);
		
		//Draw Background
		g.setColor(bgColor);
		g.fillRect(new Rect(0, 0, getWidth(), getHeight()));
		
		//Draw Tree
		g.drawNode(tree,true);
		
		//Draw dragging node on top of tree
		if(draggingNode != null){
			g.drawNode(draggingNode,false);
		}	
		
		//Update scroll panel so scroll bar is correct
		updateScrollPaneDimensions();
	}
	
	class TreeGraphics extends GraphicsWrapper{

		public TreeGraphics(Graphics g) {
			super(g);
		}
		
		/**
		 * Draws a specific node, and any child nodes to the Graphics object g
		 * @param node Node to be drawn
		 * @param g Graphics object to be drawn on
		 */
		private void drawNode(Node node, boolean drawChildren){		
			
			Rect nodeRect = getNodeRect(node);
			Rect ogNodeRect = getNodeRect(node);
			
			//If the current node being drawn is the node being dragged, 
			//adjust the node's rect to the offset position.
			if(node == draggingNode){			
				nodeRect = nodeRect.offset(draggingOffsetX, draggingOffsetY);
			}	
			
			//Sets color to draw node. if node is hidden, draw white
			setColor(node.getColor());
			if(node.isHidden()){
				setColor(Color.WHITE);
			}
			fillRect(nodeRect);
			
			//Node border
			setColor(bgColor);
			drawRect(nodeRect);		
			
			//Highlight selected node
			if(node.equals(selectedNode)){
				setColor(new Color(200,200,255,200));
				fillRect(nodeRect);			
				
				//Makes bold line around selected node
				Rect r1 = new Rect(nodeRect.getX() + 1, nodeRect.getY() + 1, nodeRect.getWidth() - 2, nodeRect.getHeight() - 2);
				Rect r2 = new Rect(nodeRect.getX() + 2, nodeRect.getY() + 2, nodeRect.getWidth() - 4, nodeRect.getHeight() - 4);
				
				setColor(otherColor);
				drawRect(r1);
				drawRect(r2);
			}	
			
			//Draw node text
			setColor(textColor);
			drawString(node.toString(),nodeRect);		
				
			//Horizontal line to the left of the node
			g.setColor(otherColor);
			drawLine(ogNodeRect.getX()-levelSpacing/2-levelSpacing,ogNodeRect.getY()+nodeHeight/2, ogNodeRect.getX()-levelSpacing/2, ogNodeRect.getY()+nodeHeight/2);
			
			//Draw Expand box if node has children.
			if(node.size() != 0){
				drawExpandBox(node);			
			}		
			
			if(node.isExpanded() && drawChildren){				
				//Vertical line under expand box
				setColor(otherColor);
				drawLine(ogNodeRect.getX()-levelSpacing/2,ogNodeRect.getY()+nodeHeight/2+boxSize/2,ogNodeRect.getX()-levelSpacing/2,ogNodeRect.getY()+nodeHeight/2+node.getExpandedNodeCount()*nodeHeight);			
				
				//Draw child nodes
				for(Node child : node){
					drawNode(child,drawChildren);
				}
			}
			
			//Draws the remove circle
			setColor(Color.RED);
			drawCircle(getRemoveCircle(node));
			
			//Draws X within remove circle
			setColor(Color.BLACK);
			int x = nodeRect.getX() + nodeRect.getWidth();
			drawLine(x - 3*nodeHeight/4, nodeRect.getY() + nodeHeight /4,x- nodeHeight/4,nodeRect.getY() + 3*nodeHeight/4);
			drawLine(x - 3*nodeHeight/4, nodeRect.getY() + 3*nodeHeight /4,x - nodeHeight/4,nodeRect.getY() + nodeHeight/4);
			
		}
		
		/**
		 * Draws the expand box to the left of a node, on the graphics object
		 * @param Node for which the expand box should be drawn
		 * @param Graphics object to draw on.
		 */
		private void drawExpandBox(Node n){	
			Rect r = getExpandRect(n);
			
			//Expand Box
			setColor(otherColor);		
			fillRect(r);
			
			//minus
			setColor(bgColor);	
			drawLine(r.getX() + 1, r.getY()  + r.getHeight()/2, r.getX() + r.getWidth() - 2, r.getY()  + r.getHeight()/2);
		
			//plus
			if(!n.isExpanded()){
				drawLine(r.getX() + r.getWidth()/2, r.getY() + 1, r.getX() + r.getWidth()/2, r.getY() + r.getHeight() - 2);
			}
		}
	}	
	

	
	//Start Rect/Circle stuff

	
	private Circle getRemoveCircle(Node n){
		int x =  getWidth() - 5 - circleRadius;
		int y = (n.getNodeNumber()+1)*nodeHeight - nodeHeight/2;
		return new Circle(x,y,circleRadius);
	}
	
	private Rect getExpandRect(Node n){
		return new Rect((n.getLevel()-1)*levelSpacing + (levelSpacing)/2 - boxSize/2, n.getNodeNumber()*nodeHeight  + nodeHeight/2 -boxSize/2, boxSize,boxSize);
	}
	
	private Rect getNodeRect(Node n){
		int x = n.getLevel()*levelSpacing;
		return new Rect(x, n.getNodeNumber()*nodeHeight, getWidth() - x - 1, nodeHeight);
	}
	//End Rect/Cicle Stuff
	
	/**
	 * Returns width of component.
	 */
	public int getWidth(){
		return this.getParent().getWidth();
	}
	
	/**
	 * Called when a node is left clicked
	 * @param Node clicked on
	 */
	private void nodeLeftClicked(Node n){
		selectNode(n);
	}
	
	/**
	 * Selects a given node. If parameter is null, deselects all nodes. 
	 * @param n
	 */
	private void selectNode(Node n){
		//Deselect old node
		if(selectedNode != null){
			selectedNode.setSelected(false);
		}			
		
		//Deselect current node
		if(selectedNode == n){
			selectedNode = null;
			
		//Set new node
		}else{
			selectedNode = n;
			
			//Set new node to null
			if(selectedNode != null){
				selectedNode.setSelected(true);
			}
		}
	}
	
	static JPopupMenu popupMenu = new JPopupMenu();
	
	/**
	 * Called when a node is right clicked
	 * @param Node right clicked
	 * @param X coordinate of click
	 * @param Y coordinate of click
	 */
	private void nodeRightClicked(Node n, int x, int y){	
		popupMenu.removeAll();
		
		if(n instanceof ClassTime){
			//Action which will remove any nodes in the current tree that conflicts with the classtime. 
			popupMenu.add(new AbstractAction("Remove Conflicting Classtimes"){
				public void actionPerformed(ActionEvent e) {
					((Schedule)(n.getTopNode())).removeConflictingClasstimes((ClassTime)n);
					//No repaint here. Need to add it somehow
				}
			});
		}
		
		popupMenu.add(new AbstractAction("Show Description"){
			public void actionPerformed(ActionEvent e) {
				Tools.displayText(n.getDescription(), n.toString());
			}	
		});
		popupMenu.show(this, x, y);
		repaint();
	}
	
	/**
	 * Updates the parent scrollPanel dimensions by updating this component's preferred size
	 * Should be called whenever the height of the tree changes
	 */
	private void updateScrollPaneDimensions(){		
		setPreferredSize(new Dimension(getWidth(),nodeHeight*tree.getExpandedNodeCount()));
		
		//fix this?
		revalidate();
	}
	
		
	
	/**
	 * Handles all user input within this component
	 * @author Justin
	 */
	private class Listener implements MouseListener, MouseMotionListener, KeyListener{
		
		//Previous x, previous y.
		int px = 0;
		int py = 0;
		
		/**
		 * Called when the mouse is dragged within this component
		 * Used to drag a node up and down within the tree. 
		 */
		public void mouseDragged(MouseEvent e) {
			//Must be left click to drag
			if(!SwingUtilities.isLeftMouseButton(e)){
				return;
			}
			
			Node clickedNode;
			if((clickedNode = tree.getVisibleNode(e.getY()/nodeHeight)) == null){
				selectNode(null);
				
				//If the clicked node is null, that means that no node was clicked on
				return;
			}
			
			if(draggingNode != null){				
				//Add change in position to offset
				draggingOffsetX += e.getX() - px;
				draggingOffsetY += e.getY() - py;					
				
				//Update previous X and Y
				px = e.getX();
				py = e.getY();							
				
				//Number of nodes to adjust by. 
				int adj = draggingOffsetY / nodeHeight;	
				
				//Moving Down
				if(adj > 0){
					int move = adj - draggingNode.getNodeBelow().getExpandedNodeCount() - draggingNode.getExpandedNodeCount();
					
					if( move > 0){						
						draggingNode.move(move);
						draggingOffsetY -= adj*nodeHeight + draggingNode.getExpandedNodeCount()*nodeHeight ;
						parentFrame.repaint();
					}
					
				//Moving up
				}else if(adj < 0){
					int move = adj + draggingNode.getNodeAbove().getExpandedNodeCount();
					
					//If you are above the next node
					if( move < 0){						
						draggingNode.move(move);
						draggingOffsetY -= adj*nodeHeight;
						parentFrame.repaint();
					}
				}
				
				repaint();
				
			//DraggingNode is null
			//Set DraggingNode to the clicked node so it can be dragged the next time this method is called
			}else{
				draggingNode = clickedNode;
			}			
		}
		
		/**
		 * Handles the event when a mouse is pressed. 
		 * Uses: Expand / Remove / Left and Right click node
		 */
		public void mousePressed(MouseEvent e) {
			
			Node clickedNode;
			if((clickedNode = tree.getVisibleNode(e.getY()/Settings.nodeHeight)) == null){
				selectNode(null);
				//If the clicked node is null, that means that no node was clicked on
				return;
			}
			
			int button = e.getButton();
			
			Rect nodeRect = getNodeRect(clickedNode);			
			Rect expandRect = getExpandRect(clickedNode);
			
			//Clicked on node
			if(nodeRect.contains(e.getX(), e.getY())){
				
				//Hit Remove circle:
				if(getRemoveCircle(clickedNode).contains(e.getX(), e.getY()) && e.getButton() == 1){
					clickedNode.remove();
					parentFrame.repaint();
					return;
				}
				
				//Left or right cick
				switch(button){
					case 1: nodeLeftClicked(clickedNode);
							parentFrame.repaint();
							break;
					case 2: clickedNode.toggleHidden();
							parentFrame.repaint();
							break;
					case 3: nodeRightClicked(clickedNode,e.getX(),e.getY());
							break;
				}
				repaint();	
				 
			//Clicked on ExpandRect for Node
			}else if(expandRect.contains(e.getX(),e.getY())){				
				clickedNode.toggleExpanded();
				updateScrollPaneDimensions();
				repaint();	
			}					
		}
		
		/**
		 * Keyboard input
		 */
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_UP:  
				
				if(selectedNode!= null){
					Node newNode = tree.getVisibleNode(selectedNode.getNodeNumber()-1);
					selectNode(newNode); 
				}else{
					selectNode(tree.getVisibleNode(tree.getExpandedNodeCount()-1));
				}
				
				
				break;
			case KeyEvent.VK_DOWN: 
				
				if(selectedNode!= null){
					Node newNode = tree.getVisibleNode(selectedNode.getNodeNumber()+1);
					selectNode(newNode); 
				}else{
					selectNode(tree.getVisibleNode(0));
				}
				
				break;
			case KeyEvent.VK_SPACE:
				if(selectedNode!=null){
					selectedNode.toggleHidden();
				}
			}
			parentFrame.repaint();
			
		}
		
		/**
		 * Update previous X and Y variables to help with mouse input
		 */
		public void mouseMoved(MouseEvent e) {
			px = e.getX();
			py = e.getY();}
		
		/**
		 * Called whenever the mouse is released. 
		 * Used for purposes of ending a node being dragged
		 */
		public void mouseReleased(MouseEvent arg0) {
			draggingNode = null;
			draggingOffsetX = 0;
			draggingOffsetY = 0;
			repaint();
		}		
		
		//Unused methods
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
		
	}
}