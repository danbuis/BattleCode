package penguin_3;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TankAI {
	static void runTank() throws GameActionException {
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

                RobotInfo robotToAttack;
                //Decide what to fire at
                if(robots.length!=0){
                	robotToAttack = selectTarget(robots);
                	float range = Utility.distanceBetweenMapLocations(rc.getLocation(), robotToAttack.location);
                	
                	//how much to fire
                	if(robotToAttack.type==RobotType.ARCHON || robotToAttack.type == RobotType.GARDENER){
                		if(rc.canFirePentadShot()){
                			rc.firePentadShot(rc.getLocation().directionTo(robotToAttack.location));
                		}
                	}else if (range<4){
                		if(rc.canFireTriadShot()&& rc.getTeamBullets()>310){
                			rc.fireTriadShot(rc.getLocation().directionTo(robotToAttack.location));
                		}
                	} else if (range<2.5){
                		if(rc.canFireTriadShot()){
                			rc.fireTriadShot(rc.getLocation().directionTo(robotToAttack.location));
                		}
                	}else{
                		if(rc.canFireSingleShot()){
                			rc.fireSingleShot(rc.getLocation().directionTo(robotToAttack.location));
                		}
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

	private static RobotInfo selectTarget(RobotInfo[] robots) {
		RobotController rc = RobotPlayer.rc;
		RobotInfo targetRobot = robots[0];
		float distance = 1000;
		
		for(RobotInfo info: robots){
			if(info.type==RobotType.ARCHON || info.type==RobotType.GARDENER){
				targetRobot = info;
				break;
			}else{
				float checkDist = Utility.distanceBetweenMapLocations(info.location, rc.getLocation());
				if(checkDist<distance){
					distance = checkDist;
					targetRobot = info;
				}
			}
		}
		return targetRobot;
		
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
