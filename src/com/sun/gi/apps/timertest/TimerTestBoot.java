/**
 *
 * <p>Title: TimerTestBoot.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Sun Microsystems, Inc.</p>
 * <p>Company: Sun Microsystems, Inc</p>
 * @author Jeff Kesselman
 * @version 1.0
 */
package com.sun.gi.apps.timertest;

import com.sun.gi.gloutils.pdtimer.PDTimer;
import com.sun.gi.logic.GLOReference;
import com.sun.gi.logic.SimBoot;
import com.sun.gi.logic.SimTask;
import com.sun.gi.logic.SimTimerListener;
import com.sun.gi.logic.SimTask.ACCESS_TYPE;

/**
 *
 * <p>Title: TimerTestBoot.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Sun Microsystems, Inc.</p>
 * <p>Company: Sun Microsystems, Inc</p>
 * @author Jeff Kesselman
 * @version 1.0
 */
public class TimerTestBoot implements SimBoot, SimTimerListener {
	GLOReference pdTimer = null;
	long oneSecEvent;
	long tenSecEvent;
	long fiveSecEvent;

	/* (non-Javadoc)
	 * @see com.sun.gi.logic.SimBoot#boot(com.sun.gi.logic.SimTask, boolean)
	 */
	public void boot(SimTask task, boolean firstBoot) {
		System.out.println("TimerTestBoot running");
		try {			
			/*
			oneSecEvent = task.registerTimerEvent(1000l,true,thisobj);
			fiveSecEvent = task.registerTimerEvent(5000l,false,thisobj);
			tenSecEvent = task.registerTimerEvent(10000l,true,thisobj);
			*/
			PDTimer timer;
			if (firstBoot){ // not instantiated yet
				timer = new PDTimer(task);
				pdTimer = task.createGLO(timer,null);
				TimerCount tc = new TimerCount();
				GLOReference tcRef = task.createGLO(tc,null);
				timer.addTimerEvent(task,ACCESS_TYPE.GET,1,true,tcRef,"increment",new Object[]{});
			} else {
				timer=(PDTimer)pdTimer.get(task);
			}
			timer.start(task,1);
			
		} catch (InstantiationException e) {			
			e.printStackTrace();
		}		

	}

	/* (non-Javadoc)
	 * @see com.sun.gi.logic.SimTimerListener#timerEvent(com.sun.gi.logic.SimTask, long)
	 */
	public void timerEvent(SimTask task, long eventID) {
		if (eventID==oneSecEvent){
			System.out.println("One second pulse recvd (repeats)");			
		} else if (eventID == fiveSecEvent){
			System.out.println("Five second pulse received (should *not* repeat)");
		} else if (eventID == tenSecEvent){
			System.out.println("Ten second pulse recieved (repeats)");
		} else {
			System.err.println("Unrecognized time event ID:"+eventID);
		}
		
	}

}
