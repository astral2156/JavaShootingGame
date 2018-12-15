import java.awt.Image;
/** player flagship */
public class StarShipSprite extends Sprite {
	private GalagaGame game;

	public StarShipSprite(GalagaGame game, Image image, int x, int y) {
		super(image, x, y);
		this.game = game;
		dx = 0;
		dy = 0;
	}

	@Override
	public void move() {
		if ((dx < 0) && (x < -10)) {
			return;
		}	
		// starship 오른쪽으로 안보여져서 수정
		if ((dx > 0) && (x > 750)) {
			return;
		}
		super.move();
	}

	// collision event with alien and laser
	@Override
	public void handleCollision(Sprite other) {
		if (other instanceof AlienSprite || other instanceof LazerSprite) {
			game.endGame();
		}
	}
}