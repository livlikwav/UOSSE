package Fighting;




import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

//일반 지역 = 0
//탐색 지역 = 1
//기존 위험 지역 = 2
//아직 발견되지 않은 Color blob = 3
//이동하다 발견된 Color blob = 4
//아직 발견되지 않은 위험 지역 = 5 -> 발견되면 2로 바꾼다.
public class ADD_ON {
   public static void main(String[] args) throws IOException{
      RobotOperationManager rom = new RobotOperationManager();
      return;
   }
}

class RobotOperationManager { // 시퀀스 다이어그램에서 볼 수 있듯이 이 클래스가 모든거 다 지시
   
   MapDataManager mdm = new MapDataManager();	// mdm에서 map 객체 , InputMapDataForm 객체 다 있음.
   SIM sim = new SIM(); //sim 객체 생성
   Map map = mdm.GetMap(); // mdm에서 만들어진 map 객체 정보 받아옴.
   
   int cur_direction;
   int next_opr;
   boolean check_step;
   
   String[] operation_list; // 명령어 하나씩 받아온다.
   RobotOperationManager() throws IOException {
      final int SOUTH = 3; // 회전 할 때마다 1씩 준다고 생각 , 여기서 선언해준건 명시해주려고
      
      RandomColorBlobCreate();
      RandomHazardCreate();
      MapDataManager.gui.setSnapshot();
      cur_direction = SOUTH; //시작 방향을 설정
      
      next_opr=0; //operation_list 인덱스 하나씩 늘리다가 한 타겟지점 도착하면 초기화하는 변수
      check_step = true; // 한 타겟지점 도착한 거 체크해주는 변수
      SetCurrentPosition(map.start_x , map.start_y);
      
      sim.TranslateMoveOperation(map.current_x, map.current_y, map.currecnt_direction); //나중에 이거 함수 바꿔야한다.
      //sim에게 현재 상태를 입력해줌
      
      do {//일단 한번은 무조건 path생성
            sim.ColorBlobSensor();

            if (sim.HazardSensor()) { //Hazard point 발견 시
               map.repathflag = true; //플래그 1로 설정
               next_opr = 0;
            }
       
            boolean flag = mdm.CheckRePathFlag(); //플래그 체크
            if (flag || check_step) { // 플래그가 1이거나 한 타겟지점 도착 시
            	map = mdm.GetMap(); //map 다시 받아오고
            
            	initalizeVistPath();

            	MakePath();
            	check_step = false; //기본은 false
            	map.repathflag = false;
            	next_opr = 0;
            }
            
            
            int tmpx = map.search_x[map.step]; // n번째 타겟지점
            int tmpy = map.search_y[map.step];
            operation_list = map.path[map.step][tmpx][tmpy].split("->"); //n번째 타겟지점에 저장된 경로를 "->"을 기준으로 split
            System.out.println(next_opr + " , " + operation_list[next_opr]);
            
            
            
            
            	sim.Move(operation_list[next_opr]); //명령어 하나씩 받아와서 sim에게 명령어 전달
            	
            	if (operation_list[next_opr].equals("MOVE")) {
                    if (sim.CurrentPositionSensor()) {
                    System.out.println("예상한 위치와 다릅니다.");
                    System.out.println("예상위치 : " + map.current_x + "," + map.current_y + "실제 위치 : " + SIM.current_x + "," + SIM.current_y);
                    MapDataManager.GUIMapSetPrint(MapDataManager.gui.getSnapshotPoint(map.current_x, map.current_y), map.current_x, map.current_y);
                    SetCurrentPosition(SIM.current_x , SIM.current_y);
                    MapDataManager.GUIMapSetPrint("ROBOT", map.current_x, map.current_y);
                    if (RobotOnGoal(tmpx, tmpy)) {
                    	MapDataManager.GUIMapSetPrint("ROBOTONGOAL", map.current_x, map.current_y);
                    }
                    map.repathflag = true;
                    
                    }
                }
            	MapDataManager.GUIOperationPrint(operation_list[next_opr]);
            	sim = sim.Get(); //sim의 움직인 정보를 받아온다.
            	SetCurrentPosition(SIM.current_x , SIM.current_y);
            
            cur_direction = sim.current_direction; //현재 방향 리턴
            System.out.println(map.current_x + " , " + map.current_y + " , " + cur_direction);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            //////////////////////////////////////////////////////////////////////////////////////////////////////////// 잘 될런지 의문이다.
            next_opr++; //인자++ 하고 (다음 명령어 받는다)
            
            if (RobotOnGoal(tmpx, tmpy)) {map.step++; check_step = true; next_opr = 0;}
            //만약 n번째 타겟지점 도착 시 step++, check_step을 true, next_opr 0으로 초기화
            
   
      }while(map.step != map.search_cnt); // step이 탐색지점 개수와 같을 때까지
   }
   
   
   
