package penguin_1;

import battlecode.common.*;

public class LumberjackAI {
	
	static TreeInfo targetNeutralTree = null;

	
	static void runLumberjack() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
        System.out.println("I'm a lumberjack!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	//check for neutralTrees
            	if(targetNeutralTree == null){
            		TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
            		for (TreeInfo info : nearbyTrees){
            			if(info.team==Team.NEUTRAL){
            				targetNeutralTree = info;
            				break;
            			}
            		}
            	}
            	
            	if(targetNeutralTree !=null){
            		attackNeutralTree();
            	}
            	

                // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
                RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);

                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else {
                    // No close robots, so search for robots within sight radius
                    robots = rc.senseNearbyRobots(-1,enemy);

                    // If there is a robot, move towards it
                    if(robots.length > 0) {
                        MapLocation myLocation = rc.getLocation();
                        MapLocation enemyLocation = robots[0].getLocation();
                        Direction toEnemy = myLocation.directionTo(enemyLocation);

                        Utility.tryMove(toEnemy);
                    } else {
                        // Move Randomly, as long as we aren't targeting a specific tree
                    	//if(targetNeutralTree==null){
                            Utility.tryMove(Utility.randomDirection());
                    //	}

                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }


	private static void attackNeutralTree() {
		RobotController rc = RobotPlayer.rc;
		
		try{
			//try to chop it
			if(rc.canChop(targetNeutralTree.ID)){
				rc.chop(targetNeutralTree.ID);
			} //if that fails, move towards it
			else if(rc.canMove(targetNeutralTree.location)){
				rc.move(targetNeutralTree.location);
			}
			
			// if tree no longer there, then remove target.
			if(rc.senseTreeAtLocation(targetNeutralTree.location)==null){
				targetNeutralTree = null;
			}
		}catch (Exception e) {
            System.out.println("Lumberjack Exception");
            e.printStackTrace();
        }
    }
		
}
