package game.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

import game.solver.model.Wall;

@RestController
@RequestMapping("/solver")
public class SolverController {

	@Autowired
	private Solver solver;

	@RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Result solveStarMat(@RequestBody InputMat mat) {
		Result rs = new Result();
		List<Wall> walls = solver.execute(mat.mat);
		rs.setWalls(walls);
		rs.setIcons(mat.getIcons());
		
		return rs;
	}
	
	@RequestMapping(path = "/greeting")
	public String greeting() {
		return "greeting";
	}
}

class Result {
	List<Wall> walls;
	@JsonProperty( "icons" )
	Map<String, Pos> icons = new HashMap<>();
	public List<Wall> getWalls() {
		return walls;
	}
	public void setWalls(List<Wall> walls) {
		this.walls = walls;
	}
	public Map<String, Pos> getIcons() {
		return icons;
	}
	public void setIcons(Map<String, Pos> icons) {
		this.icons = icons;
	}
}

class Pos {
	int x;
	int y;
	int w;
	int h;
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
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
}

class InputMat {
	List<List<Integer>> mat;
	@JsonProperty( "icons" )
	Map<String, Pos> icons = new HashMap<>();

	public List<List<Integer>> getMat() {
		return mat;
	}

	public void setMat(List<List<Integer>> mat) {
		this.mat = mat;
	}

	public Map<String, Pos> getIcons() {
		return icons;
	}

	public void setIcons(Map<String, Pos> icons) {
		this.icons = icons;
	}
}
