package penguin_2;

import battlecode.common.*;

public class MoveAttackLoop {
	
	/**
	 * general plan
	 * 
	 * first move to rally point
	 * Every 500 turns send group towards enemy origin (from Archon)
	 *
	 *in all instances move then attack
	 *soldiers - if sensing enemy units, determine lowest health, fire at it, then move to a location distance 7 from center of group
	 *lumberjacks - if !sense enemy, check for any non-friendly tree, if enemy, attack closest one. 
	 */
	
	static boolean arrivedAtRallyPoint = false;
	static MapLocation rallyPoint= null;
	
	static boolean receivedNewTargetLocation = false;
	
	/**
	 * provides general movement structure for all combat units
	 * @throws GameActionException
	 */
	public static void generalMovement() throws GameActionException{
		RobotController rc = RobotPlayer.rc;
		
		//check to see if we have the rally point stored
		if(rallyPoint.equals(null)){
			rallyPoint = new MapLocation((float) (rc.readBroadcast(100)/1000.0), (float)(rc.readBroadcast(101)/1000.0));
			System.out.println("setting rally point");
		}
		
		//check to see if we are close enough to the rally point
		if(!arrivedAtRallyPoint && !rallyPoint.equals(null)){
			float distanceToRally = Utility.distanceBetweenMapLocations(rc.getLocation(), rallyPoint);
			
			//close enough to count as reaching it
			if(distanceToRally<3){
				arrivedAtRallyPoint=true;
			}
		}
		
		//if not recieved new target, move to rally point
		if(!receivedNewTargetLocation && !rallyPoint.equals(null)){
			generalMove(rallyPoint);
		}
		
		
	}
	
	/**
	 * general movement algorithm.  Checks for enemies and breaks plan accordingly
	 * @param rallyPoint2
	 * @throws GameActionException 
	 */
	
	private static void generalMove(MapLocation targetLocation) throws GameActionException {
		Utility.tryMoveToLocation(targetLocation);
		
	}

	
	//TODO not done!!!
	public static RobotInfo[] checkForEnemyRobots(){
		RobotController rc = RobotPlayer.rc;
		RobotInfo[] info = rc.senseNearbyRobots();
		
		RobotInfo[] enemyRobots = new RobotInfo[info.length];
		
		
		
		return enemyRobots;
		
		
		
	}

}
