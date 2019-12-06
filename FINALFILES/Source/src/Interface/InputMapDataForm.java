package Interface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class InputMapDataForm //�Է� �� Ŭ����
{
   int id;
   public int row;
   public int col;
   BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in));
   StringTokenizer str;
   public int start_x;
   public int start_y;
   
   public int search_cnt;
   public int hazard_cnt;
   public int[] search_x;
   public int[] search_y;
   public int[] hazard_x;
   public int[] hazard_y;

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
   
   void EnterMapData() throws IOException {
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
   
   public InputMapDataForm() throws IOException{
      
      Open();
      do {
   
         EnterID();
         str = new StringTokenizer(bfr.readLine());
         id = Integer.parseInt(str.nextToken());
         
      }while(Verify()!=true);
   //////////////////////////////////////////////////////////////////////////////////Input Data Form/////////////////////////////////////////////////
      EnterMapData();
   
   }
   
}
