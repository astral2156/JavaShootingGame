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
	// remove sprite when it goes out of screen
	@Override
	public void move() {
		if (y > 700) {
			game.removeLazerSprite(this);
		}
		super.move();
	}
	
	// game over when hit the player
	public void handleCollision(Sprite other) {
		if (other instanceof StarShipSprite) {
			game.removeLazerSprite(this);
			game.endGame();
		}
	}
}
