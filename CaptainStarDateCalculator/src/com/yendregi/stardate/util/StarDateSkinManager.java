package com.yendregi.stardate.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;

import com.yendregi.stardate.CaptainsStarDate;

/**
 * Manager class for the various Captains Star Date  skins
 * @author Andrzej K. W.
 *
 */
public class StarDateSkinManager {

	private HashMap<String,StarDateSkin> starDateSkins;
	private String defaultSkinName;
	private HashMap<String,MenuItem> skinMenuItems;
	
	/**
	 * from the properties file init this skin manager
	 * @param prop
	 */
	public StarDateSkinManager(Properties prop) {
		starDateSkins = new HashMap<String,StarDateSkin>();
		skinMenuItems = new HashMap<String,MenuItem>();
		String[] skins = prop.getProperty("stardate.skins").split(",");
		defaultSkinName = prop.getProperty("stardate.skin.default");
		
		String skinName;
		if(skins.length > 0){
			for(int i=0; i< skins.length; i++){
				skinName = skins[i];
				String[] color = prop.getProperty(skinName+".trekTextColor").split(",");
				String[] dimension = prop.getProperty(skinName+".lockButtomDimensions").split(",");
				String[] trekTextFontD = prop.getProperty(skinName+".trekTextFont").split(",");
				Font trekTextFont = new Font(trekTextFontD[0],
												((trekTextFontD[1].compareTo("BOLD") == 0 ) ? Font.BOLD : Font.PLAIN), 
												Integer.parseInt(trekTextFontD[2]));
				int[] dimensions = new int[4];
				dimensions[0] = Integer.parseInt(dimension[0]);
				dimensions[1] = Integer.parseInt(dimension[1]);
				dimensions[2] = Integer.parseInt(dimension[2]);
				dimensions[3] = Integer.parseInt(dimension[3]);	
				String[] lockTextcolor = prop.getProperty(skinName+".trekLockTextColor").split(",");
				String[] lockTextFontD = prop.getProperty(skinName+".trekLockTextFont").split(",");
				
				Font lockTextFont = new Font(lockTextFontD[0],
						((lockTextFontD[1].compareTo("BOLD") == 0 ) ? Font.BOLD : Font.PLAIN), 
						Integer.parseInt(lockTextFontD[2]));
				
				String[] lockTextLocation = prop.getProperty(skinName+".trekLockTextLocation").split(","); 
				
				starDateSkins.put(skinName, new StarDateSkin( Integer.parseInt(prop.getProperty(skinName+".winWidth")),
																		  Integer.parseInt(prop.getProperty(skinName+".winHeight")),
																		  Integer.parseInt(prop.getProperty(skinName+".xOffset")),
																		  Integer.parseInt(prop.getProperty(skinName+".yOffset")),
																		  prop.getProperty(skinName+".trekBadge"),
																		  prop.getProperty(skinName+".trekShellUnLocked"),
																		  prop.getProperty(skinName+".trekShellLocked"),																		   
																		  createImage(prop.getProperty(skinName+".trekBadge"), "trek tray icon"),
																		  createImage(prop.getProperty(skinName+".trekShellUnLocked"), "trek shell unlocked"),
																		  createImage(prop.getProperty(skinName+".trekShellLocked"), "trek shell locked"),																		   
																		  new Color(Integer.parseInt(color[0]),
																				    Integer.parseInt(color[1]),
																				    Integer.parseInt(color[2])),
																		  trekTextFont,
																		  dimensions,
																		  prop.getProperty(skinName+".trekStarDateDisplay"),
																		  new Point(Integer.parseInt(lockTextLocation[0]),
																				    Integer.parseInt(lockTextLocation[1])),
																		  prop.getProperty(skinName+".trekLockText"),
																		  new Color(Integer.parseInt(lockTextcolor[0]),
																				    Integer.parseInt(lockTextcolor[1]),
																				    Integer.parseInt(lockTextcolor[2])),
																		  lockTextFont
																		  )
								);
			}
		} else {
			throw new IllegalStateException("property file must have at least 1 skin defined!");
		}
		
	}
	
	/**
	 * getDefaultSkin()
	 * @return the default skin
	 */
	public StarDateSkin getDefaultSkin() {
		return starDateSkins.get(defaultSkinName);
	}
	
	
	/**
	 * getDefaultSkinName()
	 * @return defaultSkinName
	 */
	public String getDefaultSkinName() {
		return defaultSkinName;
	}
	
	/**
	 * get the key set for the current skins set
	 * @return skin names Set<String>
	 */
	public Set<String> getSkinNames() {
		return starDateSkins.keySet();
	}
	
	/**
	 * get a skin by name 
	 * @param skinName
	 * @return StarDateSkin
	 */
	public StarDateSkin getSkinByName(String skinName){
		return starDateSkins.get(skinName);
	}
	
	/**
	 * addSkinMenuItem(MenuItem skinItem)
	 * @param skinItem
	 */
	public void addSkinMenuItem(MenuItem skinItem) {		
		skinMenuItems.put((skinItem.getLabel().indexOf("*") > 0) ? skinItem.getLabel().substring(0, skinItem.getLabel().length()-2) : skinItem.getLabel(),skinItem);
	}
	
	/**
	 * init all the menu items to set their relative skins
	 * @param calc
	 */
	public void initSkinMenuItemActionListeners(CaptainsStarDate calc){
		MenuItem skinItem;
		Object[] keySet = skinMenuItems.keySet().toArray();
		for(int i=0; i<keySet.length; i++) {
			skinItem = skinMenuItems.get((String)keySet[i]);
			skinItem.addActionListener(new ActionListener(){	
				@Override
				public void actionPerformed(ActionEvent e) {			
					MenuItem src = (MenuItem) e.getSource();
					String srcSkin = src.getLabel(); 
					if(!((defaultSkinName+" *").compareTo(src.getLabel()) == 0)){ // a little tricky swap						
						MenuItem ori = skinMenuItems.get(defaultSkinName);
						ori.setLabel(defaultSkinName);
						src.setLabel(srcSkin+" *");
						defaultSkinName = srcSkin;
						calc.applyStarDateSkin(getSkinByName(srcSkin));
					}
				}		  
			});
		}
	}

  /**
   * create and Image based on the relative path to the captains star date main
   * @param path	path to the image
   * @param description	description of the image
   * @return Image
   */
  public static Image createImage(String path, String description) {
      URL imageURL = CaptainsStarDate.class.getResource(path);
      if (imageURL == null) {
          System.err.println("Resource not found: " + path);
          return null;
      } else {
          return (new ImageIcon(imageURL, description)).getImage() ;
      }
  }
	  
}
