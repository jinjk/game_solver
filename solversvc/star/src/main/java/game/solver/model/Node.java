package game.solver.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C5241628 on 2/17/2017.
 */
public class Node {
    Action action;
    List<Node> children = new ArrayList<>();

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }
}
