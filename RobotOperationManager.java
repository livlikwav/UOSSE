package Fighting;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.Queue;
import java.util.LinkedList;


public class RobotOperationManager {
	public static void main(String[] args) throws IOException{
		
		final int SOUTH = 3;
		final int NORTH = 1;
		final int EAST = 2;
		final int WEST = 0;
		
		MapDataManager mdm = new MapDataManager();
		int Color_cnt=1;
		while(Color_cnt <= 3) {
			double randomvalue = Math.random();
			double randomvalue2 = Math.random();
			if (Map.map[(int) (randomvalue*7+1)][(int) (randomvalue2*7+1)] == 0) {
				Map.map[(int) (randomvalue*7+1)][(int) (randomvalue2*7+1)] = 3;
				Color_cnt++;
			}
		}
		int Hazard_cnt=1;
		while (Hazard_cnt <= 1) {
			double randomvalue = Math.random();
			double randomvalue2 = Math.random();
			if (Map.map[(int) (randomvalue*7+1)][(int) (randomvalue2*7+1)] == 0) {
				if(mdm.start_x != randomvalue*7+1 || mdm.start_y !=randomvalue2*7+1) {
				Map.map[(int) (randomvalue*7+1)][(int) (randomvalue2*7+1)] = 2;
				Hazard_cnt++;
				}
			}
		}
		mdm.print();
		SIM sim = new SIM();
		Map map = mdm.GetMap();
		
		
		
		
		
		int cur_direction = SOUTH;
		String[] direction_list;
		int check_dir=0;
		boolean check_step = true;
		map.current_x = map.start_x;
		map.current_y = map.start_y;
		
		sim.TranslateMoveOperation(map.current_x, map.current_y, map.currecnt_direction);
		
		
		
		do {
				if (sim.ColorBlobSensor()) System.out.println("중요지점 발견");
				if (sim.HazardSensor()) { 
					System.out.println("위험지역 발견!"); 
					map.repathflag = 1;
					
				}
				else { map.repathflag = 0;}
				int flag = mdm.CheckRePathFlag();
				if (flag ==1 || check_step == true) {
				map = mdm.GetMap();
				
				map.visit[Map.step][map.current_x][map.current_y] = true;
				//path[i][start_x][start_y] = "(" + start_x + " , " + start_y + ")";
				//map.path[map.step][map.start_x][map.start_y] = map.start_x + "," + map.start_y + ",";
			
				
					
				PathManager pm = new PathManager(map.mapsize_row, map.mapsize_col, Map.map, map.visit, map.path, Map.step);
				
				pm.CalculateOptimalPath(map.current_x, map.current_y, cur_direction); ///스텝 고쳐야한다.
				PathNode answer_pn = pm.GetData();
				map.path = answer_pn.path;
				
				}
				int tmpx = map.search_x[Map.step];
				
				int tmpy = map.search_y[Map.step];
				
				
				direction_list = map.path[Map.step][tmpx][tmpy].split("->");
				
				
				System.out.println(direction_list[check_dir]);
				sim.Move(direction_list[check_dir]);
				check_dir++;
				sim = sim.Get();
				
				map.current_x = sim.current_x;
				map.current_y = sim.current_y;
				cur_direction = sim.current_direction;
				System.out.println(map.current_x + " , " + map.current_y + " , " + cur_direction);
				check_step = false;
				if (map.current_x == tmpx && map.current_y == tmpy) {Map.step++; check_step = true; check_dir = 0;}
				
				//System.out.println(tmpx + " " + tmpy);
	
		}while(Map.step != map.search_cnt);
	}
}

class MapDataManager {
	int[][] map;
	
	boolean[][][] visit;
    int row;
	int col;
	int start_x;
	int start_y;
	int[] search_x;
	int[] search_y;
	
	String[][][] path;
	int search_cnt;
	int hazard_cnt;
	
	Map mp = new Map();
	
