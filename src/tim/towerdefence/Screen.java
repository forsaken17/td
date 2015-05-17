package tim.towerdefence;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Screen extends JPanel implements Runnable {

	Thread thread;
	Frame frame;
	Level level;
	LevelFile levelFile;
	User user;
	private int fps = 0;
	public int scene = 0;
	public int hand = 0;
	public int handXPos = 0;
	public int handYPos = 0;
	public boolean running = false;
	private final int DELAY = 25;
	public double towerWidth = 1;
	public double towerHeight = 1;

	public int[][] map = new int[22][14];
	public Tower[][] towerMap = new Tower[22][14];
	public Image[] terrain = new Image[100];

	private String packagename = "tim/towerdefence";

	public Screen(Frame frame) {
		this.frame = frame;
		this.frame.addKeyListener(new KeyHandler(this));
		this.frame.addMouseListener(new MouseHandler(this));
		double width = this.frame.getWidth();
		double height = this.frame.getHeight();
		System.out.println(width+":"+height);
		towerWidth = width / 27.32;
		towerHeight = height / 15.36;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// System.out.println("[Success] paintComponent");
		g.clearRect(0, 0, this.frame.getWidth(), this.frame.getHeight());

		if (scene == 0) {
			g.setColor(Color.blue);
			g.fillRect(0, 0, this.frame.getWidth(), this.frame.getHeight());
		} else if (scene == 1) {
			// background
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, this.frame.getWidth(), this.frame.getHeight());
			// grid
			g.setColor(Color.gray);
			for (int x = 0; x < 22; x++) {
				for (int y = 0; y < 11; y++) {
					g.drawImage(terrain[map[x][y]], (int) towerWidth
							+ (x * (int) towerWidth), (int) towerHeight
							+ (y * (int) towerHeight), (int) towerWidth,
							(int) towerHeight, null);
					g.drawRect((int) towerWidth + (x * (int) towerWidth),
							(int) towerHeight + (y * (int) towerHeight), (int) towerWidth,
							(int) towerHeight);
				}
			}
			// stats
			g.drawRect((int) towerWidth, 600, 200, 50);
			g.drawString("Health:" + user.player.health, 60, 620);
			g.drawRect((int) towerWidth, 650, 200, 50);
			g.drawString("Money:" + user.player.money, 60, 670);
			g.drawRect((int) towerWidth, 700, 200, 50);
			// tower scroll
			g.drawRect(250, 600, 50, 150);
			// tower list
			for (int x = 0; x < 10; x++) {
				for (int y = 0; y < 2; y++) {
					if (Tower.towerList[x * 2 + y] != null) {
						g.drawImage(Tower.towerList[x * 2 + y].texture,
								300 + (x * (int) towerWidth),
								600 + (y * (int) towerHeight),
								(int) towerWidth, (int) towerHeight, null);
						if (Tower.towerList[x * 2 + y].cost > this.user.player.money) {
							g.setColor(new Color(255, 0, 0, 100));
							g.fillRect(300 + (x * (int) towerWidth),
									600 + (y * (int) towerHeight),
									(int) towerWidth, (int) towerHeight);
						} else {

						}
					}
					g.setColor(Color.gray);
					g.drawRect(300 + (x * (int) towerWidth),
							600 + (y * (int) towerHeight), (int) towerWidth,
							(int) towerHeight);
				}
			}
			// hand
			if (hand != 0 && Tower.towerList[hand - 1] != null) {
				g.drawImage(Tower.towerList[hand - 1].texture, this.handXPos
						- (int) this.towerWidth / 2, this.handYPos
						- (int) this.towerHeight / 2, (int) towerWidth,
						(int) towerHeight, null);
			}
		} else {
			g.setColor(Color.white);
			g.fillRect(0, 0, this.frame.getWidth(), this.frame.getHeight());
		}

		g.drawString(fps + "", 10, 10);

	}

	public void loadGame() {
		user = new User(this);
		levelFile = new LevelFile();

		ClassLoader cl = this.getClass().getClassLoader();

		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 10; x++) {
				terrain[x + (y * 10)] = new ImageIcon(
						cl.getResource(packagename + "/terrain.png"))
						.getImage();
				terrain[x + (y * 10)] = createImage(new FilteredImageSource(
						terrain[x + (y * 10)].getSource(), new CropImageFilter(
								x * 25, y * 25, 25, 25)));
			}
		}
		running = true;

	}

	public void startGame(User user, String level) {
		user.createPlayer();
		this.level = levelFile.getLevel(level);
		this.level.findSpawnPoint();
		this.map = this.level.map;

		this.scene = 1;
	}

	@Override
	public void addNotify() {
		super.addNotify();

		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		long beforeTime, timeDiff, sleep;
		System.out.println("[Success] frame created");

		beforeTime = System.currentTimeMillis();
		int frames = 0;
		loadGame();

		while (running) {
			repaint();
			frames++;

			if (System.currentTimeMillis() - 1000 >= beforeTime) {
				fps = frames;
				frames = 0;
				beforeTime = System.currentTimeMillis();
				// System.out.println("[Success] running "+fps);
			}

			timeDiff = System.currentTimeMillis() - beforeTime;
			sleep = DELAY - timeDiff;

			if (sleep < 5) {
				sleep = 5;
			}

			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				System.out.println("Interrupted: " + e.getMessage());
			}

		}
		System.exit(0);
	}

	public void placeTower(int x, int y) {
		int xPos = (x - (int) towerWidth) / (int) towerWidth;
		int yPos = (y - (int) towerHeight) / (int) towerHeight;
		if (xPos > 22 || yPos > 14) {

		} else if (towerMap[xPos][yPos] == null && map[xPos][yPos] == 0) {
			user.player.money -= Tower.towerList[hand - 1].cost;
			towerMap[xPos][yPos] = Tower.towerList[hand - 1];
		}
	}

	private int getShopStartX() {
		return 300;
	}

	private int getShopEndX() {
		return 300 + (10 * (int) towerWidth);
	}

	private int getShopStartY() {
		return 600;
	}

	private int getShopEndY() {
		return 600 + (2 * (int) towerHeight);
	}

	public void mouseMoved(MouseEvent e) {
		startGame(user, "Level1");
	}

	public class MouseHeld {
		boolean mouseDown = false;

		public void mouseDown(MouseEvent e) {
			mouseDown = true;
			if (hand != 0) {
				// place tower
				placeTower(e.getXOnScreen(), e.getYOnScreen());
				hand = 0;
			}
			updateMouse(e);
		}

		public void updateMouse(MouseEvent e) {
			if (scene == 1) {
				if (mouseDown && hand == 0) {
					if (e.getXOnScreen() >= getShopStartX()
							&& e.getXOnScreen() <= getShopEndX()) {
						if (e.getYOnScreen() >= getShopStartY()
								&& e.getYOnScreen() <= getShopEndY()) {
							// Tower 1
							if (e.getXOnScreen() >= getShopStartX()
									&& e.getXOnScreen() <= (getShopStartX() + (int) towerWidth)
									&& e.getYOnScreen() >= getShopStartY()
									&& e.getYOnScreen() <= (getShopStartY() + (int) towerHeight)) {
								if (user.player.money >= Tower.towerList[0].cost) {
									System.out.println("[shop] bought"
											+ Tower.towerList[0].cost);
									hand = 1;
								}
							}
						}
					}
				}
			}
		}

		public void mouseMoved(MouseEvent e) {

			handXPos = e.getXOnScreen();
			handYPos = e.getYOnScreen();
		}

	}

	public class KeyTyped {
		public void keyESC() {
			running = false;
		}

		public void keySpace() {
			startGame(user, "Level1");
		}
	}

}
