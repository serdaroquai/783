package org.serdar.deeplearning;

public enum Label {
	user29(0),
	user31(1),
	user37(2),
	user41(3),
	user43(4),
	user47(5),
	user53(6);
	
	private int asInt;
	
	Label(int value) {
		this.asInt = value;
	}
	
	public int asInt(){
		return asInt;
	}
}