	MapDataManager() throws IOException {
		// TODO Auto-generated method stub
		InputMapDataForm IMDF = new InputMapDataForm();
		
		row = IMDF.row;
		col = IMDF.col;
		start_x = IMDF.start_x;
		start_y = IMDF.start_y;
		
		path = IMDF.path;
		search_cnt = IMDF.search_cnt;
		hazard_cnt = IMDF.hazard_cnt;
		map = IMDF.map;
		visit = IMDF.visit;
		search_x = IMDF.search_x;
		search_y = IMDF.search_y;
		
		mp.Save(row, col, start_x, start_y, map, visit, path, search_cnt, hazard_cnt, start_x, start_y, 3, search_x, search_y);
		
		///////////////////////////////////////MAP GUI FORM/////////////////////////////////////////////
		
		///////////////////////////////////////MAP GUI FORM/////////////////////////////////////////////
		
	}
	void print() {
		for (int i=1; i<=row; i++) {
			for (int j=1; j<=col; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}
	int CheckRePathFlag() {
		int flag = mp.GetRePathFlag();
		return flag;
	}
	
	Map GetMap() {
		return mp.GetMap();
	}
	
}
	
class PathManager {
		final int SOUTH = 3;
		final int NORTH = 1;
		final int EAST = 2;
		final int WEST = 0;
		
		PathNode pn;
		int[] dx = {0, -1, 0, 1};
		int[] dy = {-1, 0, 1, 0};
		int[] direction = {0, 1, 2, 3};
		
		int row;
		int col;
		int[][] map;
		boolean[][][] visit;
		String[][][] path;
		int step;
		
		PathManager(int row, int col, int[][] map, boolean[][][] visit, String[][][] path, int step) {
			this.row = row;
			this.col = col;
			this.map = map;
			this.visit = visit;
			this.path = path;
			this.step = step;
		}

		void CalculateOptimalPath(int x, int y, int my_dir) {
			
			pn = new PathNode(row, col, map, visit, path);    //초기화
			Queue<Node> q = new LinkedList<Node>();
			int my_direc = my_dir;
			
			q.add(new Node(x, y, my_direc));
			int tmp_dir=0;
			while (!q.isEmpty()) {
				Node n = q.poll();
				
				
				for (int i=0; i<4; i++) {
					my_direc = n.dir;
					int nx = n.x + dx[i];
					int ny = n.y + dy[i];
					
					if (i==3) {
						tmp_dir = SOUTH; //SOUTH
					}
					else if(i==1) {
						tmp_dir = NORTH; //NORTH
					}
					else if(i==2) {
						tmp_dir = EAST; //EAST
					}
					else if(i==0) {
						tmp_dir = WEST; //WEST
					}
					if (nx <= 0 || ny <= 0 || nx > pn.row || ny > pn.col) {
						continue;
					}
                //이미 방문했던 점이면 건너뛰기
					
					if (pn.visit[step][nx][ny] || pn.map[nx][ny] == 2) {
						continue;
					}
                
					
                
					pn.path[step][nx][ny] += pn.path[step][n.x][n.y];
					
					while(my_direc != tmp_dir) {
						pn.path[step][nx][ny] += "LEFT->";
						if(my_direc == WEST) my_direc = 4;
						my_direc = direction[my_direc-1];
					} // 경로에  맞게 회전
                
                
					pn.path[step][nx][ny] += "Move 1->";
                
                
					q.add(new Node(nx, ny, my_direc));
				
                //pn.path[step][nx][ny] = pn.path[step][n.x][n.y] + "(" + nx + " , " + ny + ")";
					
					pn.visit[step][nx][ny] = true;
                
				}
			}
		}
		PathNode GetData(){
		return pn;
		}

}
class PathNode {
	int row;
	int col;
	int[][] map;
	boolean[][][] visit;
	String[][][] path;
	PathNode (int row, int col, int[][] map, boolean visit[][][], String[][][] path){
		this.row = row;
		this.col = col;
		this.map = map;
		this.visit = visit;
		this.path = path;
	}
}

class Node {
	int x;
	int y;
	int dir;
	Node(int x, int y, int dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
}

class SensorNode {
	int x;
	int y;
	int dir;
	int sensor_value;
	SensorNode(int x, int y, int dir, int sensor_value) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.sensor_value = sensor_value;
	}
}

abstract class SensorManager{
	abstract boolean AddSensorValue(int x, int y, int dir);
	
	boolean SetRePathFalg() {
		return true;
	}
}

class HazardSensorManager extends SensorManager{
	
	boolean AddSensorValue(int x, int y, int dir) {
		int[] dx = {0, -1, 0, 1};
		int[] dy = {-1, 0, 1, 0};
		if(x+dx[dir] > 0 && y+dy[dir] > 0 && x+dx[dir] <= Map.mapsize_row && y+dy[dir] <= Map.mapsize_col) {
			if(Map.map[x+dx[dir]][y+dy[dir]] == 2) return true;
		}
		
		return false;
		
	}
}

class ColorBlobSensorManager extends SensorManager{
	boolean AddSensorValue(int x, int y, int dir) {
		
		int[] dx = {0, -1, 0, 1};
		int[] dy = {-1, 0, 1, 0};
		
		for(int i=0; i<4; i++) {
			if (x+i <= 0 || y+i <= 0 || x+i > Map.mapsize_row || y+i > Map.mapsize_col) {
				continue;
			}
			if(Map.map[x+i][y+i] == 3) return true;
		}
		return false;
	}
}

/*class PositionSensorManager extends SensorManager{
	int AddSensorValue() {
		System.out.println("현재 위치 전송!");
		
	}
}*/

class SIM {
	final int SOUTH = 3;
	final int NORTH = 1;
	final int EAST = 2;
	final int WEST = 0;
	
	
	int current_x;
	int current_y;
	int current_direction;
	
	ColorBlobSensorManager cbs = new ColorBlobSensorManager();
	HazardSensorManager hs = new HazardSensorManager();
	
	void TranslateMoveOperation(int current_x, int current_y, int current_direction) {
		
		this.current_direction = current_direction;
		this.current_x = current_x;
		this.current_y = current_y;
	}
	
	void Move(String operation) {
		if (operation.equals("LEFT")) {
			if (current_direction == WEST) current_direction = 4;
			current_direction--;
		}
		else {
			if (current_direction == SOUTH) current_x++;
			else if(current_direction == NORTH) current_x--;
			else if(current_direction == EAST) current_y++;
			else current_y--;
		}
	}
	
	SIM Get() {
		return this;
	}
	
	boolean ColorBlobSensor() {
		return cbs.AddSensorValue(this.current_x, this.current_y, this.current_direction);
	}
	
	boolean HazardSensor() {
		
		return hs.AddSensorValue(this.current_x, this.current_y, this.current_direction);
	}
}




























class InputMapDataForm
{
	int id;
	int row;
	int col;
	BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));
	StringTokenizer str;
	int start_x;
	int start_y;
	int map[][];
	boolean visit[][][];
	String path[][][];
	
