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
      System.exit(0);
   }
}

class RobotOperationManager { // ������ ���̾�׷����� �� �� �ֵ��� �� Ŭ������ ���� �� ����
   
   MapDataManager mdm = new MapDataManager();	// mdm���� map ��ü , InputMapDataForm ��ü �� ����.
   SIM sim = new SIM(); //sim ��ü ����
   
   
   
   int next_opr;
   boolean check_step;
   

   
   int[] dx = {0, -1, 0, 1};
   int[] dy = {-1, 0, 1, 0};
   
   int[][] map_temp = mdm.GetMap();
   
   String[][][] answer_pn = new String[mdm.GetMapSearchX().length][mdm.GetMapRow()][mdm.GetMapCol()];
   
   
   
   String[] operation_list; // ��ɾ� �ϳ��� �޾ƿ´�.
   
   RobotOperationManager() throws IOException {
      final int SOUTH = 3; // ȸ�� �� ������ 1�� �شٰ� ���� , ���⼭ �������ذ� ������ַ���
      
      RandomColorBlobCreate();
      RandomHazardCreate();
      MapDataManager.gui.setSnapshot();
      mdm.SetMapCurrentDir(SOUTH);//���� ������ ����
      
      
      next_opr=0; //operation_list �ε��� �ϳ��� �ø��ٰ� �� Ÿ������ �����ϸ� �ʱ�ȭ�ϴ� ����
      check_step = true; // �� Ÿ������ ������ �� üũ���ִ� ����
      SetCurrentPosition(mdm.GetMapStartX() , mdm.GetMapStartY());
      
      sim.TranslateMoveOperation(mdm.GetMapCurrentX(), mdm.GetMapCurrentY(), mdm.GetMapCurrentDirection()); //���߿� �̰� �Լ� �ٲ���Ѵ�.
      //sim���� ���� ���¸� �Է�����
      
      do {//�ϴ� �ѹ��� ������ path����
    	    for(int i=0; i<4; i++) {
    	    	if(mdm.GetMapCurrentX()+dx[i] >= 0 && mdm.GetMapCurrentY()+dy[i] >= 0 && mdm.GetMapCurrentX()+dx[i] < mdm.GetMapRow() && mdm.GetMapCurrentY()+dy[i] < mdm.GetMapCol()) {
    	    		mdm.GetColorBolobSensor(sim, mdm.GetMapCurrentX()+dx[i], mdm.GetMapCurrentY()+dy[i], mdm.GetMapCurrentDirection());
    	    	}
    	    }
            if(mdm.GetMapCurrentX()+dx[mdm.GetMapCurrentDirection()] >= 0 && mdm.GetMapCurrentY()+dy[mdm.GetMapCurrentDirection()] >= 0 && mdm.GetMapCurrentX()+dx[mdm.GetMapCurrentDirection()] < mdm.GetMapRow() && mdm.GetMapCurrentY()+dy[mdm.GetMapCurrentDirection()] < mdm.GetMapCol()) {
            	if (mdm.GetHazardSensor(sim, mdm.GetMapCurrentX()+dx[mdm.GetMapCurrentDirection()], mdm.GetMapCurrentY()+dy[mdm.GetMapCurrentDirection()], mdm.GetMapCurrentDirection())) { //Hazard point �߰� ��
            		mdm.SetMapFlag(true);
               
            		next_opr = 0;
            	}
            }
            boolean flag = mdm.CheckRePathFlag(); //�÷��� üũ
            if (flag || check_step) { // �÷��װ� 1�̰ų� �� Ÿ������ ���� ��
            	map_temp = mdm.GetMap();
            
            	initalizeVistPath();

            	MakePath();
            	check_step = false; //�⺻�� false
            	mdm.SetMapFlag(false);
            	next_opr = 0;
            }
            
            
            int tmpx = mdm.GetMapSearchX()[mdm.GetMapStep()]; // n��° Ÿ������
            int tmpy = mdm.GetMapSearchY()[mdm.GetMapStep()];
            operation_list = answer_pn[mdm.GetMapStep()][tmpx][tmpy].split("->"); //n��° Ÿ�������� ����� ��θ� "->"�� �������� split
            System.out.println(next_opr + " , " + operation_list[next_opr]);
            
            
            
            
            	sim.Move(operation_list[next_opr]); //��ɾ� �ϳ��� �޾ƿͼ� sim���� ��ɾ� ����
            	sim = sim.Get(); //sim�� ������ ������ �޾ƿ´�.
            	int tempx1 = mdm.GetMapCurrentX();
            	int tempy1 = mdm.GetMapCurrentY();
            	SetCurrentPosition(sim.GetCurrentX() , sim.GetCurrentY());
            	if (operation_list[next_opr].equals("MOVE")) {
            		Node tmpnode = mdm.GetCurrentPositionSensor(sim, mdm.GetMapCurrentX(), mdm.GetMapCurrentY(), mdm.GetMapCurrentDirection());
            		int tempx2 = mdm.GetMapCurrentX();
                	int tempy2 = mdm.GetMapCurrentY();
                    if (tempx2 != tmpnode.x || tempy2 != tmpnode.y) {
                    System.out.println("������ ��ġ�� �ٸ��ϴ�.");
                    System.out.println("������ġ : " + tempx2 + "," + tempy2 + "���� ��ġ : " + sim.GetCurrentX() + "," + sim.GetCurrentY());
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
            mdm.SetMapCurrentDir(sim.GetCurrentDir());//���� ���� ����
            
            System.out.println(mdm.GetMapCurrentX() + " , " + mdm.GetMapCurrentY() + " , " + mdm.GetMapCurrentDirection());
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            //////////////////////////////////////////////////////////////////////////////////////////////////////////// �� �ɷ��� �ǹ��̴�.
            next_opr++; //����++ �ϰ� (���� ��ɾ� �޴´�)
            
            if (RobotOnGoal(tmpx, tmpy)) {mdm.SetMapStep(mdm.GetMapStep()+1); check_step = true; next_opr = 0;}
            //���� n��° Ÿ������ ���� �� step++, check_step�� true, next_opr 0���� �ʱ�ȭ
            
   
      }while(mdm.GetMapStep() != mdm.GetMapSearchCount()); // step�� Ž������ ������ ���� ������
   }
   
   
   
   boolean RobotOnGoal(int x, int y) {
	   if (mdm.GetMapCurrentX() == x && mdm.GetMapCurrentY() == y)
		   return true;
	   else
		   return false;
   }
   
   
   void MakePath() {
   	PathManager pm = new PathManager(mdm.GetMapRow(), mdm.GetMapCol(), map_temp, mdm.GetMapVisit(), answer_pn, mdm.GetMapStep());
       //���ο� pathmanager ��ü ����, �ٲ�� �� visit, path, step�� -> ��� �� Ÿ������ ���� �� �����ɷ� �ٲ�
       pm.CalculateOptimalPath(mdm.GetMapCurrentX(), mdm.GetMapCurrentY(), mdm.GetMapCurrentDirection()); 
       // �־��� Pathmanager ������ �� �־����� ���� ��ġ ��, �������� ��� ���
       answer_pn = pm.GetPath(); //���� ��� ������
       
       
   }
   

   
   void initalizeVistPath() {
	   for (int i=0; i<mdm.GetMapRow(); i++) {
           for (int j=0; j<mdm.GetMapCol(); j++) {
                  
        	   answer_pn[mdm.GetMapStep()][i][j] = "";
        	   mdm.SetMapVisit(mdm.GetMapStep(), i, j, false);
           }
        }
	   mdm.SetMapVisit(mdm.GetMapStep(), mdm.GetMapCurrentX(), mdm.GetMapCurrentY(), true);
   }
   
   
   
   
   
   
   void RandomColorBlobCreate() {
	  	  int Color_cnt=1; // �������� ����� Color blob ���� -> 3�� ����
	        while(Color_cnt <= 3) { //���������̳�, Ÿ�������� ���� ����
	           double randomvalue = Math.random();
	           double randomvalue2 = Math.random();
	           if (mdm.GetMapmap((int) (randomvalue*7),(int) (randomvalue2*7)) == 0) {
	        	   mdm.SetMapmap((int) (randomvalue*7),(int) (randomvalue2*7), 3);
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
	         if (mdm.GetMapmap((int) (randomvalue*7),(int) (randomvalue2*7)) == 0) {
	            if(mdm.start_x != (int)(randomvalue*7) || mdm.start_y != (int)(randomvalue2*7)) {
	            	mdm.SetMapmap((int) (randomvalue*7),(int) (randomvalue2*7), 5);
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

class MapDataManager { //�ʿ��� ������ ������ ����, ��κ� �̸����� ���� ���� ����
	   int row;
	   int col;
	   int start_x;
	   int start_y;
	   int search_cnt;
	   int hazard_cnt;

	   int[][] map;
	   boolean[][][] visit; //�߿� -> 3�����迭�� ó���� step����, �� Ž������ ������ŭ ����, �� ����°�� ��ǥ
	   
	   
	   int[] search_x;
	   int[] search_y;
	   int[] hazard_x;
	   int[] hazard_y;

	   
	   Map mp = new Map(); //map ��ü ���� �� ����.
	   HazardSensorManager hsm = new HazardSensorManager();
	   ColorBlobSensorManager cbsm = new ColorBlobSensorManager();
	   PositionSensorManager psm = new PositionSensorManager();
	   
	   
	   
	   static MapGUIForm gui;
	   
	   MapDataManager() throws IOException {
	      // TODO Auto-generated method stub
	      InputMapDataForm IMDF = new InputMapDataForm();
	      
	      row = IMDF.row;
	      col = IMDF.col;
	      start_x = IMDF.start_x;
	      start_y = IMDF.start_y;
	      search_cnt = IMDF.search_cnt;
	      hazard_cnt = IMDF.hazard_cnt;
	      
	      search_x = IMDF.search_x;
	      search_y = IMDF.search_y;
	      hazard_x = IMDF.hazard_x;
	      hazard_y = IMDF.hazard_y;
	      
	      InitMapData();
	      
	      mp.Save(row, col, start_x, start_y, map, visit, search_cnt, hazard_cnt, start_x, start_y, 3, search_x, search_y, hazard_x, hazard_y);
	      
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
	   void InitMapData() {
		      map = new int[row+1][col+1];
		      visit = new boolean[search_cnt][row+1][col+1];
		      
		      //map �ʱ�ȭ
		      for (int i=0; i<row; i++) {
		         for (int j=0; j<col; j++) {
		            map[i][j] = 0;    //�Ϲ� ���� = 0
		         }
		      } 
		      // visit, path �ʱ�ȭ
		      for (int k=0; k<search_cnt; k++) {
		         for (int i=0; i<row; i++) {
		            for (int j=0; j<col; j++) {
		                              
		               visit[k][i][j] = false;
		               
		            }
		         }
		      }
		      // map ��Ŀ� Ž�� ���� ǥ��
		      for (int i=0; i<this.search_cnt; i++) {
		         this.map[search_x[i]][search_y[i]] = 1; // Ž�� ���� = 1
		      }
		      // map ��Ŀ� ���� ���� ǥ��
		      for (int i=0; i<this.hazard_cnt; i++) {
		         this.map[hazard_x[i]][hazard_y[i]] = 2;  // ���� ���� = 2
		      }
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
	   
	   int[][] GetMap() {
	      return mp.GetMap();
	   }
	   int GetMapmap(int x, int y) {
		   return mp.Getmap(x, y);
	   }
	   int GetMapCurrentDirection() {
		   return mp.GetcurrentDir();
	   }
	   
	   int GetMapStartX() {
		   return mp.GetStartX();
	   }
	   int GetMapStartY() {
		   return mp.GetStartY();
	   }
	   int GetMapCurrentX() {
		   return mp.GetCurrentX();
	   }
	   int GetMapCurrentY() {
		   return mp.GetCurrentY();
	   }
	   int GetMapStep() {
		   return mp.GetStep();
	   }
	   
	   boolean[][][] GetMapVisit() {
		   return mp.GetVisit();
	   }
	   int[] GetMapSearchX() {
		   return mp.GetSearchX();
	   }
	   int[] GetMapSearchY() {
		   return mp.GetSearchY();
	   }
	   int GetMapRow() {
		   return mp.GetMapRow();
	   }
	   int GetMapCol() {
		   return mp.GetMapCol();
	   }
	   int GetMapSearchCount() {
		   return mp.GetSearchCount();
	   }
	   boolean GetColorBolobSensor(SIM sim, int x, int y, int dir) {
		   if(cbsm.GetSensorValue(sim, x, y, dir, mp.Getmap(x, y))) {
			   mp.Setmap(x, y, 4);
			   GUIMapSetPrint("SEENCOLORBLOB", x, y);
			   return true;
		   }
		   return false;
	   }
	   boolean GetHazardSensor(SIM sim,int x, int y, int dir) {
		   if(hsm.GetSensorValue(sim, x, y, dir, mp.Getmap(x, y))) {
			   mp.Setmap(x, y, 2);
			   GUIMapSetPrint("SEENHAZARD", x, y);
			   return true;
		   }
		   return false;
	   }
	   Node GetCurrentPositionSensor(SIM sim, int x, int y, int dir) {
		   return psm.GetSensorValue(sim, x, y, dir, mp.GetMapRow(), mp.GetMapCol());
	   }
	   void SetMapCurrentDir(int dir) {
		   mp.SetCurrentDirection(dir);
	   }
	   void SetMapStartX(int x) {
		   
	   }
	   void SetMapStartY(int y) {
		   
	   }
	   void SetMapFlag(boolean flag) {
		   mp.SetFlag(flag);
	   }
	   void SetMapStep(int step) {
		   mp.SetStep(step);
	   }
	   
	   void SetMapVisit(int step, int x, int y, boolean b) {
		   mp.SetVisit(step, x, y, b);
	   }
	   void SetMapCurrentX(int x) {
		   mp.SetCurrentX(x);
	   }
	   void SetMapCurrentY(int y) {
		   mp.SetCurrentY(y);
	   }
	   void SetMapmap(int x, int y, int inf) {
		   mp.Setmap(x,y, inf);
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
         
         pn = new PathNode(path);    //�ʱ�ȭ
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
               if (nx < 0 || ny < 0 || nx >= row || ny >= col) {
                  continue;
               }
               // ���� ���� ��ǥ�� �� ũ�⸦ �Ѿ� ������ �ǳʶٱ�
               
               if (visit[step][nx][ny] || map[nx][ny] == 2) {
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
               
               visit[step][nx][ny] = true;
                
            }
         }
      }
      String[][][] GetPath(){
      return pn.getPath();
      }

}
class PathNode {
   
   String[][][] path;
   PathNode (String[][][] path){
     
      this.path = path;
   }
   String[][][] getPath() {
	   return this.path;
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
abstract class SensorManager {
	abstract boolean GetSensorValue(SIM sim, int x, int y, int dir, int mapdata);
	
}


class HazardSensorManager extends SensorManager{
	
   
   boolean GetSensorValue(SIM sim, int x, int y, int dir, int mapdata){
	  return sim.HazardSensor(sim, x, y, dir, mapdata);
   }
}

class ColorBlobSensorManager extends SensorManager{
   boolean GetSensorValue(SIM sim, int x, int y, int dir, int mapdata) {
	  
      return sim.ColorBlobSensor(sim, x, y, dir, mapdata);
   }
}

class PositionSensorManager extends SensorManager{
   boolean GetSensorValue(SIM sim, int x, int y, int dir, int mapdata){return false;}
   Node GetSensorValue(SIM sim, int x, int y, int dir, int row, int col) {
      return sim.CurrentPositionSensor(sim, x, y, dir, row, col);
   }
}

class SIM { //SIM Ŭ����. 
   final int SOUTH = 3;
   final int NORTH = 1;
   final int EAST = 2;
   final int WEST = 0;
   
   
   private int current_x;
   private int current_y;
   private int current_direction;
   
   
   
   
   void TranslateMoveOperation(int currentx, int currenty, int current_direction) {
      
      this.current_direction = current_direction;
      this.current_x = currentx; 
      this.current_y = currenty;
   }
   
   void Move(String operation) {
      if (operation.equals("RIGHT")) {
         if (this.current_direction == SOUTH) this.current_direction = -1;
         this.current_direction++;
      }
      else {
         if (this.current_direction == SOUTH) this.current_x++;
         else if(this.current_direction == NORTH) this.current_x--;
         else if(this.current_direction == EAST) this.current_y++;
         else this.current_y--;
      }
   }
   
   SIM Get() {
      return this;
   }
   int GetCurrentX() {
	   return this.current_x;
   }
   int GetCurrentY() {
	   return this.current_y;
   }
   int GetCurrentDir() {
	   return this.current_direction;
   }
   	
   
   boolean ColorBlobSensor(SIM sim, int x, int y, int dir, int mapdata) {
	     
	            if(mapdata == 3) { 
	            	return true;
	            }
	            else return false;
	   }

   boolean HazardSensor(SIM sim, int x, int y, int dir, int mapdata) {
	   
	   if(mapdata == 5) {
	        
	        	
	         return true; 
	   }
	   return false;
}
   
   Node CurrentPositionSensor(SIM sim, int x, int y, int dir, int row, int col) {
	   
	   Random random = new Random();
	      int[] Rd = {0,0,0,0,0,0,1};
	      
	      
	      int[] dx = {0, -1, 0, 1};
	      int[] dy = {-1, 0, 1, 0};
	      if(x+dx[dir] >= 0 && y+dy[dir] >= 0 && x+dx[dir] < row && y+dy[dir] < col) {
	          if(Rd[random.nextInt(7)]==1) {this.current_x = x+dx[dir]; this.current_y = y+dy[dir]; 
	          
	          }
	      }
	      Node n = new Node(this.current_x, this.current_y, dir);
	      return n;
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
      
      // visit, path �ʱ�ȭ
      search_x = new int[search_cnt];
      search_y = new int[search_cnt];
   
      for (int i=0; i<this.search_cnt; i++) {
         this.str = new StringTokenizer(bfr.readLine());
         int tempx = Integer.valueOf(str.nextToken());
         search_x[i] = tempx;
         int tempy = Integer.valueOf(str.nextToken());
         search_y[i] = tempy;
         //this.map[tempx][tempy] = 1; // Ž�� ���� = 1
         
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
         //this.map[tempx][tempy] = 2;  // ���� ���� = 2
      }
      //map�� ���� ���� ǥ��
   
   }
   
}

class Map{
   private int mapsize_row;
   private int mapsize_col;
   private int[][] map;
   
   private int start_x;
   private int start_y;
   private boolean[][][] visit;
   
   private int[] search_x;
   private int[] search_y;
   private int[] hazard_x;
   private int[] hazard_y;
   private int search_cnt;
   private int hazard_cnt;
   private int current_x;
   private int current_y;
   private int current_direction;
   private boolean repathflag;
   private int step=0;
   
   
   
   void Save(int row, int col, int start_x, int start_y, int[][] map, boolean[][][] visit, int search_cnt, int hazard_cnt, int current_x, int current_y, int current_direction, int[] search_x, int[] search_y, int[] hazard_x, int[] hazard_y) {
      SetMap(row, col, start_x, start_y, map, visit, search_cnt, hazard_cnt, current_x, current_y, current_direction, search_x, search_y, hazard_x, hazard_y);
   }
   public int Getmap(int x, int y) {
	  return this.map[x][y];
   }

public void Setmap(int x, int y, int inf) {
	   this.map[x][y] = inf;
   }
void SetMap(int row, int col, int start_x, int start_y, int[][] map, boolean[][][] visit,  int search_cnt, int hazard_cnt, int current_x, int current_y, int current_direction, int[] search_x, int[] search_y, int[] hazard_x, int[] hazard_y) {
      this.mapsize_row = row;
      this.mapsize_col = col;
      this.start_x = start_x;
      this.start_y = start_y;
      this.visit = visit;
      
      this.search_cnt = search_cnt;
      this.hazard_cnt = hazard_cnt;
      this.current_direction = current_direction;
      this.current_x = current_x;
      this.current_y = current_y;
      this.search_x = search_x;
      this.search_y = search_y;
      this.hazard_x = hazard_x;
      this.hazard_y = hazard_y;
      this.map = map;
   }
   void SetCurrentX(int x) {
	   this.current_x =x;
   }
   void SetCurrentY(int y) {
	   this.current_y = y;
   }
   
   void SetVisit(int step, int x, int y, boolean b) {
	   this.visit[step][x][y] = b;
   }
   void SetCurrentDirection(int cur) {
	   this.current_direction = cur;
   }
   
   void SetFlag(boolean flag) {
	   this.repathflag  = flag;
   }
   void SetStep(int step) {
	   this.step = step;
   }
   
   boolean GetRePathFlag() {
      return this.repathflag;
   }
   
   int GetStartX() {
	   return this.start_x;
   }
   int GetStartY() {
	   return this.start_y;
   }
   int GetCurrentX() {
	   return this.current_x;
   }
   int GetCurrentY() {
	   return this.current_y;
   }
   int GetStep() {
	   return this.step;
   }
   
   int GetcurrentDir() {
	   return this.current_direction;
   }
   int GetSearchCount() {
	   return this.search_cnt;
   }
   
   boolean[][][] GetVisit() {
	   return this.visit;
   }
   boolean GetFlag() {
	   return this.repathflag;
   }
   int[] GetSearchX() {
	   return this.search_x;
   }
   int[] GetSearchY() {
	   return this.search_y;
   }
   int GetMapRow() {
	   return this.mapsize_row;
   }
   int GetMapCol() {
	   return this.mapsize_col;
   }
   
   
   int[][] GetMap() {
      return this.map;
   }
}