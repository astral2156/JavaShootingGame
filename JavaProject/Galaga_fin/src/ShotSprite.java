import java.awt.Image;

public class ShotSprite extends Sprite {
	private GalagaGame game;

	public ShotSprite(GalagaGame game, Image image, int x, int y, int diff) {
		super(image, x, y);
		this.game = game;
		
		if (diff == 3) { // ����� ���� ���� �Ѿ� ���ǵ带 ������. �Ѿ��� ������ ���߱� �����Ƿ� 3�� hard - kdy
			dy = -2; // dy�� 0�� �������� ������ �߻��
		} else if (diff == 2) {
			dy = -5;
		} else {
			dy = -10;
		}
	}

	@Override
	public void move() {
		super.move();
		// remove when it goes out of screen
		if (y < -10) {
			// ���� removeSprite���� removeSpriteGun�� ����
			game.removeShotSprite(this);
		}
	}
	
	//shot�� alien�� ������ health--
	@Override
	public void handleCollision(Sprite other) {
		if (other instanceof AlienSprite) {
			game.removeShotSprite(this);
			other.health--;
			if(other.health==0)
				game.removeAlienSprite(other);
		}
	}
}