	int search_cnt;
	int hazard_cnt;
	int[] search_x;
	int[] search_y;

	void Open() {
		System.out.println("hi");
	}
	
	void EnterID() {
		System.out.print("ID를 입력하세요 : ");
		
	}
	
	boolean Verify() {
		if (id == 1111) {
			System.out.println("확인되었습니다!");
			return true;
		}
		else {
			System.out.println("없는 번호입니다");
			return false;
		}
	}
	
	InputMapDataForm() throws IOException{
		
		Open();
		do {
	
			EnterID();
			str = new StringTokenizer(bfr.readLine());
			id = Integer.parseInt(str.nextToken());
			
		}while(Verify()!=true);
	//////////////////////////////////////////////////////////////////////////////////Input Data Form/////////////////////////////////////////////////
		str = new StringTokenizer(bfr.readLine());
		this.row = Integer.valueOf(str.nextToken());
		this.col = Integer.valueOf(str.nextToken());
		this.start_x = Integer.valueOf(str.nextToken());
		this.start_y = Integer.valueOf(str.nextToken()); // Map 크기, 시작 벡터 입력
	
		str = new StringTokenizer(bfr.readLine());
		this.search_cnt = Integer.valueOf(str.nextToken()); // 탐색 위치 벡터 수 입력
	
		str = new StringTokenizer(bfr.readLine());
		this.hazard_cnt = Integer.valueOf(str.nextToken()); // 위험 위치 벡터 수 입력
	
		this.map = new int[row+1][col+1];
		this.visit = new boolean[search_cnt][row+1][col+1];
		this.path = new String[search_cnt][row+1][col+1];
	
		for (int i=1; i<=row; i++) {
			for (int j=1; j<=col; j++) {
				map[i][j] = 0;                   //일반 지역 = 0
			
			
			}
		}
		
		for (int k=0; k<search_cnt; k++) {
			for (int i=1; i<=this.row; i++) {
				for (int j=1; j<=this.col; j++) {
				                  
					this.visit[k][i][j] = false;
					this.path[k][i][j] = "";
				}
			}
		}
		search_x = new int[search_cnt];
		search_y = new int[search_cnt];
	
		for (int i=0; i<this.search_cnt; i++) {
			this.str = new StringTokenizer(bfr.readLine());
			int tempx = Integer.valueOf(str.nextToken());
			search_x[i] = tempx;
			int tempy = Integer.valueOf(str.nextToken());
			search_y[i] = tempy;
			this.map[tempx][tempy] = 1; // 탐색 지역 = 1
			
		}
		
	
		for (int i=0; i<this.hazard_cnt; i++) {
			str = new StringTokenizer(bfr.readLine());
			int tempx = Integer.valueOf(str.nextToken());
			int tempy = Integer.valueOf(str.nextToken());
			this.map[tempx][tempy] = 2;  // 위험 지역 = 2
		}
	
	
	}
	
}

