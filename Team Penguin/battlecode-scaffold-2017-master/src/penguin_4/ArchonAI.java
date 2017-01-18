package penguin_4;

import battlecode.common.*;

public strictfp class ArchonAI {
	
	static int hiredGardeners=0;
	static int timeSinceLastGardener = 150;
	
	
	static void runArchon() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
        System.out.println("I'm an archon!");
        System.out.println(Direction.getSouth().getAngleDegrees());

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	//draw spotted locations
            	rc.setIndicatorDot(new MapLocation(rc.readBroadcast(Channels.LLSPOTTEDENEMYX), rc.readBroadcast(Channels.LLSPOTTEDENEMYY)), 0, 0, 0);
            	rc.setIndicatorDot(new MapLocation(rc.readBroadcast(Channels.LRSPOTTEDENEMYX), rc.readBroadcast(Channels.LRSPOTTEDENEMYY)), 0, 0, 0);
            	rc.setIndicatorDot(new MapLocation(rc.readBroadcast(Channels.URSPOTTEDENEMYX), rc.readBroadcast(Channels.URSPOTTEDENEMYY)), 0, 0, 0);
            	rc.setIndicatorDot(new MapLocation(rc.readBroadcast(Channels.ULSPOTTEDENEMYX), rc.readBroadcast(Channels.ULSPOTTEDENEMYY)), 0, 0, 0);

            	
            	if(rc.getRoundNum()%3==0){
            		//reset best value
            		rc.broadcast(Channels.BESTGARDENERVALUE, 99999999);
            		//search for local best
            		if(rc.readBroadcast(Channels.FIRSTPLANT)==0){
            			GardenSpotAnalyzer.localBest();
            		}
            	}
            	
            	Utility.checkForNearbyTrees();
            	
            	
            	//if 500+ bullets, donate
            	if(rc.getTeamBullets()>700){
            		rc.donate(180);
            	}
            	
            	//if turn 0 and no other archon has selected a home side
            	if (rc.getRoundNum()== 1 && rc.readBroadcast(Channels.HOMESIDE)==0){
            		//System.out.println("about to call selection method");
            		selectHomeSide();
            	}
            	
            	//move archon away from map center
            	/*if (rc.getRoundNum()>=0 && rc.getRoundNum()<=100){
            		MapLocation mapCenter = new MapLocation((float)(rc.readBroadcast(Channels.RELATIVECENTERX)/1000), (float)(rc.readBroadcast(Channels.RELATIVECENTERY)/1000));
            		Direction directionToCenter = rc.getLocation().directionTo(mapCenter);
            		
            		Utility.tryMove(directionToCenter.opposite(), rc.getType().strideRadius);
            	}*/
            	
            	
            	//if turn multiple of 50, create conditions for census
            	if (rc.getRoundNum()%50==0){
            		setupCensus();
            	}

            	//Hire gardeners
                int currentGardeners = rc.readBroadcast(Channels.CURRENTGARDENERS);
                if (rc.getRoundNum()%50!=0 && rc.getRoundNum()%50!=1 && timeSinceLastGardener>110 && rc.getTeamBullets()>110){
                	if (performGardenerHiringCheck(currentGardeners)){
                		boolean placedGardener = false;
                		int trys = 0;
                		float directionToTry = Utility.randomDirection().radians;
                		while(!placedGardener && trys<30 ){
                			Direction tryDir = new Direction(directionToTry);
                			if(rc.canHireGardener(tryDir)){
                				rc.hireGardener(tryDir);
                				placedGardener=true;
                				timeSinceLastGardener = 0;
                			}
                			directionToTry++;
                			trys++;
                		}
                	}
                		
                }else{
                	timeSinceLastGardener++;
                }
                
//TODO smarter movement
                // Move randomly
          //if(rc.getRoundNum()%10==0){
                // Utility.tryMove(Utility.randomDirection(), 1);
         // }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

	
	
	private static boolean performGardenerHiringCheck(int currentGardeners) {
		RobotController rc = RobotPlayer.rc;
		
		//first check that there aren't too many around
		//int nearbyGardenerLimit = 2;
		int actualNearbyGardeners = 0;
		
		RobotInfo[] nearbyFriendlys = rc.senseNearbyRobots((float) 5.2, rc.getTeam());
		for (RobotInfo info: nearbyFriendlys){
			if (info.type==RobotType.GARDENER){
				actualNearbyGardeners++;
			}
		}
		
		//if(actualNearbyGardeners >= nearbyGardenerLimit) return false;
		
		int currentRound = rc.getRoundNum();
		
		if(currentRound < 450 && currentGardeners < 1){
			return true;
		}else if (currentGardeners < 15 ){
			return true;
		}else return false;
		
	}



	private static void setupCensus() {
		RobotController rc = RobotPlayer.rc;
		System.out.println("Archon : Census time");
		//set channel 2 (#gardners) to 0;
		try {
			rc.broadcast(Channels.CURRENTGARDENERS, 0);
		} catch (GameActionException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/*Select a home side based on relative positions of archons at game init
	 * 
	 * */
	 
	
	private static void selectHomeSide() {
		RobotController rc = RobotPlayer.rc;
		//System.out.println("selecting home side");
	
		MapLocation[] friendlyarchons = rc.getInitialArchonLocations(Team.A);
		MapLocation[] enemyarchons = rc.getInitialArchonLocations(Team.A);
		
		if (rc.getTeam()==Team.A){
			enemyarchons = rc.getInitialArchonLocations(Team.B);
		}else{
			friendlyarchons=rc.getInitialArchonLocations(Team.B);
		}
		
		
		//values to hold total coordinate info
		float totalFriendlyX=0;
		float totalFriendlyY=0;
		
		float totalEnemyX=0;
		float totalEnemyY=0;		
		
		for (MapLocation loc : friendlyarchons){
			totalFriendlyX+=loc.x;
			totalFriendlyY+=loc.y;
		}
		
		for (MapLocation loc : enemyarchons){
			totalEnemyX+=loc.x;
			totalEnemyY+=loc.y;
		}
		
		//piggyback these values to determine map symmetry
				try {
					if(rc.readBroadcast(Channels.SYMMETRY)==0){
						determineMapInfo(totalFriendlyX, totalFriendlyY, totalEnemyX, totalEnemyY, friendlyarchons.length);
					}
				} catch (GameActionException e1) {
					// Auto-generated catch block
					e1.printStackTrace();
				}
		
		float verticalDifference = totalFriendlyY-totalEnemyY;
		float horizontalDifference = totalFriendlyX-totalEnemyX;
		
		System.out.println("vert difference of archons:"+verticalDifference);
		System.out.println("horz difference of archons:"+horizontalDifference);
		
		
		//if teams further apart horizontally
		if (Math.abs(horizontalDifference)>=Math.abs(verticalDifference)){
			//decide if east wall or west wall to be used
			
			System.out.println("using horizontal difference");
			
			if (horizontalDifference>=0){  //then I am further east
				try {
					System.out.println("selecting east");
					rc.broadcast(Channels.HOMESIDE, 1);
					System.out.println("broadcasting east");
					//System.out.println("east");
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			} else{
				System.out.println("selecting west");
				try {
					rc.broadcast(Channels.HOMESIDE, 3);
					System.out.println("broadcasting west");
					//System.out.println("west");
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
			
		}else{
			System.out.println("Using vertical difference");
			
			//TODO use directions to determine north or south in case origin changes again
			MapLocation enemyArchonLocation = new MapLocation(totalEnemyX,totalEnemyY);
			MapLocation friendlyArchonLocation = new MapLocation(totalFriendlyX, totalFriendlyY);
			Direction directionToEnemyArchons = new Direction(friendlyArchonLocation,enemyArchonLocation);
			
			float degreesToNorth = directionToEnemyArchons.degreesBetween(Direction.getNorth());
			System.out.println(degreesToNorth);
			
			
			//decide to use north or south
			if (Math.abs(degreesToNorth)<90){ //than I am further south
				try {
					System.out.println("selecting south");
					rc.broadcast(Channels.HOMESIDE, 4);
					//System.out.println("south");
				} catch (GameActionException e) {
					//Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					System.out.println("selecting north");
					rc.broadcast(Channels.HOMESIDE, 2);
					//System.out.println("north");
				} catch (GameActionException e) {
					//Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

	}



	/**
	 * determine symmetry.  If a mirror, than either total X or Y will be the same, else its reflection
	 * @param totalFriendlyX
	 * @param totalFriendlyY
	 * @param totalEnemyX
	 * @param totalEnemyY
	 * @param numberOfArchons TODO
	 * @throws GameActionException 
	 */
	private static void determineMapInfo(float totalFriendlyX, float totalFriendlyY, float totalEnemyX,
			float totalEnemyY, int numberOfArchons) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		if(totalFriendlyX==totalEnemyX || totalFriendlyY==totalEnemyY){
			rc.broadcast(Channels.SYMMETRY, 1);
			System.out.println("Mirror Symmetry");
			System.out.println("friendlyX:"+totalFriendlyX);
			System.out.println("friendlyY:"+totalFriendlyY);
			System.out.println("enemyX:"+totalEnemyX);
			System.out.println("enemyY:"+totalEnemyY);
		}else{
			rc.broadcast(Channels.SYMMETRY,2);
			System.out.println("Rotation Symmetry");
		}
		
		rc.broadcast(Channels.EASTEDGE, -1);
		rc.broadcast(Channels.NORTHEDGE, -1);
		rc.broadcast(Channels.WESTEDGE, -1);
		rc.broadcast(Channels.SOUTHEDGE, -1);
		
		//determine relative center of map
		float relativeCenterX = (totalFriendlyX+totalEnemyX)/(2*numberOfArchons);
		float relativeCenterY = (totalFriendlyY+totalEnemyY)/(2*numberOfArchons);
		
		rc.broadcast(Channels.RELATIVECENTERX, (int)(relativeCenterX*1000));
		rc.broadcast(Channels.RELATIVECENTERY, (int)(relativeCenterY*1000));
		
		System.out.println("relative centerX :"+relativeCenterX);
		System.out.println("relative centerX :"+relativeCenterY);
		
		//determine average center of enemy archons
		float enemyCenterX = totalEnemyX/numberOfArchons;
		float enemyCenterY = totalEnemyY/numberOfArchons;
		
		rc.broadcast(Channels.ENEMYARCHONX, (int)(enemyCenterX*1000));
		rc.broadcast(Channels.ENEMYARCHONY, (int)(enemyCenterY*1000));
		
		//determine average center of friendly
		float friendlyCenterX = totalFriendlyX/numberOfArchons;
		float friendlyCenterY = totalFriendlyY/numberOfArchons;
		
		rc.broadcast(Channels.FRIENDLYARCHONX,  (int)(friendlyCenterX*1000));
		rc.broadcast(Channels.FRIENDLYARCONY, (int)(friendlyCenterY*1000));
		
	}

}
