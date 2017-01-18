package penguin_5;

import battlecode.common.*;
import penguin_5.Utility;

public strictfp class GardenerAI {
	
    static int homeEdge=0;  
    static Direction eastDir = Direction.getEast();
    static Direction northeastDir = new Direction(2 * (float)Math.PI / 6);
    static Direction northwestDir = new Direction(2 * (float)Math.PI / 3);
    static Direction westDir = new Direction(2 * (float)Math.PI/2);
    static Direction southwestDir = new Direction(northeastDir.radians+(float)Math.PI);
    static Direction southeastDir = new Direction(northwestDir.radians+(float)Math.PI);
    
    static int patience=110;
    
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
    static Boolean planted = false;
   // static Boolean initialSoldier = false;
    static int lumberjacksBuilt = 0;

	public static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");
        
        RobotController rc = RobotPlayer.rc;
        

        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	patience--;
            	
            	Utility.checkForNearbyTrees();
            	
            	//if placed search for local best
            	if(foundSuitableLocation && rc.getRoundNum()%3==0){
            		GardenSpotAnalyzer.localBest();
            	}
            	
            	if(rc.readBroadcast(Channels.FIRSTSCOUT)==0){
            		if(tryToBuildUnit(RobotType.SCOUT)){
            			rc.broadcast(Channels.FIRSTSCOUT, 1);
            		}
            	}
            	
            	if(rc.readBroadcast(Channels.FIRSTSOLDIER)==0){
            		if(tryToBuildUnit(RobotType.SOLDIER)){
            			rc.broadcast(Channels.FIRSTSOLDIER, 1);
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
            		
            		MapLocation rallyPoint = MoveAttackLoop.calculateRallyPoint();
            		Direction directionToRally = rc.getLocation().directionTo(rallyPoint);
            		
            		float diffToNorthWest = directionToRally.degreesBetween(northwestDir);
            		float diffToSouthEast = directionToRally.degreesBetween(southeastDir);
            		
            		/*if(Math.abs(diffToNorthWest)>Math.abs(diffToSouthEast)){
            			placeNorthWest = false;
            			placeSouthEast = true;
            		}*/
            		
            		if(edge == 2 || edge == 3){
            			placeNorthWest = false;
            			placeSouthEast = true;
            		}
            	}
            	
            	//while still looking for a suitable location, go to best known
            	if(!planted && !foundSuitableLocation){
            		
            		float targetX = (float) (rc.readBroadcast(Channels.BESTGARDENERX)/1000.0);
            		float targetY = (float) (rc.readBroadcast(Channels.BESTGARDENERY)/1000.0);
            		
            		//initialize target vars
            		System.out.println("moving to "+targetX+","+targetY);
            		MapLocation target = new MapLocation(targetX, targetY);
            		float dist = Utility.distanceBetweenMapLocations(rc.getLocation(), target);
            		
            		//does target spot contain a gardener?
        			RobotInfo robotAtTargetSpot=null;
        			
        			//is that robot me?
        			boolean me=false;
        			
        			if(rc.canSenseLocation(target)){
        				robotAtTargetSpot=rc.senseRobotAtLocation(target);
        				
        				if(robotAtTargetSpot!=null && robotAtTargetSpot.ID==rc.getID()){
        					System.out.println("setting me to true");
            				me=true;
            			}else{
            				System.out.println("setting me to false");
            				me=false;
            			}
        			}
        			
        			
        			
        			
        			
        			if(robotAtTargetSpot!=null && !me){
        				System.out.println("Oh no, someone else got there first!");
        				//float target2ndX = rc.readBroadcast(Channels.SECONDBESTX)/1000;
        				//float target2ndY = rc.readBroadcast(Channels.SECONDBESTY)/1000;
        				
        				Utility.moveRandom();
        			}else{
            		
            		//move to target
            		if(patience>0) {
            			Utility.tryMoveToLocation(target, Math.min(rc.getType().strideRadius, dist));
            		

            		float remainingDistance = Utility.distanceBetweenMapLocations(rc.getLocation(), target);
            		//if close enough
            		if(remainingDistance<0.001){
            			foundSuitableLocation = true;
            			planted = true;
            			rc.broadcast(Channels.FIRSTPLANT, 1);
            			System.out.println("setting up here");
            		}else if (remainingDistance<2){
            			//if 2 away, check if its off the map
            			if(!rc.onTheMap(target)){
            				planted=true;
                			rc.broadcast(Channels.FIRSTPLANT, 1);
            			}
            		}
            			//if out of patience, jitter old school
            		}else {
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
                			planted=true;
                		}else{
                			Utility.moveRandom();
                		}
            			
            		}
        			}
            	}
            	

            	
            	//if enemy nearby, build a soldier
        		RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        		if (robots.length!=0){
            		tryToBuildUnit(RobotType.SOLDIER);
            	}
        		
            	//if you have found a suitable location, begin growth process

            	if(planted && robots.length==0){
            		
            		//first plant the correct 5 trees
            		if(!placeEast && rc.canPlantTree(eastDir)){
            			rc.plantTree(eastDir);
            			//placeEast = true;
            		}
            		if(!placeNorthEast && rc.canPlantTree(northeastDir)){
            			if(homeEdge!=4 || rc.onTheMap(new MapLocation(rc.getLocation().x, rc.getLocation().y+5))){
            				rc.plantTree(northeastDir);
            			}
            			//placeNorthEast = true;
            		}
            		if(!placeNorthWest && rc.canPlantTree(northwestDir)){
            			if(homeEdge!=4 ||rc.onTheMap(new MapLocation(rc.getLocation().x, rc.getLocation().y+5))){
            				rc.plantTree(northwestDir);
            			}
            			//placeNorthWest = true;
            		}
            		if(!placeWest && rc.canPlantTree(westDir)){
            			rc.plantTree(westDir);
            			//placeWest = true;
            		}
            		if(!placeSouthWest && rc.canPlantTree(southwestDir)){
            			if(homeEdge!=2 || rc.onTheMap(new MapLocation(rc.getLocation().x, rc.getLocation().y-5))){
            				rc.plantTree(southwestDir);
            			}
            			//placeSouthWest = true;
            		}
            		if(!placeSouthEast && rc.canPlantTree(southeastDir)){
            			if(homeEdge!=2 || rc.onTheMap(new MapLocation(rc.getLocation().x, rc.getLocation().y-5))){
            				rc.plantTree(southeastDir);
            			}
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
            	}
            		
            		//Decide what to build
            		//if there is a tree nearby, build a lumberjack
            		TreeInfo[] trees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
            		if(trees.length!=0){
            			tryToBuildUnit(RobotType.LUMBERJACK);
            			System.out.println("force to lumberjack");
            		}
            		
            		if(rc.getTeamBullets()>100){
            			float random=(float) Math.random();
            			if(random>0.9){
            				tryToBuildUnit(RobotType.SOLDIER);
            			}else if(random>0.92 && lumberjacksBuilt<4){
            				System.out.println("Try to build lumberjack randomly!");
            				tryToBuildUnit(RobotType.LUMBERJACK);
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
		
		if(built&&robotType==RobotType.LUMBERJACK){
			lumberjacksBuilt++;
		}
		
		return built;
		
	}
}
