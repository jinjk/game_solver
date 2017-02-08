package game.solver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import game.solver.Solver.Wall;

@RestController
@RequestMapping("/solver")
public class SolverController {

	@Autowired
	private Solver solver;

	@RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Wall> solveStarMat(@RequestBody InputMat mat) {
		return solver.execute(mat.mat);
	}
	
	@RequestMapping(path = "/greeting")
	public String greeting() {
		return "greeting";
	}
}

class InputMat {
	List<List<Integer>> mat;

	public List<List<Integer>> getMat() {
		return mat;
	}

	public void setMat(List<List<Integer>> mat) {
		this.mat = mat;
	}
}
