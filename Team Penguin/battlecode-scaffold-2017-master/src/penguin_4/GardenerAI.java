package penguin_4;

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
    static Boolean initialSoldier = false;
    static int lumberjacksBuilt = 0;

	public static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");
        
        RobotController rc = RobotPlayer.rc;
        

        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	Utility.checkForNearbyTrees();
            	
            	//if placed search for local best
            	if(foundSuitableLocation && rc.getRoundNum()%3==0){
            		GardenSpotAnalyzer.localBest();
            	}
            	
            	if(!initialSoldier){
            		if(tryToBuildUnit(RobotType.SOLDIER)){
            			initialSoldier = true;
            		}
            	}
            	
            	//role call for census
            	if (rc.getRoundNum()%50==1){
            		int currentGardeners = rc.readBroadcast(Channels.CURRENTGARDENERS);
            		currentGardeners++;
            		rc.broadcast(Channels.CURRENTGARDENERS, currentGardeners);
            		//System.out.println("Here: "+currentGardeners);
            	}
            	
            	//determine homeEdge, and by extension which slot to leave open
            	
            	if (homeEdge==0){
            		int edge=rc.readBroadcast(Channels.HOMESIDE);
            		if(edge!=0){
            			homeEdge = edge;
            		}
            		if(edge == 2 || edge == 3){
            			placeNorthWest = false;
            			placeSouthEast = true;
            		}
            	}
            	
            	//while still looking for a suitable location, go to best known
            	if(!foundSuitableLocation){
            		
            		float targetX = rc.readBroadcast(Channels.BESTGARDENERX)/1000;
            		float targetY = rc.readBroadcast(Channels.BESTGARDENERY)/1000;
            		
            		//initialize target vars
            		System.out.println("moving to "+targetX+","+targetY);
            		MapLocation target = new MapLocation(targetX, targetY);
            		float dist = Utility.distanceBetweenMapLocations(rc.getLocation(), target);
            		
            		//move to target
            		Utility.tryMoveToLocation(target, Math.min(rc.getType().strideRadius, dist));

            		//if close enough
            		if(Utility.distanceBetweenMapLocations(rc.getLocation(), target)<0.001){
            			foundSuitableLocation = true;
            			System.out.println("setting up here");
            		}
            	}
            	

            	
            	//if you have found a suitable location, begin growth process
            	//if enemy nearby, build a soldier
        		RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            	if(foundSuitableLocation && robots.length==0){
            		
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
            		if(trees.length!=0){
            			tryToBuildUnit(RobotType.LUMBERJACK);
            		}
            		
            		if(rc.getTeamBullets()>100){
            			if(Math.random()>0.9){
            				tryToBuildUnit(RobotType.SOLDIER);
            			}else{
            				System.out.println("Try to build tank!");
            				tryToBuildUnit(RobotType.TANK);
            			}
            		}
          
            	}else if (robots.length!=0){
            		tryToBuildUnit(RobotType.SOLDIER);
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
		
		float initialDir = northwestDir.radians;
		
		if(placeSouthEast){
			initialDir = southeastDir.radians;
		}
		
		boolean built= false;
		int count =0;
		while(count<50){
			Direction tryDir =new Direction (initialDir);
			if(rc.canBuildRobot(robotType, tryDir)){
				rc.buildRobot(robotType, tryDir);
				built=true;
				count+=70;
			}else{
				initialDir++;
			}
			
			count++;
		}
		
		return built;
		
	}
}
