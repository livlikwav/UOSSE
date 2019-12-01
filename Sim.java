package sim;
import javax.swing.*;
import java.awt.*;

public class Sim {
	public static void main(String[] args) {
		SimWindow newwindow = new SimWindow(8, 8);
	}
}

//로봇이 보고있는 방향
enum MoveDirection{
	UP, LEFT, DOWN, RIGHT
}
//맵 포인트의 유형
enum PointType{
	EMPTY, ROBOT, GOAL, SEENHAZARD, SEENCOLORBLOB, NEWHAZARD, NEWCOLORBLOB
}

class SimWindow extends JFrame{
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
	SimWindow(int maprow, int mapcol){
		setLocation(framexpos, frameypos);
		setPreferredSize(new Dimension(framewidth, frameheight));
		setResizable(false); //화면 크기 조절 불가

		mapdata = new MapData(maprow, mapcol);
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
	private Image imgrobot;
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
		cellwidth = mapwidth / data.maprow;
		cellheight = mapheight/ data.mapcol;
		cellimgwidth = cellwidth - 10;
		cellimgheight = cellheight -10;
		//MapPanel 크기 초기화
		setPreferredSize(new Dimension(mapwidth, mapheight));
		//이미지 로드
		Image empty = Toolkit.getDefaultToolkit().getImage(".\\EMPTY.png");
		imgempty = empty.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		Image robot = Toolkit.getDefaultToolkit().getImage(".\\ROBOT.png");
		imgrobot = robot.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		Image goal = Toolkit.getDefaultToolkit().getImage(".\\GOAL.png");
		imggoal = goal.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		Image seenhazard = Toolkit.getDefaultToolkit().getImage(".\\SEENHAZARD.png");
		imgseenhazard = seenhazard.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		Image seencolorblob = Toolkit.getDefaultToolkit().getImage(".\\SEENCOLORBLOB.png");
		imgseencolorblob = seencolorblob.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		Image newhazard = Toolkit.getDefaultToolkit().getImage(".\\NEWHAZARD.png");
		imgnewhazard = newhazard.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
		Image newcolorblob = Toolkit.getDefaultToolkit().getImage(".\\NEWCOLORBLOB.png");
		imgnewcolorblob = newcolorblob.getScaledInstance(cellimgwidth, cellimgheight, Image.SCALE_SMOOTH);
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
		int mapmargin = 10;
		int xpos, ypos;
		for(int i = 0; i < mapdata.maprow; i++) {
			for(int j = 0; j < mapdata.mapcol; j++) {
				xpos = i*cellwidth + mapmargin;
				ypos = j*cellheight + mapmargin;
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
			buffimg_g.drawImage(imgrobot, xpos, ypos, this);
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
		
		//menupanel 초기화 button
		JButton initbutton = new JButton("initbutton");
		initbutton.setText("로봇 탐색 초기화");
		initbutton.setFont(new Font("맑은고딕",Font.BOLD,20));
		initbutton.setHorizontalAlignment(SwingConstants.CENTER);
		initbutton.setVerticalAlignment(SwingConstants.CENTER);
		
		//menupanel 한칸이동 button
		JButton stepbutton = new JButton("initbutton");
		stepbutton.setText("한칸 이동");
		stepbutton.setFont(new Font("맑은고딕",Font.PLAIN,20));
		stepbutton.setHorizontalAlignment(SwingConstants.CENTER);
		stepbutton.setVerticalAlignment(SwingConstants.CENTER);

		smallImgPanel imglogopanel = new smallImgPanel(".\\simaddon_logo.png", menuwidth, menuheight/4);
		smallImgPanel imgnamepanel = new smallImgPanel(".\\names.png", menuwidth, menuheight/4);
		
		//menupanel에 UI 추가
		add(imglogopanel);
		add(initbutton);
		add(stepbutton);
		add(imgnamepanel);
		
		//menupanel Layout 설정
		GridLayout menulayout = new GridLayout(4, 1);
		setLayout(menulayout);
	}
}

class smallImgPanel extends JPanel{
	String imgPath;
	int imgwidth;
	int imgheight;
	
	private Image originImg;
	private Image img;
	
	public smallImgPanel(String path, int width, int height) {
		imgPath = path;
		imgwidth = width;
		imgheight = height;
		
		originImg = Toolkit.getDefaultToolkit().getImage(imgPath);
		img = originImg.getScaledInstance(imgwidth, imgheight, Image.SCALE_SMOOTH);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(img,  0,  0,  this);
	}
}

class MapData{
	String robotpath; //path 명령줄
	
	int maprow;
	int mapcol;
    PointType[][] mapMatrix; //맵 정보
	
    int[] truePosition = new int[2]; //실제 로봇 위치
	int[] currentPosition = new int[2]; //예측된 현재 로봇 위치
	MoveDirection currentDirection = MoveDirection.UP; //현재 로봇 방향

	public MapData(int row, int col) {
		maprow = row;
		mapcol = col;
		
		mapMatrix = new PointType[maprow][mapcol];
		//모든 칸을 EMPTY로 초기화
		for(int i = 0; i < maprow; i++) {
			for(int j = 0; j < mapcol; j++) {
				mapMatrix[i][j] = PointType.EMPTY;
			}
		}
		
		mapMatrix[0][0] = PointType.ROBOT;
		mapMatrix[0][1] = PointType.SEENHAZARD;
		mapMatrix[0][2] = PointType.SEENCOLORBLOB;
		mapMatrix[0][3] = PointType.NEWHAZARD;
		mapMatrix[0][4] = PointType.NEWCOLORBLOB;
		mapMatrix[0][5] = PointType.GOAL;
	}
	
    public void setMapPoint(PointType type, int row, int col) {
		switch(type) { //EMPTY만 없음
		case ROBOT:
			mapMatrix[row][col] = PointType.ROBOT;
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
	
	public void setPath(String path) {
		robotpath = path;
	}
}
