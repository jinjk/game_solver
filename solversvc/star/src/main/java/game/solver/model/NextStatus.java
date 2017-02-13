package game.solver.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by C5241628 on 2/13/2017.
 */
public class NextStatus {
    public int weight;
    public Group group;
    public List<Group> groups;
    public Map<Group, Wall> actionsMap = new HashMap<>();
    public Wall wall;
    public Group selected;
}
