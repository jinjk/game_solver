package game.solver;

import game.solver.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static game.solver.Constants.ROW_NUM;

/**
 * Created by C5241628 on 2/16/2017.
 */
@Component
@Scope("prototype")
public class ActionPicker {


    Logger logger = LoggerFactory.getLogger(ActionPicker.class);
    double temperature = 1000;
    double cool = 0.9999;

    public List<Action> pick(Map<Wall, Group> actionsMap, WallWeight oldWeight, int iteration) {
        List<Action> actions = new ArrayList<>();
        List<Wall> walls = new ArrayList<Wall>();
        for (Wall wall : actionsMap.keySet()) {
            simplifyWall(wall, wall);
            walls.add(wall);
        }

        Collections.sort(walls, (Wall o1, Wall o2) -> {
            return (int) (o1.getWeight().getComplexity() - o2.getWeight().getComplexity());
        });

        long cmx = walls.get(0).getWeight().getComplexity();


        for(Wall wall : walls) {

            if (wall.getWeight().getComplexity() > cmx) {
                long delta = wall.getWeight().getComplexity() - cmx;
                double ram = Math.random();
                double vet = Math.exp(-1 * delta / temperature);
//                System.out.println(vet);
                if(ram > vet)
                    continue;
            }

            Action action = new Action();
            action.wall = wall;
            action.group = actionsMap.get(wall);
            actions.add(action);
        }
        temperature *= cool;
        return actions;
    }

    void simplifyWall(Wall image, Wall nextStep) {
        List<Brick> singleBricks = nextStep.getWeight().getSingleBricks();
        singleBricks = singleBricks.stream().map(b -> b.copy()).collect(Collectors.toList());
        singleBricks.sort((b1, b2) -> {
           return (b1.getX() * ROW_NUM + (ROW_NUM - b1.getY())) - (b2.getX() * ROW_NUM + (ROW_NUM - b2.getY()));
        });

        Wall wall = new Wall();
        Column c = null;
        int xSign = -1;
        int x = -1;
        int y = 9;
        for(Brick b : singleBricks) {
            if(b.getX() != xSign) {
                c = new Column();
                wall.getColumns().add(c);
                y = 9;
                x++;
                xSign = b.getX();
            }

            b.setX(x);
            b.setY(y);
            c.bricks.add(b);
            y--;
        }

        int num = wall.getWeight().getSingleBricksNum();
        if(num == singleBricks.size()) {
//            System.out.println("------------testing---------------");
//            Solver.printWall(wall);
            image.getWeight().setComplexity(num);
        }
        else {
            simplifyWall(image, wall);
        }
    }
}
