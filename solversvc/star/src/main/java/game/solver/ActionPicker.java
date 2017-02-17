package game.solver;

import game.solver.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by C5241628 on 2/16/2017.
 */
@Component
public class ActionPicker {
    Logger logger = LoggerFactory.getLogger(ActionPicker.class);
    public Action pick(Map<Wall, Group> actionsMap, WallWeight oldWeight) {
        List<Wall> walls = new ArrayList<Wall>();
        for(Wall wall : actionsMap.keySet()) {
            simplifyWall(wall);
            walls.add(wall);
        }

        Collections.sort(walls, (Wall o1, Wall o2) -> {
            WallWeight w1 = o1.getWeight();
            WallWeight w2 = o2.getWeight();

            int val = 0; //w1.getMaxGroupSize() - w2.getMaxGroupSize();

            if (val == 0) {
                val = (int) (w2.getComplexity() - w1.getComplexity());
            }

            if (val == 0) {
                val = w2.getBricksNum() - w1.getBricksNum();
            }
            return val * -1;
        });

        Action action = new Action();
        action.wall = walls.get(0);
        action.group = actionsMap.get(action.wall);
        return action;
    }

    void simplifyWall(Wall image) {

        List<Brick> singleBricks = image.getWeight().getSingleBricks();

        Map<Character, List<Brick>> cache = new HashMap<>();
        for(Brick b : singleBricks) {
            List<Brick> list = cache.get(b.ch);
            if(list == null) {
                 list = new ArrayList<>();
            }
            list.add(b);
        }

        long totalDist = 0;
        for(List<Brick> list : cache.values()) {
            list.sort((Brick a, Brick b) -> {
               return a.x - b.x;
            });

            long dist = 0;
            for(int i = 0; i < list.size() - 1; i++) {
                Brick a = list.get(i);
                Brick b = list.get(i + 1);
                int xDist = Math.abs(a.x - b.x);
                int yDist = Math.abs(a.y - b.y);
                dist += xDist * xDist + yDist;

            }
            totalDist += dist * dist;
        }

        image.getWeight().setComplexity(totalDist);
    }
}
