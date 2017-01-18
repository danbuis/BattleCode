package penguin_4;



	import battlecode.common.*;

	public class ScoutAI {
		
		public static int count=0;
		public static MapLocation[]archons=null;
		
	    static void runScout() throws GameActionException {
	    	
	    	RobotController rc = RobotPlayer.rc;
	    	
	        System.out.println("I'm a scout!");
	        Team enemy = rc.getTeam().opponent();
	        if (archons==null){
	        	archons = rc.getInitialArchonLocations(rc.getTeam().opponent());
	        }

	        // The code you want your robot to perform every round should be in this loop
	        while (true) {
	        	try{

	            TreeInfo[] nearbyTrees = rc.senseNearbyTrees(-1,Team.NEUTRAL);
	            System.out.println("sensing this many trees: "+nearbyTrees.length);
	            
	            if(nearbyTrees.length!=0){
	            	for(TreeInfo trees: nearbyTrees){
	            		//search for one with bullets
	            		System.out.println("with "+trees.containedBullets+" bullets");
	            		if(trees.containedBullets>0){
	            			moveAndShake(trees);
	            			break;
	            		}
	            	}
	            }
	            
	            nearbyTrees = rc.senseNearbyTrees(-1, rc.getTeam().opponent());
	            RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
	            
	            hideInTrees(nearbyTrees, robots);
	            
	            Clock.yield();
	        	 } catch (Exception e) {
	                 System.out.println("Scout Exception");
	                 e.printStackTrace();
	             }
	
	        
	            
	        }
	    }

		private static void hideInTrees(TreeInfo[] nearbyTrees, RobotInfo[] robots) throws GameActionException {
			RobotController rc = RobotPlayer.rc;
			
			//if no nearbyTrees to hide in, move random for now
			
			//find safest tree
			//add up distance from all soldiers and lumberjacks
			if(nearbyTrees.length!=0){
				float[] distanceTotals = new float[nearbyTrees.length];
				
				//for each tree sensed
				for(int i=0;i<nearbyTrees.length;i++){
					
					System.out.println("Checking tree :"+nearbyTrees[i].ID);
					
					float totalDistanceForThisTree=0;
					
					//for each robot
					for(RobotInfo info:robots){
						if(info.type==RobotType.SOLDIER || info.type==RobotType.LUMBERJACK){
							System.out.println("Using tree: "+i+" "+nearbyTrees[i].ID);
							System.out.println("Using robot "+info.ID);
							totalDistanceForThisTree+=Utility.distanceBetweenMapLocations(nearbyTrees[i].location, info.location);
						}
					}
					//assign to array
					distanceTotals[i]=totalDistanceForThisTree;
					System.out.println("total safety for this tree:"+totalDistanceForThisTree);
					
				}
				
				float safetyValue = 0;
				int safestIndex=0;
				
				for(int j=0;j<distanceTotals.length;j++){
					System.out.println("inside j loop");
					if(distanceTotals[j]>safetyValue){
						System.out.println("new safest: "+distanceTotals[j]);
						safestIndex = j;
						safetyValue = distanceTotals[j];
					}
				}
				
				Utility.tryMoveToLocation(nearbyTrees[safestIndex].location, Math.min(rc.getType().strideRadius, Utility.distanceBetweenMapLocations(rc.getLocation(), nearbyTrees[safestIndex].location)));
				
			}else{
				Utility.tryMoveToLocation(archons[(count%archons.length)], Math.min(rc.getType().strideRadius, Utility.distanceBetweenMapLocations(rc.getLocation(), archons[(count%archons.length)])));
				if(Utility.distanceBetweenMapLocations(rc.getLocation(), archons[(count%archons.length)])<0.5){
					count++;
				}
			}
			//shoot at closest gardener
			
			if (robots.length!=0){
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
				
				if(target != null){
					attack(target);
				}
			}
			
			
			
		}

		private static void attack(RobotInfo target) throws GameActionException {
			RobotController rc = RobotPlayer.rc;
						
			
			if(target!=null){
				if(rc.canFireSingleShot()){
					rc.fireSingleShot(rc.getLocation().directionTo(target.location));
				}
			}else{
				if(rc.canFireSingleShot()){
				//	rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
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
	

