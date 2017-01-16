package penguin_3;

import battlecode.common.*;

public strictfp class GardenerAI {
	
    static int homeEdge=0;  
    static Direction eastDir = Direction.getEast();
    static Direction northeastDir = new Direction(2 * (float)Math.PI / 6);
    static Direction northwestDir = new Direction(2 * (float)Math.PI / 3);
    static Direction westDir = new Direction(2 * (float)Math.PI/2);
    static Direction southwestDir = new Direction(northeastDir.radians+(float)Math.PI);
    static Direction southeastDir = new Direction(northwestDir.radians+(float)Math.PI);
    
    //initialized as one direction for where to spawn units
    static Boolean placeNorthWest= true;
    static Boolean placeSouthEast = false;
    
    //other locations to be used for trees
    static Boolean placeEast = false;
    static Boolean placeWest = false;
    static Boolean placeNorthEast = false;
    static Boolean placeSouthWest = false;
    
    //gardener variables
    static Boolean foundSuitableLocation = false;
    static Boolean initialLumberjack = false;
    static int lumberjacksBuilt = 0;

	public static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");
        
        RobotController rc = RobotPlayer.rc;
        

        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	Utility.checkForNearbyTrees();
            	
            	if(!initialLumberjack){
            		if(tryToBuildUnit(RobotType.LUMBERJACK)){
            			initialLumberjack = true;
            		}
            	}
            	
            	//role call for census
            	if (rc.getRoundNum()%50==1){
            		int currentGardeners = rc.readBroadcast(2);
            		currentGardeners++;
            		rc.broadcast(2, currentGardeners);
            		//System.out.println("Here: "+currentGardeners);
            	}
            	
            	//determine homeEdge, and by extension which slot to leave open
            	
            	if (homeEdge==0){
            		int edge=rc.readBroadcast(0);
            		if(edge!=0){
            			homeEdge = edge;
            		}
            		if(edge == 2 || edge == 3){
            			placeNorthWest = false;
            			placeSouthEast = true;
            		}
            	}
            	
            	//while still looking for a suitable location, check location, else wander
            	if(!foundSuitableLocation){
            		
            		boolean farEnoughFromArchon = true;
            		RobotInfo[] robots = rc.senseNearbyRobots(3);
            		for(RobotInfo info: robots){
            			if(info.type==RobotType.ARCHON){
            				farEnoughFromArchon = false;
            				break;
            			}
            		}
            		
            		
            		TreeInfo[] trees = rc.senseNearbyTrees((float)3, Team.NEUTRAL);
            		TreeInfo[] friendTrees = rc.senseNearbyTrees((float)5, rc.getTeam());
            		
            		if(farEnoughFromArchon && trees.length==0 && friendTrees.length==0){
            			foundSuitableLocation = true;
            		}else{
            			Utility.moveRandom();
            		}
            	}
            	
            	//if you have found a suitable location, begin growth process
            	if(foundSuitableLocation){
            		
            		//first plant the correct 5 trees
            		if(!placeEast && rc.canPlantTree(eastDir)){
            			rc.plantTree(eastDir);
            			//placeEast = true;
            		}
            		if(!placeNorthEast && rc.canPlantTree(northeastDir)){
            			rc.plantTree(northeastDir);
            			//placeNorthEast = true;
            		}
            		if(!placeNorthWest && rc.canPlantTree(northwestDir)){
            			rc.plantTree(northwestDir);
            			//placeNorthWest = true;
            		}
            		if(!placeWest && rc.canPlantTree(westDir)){
            			rc.plantTree(westDir);
            			//placeWest = true;
            		}
            		if(!placeSouthWest && rc.canPlantTree(southwestDir)){
            			rc.plantTree(southwestDir);
            			//placeSouthWest = true;
            		}
            		if(!placeSouthEast && rc.canPlantTree(southeastDir)){
            			rc.plantTree(southeastDir);
            			//placeSouthEast = true;
            		}
            		
            		//water anything nearby
            		TreeInfo[] nearTrees = rc.senseNearbyTrees((float)2.2, rc.getTeam());
            		System.out.println("Sensing "+nearTrees.length+" trees");
            		if(nearTrees.length!=0){
            			for (TreeInfo info:nearTrees){
            				System.out.println("Checking tree: "+info.ID+". Health is "+info.getHealth());;
            				if(info.getHealth()<=45){
            					System.out.println(info.ID+" needs watering");
            					if(rc.canWater(info.ID)){
            						System.out.println("watering");
            						rc.water(info.ID);
            						break;
            					}
            				}
            			}
            		}
            		
            		//Decide what to build
            		//if there is a tree nearby, build a lumberjack
            		TreeInfo[] trees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
            		if(trees.length!=0 && lumberjacksBuilt>4){
            			tryToBuildUnit(RobotType.LUMBERJACK);
            		}
            		if(rc.getTeamBullets()>300){
            			if(Math.random()>0.9){
            				tryToBuildUnit(RobotType.SOLDIER);
            			}else{
            				System.out.println("Try to build tank!");
            				tryToBuildUnit(RobotType.TANK);
            			}
            		}
          
            	}
                
            	
            	

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

	private static boolean tryToBuildUnit(RobotType robotType) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
		if(rc.canBuildRobot(robotType, northwestDir)){
			rc.buildRobot(robotType, northwestDir);
			if(robotType == RobotType.LUMBERJACK){
				lumberjacksBuilt++;
			}
			return true;
		}else if(rc.canBuildRobot(robotType, southeastDir)){
			rc.buildRobot(robotType, southeastDir);
			if(robotType == RobotType.LUMBERJACK){
				lumberjacksBuilt++;
			}
			return true;
		}else return false;
		
	}
}
