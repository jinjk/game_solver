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

    public List<Action> pick(Map<Wall, Group> actionsMap, WallWeight oldWeight) {
        List<Action> actions = new ArrayList<>();
        List<Wall> walls = new ArrayList<Wall>();
        for (Wall wall : actionsMap.keySet()) {
            simplifyWall(wall);
            walls.add(wall);
        }

        List<Wall> filtedWalls = walls.stream().filter(wall -> {
            return wall.getWeight().getComplexity() < oldWeight.getComplexity();
        }).collect(Collectors.toList());

        if(filtedWalls.size() > 0) {
            walls = filtedWalls;
        }

        Collections.sort(walls, (Wall o1, Wall o2) -> {
            WallWeight w1 = o1.getWeight();
            WallWeight w2 = o2.getWeight();

            int val = 0; //w1.getMaxGroupSize() - w2.getMaxGroupSize();

            if (val == 0) {
                val = (int) (w1.getComplexity() - w2.getComplexity());
            }

//            if(val == 0) {
//                val = w1.getSingleBricksNum() - w2.getSingleBricksNum();
//            }
//
//            if(val == 0) {
//                val = w1.getBricksNum() - w2.getBricksNum();
//            }

            return val;
        });

        long cmpx = walls.get(0).getWeight().getComplexity();

        for (Wall wall : walls) {
            if (wall.getWeight().getComplexity() > cmpx) {
                break;
            }
//            System.out.println("xmpx:" + wall.getWeight().getComplexity());
            Action action = new Action();
            action.wall = wall;
            action.group = actionsMap.get(action.wall);
            actions.add(action);
        }
        return actions;
    }

    void simplifyWall(Wall image) {

        List<Brick> singleBricks = image.getWeight().getSingleBricks();

        Map<Character, List<Brick>> cache = new HashMap<>();
        Map<Integer, List<Brick>> columns = new HashMap<>();
        for (Brick b : singleBricks) {
            List<Brick> cacheList = cache.get(b.ch);
            if (cacheList == null) {
                cacheList = new ArrayList<>();
                cache.put(b.ch, cacheList);
            }
            cacheList.add(b);
            List columnList = columns.get(b.x);
            if (columnList == null) {
                columnList = new ArrayList<>();
                columns.put(b.x, columnList);
            }
            columnList.add(b);
        }

        for(List<Brick> list: columns.values()) {
            list.sort((Brick a, Brick b) -> {
                return a.y - b.y;
            });
        }

        long totalDist = 0;
        for (List<Brick> list : cache.values()) {
            list.sort((Brick a, Brick b) -> {
                return (a.x - b.x) * 10 + (a.y - b.y);
            });

            long dist = 0;
            for (int i = 0; i < list.size() - 1; i++) {
                Brick a = list.get(i);
                Brick b = list.get(i + 1);
                // a.x ----> b.x
                long aToB = 0;
                for(int x = a.x; x <= b.x; x++) {
                    List<Brick> cList = columns.get(x);
                    if(cList == null) {
                        continue;
                    }

                    int aHeight = 0;
                    int bHeight = 0;
                    int distNum = 0;
                    for(Brick cb : cList) {
                        if(cb == a && x == a.x) {
                            aHeight = 0;
                        }
                        else if (x == a.x){
                            aHeight++;
                        }

                        if(cb == b && x == b.x) {
                            bHeight = 0;
                        }
                        else if(x == b.x) {
                            bHeight++;
                        }

                        if(x != a.x && x != b.x) {
                            distNum++;
                        }
                    }

                    aHeight = 10 - aHeight;
                    bHeight = 10 - bHeight;
                    long step = Math.abs(aHeight * aHeight - bHeight * bHeight)  +  distNum * distNum * 10;
                    aToB = step;
                }

                dist += aToB;
            }
            totalDist += dist;
        }
        image.getWeight().setComplexity(totalDist);
    }
}
