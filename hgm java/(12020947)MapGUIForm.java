package addon;
import javax.swing.*;
import java.awt.*;

public class MapGUIForm extends JFrame{
	//프레임 시작위치 설정, 창 크기 설정
	private int framexpos = 100;
	private int frameypos = 100;
	private int framewidth = 1024;
	private int frameheight = 768;
	//맵패널 크기 설정
	private int mapmargin = 50;
	private int mapwidth = framewidth/3*2 - mapmargin;
	private int mapheight = frameheight - mapmargin;
	//메뉴패널 크기 설정
	private int menuwidth = framewidth/3;
	private int menuheight = frameheight;

	//지도 정보
	MapData mapdata;
	//패널
	MapPanel mappanel;
	MenuPanel menupanel;
	
	//CONSTRUCTOR
	MapGUIForm(int maprow, int mapcol, int startrow, int startcol){
		setLocation(framexpos, frameypos);
		setPreferredSize(new Dimension(framewidth, frameheight));
		setResizable(false); //화면 크기 조절 불가

		mapdata = new MapData(maprow, mapcol, startrow, startcol);
		mappanel = new MapPanel(mapdata, mapwidth, mapheight);
		menupanel = new MenuPanel(menuwidth, menuheight);
		
		//************Frame 마무리
		//BoxLayout사용. 창 넘어가면 잘림. X_AXIS하면 가로로 일렬 배치
		BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.X_AXIS);
		setLayout(layout);
		//Frame에 Panel들 추가d
		add(mappanel);
		add(menupanel);
		pack(); //이거 없으면 창크기 변함
		setVisible(true);
	}

    public void setMapPoint(String type, int row, int col) {
		switch(type) { //EMPTY만 없음
		case "EMPTY":
			mapdata.setMapPoint(PointType.EMPTY, row, col);
			break;
		case "ROBOT":
			mapdata.setMapPoint(PointType.ROBOT, row, col);
			break;
		case "GOAL":
			mapdata.setMapPoint(PointType.GOAL, row, col);
			break;
		case "SEENHAZARD":
			mapdata.setMapPoint(PointType.SEENHAZARD, row, col);
			break;
		case "SEENCOLORBLOB":
			mapdata.setMapPoint(PointType.SEENCOLORBLOB, row, col);
			break;
		case "NEWHAZARD":
			mapdata.setMapPoint(PointType.NEWHAZARD, row, col);
			break;
		case "NEWCOLORBLOB":
			mapdata.setMapPoint(PointType.NEWCOLORBLOB, row, col);
			break;
		default:
			System.out.println("error in MapGUIForm.setMapPoint");
			break;
		}
	}
    public void setOperation(String oper) {
    	mapdata.doOperation(oper);
    	mappanel.repaint();
    }
}

//로봇이 보고있는 방향
enum MoveDirection{
	UP, LEFT, DOWN, RIGHT
}
//맵 포인트의 유형
enum PointType{
	EMPTY, ROBOT, GOAL, SEENHAZARD, SEENCOLORBLOB, NEWHAZARD, NEWCOLORBLOB, ROBOTONGOAL, ROBOTONCLRB
}

class MapPanel extends JPanel{
	private MapData mapdata;
	private int mapwidth;
	private int mapheight;
	private int cellwidth;
	private int cellheight;
	private int cellimgwidth;
	private int cellimgheight;
	
	private Image imgempty;
	private Image imgrobotup;
	private Image imgrobotdown;
	private Image imgrobotleft;
	private Image imgrobotright;
	private Image imggoal;
	private Image imgseenhazard;
	private Image imgseencolorblob;
	private Image imgnewhazard;
	private Image imgnewcolorblob;
	//더블버퍼링 위함
	private Image buffimg; 
	private Graphics buffimg_g;
	
