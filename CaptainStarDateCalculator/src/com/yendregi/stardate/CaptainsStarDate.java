package com.yendregi.stardate;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.yendregi.stardate.util.AboutStarDatePane;
import com.yendregi.stardate.util.ImagePanel;
import com.yendregi.stardate.util.StarDateSkin;
import com.yendregi.stardate.util.StarDateSkinManager;

/**
 * The main Captains Star Date Class <br/>
 * Class creates the JFrame and implements a number of custom methods for window event handling<br/>
 * <br/>
 * The algorithm to calculate the star date is based of this calculator: <br/>  
 * Stardate script © Phillip L. Sublett, <a>http://TrekGuide.com</a> <br/>
 * {@code var now = new Date();}<br/>
 * {@code var then = new Date("July 15, 1987");}<br/>
 * {@code var stardate = now.getTime() - then.getTime();}<br/>
 * {@code stardate = stardate / (1000 * 60 * 60 * 24 * 0.036525);}<br/>
 * {@code stardate = Math.floor(stardate + 410000);}<br/>
 * {@code stardate = stardate / 10;}<br/>
 * <br/>
 * Panels created for this application based off: <a>http://www.lcars.org.uk/lcars_Alien_panels.htm<a/><br/>
 * <br/>
 * @author Andrzej K. W.
 */
public class CaptainsStarDate extends JFrame {

private static final long serialVersionUID = -1446375010640805371L;
public static final String aboutString = "Stardate Calculator Log\n" +
										 "Inception: 60147.3\n"+
										 "System Tray Port: 70155.1\n"+
										 "UI Skinning: 70165.7\n"+
										 "Public Release: 71351.052\n"+
										 "Andrzej K. Wloskowicz\n"; 

//globals
protected JLabel dateDisp;
protected JLabel starDateDesc;
protected JLabel starDateDisp;
protected JTextField starDateText;
protected JTextField normDateEditText;
protected GroupLayout viewLayout;
protected GroupLayout startDateEditLayout;
protected GroupLayout normDateEditLayout;

protected boolean starDateEditLayoutModeEngaged=false;
protected boolean normDateEditLayoutModeEngaged=false;
protected boolean lockDateUpdateOnEditMode=false;
protected boolean lockStarDateUpdateOnNormEditMode=false;

protected TrayIcon trayIcon = null;

protected static boolean showOnStartUp = true;
protected static boolean showWindowMenuBar = true;

protected boolean windowFocusState = false;
protected boolean starDateTextFocusState = false;
protected boolean normDateTextFocusState = false;
protected boolean windowActive = false;
protected boolean focusStateLockOverride = false;

protected Date currdate = new Date(System.currentTimeMillis());
protected long thendate;
protected Calendar calen = Calendar.getInstance();

protected String nowDateStr = new String();
protected String nowStarDateStr = new String();

protected Object[] comm = new Object[3];// convenience object for globals that need to be shared 
protected StarDateEngine starCalcEngine;

protected Point userLastPressedPoint = new Point(-1,-1);
protected Point lastLocationOnPressedPoint = new Point(-1,-1);

protected StarDateSkinManager starDateSkinManager;

//these are cached by each skin
protected Image trekBadgeI = null;
protected Image trekShellUnLockedI = null;
protected Image trekShellLockedI = null;
protected ImagePanel stardateImagePanel = null;

private static final String propertyFile = "/conf/CaptainsStarDate.properties";

/**
 * CaptainsStarDate Constructor
 * @param String framename : the frame name
 * @param Properties prop : the properties file name
 */
public CaptainsStarDate(String framename, Properties prop){
   super(framename);
   starDateSkinManager = new StarDateSkinManager(prop);
   
   trekBadgeI = starDateSkinManager.getDefaultSkin().getTrekBadgeImage(); 
   trekShellUnLockedI = starDateSkinManager.getDefaultSkin().getTrekShellUnLockedImage();
   trekShellLockedI = starDateSkinManager.getDefaultSkin().getTrekShellLockedImage(); 
   
   calen.set(1987,6,15,0,0); //setup the past date (the date of course on the launch of tng star trek - 1987,6,15,0,0 )
   
   thendate = calen.getTimeInMillis();   
   calculateStarDate();  
   
   buildWindow();
   
   comm[0] = this; //this and that object
   comm[1] = new Boolean(false); //indication if the about dialog has been invoked
   comm[2] = null; //the initialized about JDialog 
   starCalcEngine = new StarDateEngine(comm); //init the calculator engine
   starCalcEngine.start();
}

/**
 * helper method to calculate and update this frames current star date
 * initial formula has been edited to give max range on decimal to the thousandth ( ######.### )
 */
public void calculateStarDate( ){	
	double diff = (double)System.currentTimeMillis() - (double)thendate;
	double stardate = ((double)diff)/(1000 * 60 * 60 * 24 * 0.036525);
	stardate = Math.floor( (stardate + 410000) * 100);
	stardate = stardate / 1000;
	nowStarDateStr = new String(""+ (stardate));
}

/**
 * helper method to get a date given a start date
 * @param double aStarDate
 * @return String a real date 
 */
public String getDateFromStarDate(double aStarDate) {	
	aStarDate = aStarDate * 10;
	aStarDate = aStarDate - 410000;
	aStarDate = aStarDate * (1000 * 60 * 60 * 24 * 0.036525);
	aStarDate = aStarDate + (double)thendate;
	Date now = new Date((long) aStarDate);
	return now.toString();
}

/**
 * helper method to get a Date from a date string
 * @param String dateString
 * @return Date parsed date
 * @throws ParseException
 */
public Date getDateFromDateString(String dateString) throws ParseException { 
	DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);  
return df.parse(dateString);
}

