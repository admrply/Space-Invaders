import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Mercurian extends Invader implements Cloaking, ActionListener{
	
	@SuppressWarnings("unused")
	private int width;
	@SuppressWarnings("unused")
	private int height;
	private boolean cloaked = false;
	Timer timer;
	Random random = new Random();
	
	public Mercurian(int parentWidth, int parentHeight, int x, int y){
		
		super(parentWidth, parentHeight, x, y);
		lives = 3;
		setImg();
		value = 40;
		this.height = img.getHeight()/2;
		this.width = img.getWidth()/2;
		timer = new Timer(random.nextInt(4000)+1000, this);
		timer.start();
		
	}
	
	@Override
	public void setImg(){
		try {
			img = ImageIO.read(getClass().getResource("/mercurian.png"));
			System.out.println("**MECURIAN-OK**");
		} catch (IOException e) {
			System.out.println("**CAN'T READ MERCURIAN**");
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Graphics g){
		if(!cloaked){
			super.draw(g);
		}
	}
	
	@Override
	public void changeCloak() {
		cloaked = !cloaked;
	}

	@Override
	public boolean isCloaked() {
		return cloaked;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		changeCloak();
		timer.setDelay(random.nextInt(4000)+1000);
	}

	@Override
	public void stopCloakChange() {
		timer.stop();
	}

	@Override
	public void startCloakChange() {
		timer.restart();
	}
}
