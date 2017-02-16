package game.solver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by C5241628 on 2/13/2017.
 */
public class WallWeight {
    Group maxGroup;
    List<Group> groups;
    int maxGroupSize;
    int singleBricksNum;
    List<Brick> singleBricks = new ArrayList<>();
    int bricksNum;
    int complexity;

    public WallWeight() {
    }

    public WallWeight(WallWeight weight) {
        this.maxGroupSize = weight.maxGroupSize;
        this.singleBricksNum = weight.singleBricksNum;
    }

    public void calculate(Wall wall) {
        singleBricksNum = 0;
        bricksNum = 0;

    	groups = wall.group();
    	Collections.sort(groups, (Group a, Group b) -> b.bricks.size() - a.bricks.size());
        maxGroup = groups.get(0);
        maxGroupSize = maxGroup.bricks.size();
        for (Group ng : groups) {
            bricksNum += ng.bricks.size();
            if (ng.bricks.size() == 1) {
                singleBricks.add(ng.bricks.iterator().next());
                singleBricksNum++;
            }
        }
    }

    public int getMaxGroupSize() {
        return maxGroupSize;
    }

    public int getSingleBricksNum() {
        return singleBricksNum;
    }

    public int getBricksNum() {
        return bricksNum;
    }

    public List<Brick> getSingleBricks() {
        return singleBricks;
    }

    public String toString() {
        return "maxGroupSize: " + maxGroupSize + ", singleBricksNum: " + singleBricksNum + ", bricksNum" + bricksNum;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public List<Group> getGroups() {
        return groups;
    }
}
