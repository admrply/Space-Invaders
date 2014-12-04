import java.io.IOException;

import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class Venusian extends Invader{
	
	private int width;
	private int height;
	
	public Venusian(int parentWidth, int parentHeight, int x, int y){
		super(parentWidth,parentHeight,x,y);
		lives = 2;
		setImg();
		value = 20;
		this.height = img.getHeight()/2;
		this.width = img.getWidth()/2;
	}
	
	@Override
	public void setImg(){
		try {
			img = ImageIO.read(getClass().getResource("/secondmartian.png"));
			System.out.println("**VENUSIAN-OK**");
		} catch (IOException e) {
			System.out.println("**CAN'T READ VENUSIAN**");
			e.printStackTrace();
		}
	}
	
}
