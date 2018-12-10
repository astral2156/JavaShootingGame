import java.awt.Image;

public class ShotSprite extends Sprite {
	private GalagaGame game;

	public ShotSprite(GalagaGame game, Image image, int x, int y, int diff) {
		super(image, x, y);
		this.game = game;
		
		if(diff ==3) {	// 어려움 조절 변수 총알 스피드를 조절함. 총알이 느리면 맞추기 어려우므로 3이 hard  - kdy

			dy = -2;		// dy가 0에 가까울수록 느리게 발사됨
		} else if(diff == 2) {
			dy=-5;
			
		}else {
			dy=-10;
		}
	}

	@Override
	public void move() {
		super.move();
		if (y < -100) {
			game.removeSpriteGun(this);			// 기존 removeSprite에서 removeSpriteGun로 변경
		}
	}
	/*
	@Override
	public void handleCollision(Sprite other) {
		if (other instanceof AlienSprite) {
			
			game.removeSprite(this);
			game.removeSprite(other);
			
		}
	}
	*/
	
	
	//shot과 alien이 만나면 health--
	@Override
	public void handleCollision(Sprite other) {
		if (other instanceof AlienSprite) {
			game.removeSprite(this);
			other.health--;
			if(other.health==0)
				game.removeSprite(other);
			
		}
	}
}