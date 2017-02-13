package game.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import game.solver.model.Brick;
import game.solver.model.Column;
import game.solver.model.Wall;

/**
 * Hello world!
 */
@Component
public class Solver {
	Logger logger = LoggerFactory.getLogger(Solver.class);

	public static final int WEIGHT_OF_SINGLE_UNIT = 100;
	public static final int PREDICTION_LENGTH = 2;

	public List<Wall> execute(List<List<Integer>> mat) {
		Wall wall = readMat(mat);
		return execute(wall);
	}
	

	public List<Wall> execute(Wall wall) {
		List<Wall> result = new ArrayList<Wall>();

		NextStatus status = null;
		do {
			Wall old = wall;
			status = wipeOut(wall, true);
			if (status.group != null)
				status.group.mark();

			result.add(old);
			printWall(old);

			wall = status.wall;
		} while (status != null && status.group != null);

		return result;
	}

	private Wall readMat(List<List<Integer>> input) {
		Wall wall = new Wall();
		int yIndex = 0;
		for (List<Integer> line : input) {
			List<Character> chars = line.stream().map(i -> (char) (i + '0')).collect(Collectors.toList());
			if (wall.width == 0) {
				wall.width = chars.size();
				for (int i = 0; i < chars.size(); i++) {
					wall.columns.add(new Column());
				}
			}

			for (int i = 0; i < chars.size(); i++) {
				wall.columns.get(i).bricks.add(new Brick(chars.get(i), i, yIndex));
			}

			yIndex++;
		}

		wall.height = yIndex;

		return wall;
	}

	private void printWall(Wall wall) {
		if (logger.isDebugEnabled()) {
			Brick[][] block = new Brick[wall.height][wall.width];
			Brick dummy = new Brick(' ', 0, 0);

			for (Brick[] line : block) {
				Arrays.setAll(line, i -> dummy);
			}

			List<Brick> bricks = wall.getBricks();
			for (Brick b : bricks) {
				block[b.y][b.x] = b; // String.valueOf(b.y).charAt(0);
			}

			for (Brick[] line : block) {
				String debugLine = "";
				for (Brick v : line) {
					if (v.marked) {
						debugLine += (v.ch + "<\t");
					} else {
						debugLine += (v.ch + "\t");
					}
				}
				logger.debug(debugLine);
			}

			logger.debug("--------------------------------------");
		}

	}

	public List<Group> group(Wall wall) {
		List<Group> groups = new ArrayList<>();
		List<Brick> bricks = wall.getBricks();
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
			for (int n = m + 1; n < groups.size();) {
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

		return groups;
	}

	void wipeOneGroup(Wall image, Group g) {
		for (int i = 0; i < image.columns.size(); i++) {
			Column c = image.columns.get(i);
			Fragment f = g.getFragment(i);

			if (f == null) {
				continue;
			}

			int offset = c.bricks.size() - image.height;

			for (int j = f.begin - 1 + offset; j >= 0; j--) {
				c.bricks.get(j).y += (f.end - f.begin + 1);
			}
			for (int k = f.begin; k <= f.end; k++) {
				c.bricks.remove(f.begin + offset);
			}

		}

		for (int i = 0; i < image.columns.size();) {
			Column c = image.columns.get(i);
			if (c.bricks.size() == 0) {
				for (int m = i + 1; m < image.columns.size(); m++) {
					Column latterColumn = image.columns.get(m);
					for (Brick b : latterColumn.bricks) {
						b.x -= 1;
					}
				}
				image.columns.remove(c);
			} else {
				i++;
			}
		}

		List<Group> newGroups = group(image);
		g.weight.groupSize = newGroups.size();
		for (Group ng : newGroups) {
			if (ng.bricks.size() == 1) {
				g.weight.signleBricks++;
			}
		}
	}

	NextStatus wipeOut(Wall wall, boolean lookForward) {
		NextStatus status = new NextStatus();
		List<Group> groups = group(wall);
		if (wall == null || groups.size() == wall.getBricks().size()) {
			status.wall = wall;
			status.weight = groups.size();
			return status;
		}

		Map<Group, Wall> inOut = new HashMap<>();

		for (int i = 0; i < groups.size();) {
			Group g = groups.get(i);
			if (g.bricks.size() == 1) {
				groups.remove(g);
			} else {
				i++;
			}
		}

		for (Group g : groups) {
			Wall image = wall.copy();

			wipeOneGroup(image, g);

			inOut.put(g, image);
		}

		Collections.sort(groups, (o1, o2) -> o1.weight() - o2.weight());

		status.groups = groups;
		status.inOut = inOut;
		status.weight = groups.get(0).weight();

		if (lookForward) {
			NextStatus fStatus = lookForward(status, PREDICTION_LENGTH);
			status.group = fStatus.group;
			status.wall = fStatus.inOut.get(status.group);
		}

		return status;
	}

	NextStatus lookForward(NextStatus nextStatus, int length) {

		if (nextStatus.groups == null) {
			return nextStatus;
		}

		int minWeight = -1;
		Group mg = null;
		length--;
		for (Group g : nextStatus.groups) {
			NextStatus status;
			if (length >= 0) {
				status = wipeOut(nextStatus.inOut.get(g), false);
				status = lookForward(status, length);
			} else {
				status = nextStatus;
			}

			if (minWeight == -1) {
				minWeight = status.weight;
				mg = g;
			} else if (minWeight > status.weight) {
				minWeight = status.weight;
				mg = g;
			}
		}

		nextStatus.weight = minWeight;
		nextStatus.group = mg;

		return nextStatus;
	}

	class NextStatus {
		public int weight;
		public Group group;
		List<Group> groups;
		Map<Group, Wall> inOut = new HashMap<>();
		Wall wall;
		Group selected;
	}

	class GroupWeight {
		int signleBricks;
		int groupSize;
	}



	

	class Group {
		char ch;

		int id;

		Set<Brick> bricks = new LinkedHashSet<>();
		GroupWeight weight = new GroupWeight();

		Fragment getFragment(int x) {
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

		int weight() {
			return weight.signleBricks * WEIGHT_OF_SINGLE_UNIT + weight.groupSize;
		}

		public void mark() {
			bricks.forEach(b -> b.marked = true);
		}
	}

	class Fragment {
		int begin = Integer.MAX_VALUE;
		int end = Integer.MIN_VALUE;
	}




}
