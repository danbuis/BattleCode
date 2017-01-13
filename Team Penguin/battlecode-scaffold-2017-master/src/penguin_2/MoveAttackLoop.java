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
	static MapLocation rallyPoint = new MapLocation(-1,-1);
	static MapLocation expectedEnemyCenter = new MapLocation(-1, -1);
	static MapLocation targetPoint;
	
	static boolean receivedNewTargetLocation = false;
	
	/**
	 * provides general movement structure for all combat units
	 * @throws GameActionException
	 */
	public static void generalMovement() throws GameActionException{
		RobotController rc = RobotPlayer.rc;
		
		
		System.out.println("first check: " + (rallyPoint.x<1));
		//check to see if we have the rally point stored
		if(rallyPoint.x<0){
			rallyPoint = new MapLocation((float) (rc.readBroadcast(100)/1000.0), (float)(rc.readBroadcast(101)/1000.0));
			System.out.println("setting rally point");
		}
		
		if(expectedEnemyCenter.x<0){
			expectedEnemyCenter = new MapLocation((float) (rc.readBroadcast(102)/1000), (float)(rc.readBroadcast(103)/1000));
		}
		
		
		System.out.println("Second check: "+ !arrivedAtRallyPoint +" + "+(rallyPoint.x>-1));
		//check to see if we are close enough to the rally point
		if(!arrivedAtRallyPoint && rallyPoint.x>-1){
			float distanceToRally = Utility.distanceBetweenMapLocations(rc.getLocation(), rallyPoint);
			System.out.println("not close to rally point yet: "+distanceToRally);
			
			//close enough to count as reaching it
			if(distanceToRally<6){
				System.out.println("At rally point");
				arrivedAtRallyPoint=true;
			}
		}
		
		System.out.println("Third Check: "+!receivedNewTargetLocation);
		//if not recieved new target, move to rally point
		if(!receivedNewTargetLocation && rallyPoint.x>(-1)){
			System.out.println("moving to rally point... or maybe around it...");
			generalMove(rallyPoint);
		}
		

		//if at rally, periodically assign new destinations to people
		if(arrivedAtRallyPoint && rc.getRoundNum()%150==0 && !receivedNewTargetLocation){
			System.out.println("recieved new target point");
			receivedNewTargetLocation = true;
			targetPoint = expectedEnemyCenter;
			generalMove(targetPoint);
			
		}else if (receivedNewTargetLocation){ //only if you've been rallied and given a place to go
			generalMove(targetPoint);
		}
		
		
	}
	
	/**
	 * general movement algorithm.  Checks for enemies and breaks plan accordingly.
	 * @param rallyPoint2
	 * @throws GameActionException 
	 */
	
	private static void generalMove(MapLocation targetLocation) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
		//If nearby target, move accordingly
		RobotInfo[] enemyRobots = Utility.checkForEnemyRobots();
		
		if(enemyRobots.length!=0){
			if (rc.getType()==RobotType.SOLDIER){
				SoldierAI.moveAroundHostiles(enemyRobots);
			}
		}
		
		//If target in correct quadrant, move accordingly
		//in emergency, move accordingly
		//else
		
		else{
			Utility.tryMoveToLocation(targetLocation, rc.getType().strideRadius);
		}
		
	}

}
