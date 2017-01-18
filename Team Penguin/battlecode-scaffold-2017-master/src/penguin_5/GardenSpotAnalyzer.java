package penguin_5;

import java.util.ArrayList;

import battlecode.common.*;

public class GardenSpotAnalyzer {
	
	//ArrayList<MapLocation> blacklist = new ArrayList<MapLocation>();
	
	static MapLocation[] initialArchons=null;
	
	public static void localBest() throws GameActionException{
		RobotController rc = RobotPlayer.rc;
		//if not stored archon locs yet
		if(initialArchons==null){
			initialArchons = rc.getInitialArchonLocations(rc.getTeam());
		}
		
		Direction dirToCheck = new Direction(0);
		
		int count = 0;
		int bestVal = 9999999;

		float checkRad = 8.5F;
		MapLocation checkLocation;
		MapLocation bestLoc = null;
		
		//process any blacklist broadcasts
		
		
		while(count<4){
			System.out.println("on "+count+"th time through while");
			System.out.println("checking direction: "+dirToCheck);
			
			checkLocation = rc.getLocation().add(dirToCheck, checkRad);
			

			
			//then check if location has space for full circle
			boolean validLoc = true;
			MapLocation subtractedLocation = checkLocation.subtract(dirToCheck, 1.51F);
			
			//is location on map as best we can tell?
			if(rc.canSenseLocation(subtractedLocation)){
				System.out.println("can sense location");
				System.out.println(subtractedLocation.x+","+subtractedLocation.y);
				if(!rc.onTheMap(subtractedLocation)){
					validLoc=false;
					System.out.println("Location not on map, or too close to edge");
					//processToBlacklist(checkLocation);
				}
			}
			
			//now check if it is too close to an archon
			if(validLoc){
				for(MapLocation archon:initialArchons){
					float distance = Utility.distanceBetweenMapLocations(archon, checkLocation);
					if(distance<3.1){
						validLoc=false;
					}
				}
			}
			
			if(validLoc){
				System.out.println("checking for nearby trees");
			
				//next, check its attributes in the same way as the gardeners, looking for neutral trees(r3) and friendly trees (r5)
				TreeInfo[] neutrals = rc.senseNearbyTrees(checkLocation, 5 , Team.NEUTRAL);
				TreeInfo[] friendlys = rc.senseNearbyTrees(checkLocation, 3.5F, rc.getTeam());
				TreeInfo[] notfriendlys = rc.senseNearbyTrees(checkLocation, 4, rc.getTeam().opponent());
				RobotInfo[] nearBots = rc.senseNearbyRobots(checkLocation, 4, rc.getTeam());
				
				System.out.println("Found this many friendly trees: "+friendlys.length);
				System.out.println("Found this many neutral trees: "+neutrals.length);
			
				//should have 0 friendlys
				if(friendlys.length==0){
					
					//total health of nearby trees
					int totalTreeHealth = 0;
					for(TreeInfo tree:neutrals){
						totalTreeHealth += tree.health; 
					}
					
					totalTreeHealth+=(800*notfriendlys.length);
					System.out.println("with a total health of "+totalTreeHealth);
					
					for (RobotInfo info: nearBots){
						if (info.type==RobotType.ARCHON){
							totalTreeHealth+=10000;
							System.out.println("archon found");
						}
					}
				
					if(totalTreeHealth<bestVal){
						//then we just found a new best
						System.out.println("Which is a new best");
						bestVal = totalTreeHealth;
						bestLoc = checkLocation;
					}
				}
			
			}
			count++;
			dirToCheck = dirToCheck.rotateLeftDegrees(90);
			
		}
		System.out.println("Local best: "+bestVal);
		System.out.println("Global best so far: "+rc.readBroadcast(Channels.BESTGARDENERVALUE));
		
		if(bestVal < rc.readBroadcast(Channels.BESTGARDENERVALUE)){
			//shift bests to secondbest
			//rc.broadcast(Channels.SECONDBESTX, rc.readBroadcast(Channels.BESTGARDENERX));
			//rc.broadcast(Channels.SECONDBESTY, rc.readBroadcast(Channels.BESTGARDENERY));
			
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

	/*private static void processToBlacklist(MapLocation checkLocation) throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		int currentBlackListX = rc.readBroadcast(Channels.BLACKLISTX);
		int currentBlackListY = rc.readBroadcast(Channels.BLACKLISTY);
		
		int checkX = (int) checkLocation.x;
		int checkY = (int) checkLocation.y;
		
		if(currentBlackListX==checkX && currentBlackListY==checkY){ //then this location has come back around
			//reset channels
			rc.broadcast(Channels.BLACKLISTX, 0);
			rc.broadcast(Channels.BLACKLISTY, 0);
			
		}else if(currentBlackListX==0 && currentBlackListY==0){ //then channel is empty, feel free to use
			rc.broadcast(Channels.BLACKLISTX, checkX);
			rc.broadcast(Channels.BLACKLISTY, checkY);
		}
		
	}*/
}
	
	
