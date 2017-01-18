package penguin_4;

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
	static MapLocation expectedFriendlyCenter = new MapLocation(-1,-1);
	static MapLocation centerPoint = new MapLocation(-1,-1);
	static MapLocation targetPoint;
	
	static boolean receivedNewTargetLocation = false;
	static boolean arrivedNewTargetLocation = false;
	
	/**
	 * provides general movement structure for all combat units
	 * @throws GameActionException
	 */
	public static void generalMovement() throws GameActionException{
		RobotController rc = RobotPlayer.rc;
		
		
		System.out.println("first check: " + (rallyPoint.x<1));
		//check to see if we have the rally point stored
		if(rallyPoint.x<0){
			rallyPoint = calculateRallyPoint();
			System.out.println("setting rally point");
		}
		
		if(expectedEnemyCenter.x<0){
			expectedEnemyCenter = new MapLocation((float) (rc.readBroadcast(Channels.ENEMYARCHONX)/1000), (float)(rc.readBroadcast(Channels.ENEMYARCHONY)/1000));
		}
		
		
		System.out.println("Second check: "+ !arrivedAtRallyPoint +" + "+(rallyPoint.x>-1));
		//check to see if we are close enough to the rally point
		if(!arrivedAtRallyPoint && rallyPoint.x>-1){
			float distanceToRally = Utility.distanceBetweenMapLocations(rc.getLocation(), rallyPoint);
			System.out.println("not close to rally point yet: "+distanceToRally);
			
			//close enough to count as reaching it
			if(distanceToRally<7){
				System.out.println("At rally point");
				arrivedAtRallyPoint=true;
			}
		}
		
		System.out.println("Third Check: "+!receivedNewTargetLocation);
		//if not recieved new target, move to rally point
		if(!receivedNewTargetLocation && rallyPoint.x>(-1)){
			System.out.println("moving to rally point... or maybe around it...");
			generalMoveToLoc(rallyPoint);
		}
		

		//if at rally, periodically assign new destinations to people
		if(arrivedAtRallyPoint && rc.getRoundNum()%80==0 && rc.getRoundNum()>400 && !receivedNewTargetLocation){
			System.out.println("recieved new target point");
			receivedNewTargetLocation = true;
			targetPoint = expectedEnemyCenter;
			generalMoveToLoc(targetPoint);
			
		}else if (receivedNewTargetLocation){ //only if you've been rallied and given a place to go
			generalMoveToLoc(targetPoint);
		}
		
		
	}
	
	public static MapLocation calculateRallyPoint() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		System.out.println("calculating rally point");
		
		centerPoint = new MapLocation((float) (rc.readBroadcast(Channels.RELATIVECENTERX)/1000.0), (float)(rc.readBroadcast(Channels.RELATIVECENTERY)/1000.0));
		expectedFriendlyCenter = new MapLocation((float) (rc.readBroadcast(Channels.FRIENDLYARCHONX)/1000.0), (float)(rc.readBroadcast(Channels.FRIENDLYARCONY)/1000.0));

		
		return new MapLocation((expectedFriendlyCenter.x+centerPoint.x)/2, (expectedFriendlyCenter.y+centerPoint.y)/2);
		
	}

	/**
	 * general movement algorithm.  Checks for enemies and breaks plan accordingly.
	 * @param rallyPoint2
	 * @throws GameActionException 
	 */
	
	private static void generalMoveToLoc(MapLocation targetLocation) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
		//If target in correct quadrant, move accordingly, resetting any targetlocation info
		
		//setting up spotted in each quadrant
			MapLocation upperRight = new MapLocation(rc.readBroadcast(Channels.URSPOTTEDENEMYX), rc.readBroadcast(Channels.URSPOTTEDENEMYY));
			MapLocation upperLeft = new MapLocation(rc.readBroadcast(Channels.ULSPOTTEDENEMYX), rc.readBroadcast(Channels.ULSPOTTEDENEMYY));
			MapLocation lowerRight = new MapLocation(rc.readBroadcast(Channels.LRSPOTTEDENEMYX), rc.readBroadcast(Channels.LRSPOTTEDENEMYY));
			MapLocation lowerLeft = new MapLocation(rc.readBroadcast(Channels.LLSPOTTEDENEMYX), rc.readBroadcast(Channels.LLSPOTTEDENEMYY));
		
			float distUR=Utility.distanceBetweenMapLocations(rc.getLocation(), upperRight);
			float distUL=Utility.distanceBetweenMapLocations(rc.getLocation(), upperLeft);
			float distLR=Utility.distanceBetweenMapLocations(rc.getLocation(), lowerRight);
			float distLL=Utility.distanceBetweenMapLocations(rc.getLocation(), lowerLeft);
		
		//If nearby target, move accordingly
		RobotInfo[] enemyRobots = Utility.checkForEnemyRobots();
		System.out.println("in general move");
		
		if(enemyRobots.length!=0){
			if (rc.getType()==RobotType.SOLDIER){
				SoldierAI.moveAroundHostiles(enemyRobots);
			}
			
			reportLocation(enemyRobots[0]);
		}
		
		//if there is a nearby target in quadrant
		
		else if(receivedNewTargetLocation && (distUR<120||distUL<120||distLR<120||distLL<120)){
			System.out.println("Printing distances to spotted");
			System.out.println(distUR);
			System.out.println(distUL);
			System.out.println(distLL);
			System.out.println(distLR);
			
				
				if(distUR<distLR && distUR<distLL && distUR<distUL){ 
					//System.out.println("moving UR");
					Utility.tryMoveToLocation(upperRight, Math.min(rc.getType().strideRadius, distUR));
				}else if(distLR<distUR && distLR<distLL && distLR<distUL){ 
					Utility.tryMoveToLocation(lowerRight, Math.min(rc.getType().strideRadius, distLR));
				}else if(distLL<distLR && distLL<distUR && distLL<distUL){ 
					Utility.tryMoveToLocation(lowerLeft, Math.min(rc.getType().strideRadius, distLL));
				}else{
					Utility.tryMoveToLocation(upperLeft, Math.min(rc.getType().strideRadius, distUL));
				}

		}	
		//in emergency, move accordingly
		//else
		
		else if (targetLocation!=null){
			Utility.tryMoveToLocation(targetLocation, Math.min(rc.getType().strideRadius, Utility.distanceBetweenMapLocations(targetLocation, rc.getLocation())));
			
			float distToTargetLoc = Utility.distanceBetweenMapLocations(targetLocation, rc.getLocation());
			 
			//close enough to have arrived, reset targetPoint (big scope)
			if(distToTargetLoc <4 ){
				targetPoint = null;
			}
			
		}else{
			Utility.moveRandom();
		}
		
		//if close to a reported location and it is empty, report empty
		if(distUR<2.1){
			rc.broadcast(Channels.URSPOTTEDENEMYX, 10000);
			rc.broadcast(Channels.URSPOTTEDENEMYY, 10000);
		}
		if(distUL<2.1){
			rc.broadcast(Channels.ULSPOTTEDENEMYX, 10000);
			rc.broadcast(Channels.ULSPOTTEDENEMYY, 10000);
		}
		if(distLR<2.1){
			rc.broadcast(Channels.LRSPOTTEDENEMYX, 10000);
			rc.broadcast(Channels.LRSPOTTEDENEMYY, 10000);
		}
		if(distLL<2.1){
			rc.broadcast(Channels.LLSPOTTEDENEMYX, 10000);
			rc.broadcast(Channels.LLSPOTTEDENEMYY, 10000);
		}
	}
	
	public static void reportLocation(RobotInfo robotInfo) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		float targetX=robotInfo.location.x;
		float targetY=robotInfo.location.y;
		
		if(targetX>centerPoint.x){//than target is right of center
			if(targetY>centerPoint.y){//than target is above center
				
				rc.broadcast(Channels.URSPOTTEDENEMYX, (int)targetX);
				rc.broadcast(Channels.URSPOTTEDENEMYY, (int)targetY);
				
			}else{ //below center
				
				rc.broadcast(Channels.LRSPOTTEDENEMYX, (int)targetX);
				rc.broadcast(Channels.LRSPOTTEDENEMYY, (int)targetY);
				
			}
		}else{ //than target is left of center
			if(targetY>centerPoint.y){//than target is above center
				
				rc.broadcast(Channels.ULSPOTTEDENEMYX, (int)targetX);
				rc.broadcast(Channels.ULSPOTTEDENEMYY, (int)targetY);
				
			}else{ //below center
				
				rc.broadcast(Channels.LLSPOTTEDENEMYX, (int)targetX);
				rc.broadcast(Channels.LLSPOTTEDENEMYY, (int)targetY);
				
			}
		}
		
	}


	public static void generalAttack(){
		
		RobotController rc = RobotPlayer.rc;
		
		//Info nearby targets
		RobotInfo[] enemyRobots = Utility.checkForEnemyRobots();
		
		
		//there are enemy robots around
		if(enemyRobots.length!=0){
			
		}else if(rc.getType()==RobotType.LUMBERJACK){ //Are there non-friendly trees instead?
			TreeInfo[] nearbyTreesNeutral=nearbyTreesNeutral = rc.senseNearbyTrees(-1, rc.getTeam().opponent());
			
		}
		
		
		
	}

}
