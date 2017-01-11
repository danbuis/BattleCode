package penguin_1;

import battlecode.common.*;

public class GardenerAI {
	
    static int backAndForthBounces = 0;
    static int homeEdge=0;
    
    static Boolean atGardenLine=false;
    static Boolean homeEdgeFound=false;
    static float gardenLineValue=-5;
    

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
            	if (!homeEdgeFound){
            		moveTowardsEdge();
            	}
            	if (!atGardenLine){
                	moveTowardsGardenLine();
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
	/**
	 * process to move towards gardenline, in preparation for going back and forth.  Remember that float values will almost never be equal, so we have to get close.
	 * @throws GameActionException
	 */

	private static void moveTowardsGardenLine() throws GameActionException {
		RobotController rc = RobotPlayer.rc;
		if(gardenLineValue>0){ //value has been set already when move to edge executed initially, or in the shortcut clause
			System.out.println("moving to garden line");
			if(homeEdge%2==0){//if moving north/south, execute move
				System.out.println("moving vertical");
				Utility.tryMoveVertical(gardenLineValue);
			}else{
				System.out.println("moving horizontal");
				Utility.tryMoveHorizontal(gardenLineValue);
			}
		}
		
		//after move, check if we are on the line
		if(homeEdge%2==0){
			if(Math.abs(gardenLineValue-rc.getLocation().y)<0.0001){
				atGardenLine=true;
				System.out.println("reached garden line:" + rc.getLocation().y);
				rc.broadcast(26, (int)(rc.getLocation().y*1000000));
			}
		}else{
			if(Math.abs(gardenLineValue-rc.getLocation().x)<0.001){
				atGardenLine=true;
				rc.broadcast(26, (int)(rc.getLocation().x*1000000));
			}
		}
		
	}

	
	/**
	 * main method for gardeners work.  They move back and forth, bouncing off one 
	 * another, watering, planting, and building
	 */
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

	/**
	 * Moves robot towards player edge to find value of home edge
	 */
	
	private static void moveTowardsEdge() {
		try{
		RobotController rc = RobotPlayer.rc;
		System.out.println("home edge = :"+homeEdge);
		
			//short cut below so that if home edge value already found we can bypass first of sequence of ifs
		if(rc.readBroadcast(26)>0){  //if garden line already found  *Should be independant of orientation here.
			gardenLineValue = (float)(rc.readBroadcast(26)/1000000.0);
			homeEdgeFound=true;
			System.out.println("shortcut to garden line, Value: "+gardenLineValue);
		}
			System.out.println("homeEdgeFound: "+homeEdgeFound);
			
		//if a home edge has been determined and it hasn't already been located
		if(homeEdge!=0 && !homeEdgeFound){
			if(homeEdge==1){ //headEast
				//System.out.println("accidentally east");
				if (Utility.tryMove(Direction.getEast())){

						//rc.move(Direction.getEast(), 1);
				}else{
					//System.out.println("E");
					homeEdgeFound=true;
					gardenLineValue = (float) (rc.getLocation().x-2.1);
				}
			}else if(homeEdge==2){ //headNorth
				//System.out.println("Trying North");
				if (Utility.tryMove(Direction.getNorth())){
					
						//rc.move(Direction.getNorth(), 1);
						System.out.println("headingNorth"); 
				}else{
					System.out.println("edge reached: "+rc.getLocation().y);
					homeEdgeFound=true;
					gardenLineValue = (float) (rc.getLocation().y+2.1);
					System.out.println("gardenLineValue set to:"+gardenLineValue);
				}
			}else if(homeEdge==3){ //headWest
				//System.out.println("accidentally west");
				if (Utility.tryMove(Direction.getWest())){
				
						//rc.move(Direction.getWest(), 1);
				}else{
					homeEdgeFound=true;
					gardenLineValue = (float) (rc.getLocation().x+2.1);
				}
			}else if(homeEdge==4){ //headSouth
				System.out.println("checking south");
				if (Utility.tryMove(Direction.getSouth())){
					System.out.println("headingSouth"); 
						//rc.move(Direction.getSouth(), 1);
				}else{
					System.out.println("edge reached: "+rc.getLocation().y);
					homeEdgeFound=true;
					gardenLineValue = (float) (rc.getLocation().y-2.1);
					System.out.println("gardenLineValue set to:"+gardenLineValue);

				}
			}
		}
		
			System.out.println("pre-broadcast homeEdgeFound:"+homeEdgeFound);
			//if found edge and it isn't set yet, set value here
			if (homeEdgeFound && rc.readBroadcast(25)==0){
				rc.broadcast(25, 1);
				if (homeEdge%2==0){ //if home edge is north or south, use the Y-coord
					if(homeEdge==2){
						rc.broadcast(22,(int)(rc.getLocation().y*1000));
						System.out.println("broadcasting: "+ (int)(rc.getLocation().y*1000));
					}else{
						rc.broadcast(24,  (int)rc.getLocation().y*1000);
					}
				}else{
					if(homeEdge==1){
						rc.broadcast(21, (int)rc.getLocation().x*1000);
					}else{
						rc.broadcast(23, (int)rc.getLocation().x*1000);
					}
				}
			}
		}
		
			
	
			
			
			catch (Exception e) {
				System.out.println("Gardener Exception");
				e.printStackTrace();
			}
			
		}
		


	
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
