package game.solver.model;

import java.util.ArrayList;
import java.util.List;

public class Column {
	public List<Brick> bricks = new ArrayList<>();

	public List<Brick> getBricks() {
		return bricks;
	}

	public void setBricks(List<Brick> bricks) {
		this.bricks = bricks;
	}
}