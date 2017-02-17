package game.solver.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by C5241628 on 2/13/2017.
 */
public class WallWeight {
    Group maxGroup;
    int maxGroupSize;
    int signleBricks;
    int bricksNum;

    public WallWeight() {
    }

    public WallWeight(WallWeight weight) {
        this.maxGroupSize = weight.maxGroupSize;
        this.signleBricks = weight.signleBricks;
    }

    public void calculate(Wall wall) {
        signleBricks = 0;
        bricksNum = 0;

    	List<Group> groups = wall.group();
    	Collections.sort(groups, (Group a, Group b) -> b.bricks.size() - a.bricks.size());
        maxGroup = groups.get(0);
        maxGroupSize = maxGroup.bricks.size();
        for (Group ng : groups) {
            bricksNum += ng.bricks.size();

            if (ng.bricks.size() == 1) {
                signleBricks++;
            }
        }
    }

    public int getMaxGroupSize() {
        return maxGroupSize;
    }

    public int getBricksNum() {
        return bricksNum;
    }

    public int getSignleBricks() {
        return signleBricks;
    }

    public String toString() {
        return "maxGroupSize: " + maxGroupSize + ", signleBricks: " + signleBricks + ", bricksNum" + bricksNum;
    }

}
