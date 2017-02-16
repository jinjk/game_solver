package game.solver;

import game.solver.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
@Component
public class Solver {
    public static final int WEIGHT_OF_SINGLE_UNIT = 100;
    public static final int PREDICTION_LENGTH = 0;
    static Logger logger = LoggerFactory.getLogger(Solver.class);

    @Autowired
    ActionPicker actionPicker;

    public List<Wall> execute(List<List<Integer>> mat) {
        Wall wall = readMat(mat);
        return execute(wall);
    }


    public List<Wall> execute(Wall wall) {
        List<Wall> result = new ArrayList<Wall>();

        Action status = null;
        do {
            Wall old = wall;
            status = wipeOut(wall, PREDICTION_LENGTH);
            if (status != null && status.group != null)
                status.group.mark();

            result.add(old);
            printWall(old);
            if(status != null)
                wall = status.wall;
        } while (status != null);

        return result;
    }

    private Wall readMat(List<List<Integer>> input) {
        Wall wall = new Wall();
        int yIndex = 0;
        for (List<Integer> line : input) {
            List<Character> chars = line.stream().map(i -> (char) (i + '0')).collect(Collectors.toList());
            if (wall.width == 0) {
                wall.width = chars.size();
                for (int i = 0; i < chars.size(); i++) {
                    wall.columns.add(new Column());
                }
            }

            for (int i = 0; i < chars.size(); i++) {
            	char ch = chars.get(i);
            	if (ch == '0') {
            		continue;
            	}
                wall.columns.get(i).bricks.add(new Brick(chars.get(i), i, yIndex));
            }

            List<Column> toBeRemoved = new ArrayList<Column>();
            for(Column c : wall.columns) {
            	boolean allZero = true;
            	for(Brick b : c.bricks) {
            		if(b.ch != '0') {
            			allZero = false;
            		}
            		if(allZero) {
            			toBeRemoved.add(c);
            		}
            	}
            }
            for (Column c : toBeRemoved) {
            	wall.columns.remove(c);
            }
            yIndex++;
        }
        
        

        wall.height = yIndex;

        return wall;
    }

    public static void printWall(Wall wall) {
        if (logger.isDebugEnabled()) {
            Brick[][] block = new Brick[wall.height][wall.width];
            Brick dummy = new Brick(' ', 0, 0);

            for (Brick[] line : block) {
                Arrays.setAll(line, i -> dummy);
            }

            List<Brick> bricks = wall.getBricks();
            for (Brick b : bricks) {
                block[b.y][b.x] = b; // String.valueOf(b.y).charAt(0);
            }

            for (Brick[] line : block) {
                String debugLine = "";
                for (Brick v : line) {
                    if (v.marked) {
                        debugLine += (v.ch + "<\t");
                    } else {
                        debugLine += (v.ch + "\t");
                    }
                }
                logger.debug(debugLine);
            }

            logger.debug("--------------------------------------");
        }

    }

    void wipeOneGroup(Wall image, Group g) {
        for (int i = 0; i < image.columns.size(); i++) {
            Column c = image.columns.get(i);
            Fragment f = g.getFragment(i);

            if (f == null) {
                continue;
            }

            int offset = c.bricks.size() - image.height;

            for (int j = f.begin - 1 + offset; j >= 0; j--) {
                c.bricks.get(j).y += (f.end - f.begin + 1);
            }
            for (int k = f.begin; k <= f.end; k++) {
                c.bricks.remove(f.begin + offset);
            }

        }

        for (int i = 0; i < image.columns.size(); ) {
            Column c = image.columns.get(i);
            if (c.bricks.size() == 0) {
                for (int m = i + 1; m < image.columns.size(); m++) {
                    Column latterColumn = image.columns.get(m);
                    for (Brick b : latterColumn.bricks) {
                        b.x -= 1;
                    }
                }
                image.columns.remove(c);
            } else {
                i++;
            }
        }

    }

    Action wipeOut(Wall wall, int level) {
        List<Group> groups = wall.group();

        if (wall == null || groups.size() == wall.getBricks().size()) {
            return null;
        }

        Map<Wall, Group> actionsMap = new HashMap<>();

        for (int i = 0; i < groups.size(); ) {
            Group g = groups.get(i);
            if (g.bricks.size() == 1) {
                groups.remove(g);
            } else {
                i++;
            }
        }

        WallWeight oldWeight = new WallWeight(wall.getWeight());
        for (Group g : groups) {
            Wall image = wall.copy();

            wipeOneGroup(image, g);

            actionsMap.put(image, g);
        }

        List<Action> newActions = actionPicker.pick(actionsMap, oldWeight);

        level -= 1;
        if(newActions.size() == 1 || level < 0) {
            return newActions.get(0);
        }
        else {
            SortedMap<Double, Action> cache = new TreeMap<>();
            for(Action action : newActions) {
                Action next = wipeOut(action.wall, level);
                cache.put(next.wall.getWeight().getComplexity(), action);
                System.out.print("--" + next.wall.getWeight().getComplexity() + ",");
            }
            System.out.println("\n" + cache.firstKey() + ",");
            return cache.get(cache.firstKey());
        }
    }
}
