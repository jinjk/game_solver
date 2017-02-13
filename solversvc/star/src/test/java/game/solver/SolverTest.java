package game.solver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import game.solver.model.Brick;
import game.solver.model.Column;
import game.solver.model.Wall;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class SolverTest {
	Logger logger = LoggerFactory.getLogger(SolverTest.class);
	@Autowired
	Solver solver;

	@Test
	public void testSolve() {
		String path = "/a.txt";

		Wall wall = readFile(SolverTest.class.getResourceAsStream(path));

		solver.execute(wall);
	}

	private Wall readFile(InputStream input) {
		try (Reader reader = new InputStreamReader(input)) {
			BufferedReader bufferReader = new BufferedReader(reader);
			String line = null;
			Wall wall = new Wall();
			int yIndex = 0;
			while ((line = bufferReader.readLine()) != null) {
				char[] chars = line.toCharArray();
				if (wall.width == 0) {
					wall.width = line.length();
					for (int i = 0; i < line.length(); i++) {
						wall.columns.add(new Column());
					}
				}

				for (int i = 0; i < chars.length; i++) {
					wall.columns.get(i).bricks.add(new Brick(chars[i], i, yIndex));
				}

				yIndex++;
			}

			wall.height = yIndex;

			return wall;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