	public MapPanel(MapData data, int width, int height) {
		mapdata = data;
		//각 요소별 pixel값 초기화
		mapwidth = width;
		mapheight = height;
		//row가 늘어나면 ypos값이 늘어난다
		//col이 늘어나면 xpos값이 늘어난다
		cellwidth = mapwidth / data.mapcol;
		cellheight = mapheight/ data.maprow;
		cellimgwidth = cellwidth - 10;
		cellimgheight = cellheight -10;
		//MapPanel 크기 초기화
		setPreferredSize(new Dimension(mapwidth, mapheight));
		//이미지 로드
		imgempty = Toolkit.getDefaultToolkit().getImage(".\\EMPTY.png");
		imgempty = imgempty.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imgrobotup = Toolkit.getDefaultToolkit().getImage(".\\ROBOT_UP.png");
		imgrobotup = imgrobotup.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imgrobotdown = Toolkit.getDefaultToolkit().getImage(".\\ROBOT_DOWN.png");
		imgrobotdown = imgrobotdown.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imgrobotleft = Toolkit.getDefaultToolkit().getImage(".\\ROBOT_LEFT.png");
		imgrobotleft = imgrobotleft.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imgrobotright = Toolkit.getDefaultToolkit().getImage(".\\ROBOT_RIGHT.png");
		imgrobotright = imgrobotright.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imggoal = Toolkit.getDefaultToolkit().getImage(".\\GOAL.png");
		imggoal = imggoal.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imgseenhazard = Toolkit.getDefaultToolkit().getImage(".\\SEENHAZARD.png");
		imgseenhazard = imgseenhazard.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imgseencolorblob = Toolkit.getDefaultToolkit().getImage(".\\SEENCOLORBLOB.png");
		imgseencolorblob = imgseencolorblob.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imgnewhazard = Toolkit.getDefaultToolkit().getImage(".\\NEWHAZARD.png");
		imgnewhazard = imgnewhazard.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		imgnewcolorblob = Toolkit.getDefaultToolkit().getImage(".\\NEWCOLORBLOB.png");
		imgnewcolorblob = imgnewcolorblob.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
	}
	
	@Override
	public void paint(Graphics g) {
		//더블버퍼링
		buffimg = createImage(mapwidth, mapheight);
		buffimg_g = buffimg.getGraphics();
		paintComponents(buffimg_g);
		//buffimg_g에 맵 출력
		DrawMap();
		//버퍼링 마친 이미지 한번에 출력
		g.drawImage(buffimg, 0, 0, null);
	}
	
	public void DrawMap() {
		//row가 늘어나면 ypos값이 늘어난다
		//col이 늘어나면 xpos값이 늘어난다
		int mapmargin = 10;
		int xpos, ypos;
		for(int i = 0; i < mapdata.maprow; i++) {
			for(int j = 0; j < mapdata.mapcol; j++) {
				xpos = j*cellwidth + mapmargin;
				ypos = i*cellheight + mapmargin;
				DrawPoint(i,j, xpos, ypos);
			}
		}
	}
	
	public void DrawPoint(int row, int col, int xpos, int ypos) {
		switch(mapdata.mapMatrix[row][col]) {
		case EMPTY:
			buffimg_g.drawImage(imgempty, xpos, ypos, this);
			break;
		case ROBOT:
			switch(mapdata.currentDirection) {
			case UP:
				buffimg_g.drawImage(imgrobotup, xpos, ypos, this);
				break;
			case DOWN:
				buffimg_g.drawImage(imgrobotdown, xpos, ypos, this);
				break;
			case LEFT:
				buffimg_g.drawImage(imgrobotleft, xpos, ypos, this);
				break;
			case RIGHT:
				buffimg_g.drawImage(imgrobotright, xpos, ypos, this);
				break;
			default:
				System.out.println("error in current robot state");
				break;
			}
			break;
		case GOAL:
			buffimg_g.drawImage(imggoal, xpos, ypos, this);
			break;
		case SEENHAZARD:
			buffimg_g.drawImage(imgseenhazard, xpos, ypos, this);
			break;
		case SEENCOLORBLOB:
			buffimg_g.drawImage(imgseencolorblob, xpos, ypos, this);
			break;
		case NEWHAZARD:
			buffimg_g.drawImage(imgnewhazard, xpos, ypos, this);
			break;
		case NEWCOLORBLOB:
			buffimg_g.drawImage(imgnewcolorblob, xpos, ypos, this);
			break;
		case ROBOTONGOAL: //로봇이 goal 위에 있게 두개다 그려준다
			buffimg_g.drawImage(imggoal, xpos, ypos, this);
			switch(mapdata.currentDirection) {
			case UP:
				buffimg_g.drawImage(imgrobotup, xpos, ypos, this);
				break;
			case DOWN:
				buffimg_g.drawImage(imgrobotdown, xpos, ypos, this);
				break;
			case LEFT:
				buffimg_g.drawImage(imgrobotleft, xpos, ypos, this);
				break;
			case RIGHT:
				buffimg_g.drawImage(imgrobotright, xpos, ypos, this);
				break;
			default:
				System.out.println("error in current robot state");
				break;
			}
			break;
		case ROBOTONCLRB: //로봇이 SEENCOLORBLOB 위에 있게 두개다 그려준다
			buffimg_g.drawImage(imgseencolorblob, xpos, ypos, this);
			switch(mapdata.currentDirection) {
			case UP:
				buffimg_g.drawImage(imgrobotup, xpos, ypos, this);
				break;
			case DOWN:
				buffimg_g.drawImage(imgrobotdown, xpos, ypos, this);
				break;
			case LEFT:
				buffimg_g.drawImage(imgrobotleft, xpos, ypos, this);
				break;
			case RIGHT:
				buffimg_g.drawImage(imgrobotright, xpos, ypos, this);
				break;
			default:
				System.out.println("error in current robot state");
				break;
			}
			break;
		default:
			System.out.println("error in DrawPoint");
			break;
		}
	}
}