/**
 * helper method to return a star date given a real date
 * @param Date theDate
 * @return String StarDate
 */
public String getStarDateFromDate(Date theDate) {
	double diff = (double)theDate.getTime() - (double)thendate;
	double stardate = ((double)diff)/(1000 * 60 * 60 * 24 * 0.036525);
	stardate = Math.floor( (stardate + 410000) * 100);
	stardate = stardate / 1000;
	return new String(""+stardate);
}

/**
 * helper method to verify this windows state based on states starDateTextFocusState, normDateTextFocusState, windowFocusState
 */
public void verifyWindowState(){
	if(!focusStateLockOverride) {
		if(windowActive){
			if(!(starDateTextFocusState || normDateTextFocusState || windowFocusState)){
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				this.windowActive = false;
			}
		}
	}
}

/**
 * asynchronous method to update this frames labels
 */
public void updateLabels(){
	this.currdate = new Date(System.currentTimeMillis());
	this.calculateStarDate();
	
	if(!this.lockDateUpdateOnEditMode)
		this.dateDisp.setText(" "+this.currdate.toString());
	
	if(!this.lockStarDateUpdateOnNormEditMode)
		this.starDateDisp.setText(""+this.nowStarDateStr);
	
	if(this.trayIcon != null) 
		this.trayIcon.setToolTip(this.starDateDesc.getText()+" "+this.nowStarDateStr+"\n"+currdate.toString()+"");
	
	if(this.starDateText == null) 
		this.starDateText = new JTextField(3);
	if(!this.starDateEditLayoutModeEngaged)
		this.starDateText.setText(this.nowStarDateStr);
	
	if(this.normDateEditText == null)
		this.normDateEditText = new JTextField(3);
	
	if(!this.normDateEditLayoutModeEngaged)
			this.normDateEditText.setText(this.currdate.toString());
	
	if(this.windowActive) //attempt at workaround for when the window sits inactive for a while, the background doesn't repaint 
		this.repaint();
	
}

/**
 * the main window build method for this frame
 */
