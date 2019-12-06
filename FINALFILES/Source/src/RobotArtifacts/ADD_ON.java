package RobotArtifacts;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

import mapArtifacts.MapDataManager;

//일반 지역 = 0
//탐색 지역 = 1
//기존 위험 지역 = 2
//아직 발견되지 않은 Color blob = 3
//이동하다 발견된 Color blob = 4
//아직 발견되지 않은 위험 지역 = 5 -> 발견되면 2로 바꾼다.
public class ADD_ON {
   public static void main(String[] args) throws IOException{
      RobotOperationManager rom = new RobotOperationManager();
      System.exit(0);
   }
}

class RobotOperationManager { // 시퀀스 다이어그램에서 볼 수 있듯이 이 클래스가 모든거 다 지시
   
   MapDataManager mdm = new MapDataManager();	// mdm에서 map 객체 , InputMapDataForm 객체 다 있음.
   SIM sim = new SIM(); //sim 객체 생성
   
   
   
   int next_opr;
   boolean check_step;
   

   
   int[] dx = {0, -1, 0, 1};
   int[] dy = {-1, 0, 1, 0};
   
   int[][] map_temp = mdm.GetMap();
   
   String[][][] answer_pn = new String[mdm.GetMapSearchX().length][mdm.GetMapRow()][mdm.GetMapCol()];
   
   
   
   String[] operation_list; // 명령어 하나씩 받아온다.
   
