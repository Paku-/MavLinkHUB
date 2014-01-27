package com.paku.mavlinkhub.mavlink;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.MAVLink.Messages.enums.MAV_AUTOPILOT;
import com.MAVLink.Messages.enums.MAV_STATE;
import com.MAVLink.Messages.enums.MAV_TYPE;

public class MavLinkClassExtractor {

	private ArrayList<ClassItem> mavType;
	private ArrayList<ClassItem> mavAutopilot;
	private ArrayList<ClassItem> mavState;

	// helper class
	public static class ClassItem {

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

	// sorting comparator
	private static class ClassIdComparator implements Comparator<ClassItem> {
		public int compare(ClassItem left, ClassItem right) {
			if (left.getId() > right.getId()) return 1;
			if (left.getId() < right.getId()) return -1;
			return 0;
			// return left.name.compareTo(right.name);
		}
	}

	public MavLinkClassExtractor() {

		// MAV_TYPE class fields extractor
		setMavType(new ArrayList<MavLinkClassExtractor.ClassItem>());
		MAV_TYPE tmpType = new MAV_TYPE();
		for (Field mavField : tmpType.getClass().getFields()) {
			try {
				getMavType().add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpType.getClass().getField(mavField.getName()).getInt(tmpType)));
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				e.printStackTrace();
			}

		}

		Collections.sort(getMavType(), new ClassIdComparator());

		// MAV_AUTOPILOT class fields extractor
		setMavAutopilot(new ArrayList<MavLinkClassExtractor.ClassItem>());

		MAV_AUTOPILOT tmpAutopilot = new MAV_AUTOPILOT();
		for (Field mavField : tmpAutopilot.getClass().getFields()) {
			try {
				getMavAutopilot().add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpAutopilot.getClass().getField(mavField.getName()).getInt(tmpAutopilot)));
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				e.printStackTrace();
			}

		}

		Collections.sort(getMavAutopilot(), new ClassIdComparator());

		// MAV_STATE class fields extractor
		setMavState(new ArrayList<MavLinkClassExtractor.ClassItem>());

		MAV_STATE tmpState = new MAV_STATE();
		for (Field mavField : tmpState.getClass().getFields()) {
			try {
				getMavState().add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpState.getClass().getField(mavField.getName()).getInt(tmpState)));
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (NoSuchFieldException e) {
				e.printStackTrace();
			}

		}

		Collections.sort(getMavState(), new ClassIdComparator());

	}

	public ArrayList<ClassItem> getMavState() {
		return mavState;
	}

	public void setMavState(ArrayList<ClassItem> mavState) {
		this.mavState = mavState;
	}

	public ArrayList<ClassItem> getMavType() {
		return mavType;
	}

	public void setMavType(ArrayList<ClassItem> mavType) {
		this.mavType = mavType;
	}

	public ArrayList<ClassItem> getMavAutopilot() {
		return mavAutopilot;
	}

	public void setMavAutopilot(ArrayList<ClassItem> mavAutopilot) {
		this.mavAutopilot = mavAutopilot;
	}

}
