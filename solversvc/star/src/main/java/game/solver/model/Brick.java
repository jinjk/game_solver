package game.solver.model;

public class Brick {

	public char ch;
	public int x;
	public int y;
	public boolean marked = false;

	public Brick(char ch, int x, int y) {
		this.ch = ch;
		this.x = x;
		this.y = y;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return ch + "," + x + "," + y;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Brick) {
			return this.toString().equals(((Brick) obj).toString());
		}

		return false;
	}

	public Brick copy() {
		Brick b = new Brick(ch, x, y);
		return b;
	}

	public char getCh() {
		return ch;
	}

	public void setCh(char ch) {
		this.ch = ch;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}
}