package com.yendregi.stardate;

/**
 * A simple engine that send update signals to the Captains Star Date Class
 * @author Andrzej K. W.
 *
 */
public class StarDateEngine implements Runnable {
	
//communication objects
protected CaptainsStarDate main;
//the runnable vars	
protected boolean RUNNING = false;
protected Thread engine;
protected long MESLEEP = 200; 
protected long logicCheckSleep = 600;

 public StarDateEngine(Object[] comm){
  main = (CaptainsStarDate)comm[0];
 }

public void start(){
   if(engine == null) {
     engine = new Thread(this);
     engine.start();
   } 
}

 public void stop(){
    RUNNING = false;
    engine = null;
 }     
 
 public void run(){
    Thread appThread = Thread.currentThread();    
	RUNNING = true; 
	long logicCheckTimer = 0;	
	while(appThread == engine) { //main loop
	 //calculate the new stardate and update the strings, labels & window state for the calculator			  
	 Sleeper();
	 logicCheckTimer += MESLEEP;
	 main.updateLabels();
	 if(logicCheckTimer > logicCheckSleep){
		 logicCheckTimer = 0;
		 main.verifyWindowState();
	 }
	}		
 }	

public void Sleeper(){
  try{	 
   Thread.sleep(MESLEEP);
  }catch(Exception sleep){  }	
}

	
}