package org.serdar.deeplearning;

public enum Label {
	user29(0),
	user31(1),
	user37(2),
	user41(3),
	user43(4),
	user47(5),
	user53(6),
	user59(7),
	user61(8),
	user67(9),
	user71(10),
	user73(11),
	user79(12),
	user83(13),
	user89(14),
	user97(15),
	user101(16),
	user103(17),
	user107(18),
	user109(19),
	user113(20),
	user127(21),
	user131(22),
	user137(23),
	user139(24),
	user149(25),
	user151(26);
	
	private int asInt;
	
	Label(int value) {
		this.asInt = value;
	}
	
	public int asInt(){
		return asInt;
	}
}
