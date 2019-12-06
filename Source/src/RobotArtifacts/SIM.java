package RobotArtifacts;

import java.util.Random;

public class SIM { //SIM Å¬·¡½º. 
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
	   	
	   
	   public boolean ColorBlobSensor(SIM sim, int x, int y, int dir, int mapdata) {
		     
		            if(mapdata == 3) { 
		            	return true;
		            }
		            else return false;
		   }

	   public boolean HazardSensor(SIM sim, int x, int y, int dir, int mapdata) {
		   
		   if(mapdata == 5) {
		        
		        	
		         return true; 
		   }
		   return false;
	}
	   public Node CurrentPositionSensor(SIM sim, int x, int y, int dir, int row, int col) {
		   
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

