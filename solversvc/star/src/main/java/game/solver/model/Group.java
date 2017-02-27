package game.solver.model;

import game.solver.Solver;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by C5241628 on 2/13/2017.
 */
public class Group {
    public char ch;
    public int id;
    public Wall wall;

    public Set<Brick> bricks = new LinkedHashSet<>();

    public Group(Wall wall) {
        this.wall = wall;
    }

    public Fragment getFragment(int x) {
        Fragment f = new Fragment();
        for (Brick b : bricks) {
            if (b.getX() == x) {
                if (b.getY() > f.end) {
                    f.end = b.getY();
                }
                if (b.getY() < f.begin) {
                    f.begin = b.getY();
                }
            }
        }
        if (f.begin == Integer.MAX_VALUE) {
            return null;
        } else {
            return f;
        }
    }

    public void mark() {
        bricks.forEach(b -> b.setMarked(true));
    }
}
