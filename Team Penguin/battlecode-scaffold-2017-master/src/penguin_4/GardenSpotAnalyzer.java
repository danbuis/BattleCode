package penguin_4;

import battlecode.common.*;

public class GardenSpotAnalyzer {
	
	public static void localBest() throws GameActionException{
		RobotController rc = RobotPlayer.rc;
		Direction dirToCheck = new Direction(0);
		
		int count = 0;
		int bestVal = 9999999;

		float checkRad = 7.5F;
		MapLocation checkLocation;
		MapLocation bestLoc = null;
		
		while(count<6){
			System.out.println("on "+count+"th time through while");
			System.out.println("checking direction: "+dirToCheck);
			
			checkLocation = rc.getLocation().add(dirToCheck, checkRad);
			
			//first check if location has space for full circle
			boolean validLoc = true;
			MapLocation subtractedLocation = checkLocation.subtract(dirToCheck, 3);
			
			if(rc.canSenseLocation(subtractedLocation)){
				System.out.println("can sense location");
				System.out.println(subtractedLocation.x+","+subtractedLocation.y);
				if(!rc.onTheMap(subtractedLocation)){
					validLoc=false;
					System.out.println("Location not on map, or too close to edge");
				}
			}
			
			if(validLoc){
				System.out.println("checking for nearby trees");
			
				//next, check its attributes in the same way as the gardeners, looking for neutral trees(r3) and friendly trees (r5)
				TreeInfo[] neutrals = rc.senseNearbyTrees(checkLocation, 5 , Team.NEUTRAL);
				TreeInfo[] friendlys = rc.senseNearbyTrees(checkLocation, 5.5F, rc.getTeam());
				
				System.out.println("Found this many friendly trees: "+friendlys.length);
				System.out.println("Found this many neutral trees: "+neutrals.length);
			
				//should have 0 friendlys
				if(friendlys.length==0){
					
					//total health of nearby trees
					int totalTreeHealth = 0;
					for(TreeInfo tree:neutrals){
						totalTreeHealth += tree.health; 
					}
					
					System.out.println("with a total health of "+totalTreeHealth);
				
					if(totalTreeHealth<bestVal){
						//then we just found a new best
						System.out.println("Which is a new best");
						bestVal = totalTreeHealth;
						bestLoc = checkLocation;
					}
				}
			
			}
			count++;
			dirToCheck = dirToCheck.rotateLeftDegrees(60);
			
		}
		System.out.println("Local best: "+bestVal);
		System.out.println("Global best so far: "+rc.readBroadcast(Channels.BESTGARDENERVALUE));
		
		if(bestVal < rc.readBroadcast(Channels.BESTGARDENERVALUE)){
			System.out.println("broadcasting new bests");
			rc.broadcast(Channels.BESTGARDENERVALUE, bestVal);
			System.out.println("new global");
			rc.broadcast(Channels.BESTGARDENERX, (int)(bestLoc.x*1000));
			rc.broadcast(Channels.BESTGARDENERY, (int)(bestLoc.y*1000));
			System.out.println("bestLocX = "+bestLoc.x);
			System.out.println("broadcast as.. "+rc.readBroadcast(Channels.BESTGARDENERX));
		}
		
		rc.setIndicatorLine(rc.getLocation(), bestLoc, 0, 255, 0);
	}
}
	
	
