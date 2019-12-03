package Fighting;




import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

//�Ϲ� ���� = 0
//Ž�� ���� = 1
//���� ���� ���� = 2
//���� �߰ߵ��� ���� Color blob = 3
//�̵��ϴ� �߰ߵ� Color blob = 4
//���� �߰ߵ��� ���� ���� ���� = 5 -> �߰ߵǸ� 2�� �ٲ۴�.
public class ADD_ON {
   public static void main(String[] args) throws IOException{
      RobotOperationManager rom = new RobotOperationManager();
      return;
   }
}

class RobotOperationManager { // ������ ���̾�׷����� �� �� �ֵ��� �� Ŭ������ ���� �� ����
   
   MapDataManager mdm = new MapDataManager();	// mdm���� map ��ü , InputMapDataForm ��ü �� ����.
   SIM sim = new SIM(); //sim ��ü ����
   Map map = mdm.GetMap(); // mdm���� ������� map ��ü ���� �޾ƿ�.
   
   int cur_direction;
   int next_opr;
   boolean check_step;
   
   String[] operation_list; // ��ɾ� �ϳ��� �޾ƿ´�.
   RobotOperationManager() throws IOException {
      final int SOUTH = 3; // ȸ�� �� ������ 1�� �شٰ� ���� , ���⼭ �������ذ� ������ַ���
      
      RandomColorBlobCreate();
      RandomHazardCreate();
      MapDataManager.gui.setSnapshot();
      cur_direction = SOUTH; //���� ������ ����
      
      next_opr=0; //operation_list �ε��� �ϳ��� �ø��ٰ� �� Ÿ������ �����ϸ� �ʱ�ȭ�ϴ� ����
      check_step = true; // �� Ÿ������ ������ �� üũ���ִ� ����
      SetCurrentPosition(map.start_x , map.start_y);
      
      sim.TranslateMoveOperation(map.current_x, map.current_y, map.currecnt_direction); //���߿� �̰� �Լ� �ٲ���Ѵ�.
      //sim���� ���� ���¸� �Է�����
      
      do {//�ϴ� �ѹ��� ������ path����
            sim.ColorBlobSensor();

            if (sim.HazardSensor()) { //Hazard point �߰� ��
               map.repathflag = true; //�÷��� 1�� ����
               next_opr = 0;
            }
       
            boolean flag = mdm.CheckRePathFlag(); //�÷��� üũ
            if (flag || check_step) { // �÷��װ� 1�̰ų� �� Ÿ������ ���� ��
            	map = mdm.GetMap(); //map �ٽ� �޾ƿ���
            
            	initalizeVistPath();

            	MakePath();
            	check_step = false; //�⺻�� false
            	map.repathflag = false;
            	next_opr = 0;
            }
            
            
            int tmpx = map.search_x[map.step]; // n��° Ÿ������
            int tmpy = map.search_y[map.step];
            operation_list = map.path[map.step][tmpx][tmpy].split("->"); //n��° Ÿ�������� ����� ��θ� "->"�� �������� split
            System.out.println(next_opr + " , " + operation_list[next_opr]);
            
            
            
            
            	sim.Move(operation_list[next_opr]); //��ɾ� �ϳ��� �޾ƿͼ� sim���� ��ɾ� ����
            	
            	if (operation_list[next_opr].equals("MOVE")) {
                    if (sim.CurrentPositionSensor()) {
                    System.out.println("������ ��ġ�� �ٸ��ϴ�.");
                    System.out.println("������ġ : " + map.current_x + "," + map.current_y + "���� ��ġ : " + SIM.current_x + "," + SIM.current_y);
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
            	sim = sim.Get(); //sim�� ������ ������ �޾ƿ´�.
            	SetCurrentPosition(SIM.current_x , SIM.current_y);
            
            cur_direction = sim.current_direction; //���� ���� ����
            System.out.println(map.current_x + " , " + map.current_y + " , " + cur_direction);
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            //////////////////////////////////////////////////////////////////////////////////////////////////////////// �� �ɷ��� �ǹ��̴�.
            next_opr++; //����++ �ϰ� (���� ��ɾ� �޴´�)
            
            if (RobotOnGoal(tmpx, tmpy)) {map.step++; check_step = true; next_opr = 0;}
            //���� n��° Ÿ������ ���� �� step++, check_step�� true, next_opr 0���� �ʱ�ȭ
            
   
      }while(map.step != map.search_cnt); // step�� Ž������ ������ ���� ������
   }
   
   
   
   boolean RobotOnGoal(int x, int y) {
	   if (map.current_x == x && map.current_y == y)
		   return true;
	   else
		   return false;
   }
   
   
   void MakePath() {
   	PathManager pm = new PathManager(Map.mapsize_row, Map.mapsize_col, Map.map, map.visit, map.path, map.step);
       //���ο� pathmanager ��ü ����, �ٲ�� �� visit, path, step�� -> ��� �� Ÿ������ ���� �� �����ɷ� �ٲ�
       pm.CalculateOptimalPath(map.current_x, map.current_y, cur_direction); 
       // �־��� Pathmanager ������ �� �־����� ���� ��ġ ��, �������� ��� ���
       PathNode answer_pn = pm.GetData(); //���� ��� ������
       map.path = answer_pn.path; //map�� ����
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
	  	  int Color_cnt=1; // �������� ����� Color blob ���� -> 3�� ����
	        while(Color_cnt <= 3) { //���������̳�, Ÿ�������� ���� ����
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
		int Hazard_cnt=1; //���� ����� �������� ���� -> 1�� ����
	      while (Hazard_cnt <= 5) { //���λ��� �� ���� ��������, Ÿ������, ���������� ���� ����. �ϴٺ��� �翷�� �ٸ��� ������ ��찡 ���� 
	         //�ϴ��� ���������� �ϳ��� ��
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

class MapDataManager { //�ʿ��� ������ ������ ����, ��κ� �̸����� ���� ���� ����
   int[][] map;
   
   boolean[][][] visit; //�߿� -> 3�����迭�� ó���� step����, �� Ž������ ������ŭ ����, �� ����°�� ��ǥ
   int row;
   int col;
   int start_x;
   int start_y;
   int[] search_x;
   int[] search_y;
   int[] hazard_x;
   int[] hazard_y;
   
   String[][][] path; //�߿� -> 3�����迭�� ó���� step����, �� Ž������ ������ŭ ����, �� ����°�� ��ǥ
   int search_cnt;
   int hazard_cnt;
   
   Map mp = new Map(); //map ��ü ���� �� ����.
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
   
   
   boolean CheckRePathFlag() { //�÷��� üũ �Լ�
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
      final int WEST = 0; //�� ������ ���ڷ� ����
      
      PathNode pn;
      
      int[] dx = {0, -1, 0, 1}; // i�� 1,3 �� �� x�� ������
      int[] dy = {-1, 0, 1, 0}; // i�� 0,2 �� �� y�� ������
      int[] direction = {0, 1, 2, 3}; // ȸ���� ������ 1�� ��.
      
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

      void CalculateOptimalPath(int x, int y, int my_dir) { //��� ��� �Լ�
         
         pn = new PathNode(row, col, map, visit, path);    //�ʱ�ȭ
         Queue<Node> q = new LinkedList<Node>(); // ť ����
         int my_direc = my_dir; //���� ���� ����
         
         q.add(new Node(x, y, my_direc)); //ť�� �Է¹��� ������ ������ ��� ����
         int tmp_dir=0; //�������� �����̰� ���� ��ǥ�� ���� ���� ����
         while (!q.isEmpty()) {
            Node n = q.poll(); // ť���� �ϳ� ������
            
            
            for (int i=0; i<4; i++) { // �������� �� ������.
               my_direc = n.dir;
               int nx = n.x + dx[i];
               int ny = n.y + dy[i];
               
               if (i==3) { // ���� ���� ��ǥ�� ���� ����
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
               // ���� ���� ��ǥ�� �� ũ�⸦ �Ѿ� ������ �ǳʶٱ�
               
               if (pn.visit[step][nx][ny] || pn.map[nx][ny] == 2) {
                  continue;
               }
                //�̹� �湮�ߴ� ���̰ų� ���������̸� �ǳʶٱ�
               
                
               pn.path[step][nx][ny] += pn.path[step][n.x][n.y];
               //���� ���� ��ǥ�� ������� ���µ� �ɸ� ��� ���� ��
               
               while(my_direc != tmp_dir) {
                  pn.path[step][nx][ny] += "RIGHT->";
                  if(my_direc == SOUTH) my_direc = -1;
                  my_direc = direction[my_direc+1];
               } // ��ο�  �°� ȸ��, ��� ����
                
                
               pn.path[step][nx][ny] += "MOVE->";
               // 1 �����̴� �ͱ��� ���� ����
                
               q.add(new Node(nx, ny, my_direc)); //�� ��带 �ٽ� ť�� ����
            
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

class Node { //�̰� ���߿��� ���־��� Ŭ����, �˾Ƽ� ���ĺ�
   int x;
   int y;
   int dir;
   Node(int x, int y, int dir) {
      this.x = x;
      this.y = y;
      this.dir = dir;
   }
}

class SensorNode { //�Ⱦ���. ���߿� ���ش�
   int x;
   int y;
   
   SensorNode(int x, int y) {
      this.x = x;
      this.y = y;
      
   }
}

abstract class SensorManager{ // �̰ŵ� �պ�����.
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
      //���� �ٶ󺸴� ������ ��ĭ ���� ���������ΰ�
      
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
            	
               //System.out.println("�߿� ��ġ : " + (x+dx[i]) + "," + (y+dy[i]));
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

class SIM { //SIM Ŭ����. 
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




























class InputMapDataForm //�Է� �� Ŭ����
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
      System.out.print("ID�� �Է��ϼ��� : ");
      
   }
   
   boolean Verify() {
      if (id == 1111) {
         System.out.println("Ȯ�εǾ����ϴ�!");
         return true;
      }
      else {
         System.out.println("���� ��ȣ�Դϴ�");
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
      this.start_y = Integer.valueOf(str.nextToken()); // Map ũ��, ���� ���� �Է�
   
      str = new StringTokenizer(bfr.readLine());
      this.search_cnt = Integer.valueOf(str.nextToken()); // Ž�� ��ġ ���� �� �Է�
   
      str = new StringTokenizer(bfr.readLine());
      this.hazard_cnt = Integer.valueOf(str.nextToken()); // ���� ��ġ ���� �� �Է�
   
      this.map = new int[row+1][col+1];
      this.visit = new boolean[search_cnt][row+1][col+1];
      this.path = new String[search_cnt][row+1][col+1];
   
      for (int i=0; i<row; i++) {
         for (int j=0; j<col; j++) {
            map[i][j] = 0;                   //�Ϲ� ���� = 0
         
         
         }
      } //map �ʱ�ȭ
      
      for (int k=0; k<search_cnt; k++) {
         for (int i=0; i<this.row; i++) {
            for (int j=0; j<this.col; j++) {
                              
               this.visit[k][i][j] = false;
               this.path[k][i][j] = "";
            }
         }
      }
      // visit, path �ʱ�ȭ
      search_x = new int[search_cnt];
      search_y = new int[search_cnt];
   
      for (int i=0; i<this.search_cnt; i++) {
         this.str = new StringTokenizer(bfr.readLine());
         int tempx = Integer.valueOf(str.nextToken());
         search_x[i] = tempx;
         int tempy = Integer.valueOf(str.nextToken());
         search_y[i] = tempy;
         this.map[tempx][tempy] = 1; // Ž�� ���� = 1
         
      }
      //map�� Ž�� ���� ǥ��
      hazard_x = new int[hazard_cnt];
      hazard_y = new int[hazard_cnt];
      
      for (int i=0; i<this.hazard_cnt; i++) {
         str = new StringTokenizer(bfr.readLine());
         int tempx = Integer.valueOf(str.nextToken());
         hazard_x[i] = tempx;
         int tempy = Integer.valueOf(str.nextToken());
         hazard_y[i] = tempy;
         this.map[tempx][tempy] = 2;  // ���� ���� = 2
      }
      //map�� ���� ���� ǥ��
   
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