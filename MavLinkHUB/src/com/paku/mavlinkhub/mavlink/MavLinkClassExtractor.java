package com.paku.mavlinkhub.mavlink;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.MAVLink.Messages.enums.MAV_AUTOPILOT;
import com.MAVLink.Messages.enums.MAV_TYPE;

public class MavLinkClassExtractor {

	ArrayList<ClassItem> mavType;
	ArrayList<ClassItem> mavAutopilot;

	// helper class
	private class ClassItem {

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
	    	if (left.id > right.id) return 1;
	    	if (left.id < right.id) return -1;
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
				mavType.add(new ClassItem(mavField.getName(), tmpType
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
				mavAutopilot.add(new ClassItem(mavField.getName(), tmpAutopilot
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

	}

}
