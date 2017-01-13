package penguin_2;

import battlecode.common.*;

public class SoldierAI {
	
    static void runSoldier() throws GameActionException {
    	RobotController rc = RobotPlayer.rc;
    	
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	Utility.checkForNearbyTrees();
                 
                // Move 
                MoveAttackLoop.generalMovement();

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }



                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }

	public static void moveAroundHostiles(RobotInfo[] enemyRobots) throws GameActionException {
		
		RobotController rc = RobotPlayer.rc;
		RobotInfo closestRobot = null;
		float closestDist = 90;
		Direction dirToEnemy = Direction.getNorth();
		
		
		//find the closest
		for(RobotInfo info: enemyRobots){
			if(Utility.distanceBetweenMapLocations(rc.getLocation(), info.location)<closestDist){
				closestRobot = info;
			}
		}
		
		//get direction to that robot
		if(closestRobot != null){
			dirToEnemy = rc.getLocation().directionTo(closestRobot.location);
		}
		
		MapLocation targetLocation = Utility.getLocationWithDistanceFromTarget(dirToEnemy, closestRobot.location, 5);
		
		float distToTargetLocation = Utility.distanceBetweenMapLocations(rc.getLocation(), targetLocation);
		
		Utility.tryMoveToLocation(targetLocation, Math.min(distToTargetLocation, rc.getType().strideRadius));
	
	}

}
