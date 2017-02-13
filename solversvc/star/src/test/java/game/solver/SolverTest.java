package game.solver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = { StarApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class SolverTest {
	Logger logger = LoggerFactory.getLogger(SolverTest.class);
	@Autowired
	Solver solver;

	@Test
	public void testSolve() {
		logger.info("haha");
	}

}
