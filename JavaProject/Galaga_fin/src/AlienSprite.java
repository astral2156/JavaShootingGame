import java.awt.Image;
/** enemy sprite */
public class AlienSprite extends Sprite {
	private GalagaGame game;

	public AlienSprite(GalagaGame game, Image image, int x, int y) {
		super(image, x, y);
		this.game = game;

		// 라인 내려올때마다 대형이 조금씩 달라져서 수정
		// dx = -3;
		dx = -5;
	}

	@Override
	public void move() {
		if (((dx < 0) && (x < 10)) || ((dx > 0) && (x > 780))) {
			dx = -dx;
			y += 10;
			// game over, if enemy hit the margin line
			if (y > 600) {
				game.endGame();
			}
		}
		super.move();
	}
}