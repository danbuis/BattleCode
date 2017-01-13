package penguin_2;

import battlecode.common.*;

public strictfp class Utility {
	
	/**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }
    
    static boolean tryMoveToLocation(int x, int y) throws GameActionException{
    	RobotController rc = RobotPlayer.rc;
    	MapLocation mapLoc = new MapLocation((x/1000), (y/1000));
    	return tryMoveToLocation(mapLoc, rc.getType().strideRadius);
    }
    
    /**Moves toward a given MapLocation
     * @throws GameActionException 
     * 
     */
    
 
    
    static boolean tryMoveToLocation(MapLocation loc, float speed) throws GameActionException{
    	RobotController rc = RobotPlayer.rc;
    	Direction dir = rc.getLocation().directionTo(loc);
    	return tryMove(dir,speed);
    }

    /**
     * try to move to a target Y value.  
     * @param targetY
     * @return
     * @throws GameActionException 
     */
    static void tryMoveVertical(float targetY) throws GameActionException{
    	RobotController rc = RobotPlayer.rc;
    	float currentY = rc.getLocation().y ;
    	float diffY = currentY-targetY;
    	
    	System.out.println("currentY: "+currentY);
    	System.out.println("targetY: "+targetY);
    	System.out.println("diffY: "+diffY);
    	
    	float currentX = rc.getLocation().x;
    	
    	MapLocation targetLoc = new MapLocation(currentX, targetY);
    	Direction directionToTravel = new Direction(rc.getLocation(),targetLoc);
    	float degreesToNorth = directionToTravel.degreesBetween(Direction.getNorth());
    	
    	System.out.println(degreesToNorth);
    	
    	//if need to move North
    	if (Math.abs(degreesToNorth)<90){
    		System.out.println("moving north vert");
    		if(Math.abs(diffY)<1){ //if diff greater than 1 (default stride)
    			 tryMove(Direction.getNorth(), rc.getType().strideRadius);
    		}else{ //diff less than 1
    			float remainY = Math.abs(diffY);
    			if(rc.canMove(Direction.getNorth(), remainY)){
    				rc.move(Direction.getNorth(), remainY);
    				}
    			}
    		
    	}else{ //we need to move south
    		System.out.println("moving south vert");
    		if(Math.abs(diffY)>1){ //if diff greater than 1 (default stride)
    			tryMove(Direction.getSouth(), rc.getType().strideRadius);
    		}else{ //diff less than 1
    			float remainY = Math.abs(diffY);
    			if(rc.canMove(Direction.getSouth(), remainY)){
    				rc.move(Direction.getSouth(), remainY);
    			}
    		}
    	}
    }
    
    /**
     * try to move to a target X value.  
     * @param targetX
     * @return
     * @throws GameActionException 
     */
    static void tryMoveHorizontal(float targetX) throws GameActionException{
    	RobotController rc = RobotPlayer.rc;
    	float currentX = rc.getLocation().x ;
    	float diffX = currentX-targetX;
    	
    	System.out.println("currentX: "+currentX);
    	System.out.println("targetX: "+targetX);
    	System.out.println("diffX: "+diffX);
    	
    	//if need to move West
    	if (diffX>0){
    		if(Math.abs(diffX)>1){ //if diff greater than 1 (min stride)
    			 tryMove(Direction.getWest(), rc.getType().strideRadius);
    		}else{ //diff less than 1
    			float remainX = Math.abs(diffX);
    			if(rc.canMove(Direction.getWest(), remainX)){
    				rc.move(Direction.getWest(), remainX);
    				}
    			}
    		
    	}else{ //we need to move south
    		if(Math.abs(diffX)>1){ //if diff greater than 1 (default stride)
    			tryMove(Direction.getEast(), rc.getType().strideRadius);
    		}else{ //diff less than 1
    			float remainX = Math.abs(diffX);
    			if(rc.canMove(Direction.getEast(), remainX)){
    				rc.move(Direction.getEast(), remainX);
    			}
    		}
    	}
    }
    
    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float speed) throws GameActionException {
        return tryMove(dir,(float) 22.5,5, speed);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    public static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide, float speed) throws GameActionException {

    	RobotController rc = RobotPlayer.rc;
    	
        // First, try intended direction
        if (rc.canMove(dir) && !rc.hasMoved()) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(!rc.hasMoved() && rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(!rc.hasMoved() && rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
       
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
    	
    	RobotController rc = RobotPlayer.rc;
    	
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
    
    /**
     * uses pythagorean to determine distance
     * @param loc1
     * @param loc2
     * @return
     */
    
    static float distanceBetweenMapLocations(MapLocation loc1, MapLocation loc2){
    	//traditional pythagorean
    	float distanceX = loc1.x-loc2.x;
    	float distanceY = loc1.y-loc2.y;
    	
    	float totalSquared = (distanceX*distanceX)+(distanceY*distanceY);
    	
    	return (float) Math.sqrt(totalSquared);
    	
    }
    
	
	public static RobotInfo[] checkForEnemyRobots(){
		RobotController rc = RobotPlayer.rc;
		Team enemy = rc.getTeam().opponent();
		RobotInfo[] info = rc.senseNearbyRobots(-1, enemy);
		
		return info;
	}
	
	public static MapLocation getLocationWithDistanceFromTarget(Direction directionToTarget, MapLocation targetLocation, float desiredDistance){
		return targetLocation.subtract(directionToTarget,desiredDistance);
	}

}
