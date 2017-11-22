package com.yendregi.stardate.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JComponent;

/**
 * Custom Image Panel to render the various skin backgrounds
 * @author Andrzej K. W.
 *
 */
public class ImagePanel extends JComponent {
	private static final long serialVersionUID = -5631537930897072430L;
	private Image image;
	private Color textColor;
	private Font textFont;
	private String lockText;
	private Point lockTextLocation;
	
    public ImagePanel(Image image, Color textColor, Font textFont, String lockText, Point lockTextLocation) {
        this.image = image;
        this.textColor = textColor;
        this.textFont = textFont;
        this.lockText = lockText;
        this.lockTextLocation = lockTextLocation;
    }
    
    public void setImage(Image aImage) {
    	this.image = aImage;
    }
    
    public void setFontColor(Color textColor) {
    	this.textColor = textColor;
    }
    
    public void setFont(Font textFont){
    	this.textFont = textFont;
    }
    
    public void setLockText(String lockText){
    	this.lockText = lockText;
    }
    
    public void setLockTextLocation(Point lockTextLocation){
    	this.lockTextLocation = lockTextLocation;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
        g.setColor(this.textColor);        
        g.setFont(this.textFont);
        g.drawString(this.lockText, this.lockTextLocation.x, this.lockTextLocation.y);
    }
}