public void buildWindow(){
	Container everything = this.getContentPane();
	
	stardateImagePanel = new ImagePanel(
			this.trekShellUnLockedI.getScaledInstance(starDateSkinManager.getDefaultSkin().getWinWidth(), 
													  starDateSkinManager.getDefaultSkin().getWinHeight(), Image.SCALE_SMOOTH), 
			starDateSkinManager.getDefaultSkin().getTrekLockTextColor(),
			starDateSkinManager.getDefaultSkin().getTrekLockTextFont(),
			starDateSkinManager.getDefaultSkin().getTrekLockText(),
			starDateSkinManager.getDefaultSkin().getTrekLockTextLocation()
			);
	
	viewLayout = new GroupLayout(stardateImagePanel);
	startDateEditLayout = new GroupLayout(stardateImagePanel);
	normDateEditLayout = new GroupLayout(stardateImagePanel);
	
	stardateImagePanel.setLayout(viewLayout);
	
	dateDisp = new JLabel(currdate.toString());	
	starDateDesc = new JLabel(starDateSkinManager.getDefaultSkin().getTrekStarDateDisplay());	
	
	starDateDisp = new JLabel(nowStarDateStr);
	if(starDateText==null) starDateText= new JTextField(3);
	if(normDateEditText==null) normDateEditText= new JTextField(3);
	
	dateDisp.setFont(starDateSkinManager.getDefaultSkin().getTrekTextFont());
	starDateDesc.setFont(starDateSkinManager.getDefaultSkin().getTrekTextFont());
	starDateDisp.setFont(starDateSkinManager.getDefaultSkin().getTrekTextFont());
	starDateText.setFont(starDateSkinManager.getDefaultSkin().getTrekTextFont());
	normDateEditText.setFont(starDateSkinManager.getDefaultSkin().getTrekTextFont());
	
	dateDisp.setForeground(starDateSkinManager.getDefaultSkin().getTrekTextColor());
	starDateDesc.setForeground(starDateSkinManager.getDefaultSkin().getTrekTextColor()); 
	starDateDisp.setForeground(starDateSkinManager.getDefaultSkin().getTrekTextColor());
	
	starDateText.setForeground(starDateSkinManager.getDefaultSkin().getTrekTextColor());
	starDateText.setBackground(Color.BLACK);
	normDateEditText.setForeground(starDateSkinManager.getDefaultSkin().getTrekTextColor());
	normDateEditText.setBackground(Color.BLACK);
		
	starDateDisp.setVisible(true);
	starDateText.setVisible(false);
	
	dateDisp.setVisible(true);
	normDateEditText.setVisible(false);
	
	starDateText.setSelectionColor(Color.lightGray);
	normDateEditText.setSelectionColor(Color.lightGray);
	
	starDateText.setBorder(BorderFactory.createLineBorder(starDateSkinManager.getDefaultSkin().getTrekTextColor(), 1));
	normDateEditText.setBorder(BorderFactory.createLineBorder(starDateSkinManager.getDefaultSkin().getTrekTextColor(), 1));
	
	JLabel spacer1 = new JLabel("            ");
	
	starDateText.addFocusListener(new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			starDateTextFocusState = true; 
			windowActive = true;
		}

		@Override
		public void focusLost(FocusEvent e) {
			starDateTextFocusState = false;
			//change the state to the initial view
			//reset all states to their starting points
			starDateEditLayoutModeEngaged=false;
			normDateEditLayoutModeEngaged=false;
			lockDateUpdateOnEditMode=false;
			lockStarDateUpdateOnNormEditMode=false;
			starDateDisp.setVisible(true);
			starDateText.setVisible(false);
			
			dateDisp.setVisible(true);
			normDateEditText.setVisible(false);
			stardateImagePanel.setLayout(viewLayout);
			stardateImagePanel.validate();
		}
		
	});
	
	normDateEditText.addFocusListener(new FocusListener() { 

		@Override
		public void focusGained(FocusEvent e) {
			normDateTextFocusState = true;
			windowActive = true;			
		}

		@Override
		public void focusLost(FocusEvent e) {
			normDateTextFocusState = false;
			//change the state to the initial view
			//reset all states to their starting points
			starDateEditLayoutModeEngaged=false;
			normDateEditLayoutModeEngaged=false;
			lockDateUpdateOnEditMode=false;
			lockStarDateUpdateOnNormEditMode=false;
			starDateDisp.setVisible(true);
			starDateText.setVisible(false);
			
			dateDisp.setVisible(true);
			normDateEditText.setVisible(false);
			stardateImagePanel.setLayout(viewLayout);
			stardateImagePanel.validate();
		}
		
	});
	
	starDateText.addActionListener(new ActionListener () { //on the enter key, we reverse the entered start date to a real date

		@Override
		public void actionPerformed(ActionEvent e) {
			String inputDate = starDateText.getText();
			String reversedDate = null;
			double initialDouble = 0.000000000001;
			double starDate = initialDouble;
			try {
				starDate = Double.parseDouble(inputDate);
			} catch(Exception e1){
				starDateText.setSelectionColor(Color.PINK);
				starDateText.setSelectionStart(0);
				starDateText.setSelectionEnd(1000);
			}
			if(starDate != initialDouble) {
				reversedDate = getDateFromStarDate(starDate);
				if(reversedDate != null) {
					lockDateUpdateOnEditMode=true;
					lockStarDateUpdateOnNormEditMode=false;
					dateDisp.setText(reversedDate);
					starDateText.setSelectionColor(Color.lightGray);
				}
			}
		}
		
	});
	
	normDateEditText.addActionListener(new ActionListener() { //on the enter key, change real date to star date

		@Override
		public void actionPerformed(ActionEvent e) {
			String userDate = normDateEditText.getText();
			Date theDate = null;
			try {
				theDate = getDateFromDateString(userDate);
			} catch (Exception e1) {
				normDateEditText.setSelectionColor(Color.PINK);
				normDateEditText.setSelectionStart(0);
				normDateEditText.setSelectionEnd(1000);
			}
			if( theDate != null ) {
				String newStarDate = getStarDateFromDate(theDate);
				lockDateUpdateOnEditMode=false;
				lockStarDateUpdateOnNormEditMode=true;
				starDateDisp.setText(newStarDate);
				normDateEditText.setSelectionColor(Color.lightGray);
			}
		} 
		
	});
	
	starDateDisp.addMouseListener(new MouseListener () { //on clicking the star date value - ie the date - we engage the stardate edit mode
		@Override
		public void mouseClicked(MouseEvent e) {
			starDateEditLayoutModeEngaged=true;
			normDateEditLayoutModeEngaged=false;
			lockDateUpdateOnEditMode=false;
			lockStarDateUpdateOnNormEditMode=false;
			starDateDisp.setVisible(false);
			starDateText.setVisible(true);
			starDateText.requestFocus();
			dateDisp.setVisible(true);
			normDateEditText.setVisible(false);
			stardateImagePanel.setLayout(startDateEditLayout);
			stardateImagePanel.validate();
		}

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }
		
	});
	
	starDateDesc.addMouseListener(new MouseListener() { //on clicking the 'star date' label display, reset to view mode

		@Override
		public void mouseClicked(MouseEvent e) {
			//reset all states to their starting points
			starDateEditLayoutModeEngaged=false;
			normDateEditLayoutModeEngaged=false;
			lockDateUpdateOnEditMode=false;
			lockStarDateUpdateOnNormEditMode=false;
			starDateDisp.setVisible(true);
			starDateText.setVisible(false);

			dateDisp.setVisible(true);
			normDateEditText.setVisible(false);
			stardateImagePanel.setLayout(viewLayout);
			stardateImagePanel.validate();
			//important that we request focus back to the frame
			((JFrame)comm[0]).requestFocus();
		}

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }
		
	});
	
	dateDisp.addMouseListener(new MouseListener() { //on clicking the date label, we enter the normal date edit mode

		@Override
		public void mouseClicked(MouseEvent e) {
			//engage the normal date edit mode!
			starDateEditLayoutModeEngaged=false;
			normDateEditLayoutModeEngaged=true;
			lockDateUpdateOnEditMode=false;
			lockStarDateUpdateOnNormEditMode=false;
			starDateDisp.setVisible(true);
			starDateText.setVisible(false);
			dateDisp.setVisible(false);
			normDateEditText.setVisible(true);
			normDateEditText.requestFocus();
			stardateImagePanel.setLayout(normDateEditLayout);
			stardateImagePanel.validate();
		}

		@Override
		public void mousePressed(MouseEvent e) { }

		@Override
		public void mouseReleased(MouseEvent e) { }

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }
		
	});
	
	viewLayout.setAutoCreateGaps(true);
	viewLayout.setAutoCreateContainerGaps(true);
	viewLayout.setHorizontalGroup(viewLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			 				.addGroup(viewLayout.createSequentialGroup()
			 						.addComponent(starDateDesc)
			 						.addComponent(starDateDisp))
			 				.addGroup(viewLayout.createSequentialGroup()
			 						.addComponent(dateDisp))
			);
	viewLayout.setVerticalGroup(
			   viewLayout.createSequentialGroup()
			    .addGroup(viewLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    		.addComponent(starDateDesc)
			    		.addComponent(starDateDisp)
			    		)
	    		.addComponent(dateDisp)
			);

	startDateEditLayout.setAutoCreateGaps(true);
	startDateEditLayout.setAutoCreateContainerGaps(true);
	startDateEditLayout.setHorizontalGroup(startDateEditLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			 				.addGroup(startDateEditLayout.createSequentialGroup()
			 						.addComponent(starDateDesc)
			 						.addComponent(starDateText)
			 						.addComponent(spacer1)
			 						)
			 				.addGroup(startDateEditLayout.createSequentialGroup()
			 						.addComponent(dateDisp))
			);
	startDateEditLayout.setVerticalGroup(
			startDateEditLayout.createSequentialGroup()
			    .addGroup(startDateEditLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    		.addComponent(starDateDesc)
			    		.addComponent(starDateText)
			    		.addComponent(spacer1)
			    		)
	    		.addComponent(dateDisp)
			);

	normDateEditLayout.setAutoCreateGaps(true);
	normDateEditLayout.setAutoCreateContainerGaps(true);
	normDateEditLayout.setHorizontalGroup(normDateEditLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
			 				.addGroup(normDateEditLayout.createSequentialGroup()
			 						.addComponent(starDateDesc)
			 						.addComponent(starDateDisp))
			 				.addGroup(normDateEditLayout.createSequentialGroup()
			 						.addComponent(normDateEditText))
			);
	normDateEditLayout.setVerticalGroup(
			normDateEditLayout.createSequentialGroup()
			    .addGroup(normDateEditLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    		.addComponent(starDateDesc)
			    		.addComponent(starDateDisp)
			    		)
	    		.addComponent(normDateEditText)
			);

	everything.add(stardateImagePanel);
}

