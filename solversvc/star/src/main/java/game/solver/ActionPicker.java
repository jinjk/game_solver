package game.solver;

import game.solver.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
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
            simplifyWall(wall);
            walls.add(wall);
        }

        Collections.sort(walls, (Wall o1, Wall o2) -> {
            WallWeight w1 = o1.getWeight();
            WallWeight w2 = o2.getWeight();

            int val = 0; //w1.getMaxGroupSize() - w2.getMaxGroupSize();

            if (val == 0) {
                val = w2.getComplexity() - w1.getComplexity();
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
        Wall copy = image.copy();
        Wall wall = new Wall();
        wall.width = copy.width;
        wall.height = copy.height;

        List<Brick> singleBricks = copy.getWeight().getSingleBricks();

        SortedMap<Integer, Column> columns = new TreeMap<>();
        for(Brick b : singleBricks) {
            Column c = columns.get(b.getX());
            if(c == null) {
                c = new Column();
                columns.put(b.getX(), c);
                wall.columns.add(c);
            }
            c.bricks.add(b);
        }

        int x = 0;
        for(Integer key : columns.keySet()) {
            Column c = columns.get(key);
            c.bricks.sort((Brick b1, Brick b2) -> {
                return b1.getY() - b2.getY();
            });

            int size = c.bricks.size();
            Iterator<Brick> iter = c.bricks.iterator();
            for(int y = copy.getHeight() - size; y < copy.getHeight(); y++) {
                Brick b = iter.next();
                b.setY(y);
                b.setX(x);
            }
            x++;
            wall.columns.add(c);
        }

        int complexity = wall.getWeight().getGroups().size() + wall.getWeight().getSingleBricksNum() * 10;
        image.getWeight().setComplexity(complexity);
    }
}
