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
	CURRENTROUND(7);
	
	public int channelNumber;
	
	private Channels(int channelNumberInside){
		this.channelNumber = channelNumberInside;
	}

}
