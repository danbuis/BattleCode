package penguin_3;

import battlecode.common.*;

public strictfp class LumberjackAI {
	
	static MapLocation targetNeutralTree = null;
	static int treeCountDown = 130;

	
	static void runLumberjack() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
        System.out.println("I'm a lumberjack!");
 

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	//Utility.checkForNearbyTrees();
            	
            	//do I already have a tree target?
            	System.out.println("Do I have a target: "+ targetNeutralTree==null);
            	if(targetNeutralTree == null){
            		//if not, try to read one in
            		System.out.println("nope, let's get one");
            		if(rc.readBroadcast(Channels.NEUTRALTREEX)!=0){
            			//we have one stored globally
            			float targetTreeX = rc.readBroadcast(Channels.NEUTRALTREEX)/1000;
            			float targetTreeY = rc.readBroadcast(Channels.NEUTRALTREEY)/1000;
            			
            			System.out.println(targetTreeX+" , "+targetTreeY);
            			targetNeutralTree = new MapLocation(targetTreeX, targetTreeY);
            			
            			//reset channels
            			rc.broadcast(Channels.NEUTRALTREEX, 0);
            			rc.broadcast(Channels.NEUTRALTREEY, 0);
            		}
            	}else{
            		//decrement timer
            		treeCountDown--;
            		if (treeCountDown==0){
            			targetNeutralTree=null;
            		}
            	}
            	
            	//now decide to move towards enemy or tree
            	RobotInfo[] robots = rc.senseNearbyRobots(4, rc.getTeam().opponent());
            	
            	//no nearby enemies
            	if(robots.length==0){
        			LumberjackMove();
            	}else{lumberjackAttack();}
              

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }


	private static void LumberjackMove() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		
		//if no target, jitter  TODO
		if (targetNeutralTree==null){
			Utility.tryMove(Utility.randomDirection(),1);
		}else{ //if target, move towards it
			float distToTree = Utility.distanceBetweenMapLocations(targetNeutralTree, rc.getLocation());
			Utility.tryMoveToLocation(targetNeutralTree, Math.min(rc.getType().strideRadius, distToTree));
			
			//did we arrive?
			if(distToTree<1.4){
				//yes we did, reset targetNeutralTree value
				targetNeutralTree=null;
				treeCountDown=130;
			}else{
				attackNeutralTree();
			}
		}
	
		
		
	}


	private static void lumberjackAttack() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		 // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
        RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam().opponent());

        if(robots.length > 0 && !rc.hasAttacked()) {
            // Use strike() to hit all nearby robots!
            rc.strike();
        } else {
            // No close robots, so search for robots within sight radius
            robots = rc.senseNearbyRobots(4,rc.getTeam().opponent());

            // If there is a robot, move towards it
            if(robots.length > 0 && !rc.hasMoved()) {
                MapLocation myLocation = rc.getLocation();
                MapLocation enemyLocation = robots[0].getLocation();
                Direction toEnemy = myLocation.directionTo(enemyLocation);

                Utility.tryMove(toEnemy, rc.getType().strideRadius);
            	} 
            }
        }	
	


	private static void attackNeutralTree() {
		RobotController rc = RobotPlayer.rc;
		
		try{
			//check if neutral trees in range
			TreeInfo[] trees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS,Team.NEUTRAL);
			//if we found any
			if(trees.length!=0){
				//if we found only 1
				if(trees.length==1){
					//try to chop it
					if(rc.canChop(trees[0].location)){
						rc.chop(trees[0].location);
					}
					//if we found more than 1
				}else if(trees.length>1){
					//check how many friendlys are nearby
					RobotInfo[] friendlyRobots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam());
					//no friendlys, strike everything you can reach
					if (friendlyRobots.length==0 && rc.canStrike()){
						rc.strike();
					} else {
						//find the closest one
						float closestDist = 1000;
						TreeInfo closestTree = trees[0];
	
						for(TreeInfo testTree: trees){
							float distanceAway = Utility.distanceBetweenMapLocations(testTree.location, rc.getLocation());
							if(distanceAway < closestDist){
								closestDist = distanceAway;
								closestTree = testTree;
							}
						}
						rc.chop(closestTree.ID);
					}
				}
			}
		}catch (Exception e) {
            System.out.println("Lumberjack Exception");
            e.printStackTrace();
        }
    }
		
}
