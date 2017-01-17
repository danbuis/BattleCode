package penguin_3;

import battlecode.common.*;

public class ScoutAI {
	
    static void runScout() throws GameActionException {
    	RobotController rc = RobotPlayer.rc;
    	
        System.out.println("I'm a scout!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            TreeInfo[] nearbyTrees = rc.senseNearbyTrees(-1,Team.NEUTRAL);
            
            for(TreeInfo trees: nearbyTrees){
            	//search for one with bullets
            	if(trees.containedBullets<0){
            		moveAndShake(trees);
            		break;
            	}
            }
            
            nearbyTrees = rc.senseNearbyTrees(-1, rc.getTeam().opponent());
            RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            
            hideInTrees(nearbyTrees, robots);
            
        }
    }

	private static void hideInTrees(TreeInfo[] nearbyTrees, RobotInfo[] robots) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
		//find safest tree
		//add up distance from all soldiers and lumberjacks
		
		float[] distanceTotals = new float[nearbyTrees.length];
		
		//for each tree sensed
		for(int i=0;i<nearbyTrees.length;i++){
			
			float totalDistanceForThisTree=0;
			
			//for each robot
			for(RobotInfo info:robots){
				if(info.type==RobotType.SOLDIER || info.type==RobotType.SOLDIER){
					totalDistanceForThisTree+=Utility.distanceBetweenMapLocations(rc.getLocation(), info.location);
				}
			}
			//assign to array
			distanceTotals[i]=totalDistanceForThisTree;
			
		}
		
		float safetyValue = 9999999;
		int safestIndex=0;
		
		for(int i=0;i<distanceTotals.length;i++){
			if(distanceTotals[i]<safetyValue){
				safestIndex = i;
				safetyValue = distanceTotals[i];
			}
		}
		
		Utility.tryMoveToLocation(nearbyTrees[safestIndex].location, Math.min(rc.getType().strideRadius, Utility.distanceBetweenMapLocations(rc.getLocation(), nearbyTrees[safestIndex].location)));
		
		//shoot at closest gardener
		
		if (robots.length!=0) attack(robots);
		
		
		
	}

	private static void attack(RobotInfo[] robots) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
		float closestDist = 99999;
		RobotInfo target =  null;
		
		for(RobotInfo info:robots){
			if(info.type==RobotType.GARDENER){
				if(Utility.distanceBetweenMapLocations(rc.getLocation(), info.location)<closestDist){
					target = info;
					closestDist = Utility.distanceBetweenMapLocations(rc.getLocation(), info.location);
				}
			}
		}
		
		if(target!=null){
			if(rc.canFireSingleShot()){
				rc.fireSingleShot(rc.getLocation().directionTo(target.location));
			}
		}else{
			if(rc.canFireSingleShot()){
				rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
			}
		}
		
	}

	private static void moveAndShake(TreeInfo trees) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
		Utility.tryMoveToLocation(trees.location, 2.5F);
		if(rc.canShake(trees.location)){
			rc.shake(trees.location);
		}
		
	}

}
