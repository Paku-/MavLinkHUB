// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.MAVLink.Messages.enums.MAV_AUTOPILOT;
import com.MAVLink.Messages.enums.MAV_CMD;
import com.MAVLink.Messages.enums.MAV_MODE;
import com.MAVLink.Messages.enums.MAV_MODE_FLAG;
import com.MAVLink.Messages.enums.MAV_SEVERITY;
import com.MAVLink.Messages.enums.MAV_STATE;
import com.MAVLink.Messages.enums.MAV_TYPE;

public class MavLinkClassExtractor {

	private ArrayList<ClassItem> mavType;
	private ArrayList<ClassItem> mavAutopilot;
	private ArrayList<ClassItem> mavState;
	private ArrayList<ClassItem> mavSeverity;
	private ArrayList<ClassItem> mavMode;
	private ArrayList<ClassItem> mavModeFlag;

	public ArrayList<ClassItem> getMavModeFlag() {
		return mavModeFlag;
	}

	public void setMavModeFlag(ArrayList<ClassItem> mavModeFlag) {
		this.mavModeFlag = mavModeFlag;
	}

	private ArrayList<ClassItem> mavCMD;

	// helper class
	public static class ClassItem {

		private int id;
		private String name;

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

		//=======================================
		// MAV_CMD class fields extractor

		//set empty array /for fields names/
		setMavCMD(new ArrayList<MavLinkClassExtractor.ClassItem>());

		//create example variable
		final MAV_CMD tmpCMD = new MAV_CMD();

		//scan fields of the variable and store them in the array
		for (Field mavField : tmpCMD.getClass().getFields()) {
			try {
				getMavCMD().add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpCMD.getClass().getField(mavField.getName()).getInt(tmpCMD)));
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

		//finally sort the array
		Collections.sort(getMavCMD(), new ClassIdComparator());

		//=======================================
		// MAV_MODE_FLAG class fields extractor		
		setMavModeFlag(new ArrayList<MavLinkClassExtractor.ClassItem>());

		final MAV_MODE_FLAG tmpModeFlag = new MAV_MODE_FLAG();
		for (Field mavField : tmpModeFlag.getClass().getFields()) {
			try {
				getMavModeFlag().add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpModeFlag.getClass().getField(mavField.getName()).getInt(tmpModeFlag)));
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

		Collections.sort(getMavModeFlag(), new ClassIdComparator());

		//=======================================
		// MAV_MODE class fields extractor		
		setMavMode(new ArrayList<MavLinkClassExtractor.ClassItem>());

		final MAV_MODE tmpMode = new MAV_MODE();
		for (Field mavField : tmpMode.getClass().getFields()) {
			try {
				getMavMode().add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpMode.getClass().getField(mavField.getName()).getInt(tmpMode)));
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

		Collections.sort(getMavMode(), new ClassIdComparator());

		//=======================================
		// MAV_SEVERITY class fields extractor		
		setMavSeverity(new ArrayList<MavLinkClassExtractor.ClassItem>());

		final MAV_SEVERITY tmpSeverity = new MAV_SEVERITY();
		for (Field mavField : tmpSeverity.getClass().getFields()) {
			try {
				getMavSeverity().add(new ClassItem(mavField.getName().replace("MAV_", ""), tmpSeverity.getClass().getField(mavField.getName()).getInt(tmpSeverity)));
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

		Collections.sort(getMavSeverity(), new ClassIdComparator());

		//=======================================
		// MAV_TYPE class fields extractor		
		setMavType(new ArrayList<MavLinkClassExtractor.ClassItem>());

		final MAV_TYPE tmpType = new MAV_TYPE();
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

		//=======================================
		// MAV_AUTOPILOT class fields extractor
		setMavAutopilot(new ArrayList<MavLinkClassExtractor.ClassItem>());

		final MAV_AUTOPILOT tmpAutopilot = new MAV_AUTOPILOT();
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

		//=======================================
		// MAV_STATE class fields extractor
		setMavState(new ArrayList<MavLinkClassExtractor.ClassItem>());

		final MAV_STATE tmpState = new MAV_STATE();
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

	public ArrayList<ClassItem> getMavCMD() {
		return mavCMD;
	}

	public void setMavCMD(ArrayList<ClassItem> mavCMD) {
		this.mavCMD = mavCMD;
	}

	public ArrayList<ClassItem> getMavMode() {
		return mavMode;
	}

	public void setMavMode(ArrayList<ClassItem> mavMode) {
		this.mavMode = mavMode;
	}

	public ArrayList<ClassItem> getMavSeverity() {
		return mavSeverity;
	}

	public void setMavSeverity(ArrayList<ClassItem> mavSeverity) {
		this.mavSeverity = mavSeverity;
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
