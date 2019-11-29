package sim;

import javax.swing.*;
import java.awt.*;

public class Sim {
	//프레임 시작위치 설정, 창 크기 설정
	static int framexpos = 100;
	static int frameypos = 100;
	static int framewidth = 1024;
	static int frameheight = 768;
	//맵패널 크기 설정
	static int mapwidth = framewidth/3*2;
	static int mapheight = frameheight;
	//메뉴패널 크기 설정
	static int menuwidth = framewidth/3;
	static int menuheight = frameheight;
	

	public static void main(String[] args) {
		JFrame frame = new JFrame("SIM addon");
		frame.setLocation(framexpos, frameypos);
		frame.setPreferredSize(new Dimension(framewidth, frameheight));
		frame.setResizable(false); //화면 크기 조절 불가
		
		//***********DrawPanel
		//Map 그려질 JPanel
		DrawPanel drawpanel = new DrawPanel();
		//drawpanel 크기 조절
		drawpanel.setPreferredSize(new Dimension(mapwidth, mapheight));
		drawpanel.add(new JLabel("수빈 수빈 수빈"));
		
		//***********ImgLogoPanel
		ImgLogoPanel imglogopanel = new ImgLogoPanel();
		//***********ImgNamePanel
		ImgNamePanel imgnamepanel = new ImgNamePanel();
		
		
		//************MenuPanel
		//시작버튼, 초기화버튼 있을 JPanel
		JPanel menupanel = new JPanel();
		//menupanel 크기 조절
		menupanel.setPreferredSize(new Dimension(menuwidth, menuheight));
		
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
		
		//menupanel에 UI 추가
		menupanel.add(imglogopanel);
		menupanel.add(initbutton);
		menupanel.add(stepbutton);
		menupanel.add(imgnamepanel);
		
		//menupanel Layout 설정
		GridLayout menulayout = new GridLayout(4, 1);
		menupanel.setLayout(menulayout);
		
		//************Frame 마무리
		//BoxLayout사용. 창 넘어가면 잘림. X_AXIS하면 가로로 일렬 배치
		BoxLayout layout = new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS);
		frame.setLayout(layout);
		//Frame에 Panel들 추가d
		frame.add(drawpanel);
		frame.add(menupanel);
		frame.pack(); //이거 없으면 창크기 변함
		frame.setVisible(true);
	}
}

class DrawPanel extends JPanel{
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
}

class ImgLogoPanel extends JPanel{
	
	private Image originlogoimg = Toolkit.getDefaultToolkit().getImage(".\\simaddon_logo.png");
	private Image logoimg = originlogoimg.getScaledInstance(Sim.menuwidth, Sim.menuheight/4 - 30, Image.SCALE_SMOOTH);
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		
		g.drawImage(logoimg, 0, 0, this);
	}
}

class ImgNamePanel extends JPanel{
	
	private Image originnameimg = Toolkit.getDefaultToolkit().getImage(".\\names.png");
	private Image nameimg = originnameimg.getScaledInstance(Sim.menuwidth, Sim.menuheight/4, Image.SCALE_SMOOTH);
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		
		g.drawImage(nameimg, 0, 0, this);
	}
}
