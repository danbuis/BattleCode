package penguin_3;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                ArchonAI.runArchon();
                break;
            case GARDENER:
                GardenerAI.runGardener();
                break;
            case SOLDIER:
                SoldierAI.runSoldier();
                break;
            case LUMBERJACK:
                LumberjackAI.runLumberjack();
                break;
            case TANK:
            	TankAI.runTank();
            	break;
            case SCOUT:
            	//TODO
            	break;
        }
        
        
        
     
	}


        


    

    
}