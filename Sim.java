package sim;
import javax.swing.*;
import java.awt.*;

public class Sim {
	public static void main(String[] args) {
		SimWindow newwindow = new SimWindow(8, 8);
	}
}
class SimWindow extends JFrame{
	//프레임 시작위치 설정, 창 크기 설정
	int framexpos = 100;
	int frameypos = 100;
	int framewidth = 1024;
	int frameheight = 768;
	//맵패널 크기 설정
	int mapwidth = framewidth/3*2;
	int mapheight = frameheight;
	//메뉴패널 크기 설정
	int menuwidth = framewidth/3;
	int menuheight = frameheight;

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
	MapData mapdata;
	int mapwidth;
	int mapheight;
	int cellwidth;
	int cellheight;
	int cellimgwidth;
	int cellimgheight;
	
	private Image originrobotimg;
	private Image robotimg;
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
		originrobotimg = Toolkit.getDefaultToolkit().getImage(".\\robotimg.png");
		robotimg = originrobotimg;//originrobotimg.getScaledInstance(Sim.menuwidth, Sim.menuheight/4 - 30, Image.SCALE_SMOOTH);
	}
	
	@Override
	public void paint(Graphics g) {
		//더블버퍼링
		buffimg = createImage(mapwidth, mapheight);
		buffimg_g = buffimg.getGraphics();
		paintComponents(buffimg_g);
		buffimg_g.drawImage(robotimg, 0, 0, this);
		//맵출력
		DrawMap();
		//버퍼링 마친 이미지 한번에 출력
		g.drawImage(buffimg, 0, 0, null);
	}
	
	public void DrawMap() {
		int cell_x;
		int cell_y;
		for(int i = 0; i < mapdata.maprow; i++) {
			for(int j = 0; j < mapdata.mapcol; j++) {
				cell_x = i*cellwidth;
				cell_y = j*cellheight;
				buffimg_g.drawRect(cell_x, cell_y, cellwidth, cellheight);
			}
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
	enum moveDirection{ //로봇이 보고있는 방향
		UP, LEFT, DOWN, RIGHT
	}
	enum pointType{ //맵에 나타낼 개별 점들
		EMPTY, ROBOT, START, GOAL, HAZARD, NEWHAZARD, SEENHAZARD, NEWCOLORBLOB, SEENCOLORBLOB
	}
	String robotpath; //path 명령줄
	
	int maprow;
	int mapcol;
    pointType[][] mapMatrix; //맵 정보
	
    int[] truePosition = new int[2]; //실제 로봇 위치
	int[] currentPosition = new int[2]; //예측된 현재 로봇 위치
	moveDirection currentDirection = moveDirection.UP; //현재 로봇 방향

	public MapData(int row, int col) {
		maprow = row;
		mapcol = col;
		
		mapMatrix = new pointType[maprow][mapcol];
		//모든 칸을 EMPTY로 초기화
		for(int i = 0; i < maprow; i++) {
			for(int j = 0; j < mapcol; j++) {
				mapMatrix[i][j] = pointType.EMPTY;
			}
		}
	}
	
    public void setMapPoint(pointType type, int row, int col) {
		switch(type) { //EMPTY만 없음
		case ROBOT:
			mapMatrix[row][col] = pointType.ROBOT;
			break;
		case START:
			mapMatrix[row][col] = pointType.START;
			break;
		case GOAL:
			mapMatrix[row][col] = pointType.GOAL;
			break;
		case HAZARD:
			mapMatrix[row][col] = pointType.HAZARD;
			break;
		case NEWHAZARD:
			mapMatrix[row][col] = pointType.NEWHAZARD;
			break;
		case SEENHAZARD:
			mapMatrix[row][col] = pointType.SEENHAZARD;
			break;
		case NEWCOLORBLOB:
			mapMatrix[row][col] = pointType.NEWCOLORBLOB;
			break;
		case SEENCOLORBLOB:
			mapMatrix[row][col] = pointType.SEENCOLORBLOB;
			break;
		}
	}
	
	public void setPath(String path) {
		robotpath = path;
	}
}
