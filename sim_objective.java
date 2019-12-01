package sim;

import javax.swing.*;

import com.sun.tools.javac.Main;

import java.awt.*;

public class Sim {
	public static void main(String[] args) {
		SimWindow newwindow = new SimWindow();
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
	
	//CONSTRUCTOR
	SimWindow(){
		setLocation(framexpos, frameypos);
		setPreferredSize(new Dimension(framewidth, frameheight));
		setResizable(false); //화면 크기 조절 불가
		
		MapPanel mappanel = new MapPanel();
		mappanel.setPreferredSize(new Dimension(mapwidth, mapheight));
		mappanel.setMapSize(mapwidth, mapheight);
		mappanel.init();
		
		MenuPanel menupanel = new MenuPanel();
		menupanel.setPreferredSize(new Dimension(menuwidth, menuheight));
		menupanel.setMenuSize(menuwidth, menuheight);
		menupanel.init();
		
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

class MenuPanel extends JPanel{
    private	int menuwidth = 0;
	private int menuheight = 0;
	
	public void setMenuSize(int width, int height) {
		menuwidth = width;
		menuheight = height;
	}
	
	public void init() {
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

		
		//***********ImgLogoPanel
		smallImgPanel imglogopanel = new smallImgPanel();
		imglogopanel.setImgPath(".\\simaddon_logo.png");
		imglogopanel.setImgSize(menuwidth, menuheight/4);
		imglogopanel.init();
		//***********ImgNamePanel
		smallImgPanel imgnamepanel = new smallImgPanel();
		imgnamepanel.setImgPath(".\\names.png");
		imgnamepanel.setImgSize(menuwidth, menuheight/4);
		imgnamepanel.init();
		
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

class MapPanel extends JPanel{
	int mapwidth;
	int mapheight;
	
	private Image originrobotimg;
	private Image robotimg;
	//더블버퍼링 위함
	private Image buffimg; 
	private Graphics buffimg_g;
	
	@Override
	public void paint(Graphics g) {
		//더블버퍼링
		buffimg = createImage(mapwidth, mapheight);
		buffimg_g = buffimg.getGraphics();
		paintComponents(buffimg_g);
		buffimg_g.drawImage(robotimg, 0, 0, this);
		//버퍼링 마친 이미지 한번에 출력
		g.drawImage(buffimg, 0, 0, null);
	}
	
	public void setMapSize(int width, int height) {
		mapwidth = width;
		mapheight = height;
	}
	
	public void init() {
		originrobotimg = Toolkit.getDefaultToolkit().getImage(".\\robotimg.png");
		robotimg = originrobotimg;//originrobotimg.getScaledInstance(Sim.menuwidth, Sim.menuheight/4 - 30, Image.SCALE_SMOOTH);
	}
}

class smallImgPanel extends JPanel{
	String imgPath;
	int imgwidth;
	int imgheight;
	
	private Image originImg;
	private Image img;
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(img,  0,  0,  this);
	}
	
	public void init() {
		originImg = Toolkit.getDefaultToolkit().getImage(imgPath);
		img = originImg.getScaledInstance(imgwidth, imgheight, Image.SCALE_SMOOTH);
	}
	
	public void setImgPath(String imgpath) {
		imgPath = imgpath;
	}
	
	public void setImgSize(int width, int height) {
		imgwidth = width;
		imgheight = height;
	}
}

class MapData{
	static enum moveDirection{ //로봇이 보고있는 방향
		UP, LEFT, DOWN, RIGHT
	}
	static int[] currentPosition = new int[2]; //현재 로봇 위치
	
	static enum mapPoint{ //맵에 나타낼 개별 점들
		ROBOT, EMPTY, START, GOAL, HAZARD, NEWHAZARD, SEENHAZARD, NEWCOLORBLOB, SEENCOLORBLOB
	}
	static int[][] mapMatrix; //맵 정보
	
	//생성자
	MapData(){
		
	}
	
	// ADDON으로부터 Path 받아옴
	void setPath() {
		
	}
}
