package penguin_1;

import battlecode.common.*;

public class GardenerAI {
	
    static int backAndForthBounces = 0;
    static int homeEdge=0;
    static Boolean atHomeEdge=true; //TODO fix this!

	public static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");
        
        RobotController rc = RobotPlayer.rc;



        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	//role call for census
            	if (rc.getRoundNum()%50==1){
            		int currentGardeners = rc.readBroadcast(2);
            		currentGardeners++;
            		rc.broadcast(2, currentGardeners);
            		//System.out.println("Here: "+currentGardeners);
            	}
            	
            	if (homeEdge==0){
            		int edge=rc.readBroadcast(0);
            		if(edge!=0){
            			homeEdge = edge;
            		}
            	}
                
                

                // towards home edge, once there bounce back and forth
                if (!atHomeEdge){
                	// TODO moveTowardsEdge();
                }else{
                	moveBackandForth();
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

	private static void moveBackandForth() {
		RobotController rc = RobotPlayer.rc;
		try {
			if(homeEdge%2==0){
				if(backAndForthBounces%2==0){
					if(rc.canMove(Direction.getEast())){
						rc.move(Direction.getEast());
					}else{backAndForthBounces++;}
				}else{
					if(rc.canMove(Direction.getWest())){
						rc.move(Direction.getWest());
					}else{backAndForthBounces++;}
				}
			}else{
				if(backAndForthBounces%2==0){
					if(rc.canMove(Direction.getNorth())){
						rc.move(Direction.getNorth());
					}else{backAndForthBounces++;}
				}else{
					if(rc.canMove(Direction.getSouth())){
						rc.move(Direction.getSouth());
					}else{backAndForthBounces++;}
				}
			}
		}
		catch (Exception e) {
            System.out.println("Gardener Exception");
            e.printStackTrace();
        }
		
		maintenance();
		
	}
/* TODO
	private static void moveTowardsEdge() {
		RobotController rc = RobotPlayer.rc;
		if(homeEdge!=0){
			if(homeEdge==1){ //headEast
				if (rc.canMove(Direction.getEast(), 4)){
					try {
						rc.move(Direction.getEast(), 1);
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					atHomeEdge=true;
				}
			}else if(homeEdge==2){ //headNorth
				System.out.println("Trying North");
				if (rc.canMove(Direction.getNorth(), 4)){
					try {
						rc.move(Direction.getNorth(), 1);
						System.out.println("headingNorth");
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					System.out.println("edge reached");
					atHomeEdge=true;
				}
			}else if(homeEdge==3){ //headWest
				if (rc.canMove(Direction.getWest(), 4)){
					try {
						rc.move(Direction.getWest(), 1);
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					atHomeEdge=true;
				}
			}else if(homeEdge==4){ //headSouth
				if (rc.canMove(Direction.getSouth(), 4)){
					try {
						rc.move(Direction.getSouth(), 1);
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					atHomeEdge=true;
				}
			}
			
		}
		
	}*/

	
	/*Handles things like unit spawn, planting and watering
	 * 
	 */
	private static void maintenance() {
		RobotController rc = RobotPlayer.rc;
		Boolean plantTree = false;
		Boolean buildSoldier = false;
		Boolean buildLumberjack=false;
		
		//plant a tree
        if (Math.random()<0.03){
        	plantTree=true;
        }
        
       //Attempt unit builds
        if(Math.random()<0.02){
        	buildSoldier=true;
        }else if (Math.random() < 0.01){
        	buildLumberjack = true;
        }
        
        try {
		
        	if (homeEdge == 1){
			
				if(plantTree && rc.canPlantTree(Direction.getEast())){
					rc.plantTree(Direction.getEast());
				}
				if(buildSoldier && rc.canBuildRobot(RobotType.SOLDIER, Direction.getWest())){
					rc.buildRobot(RobotType.SOLDIER, Direction.getWest());
				}
				else if(buildLumberjack &&rc.canBuildRobot(RobotType.LUMBERJACK, Direction.getWest())){
					rc.buildRobot(RobotType.LUMBERJACK, Direction.getWest());
				}
			
			}else if (homeEdge==2){
				if(plantTree && rc.canPlantTree(Direction.getNorth())){
					rc.plantTree(Direction.getNorth());
				}
				if(buildSoldier && rc.canBuildRobot(RobotType.SOLDIER, Direction.getSouth())){
					rc.buildRobot(RobotType.SOLDIER, Direction.getSouth());
				}
				else if(buildLumberjack &&rc.canBuildRobot(RobotType.LUMBERJACK, Direction.getSouth())){
					rc.buildRobot(RobotType.LUMBERJACK, Direction.getSouth());
				}
			}else if (homeEdge==3){
				if(plantTree && rc.canPlantTree(Direction.getWest())){
					rc.plantTree(Direction.getWest());
				}
				if(buildSoldier && rc.canBuildRobot(RobotType.SOLDIER, Direction.getEast())){
					rc.buildRobot(RobotType.SOLDIER, Direction.getEast());
				}
				else if(buildLumberjack &&rc.canBuildRobot(RobotType.LUMBERJACK, Direction.getEast())){
					rc.buildRobot(RobotType.LUMBERJACK, Direction.getEast());
				}
			}else{
				if(plantTree && rc.canPlantTree(Direction.getSouth())){
					rc.plantTree(Direction.getSouth());
				}
				if(buildSoldier && rc.canBuildRobot(RobotType.SOLDIER, Direction.getNorth())){
					rc.buildRobot(RobotType.SOLDIER, Direction.getNorth());
				}
				else if(buildLumberjack &&rc.canBuildRobot(RobotType.LUMBERJACK, Direction.getNorth())){
					rc.buildRobot(RobotType.LUMBERJACK, Direction.getNorth());
				}
			}
        	
        	//see if a nearby tree needs watering
        	TreeInfo[] nearTrees = rc.senseNearbyTrees((float)3.5);
        	for(TreeInfo info:nearTrees){
        		if(info.health<90 && rc.canWater(info.ID)){
        			rc.water(info.ID);
        		}
        	}
        	
        } catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
