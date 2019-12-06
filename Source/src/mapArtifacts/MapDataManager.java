package mapArtifacts;

import java.io.IOException;


		
import RobotArtifacts.Node;
import RobotArtifacts.SIM;
import Interface.InputMapDataForm;
import Interface.MapGUIForm;



public class MapDataManager { //맵에게 전달한 변수들 저장, 대부분 이름으로 역할 추정 가능
	   int row;
	   int col;
	   public int start_x;
	   public int start_y;
	   int search_cnt;
	   int hazard_cnt;

	   int[][] map;
	   boolean[][][] visit; //중요 -> 3차원배열로 처음은 step별로, 즉 탐색지점 개수만큼 생성, 두 세번째는 좌표
	   
	   
	   int[] search_x;
	   int[] search_y;
	   int[] hazard_x;
	   int[] hazard_y;

	   
	   Map mp = new Map(); //map 객체 생성 후 전달.
	   HazardSensorManager hsm = new HazardSensorManager();
	   ColorBlobSensorManager cbsm = new ColorBlobSensorManager();
	   PositionSensorManager psm = new PositionSensorManager();
	   
	   
	   
	   public static MapGUIForm gui;
	   
	   public MapDataManager() throws IOException {
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
	      
	      mp.Save(row, col, start_x, start_y, map, visit, search_cnt, hazard_cnt, 
	    		  start_x, start_y, 3, search_x, search_y, hazard_x, hazard_y);

	      gui = new MapGUIForm(row, col, start_x, start_y);
	      for (int i=0; i<search_cnt; i++) {
	         gui.setMapPoint("GOAL", search_x[i], search_y[i]);
	      }
	      for (int i=0; i<hazard_cnt; i++) {
	         gui.setMapPoint("SEENHAZARD", hazard_x[i], hazard_y[i]);
	      }
	   }
	   void InitMapData() {
		      map = new int[row+1][col+1];
		      visit = new boolean[search_cnt][row+1][col+1];
		      
		      //map 초기화
		      for (int i=0; i<row; i++) {
		         for (int j=0; j<col; j++) {
		            map[i][j] = 0;    //일반 지역 = 0
		         }
		      } 
		      // visit, path 초기화
		      for (int k=0; k<search_cnt; k++) {
		         for (int i=0; i<row; i++) {
		            for (int j=0; j<col; j++) {
		                              
		               visit[k][i][j] = false;
		               
		            }
		         }
		      }
		      // map 행렬에 탐색 지역 표시
		      for (int i=0; i<this.search_cnt; i++) {
		         this.map[search_x[i]][search_y[i]] = 1; // 탐색 지역 = 1
		      }
		      // map 행렬에 위험 지역 표시
		      for (int i=0; i<this.hazard_cnt; i++) {
		         this.map[hazard_x[i]][hazard_y[i]] = 2;  // 위험 지역 = 2
		      }
	   }
	   
	   public static void GUIOperationPrint(String operation) {
	      gui.setOperation(operation);
	      try {
	          
	          Thread.sleep(500);
	          
	          } catch (InterruptedException e) {
	          
	          e.printStackTrace();
	          
	          }
	   }
	   public static void GUIMapSetPrint(String type, int x, int y) {
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
	   
	   
	   public boolean CheckRePathFlag() { //플래그 체크 함수
	      boolean flag = mp.GetRePathFlag();
	      return flag;
	   }
	   
	   public int[][] GetMap() {
	      return mp.GetMap();
	   }
	   public int GetMapPointData(int x, int y) {
		   return mp.Getmapinf(x, y);
	   }
	   public int GetMapCurrentDirection() {
		   return mp.GetcurrentDir();
	   }
	   
	   public int GetMapStartX() {
		   return mp.GetStartX();
	   }
	   public int GetMapStartY() {
		   return mp.GetStartY();
	   }
	   public int GetMapCurrentX() {
		   return mp.GetCurrentX();
	   }
	   public int GetMapCurrentY() {
		   return mp.GetCurrentY();
	   }
	   public int GetMapStep() {
		   return mp.GetStep();
	   }
	   
	   public boolean[][][] GetMapVisit() {
		   return mp.GetVisit();
	   }
	   public int[] GetMapSearchX() {
		   return mp.GetSearchX();
	   }
	   public int[] GetMapSearchY() {
		   return mp.GetSearchY();
	   }
	   public int GetMapRow() {
		   return mp.GetMapRow();
	   }
	   public int GetMapCol() {
		   return mp.GetMapCol();
	   }
	   public int GetMapSearchCount() {
		   return mp.GetSearchCount();
	   }
	   public boolean GetColorBolobSensor(SIM sim, int x, int y, int dir) {
		   if(cbsm.GetSensorValue(sim, x, y, dir, mp.Getmapinf(x, y))) {
			   mp.Setmapinf(x, y, 4);
			   GUIMapSetPrint("SEENCOLORBLOB", x, y);
			   return true;
		   }
		   return false;
	   }
	   public boolean GetHazardSensor(SIM sim,int x, int y, int dir) {
		   if(hsm.GetSensorValue(sim, x, y, dir, mp.Getmapinf(x, y))) {
			   mp.Setmapinf(x, y, 2);
			   GUIMapSetPrint("SEENHAZARD", x, y);
			   return true;
		   }
		   return false;
	   }
	   public Node GetCurrentPositionSensor(SIM sim, int x, int y, int dir) {
		   return psm.GetSensorValue(sim, x, y, dir, mp.GetMapRow(), mp.GetMapCol());
	   }
	   public void SetMapCurrentDir(int dir) {
		   mp.SetCurrentDirection(dir);
	   }
	   void SetMapStartX(int x) {
		   
	   }
	   void SetMapStartY(int y) {
		   
	   }
	   public void SetMapFlag(boolean flag) {
		   mp.SetFlag(flag);
	   }
	   public void SetMapStep(int step) {
		   mp.SetStep(step);
	   }
	   
	   public void SetMapVisit(int step, int x, int y, boolean b) {
		   mp.SetVisit(step, x, y, b);
	   }
	   public void SetMapCurrentX(int x) {
		   mp.SetCurrentX(x);
	   }
	   public void SetMapCurrentY(int y) {
		   mp.SetCurrentY(y);
	   }
	   public void SetMapPointData(int x, int y, int inf) {
		   mp.Setmapinf(x,y, inf);
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
	   public int Getmapinf(int x, int y) {
		  return this.map[x][y];
	   }

	   public void Setmapinf(int x, int y, int inf) {
		   this.map[x][y] = inf;
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
class SensorNode { //안쓴다. 나중에 없앤다
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