package penguin_3;

import battlecode.common.*;

public strictfp class ArchonAI {
	
	static int hiredGardeners=0;
	
	
	static void runArchon() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
        System.out.println("I'm an archon!");
        System.out.println(Direction.getSouth().getAngleDegrees());

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	Utility.checkForNearbyTrees();
            	

            	
            	//if 500+ bullets, donate
            	if(rc.getTeamBullets()>700){
            		rc.donate(180);
            	}
            	
            	//if turn 0 and no other archon has selected a home side
            	if (rc.getRoundNum()== 1 && rc.readBroadcast(0)==0){
            		//System.out.println("about to call selection method");
            		selectHomeSide();
            	}
            	
            	//move archon away from map center
            	if (rc.getRoundNum()>=0 && rc.getRoundNum()<=100){
            		MapLocation mapCenter = new MapLocation((float)(rc.readBroadcast(100)/1000), (float)(rc.readBroadcast(101)/1000));
            		Direction directionToCenter = rc.getLocation().directionTo(mapCenter);
            		
            		Utility.tryMove(directionToCenter.opposite(), rc.getType().strideRadius);
            	}
            	
            	
            	//if turn multiple of 50, create conditions for census
            	if (rc.getRoundNum()%50==0){
            		setupCensus();
            	}

            	//Hire gardeners
                int currentGardeners = rc.readBroadcast(2);
                if (rc.getRoundNum()%50!=0 && rc.getRoundNum()%50!=1){
                	if (performGardenerHiringCheck(currentGardeners)){
                		boolean placedGardener = false;
                		int trys = 0;
                		float directionToTry = Utility.randomDirection().radians;
                		while(!placedGardener && trys<30 ){
                			Direction tryDir = new Direction(directionToTry);
                			if(rc.canHireGardener(tryDir)){
                				rc.hireGardener(tryDir);
                				placedGardener=true;
                			}
                			directionToTry++;
                			trys++;
                		}
                	}
                		
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
		}else if (currentGardeners < 9 ){
			return true;
		}else return false;
		
	}



	private static void setupCensus() {
		RobotController rc = RobotPlayer.rc;
		System.out.println("Archon : Census time");
		//set channel 2 (#gardners) to 0;
		try {
			rc.broadcast(2, 0);
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
					if(rc.readBroadcast(20)==0){
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
					rc.broadcast(0, 1);
					System.out.println("broadcasting east");
					//System.out.println("east");
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			} else{
				System.out.println("selecting west");
				try {
					rc.broadcast(0, 3);
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
					rc.broadcast(0, 4);
					//System.out.println("south");
				} catch (GameActionException e) {
					//Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					System.out.println("selecting north");
					rc.broadcast(0, 2);
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
			rc.broadcast(20, 1);
			System.out.println("Mirror Symmetry");
			System.out.println("friendlyX:"+totalFriendlyX);
			System.out.println("friendlyY:"+totalFriendlyY);
			System.out.println("enemyX:"+totalEnemyX);
			System.out.println("enemyY:"+totalEnemyY);
		}else{
			rc.broadcast(20,2);
			System.out.println("Rotation Symmetry");
		}
		
		rc.broadcast(21, -1);
		rc.broadcast(22, -1);
		rc.broadcast(23, -1);
		rc.broadcast(24, -1);
		
		//determine relative center of map
		float relativeCenterX = (totalFriendlyX+totalEnemyX)/(2*numberOfArchons);
		float relativeCenterY = (totalFriendlyY+totalEnemyY)/(2*numberOfArchons);
		
		rc.broadcast(100, (int)(relativeCenterX*1000));
		rc.broadcast(101, (int)(relativeCenterY*1000));
		
		System.out.println("relative centerX :"+relativeCenterX);
		System.out.println("relative centerX :"+relativeCenterY);
		
		//determine average center of enemy archons
		float enemyCenterX = totalEnemyX/numberOfArchons;
		float enemyCenterY = totalEnemyY/numberOfArchons;
		
		rc.broadcast(102, (int)(enemyCenterX*1000));
		rc.broadcast(103, (int)(enemyCenterY*1000));
		
	}

}
