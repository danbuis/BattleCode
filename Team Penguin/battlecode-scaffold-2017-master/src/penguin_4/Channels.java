package penguin_4;

public class Channels {
	
	/* 1 = east, 2 = north, 3 = west, 4 = south*/	
	public static int HOMESIDE = 0;
	
	//census taken every 50 turns
	public static int CURRENTARCHONS =1;
	public static int CURRENTGARDENERS=2;
	public static int CURRENTSOLDIERS=3;
	public static int CURRENTLUMBERJACKS=4;
	public static int CURRENTTANKS=5;
	public static int CURRENTSCOUTS=6;
	
	
	//map Info
	public static int SYMMETRY=20;//1 = mirror, 2 = rotation
	public static int EASTEDGE=21;  //edges stored as truncated int of (coordinate times 1,000)
	public static int NORTHEDGE=22;
	public static int WESTEDGE=23;
	public static int SOUTHEDGE=24;
	public static int EDGEFOUND=25;
	public static int GARDENLINE=26;
	
	//map Locations
	public static int RELATIVECENTERX=100;
	public static int RELATIVECENTERY=101;
	public static int ENEMYARCHONX =102;
	public static int ENEMYARCHONY =103;
	public static int FRIENDLYARCHONX = 104;
	public static int FRIENDLYARCONY = 105;
	
	public static int NEUTRALTREEX =110;
	public static int NEUTRALTREEY = 111;
	
	public static int BESTGARDENERVALUE = 200;
	public static int BESTGARDENERX = 201;
	public static int BESTGARDENERY = 202;
	
	public static int ULSPOTTEDENEMYX = 400;
	public static int ULSPOTTEDENEMYY = 401;
	public static int URSPOTTEDENEMYX = 402;
	public static int URSPOTTEDENEMYY = 403;
	public static int LLSPOTTEDENEMYX = 404;
	public static int LLSPOTTEDENEMYY = 405;
	public static int LRSPOTTEDENEMYX = 406;
	public static int LRSPOTTEDENEMYY = 407;

}
