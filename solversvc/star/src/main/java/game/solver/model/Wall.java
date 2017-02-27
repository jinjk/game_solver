package game.solver.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static game.solver.Constants.COL_NUM;
import static game.solver.Constants.ROW_NUM;


public class Wall {

    private int width = ROW_NUM;
	private int height = COL_NUM;
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
                        if (b.getCh() == g.ch && (gb.getX() == b.getX() || gb.getY() == b.getY())
                                && (Math.abs(gb.getX() - b.getX()) == 1 || Math.abs(gb.getY() - b.getY()) == 1)) {
                            g.bricks.add(b);
                            added = true;
                            break;
                        }
                    }
                }

                if (!added) {
                    Group g = new Group(this);
                    g.id = i++;
                    g.ch = b.getCh();
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