class Map{
	static int mapsize_row;
	static int mapsize_col;
	
	int start_x;
	int start_y;
	static int[][] map;
	boolean[][][] visit;
	String[][][] path;
	int[] search_x;
	int[] search_y;
	int search_cnt;
	int hazard_cnt;
	int color_blob;
	int current_x;
	int current_y;
	int currecnt_direction;
	int repathflag;
	static int step=0;
	
	
	
	void Save(int row, int col, int start_x, int start_y, int[][] map, boolean[][][] visit, String[][][]path, int search_cnt, int hazard_cnt, int current_x, int current_y, int current_direction, int[] search_x, int[] search_y) {
		SetMap(row, col, start_x, start_y, map, visit, path, search_cnt, hazard_cnt, current_x, current_y, current_direction, search_x, search_y);
	}
	void SetMap(int row, int col, int start_x, int start_y, int[][] map, boolean[][][] visit, String[][][]path, int search_cnt, int hazard_cnt, int current_x, int current_y, int current_direction, int[] search_x, int[] search_y) {
		this.mapsize_row = row;
		this.mapsize_col = col;
		this.start_x = start_x;
		this.start_y = start_y;
		this.visit = visit;
		this.path = path;
		this.search_cnt = search_cnt;
		this.hazard_cnt = hazard_cnt;
		this.currecnt_direction = current_direction;
		this.current_x = current_x;
		this.current_y = current_y;
		this.search_x = search_x;
		this.search_y = search_y;
		Map.map = map;
	}
	
	int GetRePathFlag() {
		return repathflag;
	}
	
	Map GetMap() {
		return this;
	}
}