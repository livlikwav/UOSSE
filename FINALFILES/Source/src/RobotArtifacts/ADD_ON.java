package RobotArtifacts;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

import mapArtifacts.MapDataManager;

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
            
            	initializeVistPath();

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
	  	  int Color_cnt=1; // �������� ����� Color blob ���� -> 3�� ����
	        while(Color_cnt <= 3) { //���������̳�, Ÿ�������� ���� ����
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
		int Hazard_cnt=1; //���� ����� �������� ���� -> 1�� ����
	      while (Hazard_cnt <= 5) { //���λ��� �� ���� ��������, Ÿ������, ���������� ���� ����. �ϴٺ��� �翷�� �ٸ��� ������ ��찡 ���� 
	         //�ϴ��� ���������� �ϳ��� ��
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
      final int WEST = 0; //�� ������ ���ڷ� ����
      
      Path pn;
      
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
         
         pn = new Path(path);    //�ʱ�ȭ
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
               
               pn.SetPathPoint(step, nx, ny, pn.GetPathPoint(step, n.x, n.y)); 
               
               //���� ���� ��ǥ�� ������� ���µ� �ɸ� ��� ���� ��
               
               while(my_direc != tmp_dir) {
                  pn.SetPathPoint(step, nx, ny, "RIGHT->");
                  if(my_direc == SOUTH) my_direc = -1;
                  my_direc = direction[my_direc+1];
               } // ��ο�  �°� ȸ��, ��� ����
                
               pn.SetPathPoint(step, nx, ny, "MOVE->"); 
               
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




   
  





