/**
 * helper method to apply a skin to the current frame
 * @param StarDateSkin skin 
 */
public void applyStarDateSkin(StarDateSkin skin){
	//core images
         
    trekBadgeI = starDateSkinManager.getDefaultSkin().getTrekBadgeImage(); 
    trekShellUnLockedI = starDateSkinManager.getDefaultSkin().getTrekShellUnLockedImage();
    trekShellLockedI = starDateSkinManager.getDefaultSkin().getTrekShellLockedImage();
    
	// main panel properties   
	//have to match the current state of the lock button
	if(this.focusStateLockOverride){
		this.stardateImagePanel.setImage(this.trekShellLockedI.getScaledInstance(skin.getWinWidth(), skin.getWinHeight(), Image.SCALE_SMOOTH));
		this.stardateImagePanel.setFontColor(skin.getTrekTextColor());
	}else{		
		this.stardateImagePanel.setImage(this.trekShellUnLockedI.getScaledInstance(skin.getWinWidth(), skin.getWinHeight(), Image.SCALE_SMOOTH));
		this.stardateImagePanel.setFontColor(skin.getTrekLockTextColor());		
	}	
    stardateImagePanel.setFont(skin.getTrekLockTextFont());
    stardateImagePanel.setLockText(skin.getTrekLockText());
    stardateImagePanel.setLockTextLocation(skin.getTrekLockTextLocation());

	// the ui
	starDateDesc.setText(skin.getTrekStarDateDisplay());
	
	dateDisp.setFont(skin.getTrekTextFont());
	starDateDesc.setFont(skin.getTrekTextFont());
	starDateDisp.setFont(skin.getTrekTextFont());
	starDateText.setFont(skin.getTrekTextFont());
	normDateEditText.setFont(skin.getTrekTextFont());
	
	dateDisp.setForeground(skin.getTrekTextColor());
	starDateDesc.setForeground(skin.getTrekTextColor()); 
	starDateDisp.setForeground(skin.getTrekTextColor());
	
	starDateText.setForeground(skin.getTrekTextColor());
	starDateText.setBackground(Color.BLACK);
	normDateEditText.setForeground(skin.getTrekTextColor());
	normDateEditText.setBackground(Color.BLACK);
	
	starDateText.setSelectionColor(Color.lightGray);
	normDateEditText.setSelectionColor(Color.lightGray);
	
	starDateText.setBorder(BorderFactory.createLineBorder(skin.getTrekTextColor(), 1));
	normDateEditText.setBorder(BorderFactory.createLineBorder(skin.getTrekTextColor(), 1));
	
	// frame properties
	this.setIconImage( trekBadgeI );	  
	this.setSize(skin.getWinWidth(), skin.getWinHeight());
	// don't change the re:packing, as it causes the frame to shrink which something we don't want - we've already set the size
	//this.pack();    
	SwingUtilities.updateComponentTreeUI(this);
	this.repaint();

}