class MenuPanel extends JPanel{
    private	int menuwidth = 0;
	private int menuheight = 0;
	
	public MenuPanel(int width, int height) {
		menuwidth = width;
		menuheight = height;
		setPreferredSize(new Dimension(menuwidth, menuheight));
		
		JLabel label1 = new JLabel("SIM ADD-ON");
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label1.setVerticalAlignment(SwingConstants.CENTER);
		label1.setFont(new Font("맑은고딕", Font.BOLD, 20));
		JLabel label2 = new JLabel("로봇 동작 자동제어 시스템");
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		label2.setVerticalAlignment(SwingConstants.CENTER);
		label2.setFont(new Font("맑은고딕", Font.PLAIN, 20));
		JLabel label3 = new JLabel("컴퓨터과학부");
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		label3.setVerticalAlignment(SwingConstants.CENTER);
		label3.setFont(new Font("맑은고딕", Font.BOLD, 20));
		JLabel label4 = new JLabel("2015920016 도우찬");
		label4.setHorizontalAlignment(SwingConstants.CENTER);
		label4.setVerticalAlignment(SwingConstants.CENTER);
		label4.setFont(new Font("맑은고딕", Font.PLAIN, 20));
		JLabel label5 = new JLabel("2015920057 하경민");
		label5.setHorizontalAlignment(SwingConstants.CENTER);
		label5.setVerticalAlignment(SwingConstants.CENTER);
		label5.setFont(new Font("맑은고딕", Font.PLAIN, 20));
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		
		//menupanel에 UI 추가
		add(label1);
		add(label2);
		add(label3);
		add(label4);
		add(label5);
		
	}
}

class MapData{
	int maprow;
	int mapcol;
    PointType[][] mapMatrix; //맵 정보
    
	int[] currentPosition = new int[2]; //예측된 현재 로봇 위치
	MoveDirection currentDirection; //현재 로봇 방향

	public MapData(int row, int col, int startrow, int startcol) {
		maprow = row;
		mapcol = col;
		currentPosition[0] = startrow;
		currentPosition[1] = startcol;
		currentDirection = MoveDirection.DOWN; //초기 로봇 방향 아래
		System.out.println("currentDirection = " + currentDirection.name());
		System.out.println("currentPosition = [" + Integer.toString(currentPosition[0]) + ", " + Integer.toString(currentPosition[1]) + "]");

		//맵 2차원 행렬의 모든 요소를 EMPTY로 초기화
		mapMatrix = new PointType[maprow][mapcol];
		for(int i = 0; i < maprow; i++) {
			for(int j = 0; j < mapcol; j++) {
				mapMatrix[i][j] = PointType.EMPTY;
			}
		}
		//맵 행렬에 초기 로봇 위치 설정
		mapMatrix[startrow][startcol] = PointType.ROBOT; 
	}
	
    public void setMapPoint(PointType type, int row, int col) {
		switch(type) {
		case EMPTY:
			mapMatrix[row][col] = PointType.EMPTY;
			break;
		case ROBOT:
			mapMatrix[row][col] = PointType.ROBOT;
			currentPosition[0] = row;
			currentPosition[1] = col;
			break;
		case GOAL:
			mapMatrix[row][col] = PointType.GOAL;
			break;
		case SEENHAZARD:
			mapMatrix[row][col] = PointType.SEENHAZARD;
			break;
		case SEENCOLORBLOB:
			mapMatrix[row][col] = PointType.SEENCOLORBLOB;
			break;
		case NEWHAZARD:
			mapMatrix[row][col] = PointType.NEWHAZARD;
			break;
		case NEWCOLORBLOB:
			mapMatrix[row][col] = PointType.NEWCOLORBLOB;
			break;
		default:
			System.out.println("error in setMapPoint");
			break;
		}
	}
    
