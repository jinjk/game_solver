package game.solver.model;

import java.util.ArrayList;
import java.util.List;


public class Wall {
	public int width;
	public int height;

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
}