   RobotOperationManager() throws IOException {
      final int SOUTH = 3; // 회전 할 때마다 1씩 준다고 생각 , 여기서 선언해준건 명시해주려고
      
      RandomColorBlobCreate();
      RandomHazardCreate();
      MapDataManager.gui.setSnapshot();
      mdm.SetMapCurrentDir(SOUTH);//시작 방향을 설정
      
      
      next_opr=0; //operation_list 인덱스 하나씩 늘리다가 한 타겟지점 도착하면 초기화하는 변수
      check_step = true; // 한 타겟지점 도착한 거 체크해주는 변수
      SetCurrentPosition(mdm.GetMapStartX() , mdm.GetMapStartY());
      
      sim.TranslateMoveOperation(mdm.GetMapCurrentX(), mdm.GetMapCurrentY(), mdm.GetMapCurrentDirection()); //나중에 이거 함수 바꿔야한다.
      //sim에게 현재 상태를 입력해줌
      
      do {//일단 한번은 무조건 path생성
    	    for(int i=0; i<4; i++) {
    	    	if(mdm.GetMapCurrentX()+dx[i] >= 0 && mdm.GetMapCurrentY()+dy[i] >= 0 && mdm.GetMapCurrentX()+dx[i] < mdm.GetMapRow() && mdm.GetMapCurrentY()+dy[i] < mdm.GetMapCol()) {
    	    		mdm.GetColorBolobSensor(sim, mdm.GetMapCurrentX()+dx[i], mdm.GetMapCurrentY()+dy[i], mdm.GetMapCurrentDirection());
    	    	}
    	    }
            if(mdm.GetMapCurrentX()+dx[mdm.GetMapCurrentDirection()] >= 0 && mdm.GetMapCurrentY()+dy[mdm.GetMapCurrentDirection()] >= 0 && mdm.GetMapCurrentX()+dx[mdm.GetMapCurrentDirection()] < mdm.GetMapRow() && mdm.GetMapCurrentY()+dy[mdm.GetMapCurrentDirection()] < mdm.GetMapCol()) {
            	if (mdm.GetHazardSensor(sim, mdm.GetMapCurrentX()+dx[mdm.GetMapCurrentDirection()], mdm.GetMapCurrentY()+dy[mdm.GetMapCurrentDirection()], mdm.GetMapCurrentDirection())) { //Hazard point 발견 시
            		mdm.SetMapFlag(true);
               
            		next_opr = 0;
            	}
            }
            boolean flag = mdm.CheckRePathFlag(); //플래그 체크
            if (flag || check_step) { // 플래그가 1이거나 한 타겟지점 도착 시
            	map_temp = mdm.GetMap();
            
            	initializeVistPath();

            	MakePath();
            	check_step = false; //기본은 false
            	mdm.SetMapFlag(false);
            	next_opr = 0;
            }
            
            
            int tmpx = mdm.GetMapSearchX()[mdm.GetMapStep()]; // n번째 타겟지점
            int tmpy = mdm.GetMapSearchY()[mdm.GetMapStep()];
            operation_list = answer_pn[mdm.GetMapStep()][tmpx][tmpy].split("->"); //n번째 타겟지점에 저장된 경로를 "->"을 기준으로 split
            System.out.println(next_opr + " , " + operation_list[next_opr]);
            
            
            
            
            	sim.Move(operation_list[next_opr]); //명령어 하나씩 받아와서 sim에게 명령어 전달
            	sim = sim.Get(); //sim의 움직인 정보를 받아온다.
            	int tempx1 = mdm.GetMapCurrentX();
            	int tempy1 = mdm.GetMapCurrentY();
            	SetCurrentPosition(sim.GetCurrentX() , sim.GetCurrentY());
            	if (operation_list[next_opr].equals("MOVE")) {
            		Node tmpnode = mdm.GetCurrentPositionSensor(sim, mdm.GetMapCurrentX(), mdm.GetMapCurrentY(), mdm.GetMapCurrentDirection());
            		int tempx2 = mdm.GetMapCurrentX();
                	int tempy2 = mdm.GetMapCurrentY();
                    if (tempx2 != tmpnode.x || tempy2 != tmpnode.y) {
                    System.out.println("예상한 위치와 다릅니다.");
                    System.out.println("예상위치 : " + tempx2 + "," + tempy2 + "실제 위치 : " + sim.GetCurrentX() + "," + sim.GetCurrentY());
                    MapDataManager.GUIMapSetPrint(MapDataManager.gui.getSnapshot2Point(tempx1, tempy1), tempx1, tempy1);
                    SetCurrentPosition(sim.GetCurrentX() , sim.GetCurrentY());
                    MapDataManager.GUIMapSetPrint("ROBOT", mdm.GetMapCurrentX(), mdm.GetMapCurrentY());
                    if (RobotOnGoal(tmpx, tmpy)) {
                    	MapDataManager.GUIMapSetPrint("ROBOTONGOAL", mdm.GetMapCurrentX(), mdm.GetMapCurrentY());
                    }
                    mdm.SetMapFlag(true);
                    
                    }
                    else {
                    	MapDataManager.GUIOperationPrint(operation_list[next_opr]);
                    }
                }
            	else {
            	MapDataManager.GUIOperationPrint(operation_list[next_opr]);
            	}
            	MapDataManager.gui.setSnapshot2();;
            	SetCurrentPosition(sim.GetCurrentX() , sim.GetCurrentY());
            mdm.SetMapCurrentDir(sim.GetCurrentDir());//현재 방향 리턴
            
            System.out.println(mdm.GetMapCurrentX() + " , " + mdm.GetMapCurrentY() + " , " + mdm.GetMapCurrentDirection());
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            //////////////////////////////////////////////////////////////////////////////////////////////////////////// 잘 될런지 의문이다.
            next_opr++; //인자++ 하고 (다음 명령어 받는다)
            
            if (RobotOnGoal(tmpx, tmpy)) {mdm.SetMapStep(mdm.GetMapStep()+1); check_step = true; next_opr = 0;}
            //만약 n번째 타겟지점 도착 시 step++, check_step을 true, next_opr 0으로 초기화
            
   
      }while(mdm.GetMapStep() != mdm.GetMapSearchCount()); // step이 탐색지점 개수와 같을 때까지
   }
   
   
   
