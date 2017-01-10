package penguin_1;

import battlecode.common.*;

public class ArchonAI {
	
	
	static void runArchon() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
        System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	//if turn 0 and no other archon has selected a home side
            	if (rc.getRoundNum()== 1 && rc.readBroadcast(0)==0){
            		//System.out.println("about to call selection method");
            		selectHomeSide();
            	}
            	
            	//if turn multiple of 50, create conditions for census
            	if (rc.getRoundNum()%50==0){
            		setupCensus();
            	}

                // Generate a random direction
                Direction dir = Utility.randomDirection();

                // Build a gardener in direction perpendicular to home as long as there are 4 or less gardeners
                int currentGardeners = rc.readBroadcast(2);
                if (currentGardeners<4 && rc.getRoundNum()%50!=0 && rc.getRoundNum()%50!=1){
                	
                	//determine home orientation;
                	int direction = rc.readBroadcast(0)%2;
                	if (direction==1){ //if odd we are using east or west, so hire north and south
                		if (currentGardeners%2==1 && rc.canHireGardener(Direction.getNorth()))
                		{
                			rc.hireGardener((Direction.getNorth()));
                			currentGardeners++;
                    		//update gardener count
                    		rc.broadcast(2, currentGardeners);
                		}else if (rc.canHireGardener(Direction.getSouth())){
                			rc.hireGardener(Direction.getSouth());
                			currentGardeners++;
                    		//update gardener count
                    		rc.broadcast(2, currentGardeners);
                		}	
                	}else{
                		if (currentGardeners%2==1 && rc.canHireGardener(Direction.getEast()))
                		{
                			rc.hireGardener((Direction.getEast()));
                			currentGardeners++;
                    		//update gardener count
                    		rc.broadcast(2, currentGardeners);
                		}else if (rc.canHireGardener(Direction.getWest())){
                			rc.hireGardener(Direction.getWest());
                			currentGardeners++;
                    		//update gardener count
                    		rc.broadcast(2, currentGardeners);
                		}
                	}
                		
                }
                
//TODO smarter movement
                // Move randomly
                // Utility.tryMove(Utility.randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

	
	
	private static void setupCensus() {
		RobotController rc = RobotPlayer.rc;
		System.out.println("Archon : Census time");
		//set channel 2 (#gardners) to 0;
		try {
			rc.broadcast(2, 0);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
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
		
		float verticalDifference = totalFriendlyY-totalEnemyY;
		float horizontalDifference = totalFriendlyX-totalEnemyX;
		
		//if teams further apart horizontally
		if (Math.abs(horizontalDifference)>=Math.abs(verticalDifference)){
			//decide if east wall or west wall to be used
			
			if (horizontalDifference>=0){  //then I am further east
				try {
					rc.broadcast(0, 1);
					//System.out.println("east");
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			} else
				try {
					rc.broadcast(0, 3);
					//System.out.println("west");
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			
		}else{
			//decide to use north or south
			if (verticalDifference>=0){ //than I am further south
				try {
					rc.broadcast(0, 4);
					//System.out.println("south");
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					rc.broadcast(0, 2);
					//System.out.println("north");
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

	}

}
