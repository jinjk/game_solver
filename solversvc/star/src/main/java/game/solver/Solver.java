package game.solver;

import game.solver.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger logger = LoggerFactory.getLogger(Solver.class);

    public List<Wall> execute(List<List<Integer>> mat) {
        Wall wall = readMat(mat);
        return execute(wall);
    }


    public List<Wall> execute(Wall wall) {
        List<Wall> result = new ArrayList<Wall>();

        NextStatus status = null;
        do {
            Wall old = wall;
            status = wipeOut(wall, true);
            if (status.group != null)
                status.group.mark();

            result.add(old);
            printWall(old);

            wall = status.wall;
        } while (status != null && status.group != null);

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
                wall.columns.get(i).bricks.add(new Brick(chars.get(i), i, yIndex));
            }

            yIndex++;
        }

        wall.height = yIndex;

        return wall;
    }

    private void printWall(Wall wall) {
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

        List<Group> newGroups = image.group();
        g.weight.groupSize = newGroups.size();
        for (Group ng : newGroups) {
            if (ng.bricks.size() == 1) {
                g.weight.signleBricks++;
            }
        }
    }

    NextStatus wipeOut(Wall wall, boolean lookForward) {
        NextStatus actionImage = new NextStatus();
        List<Group> groups = wall.group();
        if (wall == null || groups.size() == wall.getBricks().size()) {
            actionImage.wall = wall;
            actionImage.weight = groups.size();
            return actionImage;
        }

        Map<Group, Wall> actionsMap = new HashMap<>();

        for (int i = 0; i < groups.size(); ) {
            Group g = groups.get(i);
            if (g.bricks.size() == 1) {
                groups.remove(g);
            } else {
                i++;
            }
        }

        for (Group g : groups) {
            Wall image = wall.copy();

            wipeOneGroup(image, g);

            actionsMap.put(g, image);
        }

        Collections.sort(groups, (o1, o2) -> o1.weight() - o2.weight());

        actionImage.groups = groups;
        actionImage.actionsMap = actionsMap;
        actionImage.weight = groups.get(0).weight();

        if (lookForward) {
            NextStatus fStatus = lookForward(actionImage, PREDICTION_LENGTH);
            actionImage.group = fStatus.group;
            actionImage.wall = fStatus.actionsMap.get(actionImage.group);
        }

        return actionImage;
    }

    NextStatus lookForward(NextStatus nextStatus, int length) {

        if (nextStatus.groups == null) {
            return nextStatus;
        }

        int minWeight = -1;
        Group mg = null;
        length--;
        for (Group g : nextStatus.groups) {
            NextStatus status;
            if (length >= 0) {
                status = wipeOut(nextStatus.actionsMap.get(g), false);
                status = lookForward(status, length);
            } else {
                status = nextStatus;
            }

            if (minWeight == -1) {
                minWeight = status.weight;
                mg = g;
            } else if (minWeight > status.weight) {
                minWeight = status.weight;
                mg = g;
            }
        }

        nextStatus.weight = minWeight;
        nextStatus.group = mg;

        return nextStatus;
    }


}
