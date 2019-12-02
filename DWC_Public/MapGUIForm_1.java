package Fighting;
import javax.swing.*;
import java.awt.*;

public class MapGUIForm extends JFrame{
	//������ ������ġ ����, â ũ�� ����
	private int framexpos = 100;
	private int frameypos = 100;
	private int framewidth = 1024;
	private int frameheight = 768;
	//���г� ũ�� ����
	private int mapmargin = 50;
	private int mapwidth = framewidth/3*2 - mapmargin;
	private int mapheight = frameheight - mapmargin;
	//�޴��г� ũ�� ����
	private int menuwidth = framewidth/3;
	private int menuheight = frameheight;

	//���� ����
	MapData mapdata;
	//�г�
	MapPanel mappanel;
	MenuPanel menupanel;
	
	//CONSTRUCTOR
	MapGUIForm(int maprow, int mapcol, int startrow, int startcol){
		setLocation(framexpos, frameypos);
		setPreferredSize(new Dimension(framewidth, frameheight));
		setResizable(false); //ȭ�� ũ�� ���� �Ұ�

		mapdata = new MapData(maprow, mapcol, startrow, startcol);
		mappanel = new MapPanel(mapdata, mapwidth, mapheight);
		menupanel = new MenuPanel(menuwidth, menuheight);
		
		//************Frame ������
		//BoxLayout���. â �Ѿ�� �߸�. X_AXIS�ϸ� ���η� �Ϸ� ��ġ
		BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.X_AXIS);
		setLayout(layout);
		//Frame�� Panel�� �߰�d
		add(mappanel);
		add(menupanel);
		pack(); //�̰� ������ âũ�� ����
		setVisible(true);
	}

    public void setMapPoint(String type, int row, int col) {
		switch(type) { //EMPTY�� ����
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

//�κ��� �����ִ� ����
enum MoveDirection{
	UP, LEFT, DOWN, RIGHT
}
//�� ����Ʈ�� ����
enum PointType{
	EMPTY, ROBOT, GOAL, SEENHAZARD, SEENCOLORBLOB, NEWHAZARD, NEWCOLORBLOB, ROBOTONGOAL
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
	//������۸� ����
	private Image buffimg; 
	private Graphics buffimg_g;
	
	public MapPanel(MapData data, int width, int height) {
		mapdata = data;
		//�� ��Һ� pixel�� �ʱ�ȭ
		mapwidth = width;
		mapheight = height;
		//row�� �þ�� ypos���� �þ��
		//col�� �þ�� xpos���� �þ��
		cellwidth = mapwidth / data.mapcol;
		cellheight = mapheight/ data.maprow;
		cellimgwidth = cellwidth - 10;
		cellimgheight = cellheight -10;
		//MapPanel ũ�� �ʱ�ȭ
		setPreferredSize(new Dimension(mapwidth, mapheight));
		//�̹��� �ε�
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
		//������۸�
		buffimg = createImage(mapwidth, mapheight);
		buffimg_g = buffimg.getGraphics();
		paintComponents(buffimg_g);
		//buffimg_g�� �� ���
		DrawMap();
		//���۸� ��ģ �̹��� �ѹ��� ���
		g.drawImage(buffimg, 0, 0, null);
	}
	
	public void DrawMap() {
		//row�� �þ�� ypos���� �þ��
		//col�� �þ�� xpos���� �þ��
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
		case ROBOTONGOAL: //�κ��� goal ���� �ְ� �ΰ��� �׷��ش�
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
		label1.setFont(new Font("�������", Font.BOLD, 20));
		JLabel label2 = new JLabel("�κ� ���� �ڵ����� �ý���");
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		label2.setVerticalAlignment(SwingConstants.CENTER);
		label2.setFont(new Font("�������", Font.PLAIN, 20));
		JLabel label3 = new JLabel("��ǻ�Ͱ��к�");
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		label3.setVerticalAlignment(SwingConstants.CENTER);
		label3.setFont(new Font("�������", Font.BOLD, 20));
		JLabel label4 = new JLabel("2015920016 ������");
		label4.setHorizontalAlignment(SwingConstants.CENTER);
		label4.setVerticalAlignment(SwingConstants.CENTER);
		label4.setFont(new Font("�������", Font.PLAIN, 20));
		JLabel label5 = new JLabel("2015920057 �ϰ��");
		label5.setHorizontalAlignment(SwingConstants.CENTER);
		label5.setVerticalAlignment(SwingConstants.CENTER);
		label5.setFont(new Font("�������", Font.PLAIN, 20));

		//smallImgPanel imglogopanel = new smallImgPanel(".\\logo.png", menuwidth, menuwidth);
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		
		//menupanel�� UI �߰�
		add(label1);
		add(label2);
		//add(imglogopanel);
		add(label3);
		add(label4);
		add(label5);
		
	}
}

class smallImgPanel extends JPanel{
	private Image img;
	
	public smallImgPanel(String path, int width, int height) {
		
		img = Toolkit.getDefaultToolkit().getImage(path);
		img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, this);
	}
}

class MapData{
	int maprow;
	int mapcol;
    PointType[][] mapMatrix; //�� ����
    
	String operation; //�̵����
	int[] currentPosition = new int[2]; //������ ���� �κ� ��ġ
	MoveDirection currentDirection; //���� �κ� ����

	public MapData(int row, int col, int startrow, int startcol) {
		maprow = row;
		mapcol = col;
		currentPosition[0] = startrow;
		currentPosition[1] = startcol;
		currentDirection = MoveDirection.DOWN; //�ʱ� �κ� ���� �Ʒ�
		System.out.println("currentDirection = " + currentDirection.name());
		System.out.println("currentPosition = [" + Integer.toString(currentPosition[0]) + ", " + Integer.toString(currentPosition[1]) + "]");

		//�� 2���� ����� ��� ��Ҹ� EMPTY�� �ʱ�ȭ
		mapMatrix = new PointType[maprow][mapcol];
		for(int i = 0; i < maprow; i++) {
			for(int j = 0; j < mapcol; j++) {
				mapMatrix[i][j] = PointType.EMPTY;
			}
		}
		//�� ��Ŀ� �ʱ� �κ� ��ġ ����
		mapMatrix[startrow][startcol] = PointType.ROBOT; 
	}
	
    public void setMapPoint(PointType type, int row, int col) {
		switch(type) { //EMPTY�� ����
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
    
    public void doOperation(String oper) {
    	operation = oper;
    	//operation TURN, GO
    	if (oper.equals("LEFT")) {
    	    switch(currentDirection) { //�������� ���� (�ð�ݴ�������� ����)
    	    case UP:
    	    	currentDirection = MoveDirection.LEFT;
    	    	break;
    	    case DOWN:
    	    	currentDirection = MoveDirection.RIGHT;
    	    	break;
    	    case LEFT:
    	    	currentDirection = MoveDirection.DOWN;
    	    	break;
    	    case RIGHT:
    	    	currentDirection = MoveDirection.UP;
    	    	break;
    	    default:
    			System.out.println("error in do TURN operation");
    	    	break;
    	    }
			System.out.println("currentDirection = " + currentDirection.name());
    	}
    	else if(oper.equals("MOVE")) {
    		//index�� ����� �ʴ´ٸ� �ٲ۴�
    		//Ž������ �������� ���� �׷�����ϳ�
    		int row, col;
    		row = currentPosition[0];
    		col = currentPosition[1];
    	    switch(currentDirection) { //���� ���⿡ ���� �����̴� ��ġ �޶�����
    	    //���ΰ��ٴ°� = row - 1
    	    //���������� ���ٴ� �� = col + 1
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
    	if(mapMatrix[row][col] == PointType.ROBOTONGOAL) { //���� �ִ� �ڸ��� �����ִ� �̹������
        	if((next_col >= 0)&&(next_col < mapcol)&&(next_row >= 0)&&(next_row < maprow)) { //��ĭ ������ ���� map���� �ƴϸ�
        		if(mapMatrix[next_row][next_col] == PointType.EMPTY) { //���� ĭ�� ��ĭ�� �´ٸ�
        			mapMatrix[row][col] = PointType.GOAL;
        			mapMatrix[next_row][next_col] = PointType.ROBOT;
        			currentPosition[0] = next_row;
        			currentPosition[1] = next_col;
        		}
        		else if(mapMatrix[next_row][next_col] == PointType.GOAL) { //���� ĭ�� GOAL�̶��
        			mapMatrix[row][col] = PointType.GOAL;
        			mapMatrix[next_row][next_col] = PointType.ROBOTONGOAL;
        			currentPosition[0] = next_row;
        			currentPosition[1] = next_col;
        		}
        		else { //����ĭ�� hazard�� colorblob�� �ִٸ�
            		System.out.println("error at doing Operation goStep");
        		}
        	}
    	}
    	else { //���� �ִ� �ڸ��� �������� �ʾҴٸ�
    		if((next_col >= 0)&&(next_col < mapcol)&&(next_row >= 0)&&(next_row < maprow)) { //��ĭ ������ ���� map���� �ƴϸ�
        		if(mapMatrix[next_row][next_col] == PointType.EMPTY) { //���� ĭ�� ��ĭ�� �´ٸ�
        			mapMatrix[row][col] = PointType.EMPTY;
        			mapMatrix[next_row][next_col] = PointType.ROBOT;
        			currentPosition[0] = next_row;
        			currentPosition[1] = next_col;
        		}
        		else if(mapMatrix[next_row][next_col] == PointType.GOAL) { //���� ĭ�� GOAL�̶��
        			mapMatrix[row][col] = PointType.EMPTY;
        			mapMatrix[next_row][next_col] = PointType.ROBOTONGOAL;
        			currentPosition[0] = next_row;
        			currentPosition[1] = next_col;
        		}
        		else { //����ĭ�� hazard�� colorblob�� �ִٸ�
            		System.out.println("error at doing Operation goStep");
        		}
        	}
    	}
    }
}