   boolean RobotOnGoal(int x, int y) {
	   if (map.current_x == x && map.current_y == y)
		   return true;
	   else
		   return false;
   }
   
   
   void MakePath() {
   	PathManager pm = new PathManager(Map.mapsize_row, Map.mapsize_col, Map.map, map.visit, map.path, map.step);
       //새로운 pathmanager 객체 생성, 바뀌는 건 visit, path, step뿐 -> 모두 한 타겟지점 도착 시 다음걸로 바뀜
       pm.CalculateOptimalPath(map.current_x, map.current_y, cur_direction); 
       // 주어진 Pathmanager 정보와 또 주어지는 현재 위치 및, 방향으로 경로 계산
       PathNode answer_pn = pm.GetData(); //계산된 경로 가져옴
       map.path = answer_pn.path; //map에 저장
   }
   

   
   void initalizeVistPath() {
	   for (int i=0; i<Map.mapsize_row; i++) {
           for (int j=0; j<Map.mapsize_col; j++) {
                             
              map.visit[map.step][i][j] = false;
              map.path[map.step][i][j] = "";
           }
        }
	   map.visit[map.step][map.current_x][map.current_y] = true;
   }
   
   
   
   
   
   
   void RandomColorBlobCreate() {
	  	  int Color_cnt=1; // 랜덤으로 생기는 Color blob 개수 -> 3개 생성
	        while(Color_cnt <= 3) { //위험지역이나, 타겟지점은 빼고 생성
	           double randomvalue = Math.random();
	           double randomvalue2 = Math.random();
	           if (Map.map[(int) (randomvalue*7)][(int) (randomvalue2*7)] == 0) {
	              Map.map[(int) (randomvalue*7)][(int) (randomvalue2*7)] = 3;
	              MapDataManager.GUIMapSetPrint("NEWCOLORBLOB", (int) (randomvalue*7), (int) (randomvalue2*7));
	              Color_cnt++;
	           }
	        } 
	    }
	void RandomHazardCreate() {
		int Hazard_cnt=1; //새로 생기는 위험지역 개수 -> 1개 생성
	      while (Hazard_cnt <= 5) { //새로생길 때 기존 위험지역, 타겟지점, 시작지점은 빼고 생성. 하다보니 양옆이 다막여 못가는 경우가 생겨 
	         //일단은 생성개수를 하나로 함
	         double randomvalue = Math.random();
	         double randomvalue2 = Math.random();
	         if (Map.map[(int) (randomvalue*7)][(int) (randomvalue2*7)] == 0) {
	            if(mdm.start_x != (int)(randomvalue*7) || mdm.start_y != (int)(randomvalue2*7)) {
	            Map.map[(int) (randomvalue*7)][(int) (randomvalue2*7)] = 5;
	            MapDataManager.GUIMapSetPrint("NEWHAZARD", (int) (randomvalue*7), (int) (randomvalue2*7));
	            Hazard_cnt++;
	            }
	            else {
	               continue;
	            }
	         }
	      }
	}
	void SetCurrentPosition(int x, int y) {
		   map.current_x = x;
		   map.current_y = y;
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
   int[] hazard_x;
   int[] hazard_y;
   
   String[][][] path; //중요 -> 3차원배열로 처음은 step별로, 즉 탐색지점 개수만큼 생성, 두 세번째는 좌표
   int search_cnt;
   int hazard_cnt;
   
   Map mp = new Map(); //map 객체 생성 후 전달.
   static MapGUIForm gui;
   
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
      hazard_x = IMDF.hazard_x;
      hazard_y = IMDF.hazard_y;
      
      
      mp.Save(row, col, start_x, start_y, map, visit, path, search_cnt, hazard_cnt, start_x, start_y, 3, search_x, search_y, hazard_x, hazard_y);
      
      ///////////////////////////////////////MAP GUI FORM/////////////////////////////////////////////
      gui = new MapGUIForm(row, col, start_x, start_y);
      for (int i=0; i<search_cnt; i++) {
         gui.setMapPoint("GOAL", search_x[i], search_y[i]);
      }
      for (int i=0; i<hazard_cnt; i++) {
         gui.setMapPoint("SEENHAZARD", hazard_x[i], hazard_y[i]);
      }
      
      
      ///////////////////////////////////////MAP GUI FORM/////////////////////////////////////////////
      
   }
   
