package game.solver.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class Wall {
	public int width;
	public int height;
	private WallWeight weight;
	private List<Group> groups;

	public List<Column> columns = new ArrayList<>();

	public List<Brick> getBricks() {
		List<Brick> bricks = new ArrayList<Brick>();
		for (Column c : columns) {
			bricks.addAll(c.bricks);
		}
		return bricks;
	}

	public Wall copy() {
		Wall image = new Wall();
		image.width = width;
		image.height = height;

		image.columns = new ArrayList<>();
		for (Column c : columns) {
			Column imageColumn = new Column();
			image.columns.add(imageColumn);
			for (Brick b : c.bricks) {
				Brick imageBrick = b.copy();
				imageColumn.bricks.add(imageBrick);
			}

		}
		return image;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	@JsonIgnore
	public WallWeight getWeight() {
	    if(weight == null) {
            weight = new WallWeight();
            weight.calculate(this);
        }
	    return weight;
    }

    public List<Group> group() {
	    if(this.groups != null) {
	        return this.groups;
        }
        else {
            List<Group> groups = new ArrayList<>();
            List<Brick> bricks = this.getBricks();
            int i = 0;
            for (Brick b : bricks) {
                boolean added = false;
                for (Group g : groups) {
                    for (Brick gb : g.bricks) {
                        if (b.ch == g.ch && (gb.x == b.x || gb.y == b.y)
                                && (Math.abs(gb.x - b.x) == 1 || Math.abs(gb.y - b.y) == 1)) {
                            g.bricks.add(b);
                            added = true;
                            break;
                        }
                    }
                }

                if (!added) {
                    Group g = new Group();
                    g.id = i++;
                    g.ch = b.ch;
                    g.bricks.add(b);
                    groups.add(g);
                }
            }

            for (int m = 0; m < groups.size() - 1; m++) {
                Group g1 = groups.get(m);
                for (int n = m + 1; n < groups.size(); ) {
                    Group g2 = groups.get(n);
                    boolean removed = false;
                    for (Brick b : g2.bricks) {
                        if (g1.bricks.contains(b)) {
                            g1.bricks.addAll(g2.bricks);
                            groups.remove(g2);
                            removed = true;
                            break;
                        }
                    }

                    if (!removed) {
                        n++;
                    }
                }
            }
            this.groups = groups;
            return groups;
        }
    }
}