/**
 * helper method to set the current frame in default view
 */
public void setFrameInDefaultView(){
	//reset all states to their starting points
	starDateEditLayoutModeEngaged=false;
	normDateEditLayoutModeEngaged=false;
	lockDateUpdateOnEditMode=false;
	lockStarDateUpdateOnNormEditMode=false;
	starDateDisp.setVisible(true);
	starDateText.setVisible(false);

	dateDisp.setVisible(true);
	normDateEditText.setVisible(false);
	stardateImagePanel.setLayout(viewLayout);
	stardateImagePanel.validate();
	//important that we request focus back to the frame
	((JFrame)comm[0]).requestFocus();
}

/**
 * load the 'propertyFile' property file
 * @return Properties
 */
public static Properties loadProperties(){
   Properties prop = new Properties();
   InputStream in = CaptainsStarDate.class.getResourceAsStream(propertyFile);
   try {
	 prop.load(in);
	 in.close();
   } catch (IOException e) {
	  System.err.println("Oops! No Property File!");
	  prop = null;
   }
   return prop;
}

/**
 * main function that 'engages' everything 
 * @param args
 */
public static void main(String[] args){	   
	  // Initialize all internal variables based of the property file
	  Properties prop = loadProperties();
	
 	  CaptainsStarDate calc = new CaptainsStarDate("Star Date Calculator",prop);
	  calc.createTrayStarDateCalc(calc);	  
	  
	  /** window init */
	  calc.setUndecorated(showWindowMenuBar);        
      calc.setIconImage( calc.trekBadgeI );    
      
      GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      int width = gd.getDisplayMode().getWidth();
      int height = gd.getDisplayMode().getHeight();

      calc.setLocation(width - calc.starDateSkinManager.getDefaultSkin().getWinWidth() - calc.starDateSkinManager.getDefaultSkin().getxOffset(), height - calc.starDateSkinManager.getDefaultSkin().getWinHeight() - calc.starDateSkinManager.getDefaultSkin().getyOffset());
      calc.pack();
      calc.setSize(calc.starDateSkinManager.getDefaultSkin().getWinWidth(), calc.starDateSkinManager.getDefaultSkin().getWinHeight());
      calc.setBackground(new Color(1.0f,1.0f,1.0f,0.5f));   
      calc.validate();
      calc.setState(Frame.NORMAL);
      calc.toFront();
      calc.setVisible(showOnStartUp);
      // setup custom dragging actions
      calc.addMouseListener(new MouseListener(){

	    @Override
		public void mouseClicked(MouseEvent e) { }
	
		@Override
		public void mousePressed(MouseEvent e) {
			calc.userLastPressedPoint.setLocation(e.getX(), e.getY());
			calc.lastLocationOnPressedPoint.setLocation( calc.getLocation().x, calc.getLocation().y);
			//do the lock button action
			int px = e.getX();
			int py = e.getY();
			int[] lockButtomDimensions = calc.starDateSkinManager.getDefaultSkin().getLockButtomDimensions();
			//if( bb.ix <= p.x && p.x <= bb.ax && bb.iy <= p.y && p.y <= bb.ay )
			if ( lockButtomDimensions[0] <= px && px <= lockButtomDimensions[2] && lockButtomDimensions[1] <= py && py <= lockButtomDimensions[3] ){
				calc.focusStateLockOverride = !calc.focusStateLockOverride;
				if(calc.focusStateLockOverride){
					calc.stardateImagePanel.setImage(calc.trekShellLockedI.getScaledInstance(calc.starDateSkinManager.getDefaultSkin().getWinWidth(), calc.starDateSkinManager.getDefaultSkin().getWinHeight(), Image.SCALE_SMOOTH));
					calc.stardateImagePanel.setFontColor(calc.starDateSkinManager.getDefaultSkin().getTrekTextColor());
					calc.stardateImagePanel.repaint();
					calc.setAlwaysOnTop(true);
				}else{
					calc.stardateImagePanel.setImage(calc.trekShellUnLockedI.getScaledInstance(calc.starDateSkinManager.getDefaultSkin().getWinWidth(), calc.starDateSkinManager.getDefaultSkin().getWinHeight(), Image.SCALE_SMOOTH));
					calc.stardateImagePanel.setFontColor(calc.starDateSkinManager.getDefaultSkin().getTrekLockTextColor());
					calc.stardateImagePanel.repaint();
					calc.setAlwaysOnTop(false);
				}
			} else {
				calc.setFrameInDefaultView();
			}			
		}
	
		@Override
		public void mouseReleased(MouseEvent e) { }
	
		@Override
		public void mouseEntered(MouseEvent e) { }
	
		@Override
		public void mouseExited(MouseEvent e) { }
	  
      });
  
	  calc.addMouseMotionListener(new MouseMotionListener(){
	
		@Override
		public void mouseDragged(MouseEvent e) {			
				calc.setLocation( (calc.getLocation().x - calc.userLastPressedPoint.x + e.getX()) ,
								  (calc.getLocation().y - calc.userLastPressedPoint.y + e.getY())
								  );
		}
	
		@Override
		public void mouseMoved(MouseEvent e) {	}
		  
	  });
  
	  calc.addFocusListener(new FocusListener() {
	  		@Override
	  		public void focusGained(FocusEvent e) {	//maybe fire a little event here like a treky noise
	  			calc.windowFocusState = true;
	  			calc.windowActive = true;
	  			SwingUtilities.updateComponentTreeUI(calc);
	  			calc.repaint();
	  		}
	  		@Override
	  		public void focusLost(FocusEvent e) { //close the window
	  			calc.windowFocusState = false;	  			
	  		}
	  	  });
  }

  /**
   * create the tray bit
   * @param calc
   */
  public void createTrayStarDateCalc (CaptainsStarDate calc) {
	    /* Use an appropriate Look and Feel */
	    try { //this actually has no impact on the jframe
	        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	    } catch (UnsupportedLookAndFeelException ex) {
	        ex.printStackTrace();
	    } catch (IllegalAccessException ex) {
	        ex.printStackTrace();
	    } catch (InstantiationException ex) {
	        ex.printStackTrace();
	    } catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	    /* Turn off metal's use of bold fonts */
	    UIManager.put("swing.boldMetal", Boolean.FALSE);
	    
	    //Schedule a job for the event-dispatching thread:
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            createAndShowSysTrayGUI(calc);
	        }
	    });
	}
  
  /**
   * create the tray gui
   * @param calc CaptainsStarDate obj
   */
  private void createAndShowSysTrayGUI(CaptainsStarDate calc) {
      //Check the SystemTray support
      if (!SystemTray.isSupported()) {
          System.err.println("SystemTray is not supported");
          return;
      }
      final PopupMenu popup = new PopupMenu();
      trayIcon = new TrayIcon(calc.trekBadgeI);
      
      trayIcon.setImageAutoSize(true);
            
      final SystemTray tray = SystemTray.getSystemTray();
      
      // just need about, calc, exit
      // Create a popup menu components
      MenuItem aboutItem = new MenuItem("About");
      Menu skinsMenu = new Menu("Skins");
      // MenuItem starDateLogs = new MenuItem("StarDate Logs"); // maybe one fine star date .. 
      MenuItem exitItem = new MenuItem("Exit");
      String defaultSkin = calc.starDateSkinManager.getDefaultSkinName();
      Object[] skinNames = calc.starDateSkinManager.getSkinNames().toArray();
      
      Arrays.sort(skinNames);
      MenuItem skinItem;
      if(skinNames.length > 1){
    	  for(int i=0;i<skinNames.length;i++){
    		  if(!(defaultSkin.compareTo((String)skinNames[i])==0)){
    			  skinItem = new MenuItem((String)skinNames[i]); 
    		  } else {
    			  skinItem = new MenuItem((String)skinNames[i]+" *"); 
    		  }
    		  skinsMenu.add(skinItem);
    		  calc.starDateSkinManager.addSkinMenuItem(skinItem);
    	  }
      }
      calc.starDateSkinManager.initSkinMenuItemActionListeners(calc);
      
      //Add components to popup menu
      popup.add(aboutItem);
      popup.add(skinsMenu);
      //popup.add(starDateLogs);
      popup.addSeparator();
      popup.add(exitItem);
      
      trayIcon.setPopupMenu(popup);
      
      try {
          tray.add(trayIcon);
      } catch (AWTException e) {
          System.err.println("TrayIcon could not be added.");
          return;
      }
      
      trayIcon.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {              
        	  calc.setVisible(true);
           }
      });
      
      aboutItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(!((Boolean)comm[1]).booleanValue()) {
				if(comm[2] instanceof JDialog){
					((JDialog)comm[2]).setVisible(true);
				} else {				
				  comm[1] = new Boolean(true);
				  comm[2] = (new AboutStarDatePane(aboutString)).createDialog((Frame)comm[0], "About Captains Star Date Calc");
				  ((JDialog)comm[2]).setPreferredSize(new Dimension(200,200));	              
				  ((JDialog)comm[2]).addWindowFocusListener(new WindowFocusListener() {
	            	  public void windowLostFocus(WindowEvent e) { ((JDialog)comm[2]).setVisible(false); }
	            	  public void windowGainedFocus(WindowEvent e) { } 
	            	  });
				  ((JDialog)comm[2]).setVisible(true);
	              comm[1] = new Boolean(false);
				}
			}
		}
      });

      skinsMenu.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) { //action of left click on the sys tray icon
        	  calc.setVisible(true);
          }
      });
      
      exitItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              tray.remove(trayIcon);
              System.exit(0);
          }
      });
  }
   
}

