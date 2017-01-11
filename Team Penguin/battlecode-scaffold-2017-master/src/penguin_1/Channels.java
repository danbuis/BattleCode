package penguin_1;

public enum Channels {
	
	/* 1 = east, 2 = north, 3 = west, 4 = south*/	
	HOMESIDE (0),
	
	//census taken every 50 turns
	CURRENTARCHONS(1),
	CURRENTGARDENERS(2),
	CURRENTSOLDIERS(3),
	CURRENTLUMBERJACKS(4),
	CURRENTTANKS(5),
	CURRENTSCOUTS(6),
	
	
	//map Info
	SYMMETRY(20), //1 = mirror, 2 = rotation
	EASTEDGE(21),  //edges stored as truncated int of (coordinate times 1,000)
	NORTHEDGE(22),
	WESTEDGE(23),
	SOUTHEDGE(24),
	EDGEFOUND(25);
	
	
	
	public int channelNumber;
	
	private Channels(int channelNumberInside){
		this.channelNumber = channelNumberInside;
	}

}
