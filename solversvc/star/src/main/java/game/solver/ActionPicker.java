package game.solver;

import game.solver.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by C5241628 on 2/16/2017.
 */
@Component
public class ActionPicker {
    Logger logger = LoggerFactory.getLogger(ActionPicker.class);

    public List<Action> pick(Map<Wall, Group> actionsMap, WallWeight oldWeight) {
        List<Wall> walls = new ArrayList<Wall>();
        for(Wall wall : actionsMap.keySet()) {
            simplifyWallLoop(wall);
            walls.add(wall);
        }

        Collections.sort(walls, (Wall o1, Wall o2) -> {
            WallWeight w1 = o1.getWeight();
            WallWeight w2 = o2.getWeight();

            int val = 0; //w1.getMaxGroupSize() - w2.getMaxGroupSize();

            if (val == 0) {
                double v = w2.getComplexity() - w1.getComplexity();
                if(v > 0) {
                    val = 1;
                }
                else if(v < 0) {
                    val = -1;
                }
                else {
                    val = 0;
                }
            }

            return val * -1;
        });
        List<Action> actions = new ArrayList<>();
        double cmpx = walls.get(0).getWeight().getComplexity();
        for(Wall w : walls) {
            if(w.getWeight().getComplexity() == cmpx) {
                Action action = new Action();
                action.wall = walls.get(0);
                action.group = actionsMap.get(action.wall);
                actions.add(action);
            }
        }

        return actions;
    }

    void simplifyWallLoop(Wall image) {
        Wall wall = image;
        do {
            wall = simplifyWall(wall);
        } while(wall.getWeight().getGroups().size() < wall.getWeight().getBricksNum());

        List<Double> colLenList = new ArrayList<>();
        for(Column c : wall.columns) {
            colLenList.add((double) c.bricks.size());
        }
        double array[] = new double[colLenList.size()];
        for(int i = 0; i < colLenList.size(); i++) {
            array[i] = colLenList.get(i);
        }
        Statistics st = new Statistics(array);

        image.getWeight().setComplexity(st.getVariance());

    }

    Wall simplifyWall(Wall image) {
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

        return wall;

    }
}
