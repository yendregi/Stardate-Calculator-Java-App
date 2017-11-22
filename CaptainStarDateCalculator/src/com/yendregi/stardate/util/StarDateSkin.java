package com.yendregi.stardate.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;

/**
 * A Pojo for various skins for the Captains Star Date 
 * @author Andrzej K. W.
 *
 */
public class StarDateSkin {

	private int winWidth;
	private int winHeight;
	private int xOffset;
	private int yOffset;
	private String trekBadge;
	private String trekShellUnLocked;
	private String trekShellLocked;

	private Image trekBadgeImage;
	private Image trekShellUnLockedImage;
	private Image trekShellLockedImage;
	
	private Color trekTextColor;
	private Font trekTextFont;
	
	private String trekStarDateDisplay;
	private Point trekLockTextLocation;
	
	private int[] lockButtomDimensions;
	private String trekLockText;
	private Color trekLockTextColor;
	private Font trekLockTextFont;
	
	public StarDateSkin() {}
	
	public StarDateSkin(int winWidth, int winHeight, int xOffset, int yOffset, 
			String trekBadge, String trekShellUnLocked, String trekShellLocked, 
			Image trekBadgeImage, Image trekShellUnLockedImage, Image trekShellLockedImage,
			Color trekTextColor, Font trekTextFont, 
			int[] lockButtomDimensions, String trekStarDateDisplay, Point trekLockTextLocation,
			String trekLockText, Color trekLockTextColor, Font trekLockTextFont){
		this.setWinWidth(winWidth);
		this.setWinHeight(winHeight);
		this.setxOffset(xOffset);
		this.setyOffset(yOffset);
		this.setTrekBadge(trekBadge);
		this.setTrekShellUnLocked(trekShellUnLocked);
		this.setTrekShellLocked(trekShellLocked);
		this.setTrekTextColor(trekTextColor);
		this.setTrekTextFont(trekTextFont);
		this.setTrekStarDateDisplay(trekStarDateDisplay);
		this.setTrekLockTextLocation(trekLockTextLocation);
		this.setLockButtomDimensions(lockButtomDimensions);
		this.setTrekLockText(trekLockText);
		this.setTrekLockTextColor(trekLockTextColor);
		this.setTrekLockTextFont(trekLockTextFont);
		this.setTrekBadgeImage(trekBadgeImage);
		this.setTrekShellUnLockedImage(trekShellUnLockedImage);
		this.setTrekShellLockedImage(trekShellLockedImage);
	}
	
	public int getWinWidth() {
		return winWidth;
	}

	public void setWinWidth(int winWidth) {
		this.winWidth = winWidth;
	}

	public int getWinHeight() {
		return winHeight;
	}

	public void setWinHeight(int winHeight) {
		this.winHeight = winHeight;
	}

	public int getxOffset() {
		return xOffset;
	}

	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
	public String getTrekBadge() {
		return trekBadge;
	}

	public void setTrekBadge(String trekBadge) {
		this.trekBadge = trekBadge;
	}

	public String getTrekShellUnLocked() {
		return trekShellUnLocked;
	}

	public void setTrekShellUnLocked(String trekShellUnLocked) {
		this.trekShellUnLocked = trekShellUnLocked;
	}

	public String getTrekShellLocked() {
		return trekShellLocked;
	}

	public void setTrekShellLocked(String trekShellLocked) {
		this.trekShellLocked = trekShellLocked;
	}

	public Color getTrekTextColor() {
		return trekTextColor;
	}

	public void setTrekTextColor(Color trekTextColor) {
		this.trekTextColor = trekTextColor;
	}

	public int[] getLockButtomDimensions() {
		return lockButtomDimensions;
	}

	public void setLockButtomDimensions(int[] lockButtomDimensions) {
		this.lockButtomDimensions = lockButtomDimensions;
	}

	public Font getTrekTextFont() {
		return trekTextFont;
	}

	public void setTrekTextFont(Font trekTextFont) {
		this.trekTextFont = trekTextFont;
	}

	public String getTrekLockText() {
		return trekLockText;
	}

	public void setTrekLockText(String trekLockText) {
		this.trekLockText = trekLockText;
	}

	public Color getTrekLockTextColor() {
		return trekLockTextColor;
	}

	public void setTrekLockTextColor(Color trekLockTextColor) {
		this.trekLockTextColor = trekLockTextColor;
	}

	public Font getTrekLockTextFont() {
		return trekLockTextFont;
	}

	public void setTrekLockTextFont(Font trekLockTextFont) {
		this.trekLockTextFont = trekLockTextFont;
	}

	public String getTrekStarDateDisplay() {
		return trekStarDateDisplay;
	}

	public void setTrekStarDateDisplay(String trekStarDateDisplay) {
		this.trekStarDateDisplay = trekStarDateDisplay;
	}

	public Point getTrekLockTextLocation() {
		return trekLockTextLocation;
	}

	public void setTrekLockTextLocation(Point trekLockTextLocation) {
		this.trekLockTextLocation = trekLockTextLocation;
	}

	public Image getTrekBadgeImage() {
		return trekBadgeImage;
	}

	public void setTrekBadgeImage(Image trekBadgeImage) {
		this.trekBadgeImage = trekBadgeImage;
	}

	public Image getTrekShellUnLockedImage() {
		return trekShellUnLockedImage;
	}

	public void setTrekShellUnLockedImage(Image trekShellUnLockedImage) {
		this.trekShellUnLockedImage = trekShellUnLockedImage;
	}

	public Image getTrekShellLockedImage() {
		return trekShellLockedImage;
	}

	public void setTrekShellLockedImage(Image trekShellLockedImage) {
		this.trekShellLockedImage = trekShellLockedImage;
	}

	
}
