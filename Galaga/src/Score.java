import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Score {
	public int score=0;
	public String highScore = ""+":"+"00"; // not activated
	public String name = "";
	
	
	
	FileWriter writeFile = null;
	FileReader readerFile = null;
	BufferedWriter writer = null;
	BufferedReader reader = null;
	public String whoBest = "";
	
	public void check()
	{
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
		readerFile = new FileReader(scoreFile);
		writer = new BufferedWriter(writeFile);
		reader = new BufferedReader(readerFile);
		
		whoBest=reader.readLine();
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
	
	/*
	public void Score(){
		this.score =0;
		this.highScore =-1;
	}*/
	
	public void scoreAdd(){
		score +=1;
	}

}
