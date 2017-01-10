package penguin_1;

import battlecode.common.*;

public class GardenerAI {

	public static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");
        
        RobotController rc = RobotPlayer.rc;
        Boolean atHomeEdge=false;
        int backAndForthBounces = 0;

        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	if (rc.getRoundNum()%50==1){
            		int currentGardeners = rc.readBroadcast(2);
            		currentGardeners++;
            		rc.broadcast(2, currentGardeners);
            		System.out.println("Here: "+currentGardeners);
            	}

                // Listen for home archon's location
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                MapLocation archonLoc = new MapLocation(xPos,yPos);

                // Generate a random direction
                Direction dir = Utility.randomDirection();
                
                //plant a tree
                if (rc.canPlantTree(dir)&&Math.random()<0.01){
                	rc.plantTree(dir);
                }
                
               

                // Randomly attempt to build a soldier or lumberjack in this direction
                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .02) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                }

                // towards home edge, once there bounce back and forth
                if (!atHomeEdge){
                	moveTowardsEdge();
                }else{
                	moveBackandForth();
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

	private static void moveBackandForth() {
		// TODO Auto-generated method stub
		
	}

	private static void moveTowardsEdge() {
		// TODO Auto-generated method stub
		
	}
}
