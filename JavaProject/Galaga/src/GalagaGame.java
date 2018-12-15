import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GalagaGame extends JPanel implements KeyListener {
	// check running state
	private boolean running = true;

	// array list for saving sprites
	private ArrayList sprites = new ArrayList();
	private Sprite starship;

	// new array list for managing sprite separately
	// assprites = alien, shot
	// lsprites = starship, lazer
	private ArrayList assprites = new ArrayList();
	private ArrayList lsprites = new ArrayList();
	
	// image for enemy, player, attack, background
	private BufferedImage alienImage;
	private BufferedImage shotImage;
	private BufferedImage shipImage;
	private BufferedImage spaceImage;
	
	// add
	private int stage;
	private BufferedImage lazerImage;
	private static int cnt = 0; // ������ ��� Ÿ�̹� ����
	private String difficulty ="1"; // ����� ���� ����
	
	// audio buffer and clip for playing music
	private Clip clip;
	private AudioInputStream audioInputStream;
	private File audioFile;
	//private int shotCnt = 0;
	
	// score information
	Score scoreF = new Score();
	//int block = 0; // ���Ͻ� score�� 2�� �ö󰡴� �� ������ kȦ���϶��� score ���� kdy

	// constructor, launch frame and game
	public GalagaGame() {	
		JFrame frame = new JFrame("Galaga Game");
		frame.setSize(800, 700);
		frame.add(this);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// put img file data into img buffer
		try {
			shotImage = ImageIO.read(new File("fire.png"));
			shipImage = ImageIO.read(new File("starship.png"));
			alienImage = ImageIO.read(new File("ufo.gif"));
			lazerImage = ImageIO.read(new File("lazer.png"));
			spaceImage = ImageIO.read(new File("space.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// audio file designate
		audioFile = new File("playingmusic.wav").getAbsoluteFile(); 
		
		/** game start - select difficulty */
		difficulty = JOptionPane.showInputDialog("select difficulty 1(easy) / 2(normal) / 3(hard) ?");

		// difficulty checker
		// System.out.println("setting " + difficulty);

		// set exception catcher
		if (!difficulty.equals("2") && !difficulty.equals("3")) {
			difficulty = "1";
			// System.out.println("changed " + difficulty);
		}
		
		// load score
		scoreF.check();
		System.out.println(scoreF);
		
		this.requestFocus();
		// this.initSprites(); //��ŸƮ���� �־ ����
		stage = 1;
		startGame();

		addKeyListener(this);

		// play audio
		playSound();
	}

	// create player, enemy sprite
	private void initSprites() {
		// player sprite
		starship = new StarShipSprite(this, shipImage, 370, 550);
		lsprites.add(starship);
		
		// enemy sprite
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 12; x++) {
				Sprite alien = new AlienSprite(this, alienImage,
						100 + (x * 50), (50) + y * 30);

				// ����۽� �ϳ��� �ö󰡰�
				alien.health = stage;
				assprites.add(alien);
			}
		}
	}

	private void startGame() {
		assprites.clear();
		lsprites.clear();
		// �����Ҷ� Ÿ�̹� �α�(���ص� ��������ϴ�.)
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		initSprites();
	}

	// end game
	public void endGame() {
		CheckScore(); // ���������� �������� ��� �ּ� ������ �����Ű�� kdy
		System.exit(0); // ���������� �������� ��� �ּ� ������ �����Ű�� kdy
	}

	/** separate sprite object by image*/
	// remove sprite
	public void removeAlienSprite(Sprite sprite) {
		assprites.remove(sprite);
		scoreF.scoreAdd();
		/*
		block++;
		// scoreAdd�� 2���� ȣ��Ǿ� 2�� ���ھ �����ϴ����� �������� ���� ��� kdy
		if (block % 2 == 1) {		
			scoreF.scoreAdd(); // score add ���ھ 1�� �߰���Ŵ kdy
		}*/
	}
	
	// ������ removeSprite�� �Ѿ����Ž�, �� ���Ž� �����޼ҵ带 ��� => separate
	public void removeShotSprite(Sprite sprite) {
		assprites.remove(sprite);
	}
	
	// another remove method for deal with laser removing
	public void removeLazerSprite(Sprite sprite) {
		lsprites.remove(sprite);
	}

	// player attack
	public void fire() {
		// mend coord. of shot fire
		ShotSprite shot = new ShotSprite(this, shotImage,
				starship.getX() + 24, starship.getY() - 30,
				Integer.parseInt(difficulty));
		assprites.add(shot);
	}

	// �� ��� ��ŵƴ��� Ȯ�� �� �����
	public void restart() {
		boolean flag = true;
		for (int i = 0; i < assprites.size(); i++) {
			if (assprites.get(i) instanceof AlienSprite) {
				flag = false;
				break;
			}
		}
		if (flag) {
			stage++;
			startGame();
		}
	}

	// �� ��ü�� ������ ���
	public void enemyLazer() {
		// stage goes, become harder by increasing enemy shots
		for (int k = 0; k < stage * 2 + 1; k++) {
			if (cnt == 100) {
				int i = (int) (1 + (Math.random() * (assprites.size())));
				for (int j = 0; j < assprites.size(); j++) {
					if (j == i) { // if match, generate laser
						Sprite sprite = (Sprite) assprites.get(i);
						if (sprite instanceof AlienSprite) {
							LazerSprite lazer = new LazerSprite(this,
									lazerImage,
									sprite.x + 15, sprite.y + 30);
							lsprites.add(lazer);
						}
					}
				}
				cnt = 0;
			} else
				cnt++;
		}
	}

	// paint sprite, score information
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 700);
		g.drawImage(spaceImage, 0, 0, 800, 700, this);
		
		// draw sprite
		for (int i = 0; i < assprites.size(); i++) {
			Sprite sprite = (Sprite) assprites.get(i);
			sprite.draw(g);
		}
		for (int i = 0; i < lsprites.size(); i++) {
			Sprite sprite = (Sprite) lsprites.get(i);
			sprite.draw(g);
		}
		
		// draw score kdy
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.PLAIN, 14));
		g.drawString("Score : " + scoreF.score, 500, 30);
		g.drawString("Best Score : " + scoreF.whoIsBest, 600, 30);
	}

	/** GAMELOOP - play-in-status */
	// move sprite objects and repaint, check collision
	public void gameLoop() {
		while (true) {	
			// move sprite
			for (int i = 0; i < assprites.size(); i++) {
				Sprite sprite = (Sprite) assprites.get(i);
				sprite.move();
			}
			for (int i = 0; i < lsprites.size(); i++) {
				Sprite sprite = (Sprite) lsprites.get(i);
				sprite.move();
			}
			
			// check collision between enemy and shot
			for (int p = 0; p < assprites.size(); p++) {
				for (int s = p + 1; s < assprites.size(); s++) {
					Sprite me = (Sprite) assprites.get(p);
					Sprite other = (Sprite) assprites.get(s);

					if (me.checkCollision(other)) {
						me.handleCollision(other);
						other.handleCollision(me);
					}
				}
			}
			
			// check collision between player and laser
			for (int s = 1; s < lsprites.size(); s++) {
				Sprite me = (Sprite) lsprites.get(0);
				Sprite other = (Sprite) lsprites.get(s);
				
				if (me.checkCollision(other)) {
					me.handleCollision(other);
					other.handleCollision(me);
				}
			}

			// check enemy break through margin line
			for (int p = 0; p < assprites.size(); p++) {
				Sprite me = (Sprite) lsprites.get(0);
				Sprite other = (Sprite) assprites.get(p);

				if (me.checkCollision(other)) {
					me.handleCollision(other);
					other.handleCollision(me);
				}
			}
			
			// ������ ����
			enemyLazer();
			// �� ��� ��ŵƴ��� Ȯ�� �� �����
			restart();

			// update sprite movement
			repaint();
			
			// music replay
			if(clip.isActive()) {
				//System.out.println("still playin");
			} else {	// if music is stop, play again
				//System.out.println("stopped");
				try {
					audioInputStream = AudioSystem
							.getAudioInputStream(audioFile);
					clip = AudioSystem.getClip();
					clip.open(audioInputStream);
					clip.start();
					//clip.loop(0);
					//System.out.println("replay success");
				} catch (Exception ex) {
					System.out.println(ex);
					ex.printStackTrace();
				}
			}

			try {
				// alien�̵� 5�� ���� �ð�����
				Thread.sleep(13);
			} catch (Exception e) {
			}
		}
	}

	/** keyboard event */
	@Override
	public void keyPressed(KeyEvent e) {
		// move player when left or right key pressed
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
			starship.setDx(-4);
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
			starship.setDx(+4);
		/*
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			// delay shot
			shotCnt++;
			if(shotCnt > 0) {
				fire();
				shotCnt = 0;
			} else {
				shotCnt++;
			}
			//fire();
		*/
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// stop player moving when key released
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
			starship.setDx(0);
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
			starship.setDx(0);		
		// perform fire() method when space key released
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			fire();
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public static void main(String argv[]) {
		GalagaGame g = new GalagaGame();
		g.gameLoop();
	}

	public void CheckScore() { // ���� ������ �ְ������� ���ϴ� �޼ҵ�
		//System.out.println(scoreF.highScore);
		FileWriter writeFile = null;
		BufferedWriter writer = null;

		if (scoreF.highScore.equals(""))
			return;

		// ���� ������ dat ���� ���� �ְ����� ���Ͽ� ���� ������ Ŭ�� �۵�
		if (scoreF.score > Integer.parseInt((scoreF.whoIsBest.split(":")[1]))) {
			scoreF.name = JOptionPane.showInputDialog("You set a High Score!! what is your name?");
			scoreF.highScore = scoreF.name + ":" + scoreF.score;

			File scoreFile = new File("highscore.dat");

			if (!scoreFile.exists()) {
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);

				// writer.newLine();
				writer.write(scoreF.highScore);
			} catch (Exception e) {
				return;
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (Exception e) {
				}
			}
		} else {  // �׷��� �����ÿ��� ���� �����Ŵ
			JOptionPane.showMessageDialog(null, "Game Over!");
			// �׷��� ������� �̹� endGame�޼ҵ忡��  �ڵ� ����ǹǷ� �ʿ����
		}
	}

	// get audio file by using audio stream and clip
	public void playSound() {
		try {
			audioInputStream = AudioSystem
					.getAudioInputStream(audioFile);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
			//clip.loop(0);
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}
	}
}