   static void GUIOperationPrint(String operation) {
      gui.setOperation(operation);
      try {
          
          Thread.sleep(500);
          
          } catch (InterruptedException e) {
          
          e.printStackTrace();
          
          }
   }
   static void GUIMapSetPrint(String type, int x, int y) {
	   gui.setMapPoint(type, x, y);
	   gui.updateMap();
	   try {
           
           Thread.sleep(1000);
           
           } catch (InterruptedException e) {
           
           e.printStackTrace();
           
           }
   }
   
   void print() {
      for (int i=0; i<row; i++) {
         for (int j=0; j<col; j++) {
            System.out.print(map[i][j]);
         }
         System.out.println();
      }
   }
   
   
   boolean CheckRePathFlag() { //플래그 체크 함수
      boolean flag = mp.GetRePathFlag();
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
               if (nx < 0 || ny < 0 || nx >= pn.row || ny >= pn.col) {
                  continue;
               }
               // 가고 싶은 좌표가 맵 크기를 넘어 버리면 건너뛰기
               
               if (pn.visit[step][nx][ny] || pn.map[nx][ny] == 2) {
                  continue;
               }
                //이미 방문했던 점이거나 위험지역이면 건너뛰기
               
                
               pn.path[step][nx][ny] += pn.path[step][n.x][n.y];
               //가고 싶은 좌표에 현재까지 오는데 걸린 경로 저장 후
               
               while(my_direc != tmp_dir) {
                  pn.path[step][nx][ny] += "RIGHT->";
                  if(my_direc == SOUTH) my_direc = -1;
                  my_direc = direction[my_direc+1];
               } // 경로에  맞게 회전, 모두 저장
                
                
               pn.path[step][nx][ny] += "MOVE->";
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
   
   SensorNode(int x, int y) {
      this.x = x;
      this.y = y;
      
   }
}

abstract class SensorManager{ // 이거도 손봐야함.
   abstract boolean AddSensorValue(int x, int y, int dir);
   
   boolean SetRePathFalg() {
      return true;
   }
}

class HazardSensorManager extends SensorManager{
	
   
   boolean AddSensorValue(int x, int y, int dir){
	  MapDataManager mdm = null;
      int[] dx = {0, -1, 0, 1};
      int[] dy = {-1, 0, 1, 0};
      if(x+dx[dir] >= 0 && y+dy[dir] >= 0 && x+dx[dir] < Map.mapsize_row && y+dy[dir] < Map.mapsize_col) {
         if(Map.map[x+dx[dir]][y+dy[dir]] == 5) {
        	 Map.map[x+dx[dir]][y+dy[dir]] =2; 
        	 
        	mdm.GUIMapSetPrint("SEENHAZARD", x+dx[dir], y+dy[dir]);
        	
         return true; }
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
         if (x+dx[i] >= 0 && y+dy[i] >= 0 && x+dx[i] < Map.mapsize_row && y+dy[i] < Map.mapsize_col) {
            if(Map.map[x+dx[i]][y+dy[i]] == 3) { 
            	
               //System.out.println("중요 위치 : " + (x+dx[i]) + "," + (y+dy[i]));
            	MapDataManager.GUIMapSetPrint("SEENCOLORBLOB", x+dx[i], y+dy[i]); 
               
               
               
            
               return true;}
         }
         
      }
      return false;
   }
}

class PositionSensorManager extends SensorManager{
   boolean AddSensorValue(int x, int y, int dir) {
      Random random = new Random();
      int[] Rd = {0,0,0,0,0,0,1};
      
      
      int[] dx = {0, -1, 0, 1};
      int[] dy = {-1, 0, 1, 0};
      if(x+dx[dir] >= 0 && y+dy[dir] >= 0 && x+dx[dir] < Map.mapsize_row && y+dy[dir] < Map.mapsize_col) {
         if(Rd[random.nextInt(7)]==1) {SIM.current_x = x+dx[dir]; SIM.current_y = y+dy[dir]; 
          return true; }
      }
      return false;
   }
}

class SIM { //SIM 클래스. 
   final int SOUTH = 3;
   final int NORTH = 1;
   final int EAST = 2;
   final int WEST = 0;
   
   
   static int current_x;
   static int current_y;
   int current_direction;
   
