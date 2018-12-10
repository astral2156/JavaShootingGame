import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

	private boolean running = true;

	private ArrayList sprites = new ArrayList();
	private Sprite starship;

	private BufferedImage alienImage;
	private BufferedImage shotImage;
	private BufferedImage shipImage;

	// add
	private int stage;
	private BufferedImage lazerImage;
	private static int cnt = 0; // 레이저 쏘는 타이밍 조절
	private String difficulty =""; // 어려움 조절 변수
	

	Score scoreF = new Score();
	int block = 0; // 죽일시 score가 2씩 올라가는 거 수정용 k홀수일때만 score 증가 kdy

	public GalagaGame() {
		
		JFrame frame = new JFrame("Galaga Game");

		frame.setSize(800, 700);
		frame.add(this);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	
		try {
			shotImage = ImageIO.read(new File("fire.png"));
			shipImage = ImageIO.read(new File("starship.png"));
			alienImage = ImageIO.read(new File("alien.png"));

			// 레이저 이미지 추가
			lazerImage = ImageIO.read(new File("lazer.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		difficulty = JOptionPane.showInputDialog("select difficulty 1 / 2 / 3(hard) ?");
		scoreF.check();
		
		this.requestFocus();
		// this.initSprites(); //스타트게임 있어서 제외
		stage = 1;
		startGame();

		addKeyListener(this);

		// play audio
		playSound();
	}

	private void initSprites() {
		starship = new StarShipSprite(this, shipImage, 370, 550);
		sprites.add(starship);
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 12; x++) {
				Sprite alien = new AlienSprite(this, alienImage, 100 + (x * 50), (50) + y * 30);

				// 재시작시 하나씩 올라가게
				alien.health = stage;

				sprites.add(alien);
			}
		}
	}

	private void startGame() {
		sprites.clear();
		// 시작할때 타이밍 두기(안해도 상관없습니다.)
		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}

		initSprites();
	}

	public void endGame() {
		 CheckScore(); // 종료조건이 정해졌을 경우 주석 지워서 실행시키기 kdy
		// System.exit(0); // 종료조건이 정해졌을 경우 주석 지워서 실행시키기 kdy

	}

	public void removeSprite(Sprite sprite) {
		sprites.remove(sprite);

		block++;
		if (block % 2 == 1) {		// scoreAdd가 2번씩 호출되어 2씩 스코어가 증가하던것을 막기위해 변수 사용 kdy
			scoreF.scoreAdd(); // score add 스코어를 1씩 추가시킴 kdy
		}
		
	}
	
	public void removeSpriteGun(Sprite sprite) {	// 기존의 removeSprite는 총알제거시, 적 제거시 같은메소드를 사용했음. 이를 분리해서 사용함 kdy
		sprites.remove(sprite);
	}


	public void fire() {
		ShotSprite shot = new ShotSprite(this, shotImage, starship.getX() + 10, starship.getY() - 30, Integer.parseInt(difficulty));
		sprites.add(shot);
	}

	///////////////////
	// 추가
	// 적 모두 재거됐는지 확인 및 재시작
	public void restart() {
		boolean flag = true;
		for (int i = 0; i < sprites.size(); i++) {
			if (sprites.get(i) instanceof AlienSprite) {
				flag = false;
				break;
			}
		}
		if (flag) {
			stage++;
			startGame();
		}
	}

	// 적 기체가 레이저 쏘기
	public void enemyLazer() {
		if (cnt == 50) {
			int i = (int) (1 + (Math.random() * sprites.size()));
			for (int j = 0; j < sprites.size(); j++) {
				if (j == i) {
					Sprite sprite = (Sprite) sprites.get(i);
					if (sprite instanceof AlienSprite) {
						LazerSprite lazer = new LazerSprite(this, lazerImage, sprite.x + 15, sprite.y + 30);
						sprites.add(lazer);
					}
				}
			}
			cnt = 0;
		} else
			cnt++;
	}
	/////////////////////////

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 700);
		for (int i = 0; i < sprites.size(); i++) {
			Sprite sprite = (Sprite) sprites.get(i);
			sprite.draw(g);
		}

		// draw score kdy
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.PLAIN, 14));
		g.drawString("Score : " + scoreF.score, 500, 30);
		if (scoreF.highScore == "") {
			scoreF.highScore = this.getHighScore();
		}

		g.drawString("Best Score : " + scoreF.highScore, 600, 30);

	}

	public void gameLoop() {
		while (running) {
			for (int i = 0; i < sprites.size(); i++) {
				Sprite sprite = (Sprite) sprites.get(i);
				sprite.move();
			}

			for (int p = 0; p < sprites.size(); p++) {
				for (int s = p + 1; s < sprites.size(); s++) {
					Sprite me = (Sprite) sprites.get(p);
					Sprite other = (Sprite) sprites.get(s);

					if (me.checkCollision(other)) {
						me.handleCollision(other);
						other.handleCollision(me);
					}

				}
			}

			// 레이저 생성
			enemyLazer();

			// 적 모두 재거됐는지 확인 및 재시작
			restart();

			repaint();

			try {
				// alien이동 5가 빨라서 시간증가
				Thread.sleep(13);
			} catch (Exception e) {
			}

		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			starship.setDx(-3);
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			starship.setDx(+3);
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			fire();

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			starship.setDx(0);
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			starship.setDx(0);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public static void main(String argv[]) {
		GalagaGame g = new GalagaGame();
		g.gameLoop();
	}

	public String getHighScore() { // kdy
		// format : ( name : 142 )
		FileReader readFile = null;
		BufferedReader reader = null;
		try {
			readFile = new FileReader("highscore.dat");
			reader = new BufferedReader(readFile);
			return reader.readLine();

		} catch (Exception e) {
			return "";
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void CheckScore() { // kdy

		//System.out.println(scoreF.highScore);
		FileWriter writeFile = null;
		BufferedWriter writer = null;

		if (scoreF.highScore.equals(""))
			return;

		if (scoreF.score > Integer.parseInt((scoreF.highScore.split(":")[1]))) {
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
		}

	}

	// get audio file
	public void playSound() {
		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new File("playingmusic.wav").getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}
	}

}