    public void doOperation(String oper) {
    	//operation RIGHT, MOVE
    	if (oper.equals("RIGHT")) {
    	    switch(currentDirection) { //시계방향, 오른쪽으로 돈다.
    	    case UP:
    	    	currentDirection = MoveDirection.RIGHT;
    	    	break;
    	    case DOWN:
    	    	currentDirection = MoveDirection.LEFT;
    	    	break;
    	    case LEFT:
    	    	currentDirection = MoveDirection.UP;
    	    	break;
    	    case RIGHT:
    	    	currentDirection = MoveDirection.DOWN;
    	    	break;
    	    default:
    			System.out.println("error in do TURN operation");
    	    	break;
    	    }
			System.out.println("currentDirection = " + currentDirection.name());
    	}
    	else if(oper.equals("MOVE")) { //옳은 명령만 온다고 가정하고 그냥 그려준다
    		int row, col;
    		row = currentPosition[0];
    		col = currentPosition[1];
    	    switch(currentDirection) { //현재 방향에 따라 움직이는 위치 달라진다
    	    //위로간다는것 = row - 1
    	    //오른쪽으로 간다는 것 = col + 1
    	    case UP:
    	    	goStep(row, col, row - 1, col);
    	    	break;
    	    case DOWN:
    	    	goStep(row, col, row + 1, col);
    	    	break;
    	    case LEFT:
    	    	goStep(row, col, row, col - 1);
    	    	break;
    	    case RIGHT:
    	    	goStep(row, col, row, col + 1);
    	    	break;
    	    default:
    			System.out.println("error in do GO operation");
    	    	break;
    	    }
    		System.out.println("currentPosition = [" + Integer.toString(currentPosition[0]) + ", " + Integer.toString(currentPosition[1]) + "]");
    		
    	}
    	else {
    		System.out.println("error at doOperation");
    	}
    }
    
    public void goStep(int row, int col, int next_row, int next_col) {
    	//ROBOT > EMPTY
    	//ROBOT > GOAL
    	//ROBOT > CLRB
    	//ROBOTONGOAL > EMPTY
    	//ROBOTONGOAL > GOAL
    	//ROBOTONGOAL > CLRB
    	//ROBOTONCLRB > EMPTY
    	//ROBOTONCLRB > GOAL
    	//ROBOTONCLRB > CLRB
    	//로봇이 밟을수 있는 경우의 수는 총 9가지
    	//CLRB는 싹다 SEENCLRB일것.
    	switch(mapMatrix[row][col]) {
    	case ROBOT:
    		mapMatrix[row][col] = PointType.EMPTY;
    		switch(mapMatrix[next_row][next_col]) {
    		case EMPTY:
    			mapMatrix[next_row][next_col] = PointType.ROBOT;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    		case GOAL:
    			mapMatrix[next_row][next_col] = PointType.ROBOTONGOAL;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    		case SEENCOLORBLOB:
    			mapMatrix[next_row][next_col] = PointType.ROBOTONCLRB;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    	    default: //ERROR
    	    	break;
    		}
    		break;
    	case ROBOTONGOAL:
    		mapMatrix[row][col] = PointType.GOAL;
    		switch(mapMatrix[next_row][next_col]) {
    		case EMPTY:
    			mapMatrix[next_row][next_col] = PointType.ROBOT;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    		case GOAL:
    			mapMatrix[next_row][next_col] = PointType.ROBOTONGOAL;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    		case SEENCOLORBLOB:
    			mapMatrix[next_row][next_col] = PointType.ROBOTONCLRB;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    	    default: //ERROR
    	    	break;
    		}
    		break;
    	case ROBOTONCLRB:
    		mapMatrix[row][col] = PointType.SEENCOLORBLOB;
    		switch(mapMatrix[next_row][next_col]) {
    		case EMPTY:
    			mapMatrix[next_row][next_col] = PointType.ROBOT;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    		case GOAL:
    			mapMatrix[next_row][next_col] = PointType.ROBOTONGOAL;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    		case SEENCOLORBLOB:
    			mapMatrix[next_row][next_col] = PointType.ROBOTONCLRB;
    			currentPosition[0] = next_row;
    			currentPosition[1] = next_col;
    			break;
    	    default: //ERROR
    	    	break;
    		}
    		break;
    	default: //ERROR
    		break;
    	}
    }
}