   boolean RobotOnGoal(int x, int y) {
	   if (mdm.GetMapCurrentX() == x && mdm.GetMapCurrentY() == y)
		   return true;
	   else
		   return false;
   }
   
   
   void MakePath() {
   	PathManager pm = new PathManager(mdm.GetMapRow(), mdm.GetMapCol(), map_temp, mdm.GetMapVisit(), answer_pn, mdm.GetMapStep());
       //새로운 pathmanager 객체 생성, 바뀌는 건 visit, path, step뿐 -> 모두 한 타겟지점 도착 시 다음걸로 바뀜
       pm.CalculateOptimalPath(mdm.GetMapCurrentX(), mdm.GetMapCurrentY(), mdm.GetMapCurrentDirection()); 
       // 주어진 Pathmanager 정보와 또 주어지는 현재 위치 및, 방향으로 경로 계산
       answer_pn = pm.GetPath(); //계산된 경로 가져옴
       
       
   }
   

   
   void initializeVistPath() {
	   for (int i=0; i<mdm.GetMapRow(); i++) {
           for (int j=0; j<mdm.GetMapCol(); j++) {
                  
        	   answer_pn[mdm.GetMapStep()][i][j] = "";
        	   mdm.SetMapVisit(mdm.GetMapStep(), i, j, false);
           }
        }
	   mdm.SetMapVisit(mdm.GetMapStep(), mdm.GetMapCurrentX(), mdm.GetMapCurrentY(), true);
   }
   
   
   
   
   
   
   void RandomColorBlobCreate() {
	  	  int Color_cnt=1; // 랜덤으로 생기는 Color blob 개수 -> 3개 생성
	        while(Color_cnt <= 3) { //위험지역이나, 타겟지점은 빼고 생성
	           double randomvalue = Math.random();
	           double randomvalue2 = Math.random();
	           if (mdm.GetMapPointData((int) (randomvalue*7),(int) (randomvalue2*7)) == 0) {
	        	   mdm.SetMapPointData((int) (randomvalue*7),(int) (randomvalue2*7), 3);
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
	         if (mdm.GetMapPointData((int) (randomvalue*7),(int) (randomvalue2*7)) == 0) {
	            if(mdm.start_x != (int)(randomvalue*7) || mdm.start_y != (int)(randomvalue2*7)) {
	            	mdm.SetMapPointData((int) (randomvalue*7),(int) (randomvalue2*7), 5);
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
		   mdm.SetMapCurrentX(x);
		   mdm.SetMapCurrentY(y);
	   }
}


class PathManager {
      final int SOUTH = 3;
      final int NORTH = 1;
      final int EAST = 2;
      final int WEST = 0; //각 방향을 숫자로 지정
      
      Path pn;
      
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
         
         pn = new Path(path);    //초기화
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
               if (nx < 0 || ny < 0 || nx >= row || ny >= col) {
                  continue;
               }
               // 가고 싶은 좌표가 맵 크기를 넘어 버리면 건너뛰기
               
               if (visit[step][nx][ny] || map[nx][ny] == 2) {
                  continue;
               }
                //이미 방문했던 점이거나 위험지역이면 건너뛰기
               
               pn.SetPathPoint(step, nx, ny, pn.GetPathPoint(step, n.x, n.y)); 
               
               //가고 싶은 좌표에 현재까지 오는데 걸린 경로 저장 후
               
               while(my_direc != tmp_dir) {
                  pn.SetPathPoint(step, nx, ny, "RIGHT->");
                  if(my_direc == SOUTH) my_direc = -1;
                  my_direc = direction[my_direc+1];
               } // 경로에  맞게 회전, 모두 저장
                
               pn.SetPathPoint(step, nx, ny, "MOVE->"); 
               
               // 1 움직이는 것까지 최종 저장
                
               q.add(new Node(nx, ny, my_direc)); //그 노드를 다시 큐에 삽입
            
                //pn.path[step][nx][ny] = pn.path[step][n.x][n.y] + "(" + nx + " , " + ny + ")";
               
               visit[step][nx][ny] = true;
                
            }
         }
      }
      String[][][] GetPath(){
      return pn.getPath();
      }

}
class Path {
   
   String[][][] path;
   Path (String[][][] path){
     
      this.path = path;
   }
   String[][][] getPath() {
	   return this.path;
   }
   void SetPathPoint(int step, int x, int y, String s) {
	   this.path[step][x][y] += s;
   }
   String GetPathPoint(int step, int x, int y) {
	   return this.path[step][x][y];
   }
}




   
  





























