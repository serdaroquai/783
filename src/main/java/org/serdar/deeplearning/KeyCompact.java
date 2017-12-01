package org.serdar.deeplearning;

public class KeyCompact {

	Integer label;
	Integer key;
	long keydown;
	long keyup;
	long dwell;
	long flight;
	
	
	public KeyCompact() {
	
	}
	
	public KeyCompact(Integer label, Integer key, long keydown) {
		super();
		this.label = label;
		this.key = key;
		this.keydown = keydown;
	}
	
	public Integer getLabel() {
		return label;
	}

	public void setLabel(Integer label) {
		this.label = label;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public long getDwell() {
		return dwell;
	}

	public void setDwell(long dwell) {
		this.dwell = dwell;
	}

	public long getFlight() {
		return flight;
	}

	public void setFlight(long flight) {
		this.flight = flight;
	}

	@Override
	public String toString() {
		return key+"";
	}
}
