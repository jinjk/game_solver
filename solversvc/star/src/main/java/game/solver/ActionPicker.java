package game.solver;

import game.solver.model.Action;
import game.solver.model.Group;
import game.solver.model.Wall;
import game.solver.model.WallWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by C5241628 on 2/16/2017.
 */
@Component
public class ActionPicker {
    Logger logger = LoggerFactory.getLogger(ActionPicker.class);
    public Action pick(Map<Wall, Group> actionsMap, WallWeight oldWeight) {
        List<Wall> walls = new ArrayList<Wall>();
        for(Wall wall : actionsMap.keySet()) {
            walls.add(wall);
        }

//        List<Wall> filtedWalls = walls.stream().filter(wall ->
//                wall.getWeight().getMaxGroupSize() >= oldWeight.getMaxGroupSize()).collect(Collectors.toList());
//
//        if (filtedWalls.size() > 0) {
//
//            Collections.sort(filtedWalls, (Wall o1, Wall o2) -> {
//                WallWeight w1 = o1.getWeight();
//                WallWeight w2 = o2.getWeight();
//
//                int val =  w2.getBricksNum() - w1.getBricksNum();
//
//                return val * -1;
//            });
//
//            walls = filtedWalls;
//        } else {
            Collections.sort(walls, (Wall o1, Wall o2) -> {
                WallWeight w1 = o1.getWeight();
                WallWeight w2 = o2.getWeight();

                int val = 0; //w1.getMaxGroupSize() - w2.getMaxGroupSize();

                if (val == 0) {
                    val = w2.getSignleBricks() - w1.getSignleBricks();
                }

                if (val == 0) {
                    val = w2.getBricksNum() - w1.getBricksNum();
                }
                return val * -1;
            });
//        }


        Action action = new Action();
        if(walls.size() > 1) {
            logger.debug(walls.get(1).getWeight().toString());
            logger.debug(walls.get(0).getWeight().toString());
        }
        else {
            logger.debug(String.valueOf(walls.size()));

        }
        action.wall = walls.get(0);
        action.group = actionsMap.get(action.wall);
        return action;
    }
}
