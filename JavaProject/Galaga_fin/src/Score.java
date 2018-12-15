import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Score {
	FileWriter writeFile = null;
	FileReader readerFile = null;
	BufferedWriter writer = null;
	BufferedReader reader = null;
	String whoIsBest;
	
	public int score=0;
	public String highScore = ""+":"+"00"; // not activated
	public String name = "";
	public String whoBest = "";
	
	public void check() {  // 처음 시작시 최고 점수 초기화하는 메소드  
		// read score data
		File scoreFile = new File("highscore.dat");

		try {
			FileReader readerFile = new FileReader(scoreFile);
			BufferedReader reader = new BufferedReader(readerFile);
			try {
				whoIsBest = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// get score when destroy enemy
	public void scoreAdd(){
		score +=1;
	}
}