package com.paku.mavlinkhub.mavlink;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.MAVLink.Messages.enums.MAV_AUTOPILOT;
import com.MAVLink.Messages.enums.MAV_STATE;
import com.MAVLink.Messages.enums.MAV_TYPE;

public class MavLinkClassExtractor {

	ArrayList<ClassItem> mavType;
	ArrayList<ClassItem> mavAutopilot;
	ArrayList<ClassItem> mavState;

	// helper class
	public class ClassItem {

		private String name;
		private int id;

		public ClassItem(String name, int id) {
			setName(name);
			setId(id);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
	
	
	//sorting comparator
	private class ClassIdComparator implements Comparator<ClassItem>
	{
	    public int compare(ClassItem left, ClassItem right) {
	    	if (left.getId() > right.getId()) return 1;
	    	if (left.getId() < right.getId()) return -1;
	    	return 0;
	        //return left.name.compareTo(right.name);
	    }
	}
	

	public MavLinkClassExtractor() {
		
		
		//MAV_TYPE class fields extractor
		mavType = new ArrayList<MavLinkClassExtractor.ClassItem>();
		MAV_TYPE tmpType = new MAV_TYPE();
		for (Field mavField : tmpType.getClass().getFields()) {
			try {
				mavType.add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpType
						.getClass().getField(mavField.getName())
						.getInt(tmpType)));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		Collections.sort(mavType, new ClassIdComparator());

		
		//MAV_AUTOPILOT class fields extractor		
		mavAutopilot = new ArrayList<MavLinkClassExtractor.ClassItem>();

		MAV_AUTOPILOT tmpAutopilot = new MAV_AUTOPILOT();
		for (Field mavField : tmpAutopilot.getClass().getFields()) {
			try {
				mavAutopilot.add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpAutopilot
						.getClass().getField(mavField.getName())
						.getInt(tmpAutopilot)));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		Collections.sort(mavAutopilot, new ClassIdComparator());
		
		
		//MAV_STATE class fields extractor		
		mavState = new ArrayList<MavLinkClassExtractor.ClassItem>();

		MAV_STATE tmpState = new MAV_STATE();
		for (Field mavField : tmpState.getClass().getFields()) {
			try {
				mavState.add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpState
						.getClass().getField(mavField.getName())
						.getInt(tmpState)));
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		Collections.sort(mavState, new ClassIdComparator());
		
		
		

	}

}
