package Problem;



import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.Queue;
import java.util.LinkedList;

//일반 지역 = 0
//탐색 지역 = 1
//기존 위험 지역 = 2
//아직 발견되지 않은 Color blob = 3
//이동하다 발견된 Color blob = 4
//아직 발견되지 않은 위험 지역 = 5 -> 발견되면 2로 바꾼다.


public class RobotOperationManager { // 시퀀스 다이어그램에서 볼 수 있듯이 이 클래스가 모든거 다 지시
	public static void main(String[] args) throws IOException{
		
		final int SOUTH = 3; // 회전 할 때마다 1씩 준다고 생각 , 여기서 선언해준건 명시해주려고
		final int NORTH = 1;
		final int EAST = 2;
		final int WEST = 0;
		
		MapDataManager mdm = new MapDataManager(); // mdm에서 map 객체 , InputMapDataForm 객체 다 있음.
		int Color_cnt=1; // 랜덤으로 생기는 Color blob 개수 -> 3개 생성
		while(Color_cnt <= 3) { //위험지역이나, 타겟지점은 빼고 생성
			double randomvalue = Math.random();
			double randomvalue2 = Math.random();
			if (Map.map[(int) (randomvalue*7+1)][(int) (randomvalue2*7+1)] == 0) {
				Map.map[(int) (randomvalue*7+1)][(int) (randomvalue2*7+1)] = 3;
				Color_cnt++;
			}
		} 
		int Hazard_cnt=1; //새로 생기는 위험지역 개수 -> 1개 생성
		while (Hazard_cnt <= 1) { //새로생길 때 기존 위험지역, 타겟지점, 시작지점은 빼고 생성. 하다보니 양옆이 다막여 못가는 경우가 생겨 
			//일단은 생성개수를 하나로 함
			double randomvalue = Math.random();
			double randomvalue2 = Math.random();
			if (Map.map[(int) (randomvalue*7+1)][(int) (randomvalue2*7+1)] == 0) {
				if(mdm.start_x != randomvalue*7+1 || mdm.start_y !=randomvalue2*7+1) {
				Map.map[(int) (randomvalue*7+1)][(int) (randomvalue2*7+1)] = 2;
				Hazard_cnt++;
				}
			}
		}
		mdm.print(); //입력, 랜덤 생성 다 된 후의 map 출력
		SIM sim = new SIM(); //sim 객체 생성
		Map map = mdm.GetMap(); // mdm에서 만들어진 map 객체 정보 받아옴.
		
		
		
		
		
		int cur_direction = SOUTH; //시작 방향을 설정
		String[] operation_list; // 명령어 하나씩 받아온다.
		int check_dir=0; //operation_list 인덱스 하나씩 늘리다가 한 타겟지점 도착하면 초기화하는 변수
		boolean check_step = true; // 한 타겟지점 도착한 거 체크해주는 변수
		map.current_x = map.start_x; // 현재지점을 입력받은 시작지점으로 설정
		map.current_y = map.start_y;
		
		sim.TranslateMoveOperation(map.current_x, map.current_y, map.currecnt_direction);
		//sim에게 현재 상태를 입력해줌
		
		
		do {//일단 한번은 무조건 path생성
				if (sim.ColorBlobSensor()) System.out.println("중요지점 발견"); //color blob 발견 시
				if (sim.HazardSensor()) { //Hazard point 발견 시
					System.out.println("위험지역 발견!"); 
					map.repathflag = 1; //플래그 1로 설정
					
				}
				else { map.repathflag = 0;} //아니면 0으로 설정
				int flag = mdm.CheckRePathFlag(); //플래그 체크
				if (flag ==1 || check_step == true) { // 플래그가 1이거나 한 타겟지점 도착 시
				map = mdm.GetMap(); //map 다시 받아오고
				
				map.visit[Map.step][map.current_x][map.current_y] = true; //지금 있는 위치의 visit을 true로 한다.
				//path[i][start_x][start_y] = "(" + start_x + " , " + start_y + ")";
				//map.path[map.step][map.start_x][map.start_y] = map.start_x + "," + map.start_y + ",";
			
				
					
				PathManager pm = new PathManager(map.mapsize_row, map.mapsize_col, Map.map, map.visit, map.path, Map.step);
				//새로운 pathmanager 객체 생성, 바뀌는 건 visit, path, step뿐 -> 모두 한 타겟지점 도착 시 다음걸로 바뀜
				pm.CalculateOptimalPath(map.current_x, map.current_y, cur_direction); 
				// 주어진 Pathmanager 정보와 또 주어지는 현재 위치 및, 방향으로 경로 계산
				PathNode answer_pn = pm.GetData(); //계산된 경로 가져옴
				map.path = answer_pn.path; //map에 저장
				
				}
				int tmpx = map.search_x[Map.step]; // n번째 타겟지점
				
				int tmpy = map.search_y[Map.step];
				
				
				operation_list = map.path[Map.step][tmpx][tmpy].split("->"); //n번째 타겟지점에 저장된 경로를 "->"을 기준으로 split
				
				
				System.out.println(operation_list[check_dir]);
				sim.Move(operation_list[check_dir]); //명령어 하나씩 받아와서 sim에게 명령어 전달
				check_dir++; //인자++ 하고 (다음 명령어 받는다)
				sim = sim.Get(); //sim의 움직인 정보를 받아온다.
				
				map.current_x = sim.current_x; //현재 위치 리턴
				map.current_y = sim.current_y;
				cur_direction = sim.current_direction; //현재 방향 리턴
				System.out.println(map.current_x + " , " + map.current_y + " , " + cur_direction);
				check_step = false; //기본은 false
				if (map.current_x == tmpx && map.current_y == tmpy) {Map.step++; check_step = true; check_dir = 0;}
				//만약 n번째 타겟지점 도착 시 step++, check_step을 true, check_dir 0으로 초기화
				
	
		}while(Map.step != map.search_cnt); // step이 탐색지점 개수와 같을 때까지
	}
}

