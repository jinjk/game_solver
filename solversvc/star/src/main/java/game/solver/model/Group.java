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
    public int len;

    public Set<Brick> bricks = new LinkedHashSet<>();

    public Fragment getFragment(int x) {
        Fragment f = new Fragment();
        for (Brick b : bricks) {
            if (b.x == x) {
                if (b.y > f.end) {
                    f.end = b.y;
                }
                if (b.y < f.begin) {
                    f.begin = b.y;
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
        bricks.forEach(b -> b.marked = true);
    }
}
