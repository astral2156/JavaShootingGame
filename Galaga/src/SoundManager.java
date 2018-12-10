import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class SoundManager {
	// check sound status
	private static boolean flag_BackSound = true;
	private static boolean flag_EffectSound = true;
	
	// clips for music
    private Clip clip;
    private Clip BGM;
    
    // take multiple musics
    private ArrayList<Clip> ClipArray;
    
    // use singleton pattern 
    private static SoundManager mSoundManager = null;
	
    private SoundManager() {
    	ClipArray = new ArrayList<Clip>();
    }
    
    public static  void setFlag_BGM(boolean flag) {
    	flag_BackSound = flag;
    }
    
    public static  void setFlag_Effect(boolean flag) {
    	flag_EffectSound = flag;
    }
    
    public static boolean getFlag_BGM() { return flag_BackSound ; }
    public static boolean getFlag_Effect() { return flag_EffectSound ; }
    
    public static SoundManager getInstance(){
    	// if sound manager is initial status
    	if(mSoundManager == null) {
    		// create object
    		mSoundManager = new SoundManager();
    		return mSoundManager;
    	}
    	// otherwise return pre-created sound manager
    	return mSoundManager;
    }
    
    // find and get music file
    public void getSoundFile(String fileName) {
    	  try {
              File file = new File(fileName);
              
              // check file is at place
              if (file.exists()) {
                  AudioInputStream sound = AudioSystem.getAudioInputStream(file);
               
                  clip = AudioSystem.getClip();
                  clip.open(sound);
                  ClipArray.add(clip);
              }
              // if not, throw exception
              else {
                  throw new RuntimeException(fileName);
              } 
          // deal with other exceptions
          } catch (MalformedURLException e) {
              e.printStackTrace();
              throw new RuntimeException(e);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
    
    // STOP, clear, set clipped music
    public void StopBGM(){
    	BGM.stop();
    }
    
    public void Clear() {
    	for(int i=0;i<ClipArray.size();i++) {
    		ClipArray.get(i).stop();
    	}
    }
    
    public void setSound(int i) {
    	clip = ClipArray.get(i);
    }
    
    /** play, stop, loop the sound clip */
    public void Play(){
    	if(flag_EffectSound==true){
        clip.setFramePosition(0);  //frameposition
        clip.start();
    	}
    }
    
    public void Stop(int soundNumber) {
        clip.stop();
        clip.close();
    }
    
    public Clip Loop(){
    	if(flag_BackSound==true){
    		clip.loop(Clip.LOOP_CONTINUOUSLY);
    		BGM = clip;
    		return clip;
    	} return null;
    }
}