class MapDataManager { //맵에게 전달한 변수들 저장, 대부분 이름으로 역할 추정 가능
	int[][] map;
	
	boolean[][][] visit; //중요 -> 3차원배열로 처음은 step별로, 즉 탐색지점 개수만큼 생성, 두 세번째는 좌표
    int row;
	int col;
	int start_x;
	int start_y;
	int[] search_x;
	int[] search_y;
	
	String[][][] path; //중요 -> 3차원배열로 처음은 step별로, 즉 탐색지점 개수만큼 생성, 두 세번째는 좌표
	int search_cnt;
	int hazard_cnt;
	
	Map mp = new Map(); //map 객체 생성 후 전달.
	
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
	int CheckRePathFlag() { //플래그 체크 함수
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
		final int WEST = 0; //각 방향을 숫자로 지정
		
		PathNode pn;
		
		int[] dx = {0, -1, 0, 1}; // i가 1,3 일 시 x축 움직임
		int[] dy = {-1, 0, 1, 0}; // i가 0,2 일 시 y축 움직임
		int[] direction = {0, 1, 2, 3}; // 회전할 때마다 1씩 뺌.
		
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

		void CalculateOptimalPath(int x, int y, int my_dir) { //경로 계산 함수
			
			pn = new PathNode(row, col, map, visit, path);    //초기화
			Queue<Node> q = new LinkedList<Node>(); // 큐 생성
			int my_direc = my_dir; //현재 방향 저장
			
			q.add(new Node(x, y, my_direc)); //큐에 입력받은 정보로 생성된 노드 저장
			int tmp_dir=0; //가상으로 움직이고 싶은 좌표의 방향 저장 변수
			while (!q.isEmpty()) {
				Node n = q.poll(); // 큐에서 하나 꺼내서
				
				
				for (int i=0; i<4; i++) { // 동서남북 다 가본다.
					my_direc = n.dir;
					int nx = n.x + dx[i];
					int ny = n.y + dy[i];
					
					if (i==3) { // 가고 싶은 좌표의 방향 설정
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
					// 가고 싶은 좌표가 맵 크기를 넘어 버리면 건너뛰기
					
					if (pn.visit[step][nx][ny] || pn.map[nx][ny] == 2 || pn.map[nx][ny] == 5) {
						continue;
					}
					 //이미 방문했던 점이거나 위험지역이면 건너뛰기
					
                
					pn.path[step][nx][ny] += pn.path[step][n.x][n.y];
					//가고 싶은 좌표에 현재까지 오는데 걸린 경로 저장 후
					
					while(my_direc != tmp_dir) {
						pn.path[step][nx][ny] += "LEFT->";
						if(my_direc == WEST) my_direc = 4;
						my_direc = direction[my_direc-1];
					} // 경로에  맞게 회전, 모두 저장
                
                
					pn.path[step][nx][ny] += "Move 1->";
					// 1 움직이는 것까지 최종 저장
                
					q.add(new Node(nx, ny, my_direc)); //그 노드를 다시 큐에 삽입
				
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

class Node { //이건 나중에라도 없애야할 클래스, 알아서 고쳐봄
	int x;
	int y;
	int dir;
	Node(int x, int y, int dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
}

class SensorNode { //안쓴다. 나중에 없앤다
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

abstract class SensorManager{ // 이거도 손봐야함.
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
			if(Map.map[x+dx[dir]][y+dy[dir]] == 5) {Map.map[x+dx[dir]][y+dy[dir]] =2; return true; }
		}
		//현재 바라보는 방향의 한칸 앞이 위험지역인가
		
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
			if(Map.map[x+i][y+i] == 3) {Map.map[x+i][y+i] = 4; return true;}
		}
		return false;
	}
}

/*class PositionSensorManager extends SensorManager{
	int AddSensorValue() {
		System.out.println("현재 위치 전송!");
		
	}
}*/

class SIM { //SIM 클래스. 
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




























class InputMapDataForm //입력 폼 클래스
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
		} //map 초기화
		
		for (int k=0; k<search_cnt; k++) {
			for (int i=1; i<=this.row; i++) {
				for (int j=1; j<=this.col; j++) {
				                  
					this.visit[k][i][j] = false;
					this.path[k][i][j] = "";
				}
			}
		}
		// visit, path 초기화
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
		//map에 탐색 지역 표시
	
		for (int i=0; i<this.hazard_cnt; i++) {
			str = new StringTokenizer(bfr.readLine());
			int tempx = Integer.valueOf(str.nextToken());
			int tempy = Integer.valueOf(str.nextToken());
			this.map[tempx][tempy] = 2;  // 위험 지역 = 2
		}
		//map에 위험 지역 표시
	
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