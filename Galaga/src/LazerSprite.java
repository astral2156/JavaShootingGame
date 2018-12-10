import java.awt.Image;

public class LazerSprite extends Sprite {
	private GalagaGame game;
	public LazerSprite(GalagaGame game, Image image, int x, int y) {
		super(image, x, y);
		// TODO Auto-generated constructor stub
		this.game = game;
		
			dx = 0;
			dy = 5;	

		
	}
	@Override
	public void move() {
		if (dy>700) {
			game.removeSprite(this);
		}
		super.move();
	}
	@Override
	public void handleCollision(Sprite other) {
		if (other instanceof StarShipSprite) 
			game.endGame();
	}
}