   ColorBlobSensorManager cbs = new ColorBlobSensorManager();
   HazardSensorManager hs = new HazardSensorManager();
   PositionSensorManager ps = new PositionSensorManager();
   
   
   void TranslateMoveOperation(int currentx, int currenty, int current_direction) {
      
      this.current_direction = current_direction;
      current_x = currentx; 
      current_y = currenty;
   }
   
   void Move(String operation) {
      if (operation.equals("RIGHT")) {
         if (current_direction == SOUTH) current_direction = -1;
         current_direction++;
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
      return cbs.AddSensorValue(current_x, current_y, this.current_direction);
   }
   
   boolean HazardSensor() {
      
      return hs.AddSensorValue(current_x, current_y, this.current_direction);
   }
   
   boolean CurrentPositionSensor() {
      return ps.AddSensorValue(current_x, current_y, this.current_direction);
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
   int[] hazard_x;
   int[] hazard_y;

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
   
      for (int i=0; i<row; i++) {
         for (int j=0; j<col; j++) {
            map[i][j] = 0;                   //일반 지역 = 0
         
         
         }
      } //map 초기화
      
      for (int k=0; k<search_cnt; k++) {
         for (int i=0; i<this.row; i++) {
            for (int j=0; j<this.col; j++) {
                              
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
      hazard_x = new int[hazard_cnt];
      hazard_y = new int[hazard_cnt];
      
      for (int i=0; i<this.hazard_cnt; i++) {
         str = new StringTokenizer(bfr.readLine());
         int tempx = Integer.valueOf(str.nextToken());
         hazard_x[i] = tempx;
         int tempy = Integer.valueOf(str.nextToken());
         hazard_y[i] = tempy;
         this.map[tempx][tempy] = 2;  // 위험 지역 = 2
      }
      //map에 위험 지역 표시
   
   }
   
}

class Map{
   static int mapsize_row;
   static int mapsize_col;
   static int[][] map;
   
   int start_x;
   int start_y;
   boolean[][][] visit;
   String[][][] path;
   int[] search_x;
   int[] search_y;
   int[] hazard_x;
   int[] hazard_y;
   int search_cnt;
   int hazard_cnt;
   int current_x;
   int current_y;
   int currecnt_direction;
   boolean repathflag;
   int step=0;
   
   
   
   void Save(int row, int col, int start_x, int start_y, int[][] map, boolean[][][] visit, String[][][]path, int search_cnt, int hazard_cnt, int current_x, int current_y, int current_direction, int[] search_x, int[] search_y, int[] hazard_x, int[] hazard_y) {
      SetMap(row, col, start_x, start_y, map, visit, path, search_cnt, hazard_cnt, current_x, current_y, current_direction, search_x, search_y, hazard_x, hazard_y);
   }
   void SetMap(int row, int col, int start_x, int start_y, int[][] map, boolean[][][] visit, String[][][]path, int search_cnt, int hazard_cnt, int current_x, int current_y, int current_direction, int[] search_x, int[] search_y, int[] hazard_x, int[] hazard_y) {
      mapsize_row = row;
      mapsize_col = col;
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
      this.hazard_x = hazard_x;
      this.hazard_y = hazard_y;
      Map.map = map;
   }
   
   boolean GetRePathFlag() {
      return repathflag;
   }
   
   Map GetMap() {
      return this;
   }
}