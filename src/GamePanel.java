import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

//TODO Add player shelters
//TODO Comment the fuck out of everything!

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener, KeyListener{

	//This is the class where most of the game code occurs
	private Player player;
//	private Enemy enemy;
	private ArrayList<Enemy> enemyList;
	//This is where all of the enemies are stored. This list is modified by a series of iterators.
	private ArrayList<Shelter> shelterList;
	//This is where the player shelters are stored.
	public static int score = 0;
	//This score is made static so the enemy values can be easily added to it.
	public static int gameHeight;
	public static int gameWidth;
	//The game window constraints can be made static as the window is not resizable.
	
	public static boolean paused = false;
	public static int level = 1;
	private boolean mothershipsTrigger = true;
	//Determines whether motherShips should be drawn into the array instead of Invaders.
	//As Invaders are drawn in when the game starts, this needs to be set to true, so motherships
	//are drawn when there are no remaining Invaders. This makes more sense when looking at the
	//containsInstance checker at the bottom of the paintComponent method below.
	private boolean menuScreen = true;
	private boolean gameDeath=false;
	//You haven't died before you've played!
	//Game starts without a death, so the death count and score achieved are not shown in the menu
	private int deathCount=0;
	private int frameCount=0;
	//The frame counter is used to limit the number of bullets a player can fire per unit time.
	
	private boolean saved = false;
	public boolean loaded = false;
	//These booleans prevent double saving and allow user feedback on the pause screen to let the
	//player know the game saved successfully
	//The loaded boolean allows for a pause screen to be set on loading the game file.
	
	private String highScore;
	FileReader readBase;
	BufferedReader rIn;
	FileWriter writeBase;
	BufferedWriter wIn;
	PrintWriter finalWriter;
	JFileChooser fileChooser = new JFileChooser();
	ObjectOutputStream objectOutput;
	ObjectInputStream objectInput;
	//Above are the objects which assist in the reading and writing of files including serialisation.
	
	Font retro;
	
	AudioInputStream ais;
	Clip clip;
	boolean mothershipSoundPlaying = false;
	//This boolean is used to prevent the sound playing from the start and on top of itself in a loop

	Timer redrawTimer;
	Timer meteorTimer;
	Timer destroyerTimer;
	private int destroyerRate = new Random().nextInt(8000)+2500;
	private int meteorRate = new Random().nextInt(5000)+2500;
	//Above are the timers for the game, the redrawTimer always runs as this is the refresh rate.
	//The meteorTimers and destroyerTimers are spearate as they appear at different times and can be
	//stopped independently of the redrawTimer to prevent them appearing on screen.
	
	/*KONAMI CODE LISTENER!! :D UUDDLRLRBA*/
	private int konamiStage = 0;
	//Keeps track of how far through the code the user is
	public static boolean konamiActive = false;
	//Obviously starts false, sets konami status so all classes know how to behave.
	BufferedImage konamiBackground;
	//Image for the background declared here so the arguments for the paintComponent .drawImage
	//can be satisfied with the 'this' keyword.

	/** Constructor - Declares all the items needed at the start of the game **/
	public GamePanel(){
		enemyList = new ArrayList<Enemy>();
		//All enemies are stored in this array list and are written to/removed from with Iterators
		shelterList = new ArrayList<Shelter>();
		//All shelters are stored in this array
		redrawTimer = new Timer(10, this);
		//Refresh rate: 10ms
		meteorTimer = new Timer(meteorRate, this);
		destroyerTimer = new Timer(destroyerRate, this);
		//Timers that independently make meteors and destroyers appear
		
		try{
			konamiBackground = ImageIO.read(getClass().getResource("/konamibackground.jpg"));
			retro = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("retro.ttf"));
			//Attach the image to the konamiBackground variable.
		}
		catch(IOException e){
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		
	}
	
	/** Method to start the game, gets called from the InvadersGame class **/
	public void startGame(int width, int height){
		redrawTimer.start();
		//Starts the timer to redraw the screen when the game loads.
		GamePanel.gameHeight = height;
		GamePanel.gameWidth = width;
		//Defines the global game width and height which is used by all classes for positioning
		player = new Player(width,height);
		//Creates the player
	}

	/** Method to draw the invaders, gets called when there are no motherships left or when the **
	 ** game starts. 																			**/
	public void placeInvaders(int width, int height){
		redrawTimer.stop();
		//Stopping the timer before adding the enemies to the array prevents cascade drawing
		for(int i=0; i<8;i++){
			enemyList.add(new Martian(width,height,i * 100, 120));
		}
		//Adds 8 Martians to the array at starting y position 60
		for(int i=0; i<8; i++){
			enemyList.add(new Venusian(width,height,i * 100,200));
		}
		//Add 8 Venusians to the array at starting y position 140
		for(int i=0; i<8; i++){
			enemyList.add(new Mercurian(width,height,i * 100,280));
		}
		//Add 8 Mercurians to the array at starting y position 220
		//The x position is drawn depending on how far through the loop it is.
		redrawTimer.restart();
		//Restarts the timer so all enemies are drawn at the same time and remain in line.
	}

	/** Method to draw the motherships, gets called when there are no invaders left **/
	public void placeMotherships(int width, int height){
		for(int i=0;i<4;i++){
			enemyList.add(new Mothership(GamePanel.gameWidth, GamePanel.gameHeight, i * 200, 120));
		}
		//Same as above however this time a timer stop isn't neccessary as they are drawn in a line
		//so the alignment doesn't matter and isn't noticable at all.
	}
	
	//This is to add the shelters
	public void placeShelters(int width, int height){
		for(int i=0;i<4;i++){
			shelterList.add(new Shelter(width, height, i*(GamePanel.gameWidth/4)+75, GamePanel.gameHeight-110));
		}
	}

	/** handles the timer event, this method repeats based on the interval set in the timer (10ms) **/ 
	@Override
	public void actionPerformed(ActionEvent e) {	
		this.revalidate();
		//This is to fix OS X greyscreen issues.
		this.repaint();
		//causes the screen to be redrawn
		
		if(e.getSource()==meteorTimer){
			//If the meteorTimer fires...
			enemyList.add(new Meteor(GamePanel.gameWidth, GamePanel.gameHeight, new Random().nextInt(GamePanel.gameWidth),-20));
			//...add a meteor to the array at some random y value just off the top of the screen.
			meteorTimer.setDelay(new Random().nextInt(5000)+2500);
			//Change the delay to a new random value
		}
		
		if(e.getSource()==destroyerTimer){
			//If the destroyerTimer fires...
			enemyList.add(new Destroyer(GamePanel.gameWidth, GamePanel.gameHeight, GamePanel.gameWidth, 60));
			//...add a destroyer to the array just off screen to the right at a fixed height.
			destroyerTimer.setDelay(new Random().nextInt(8000)+4500);
			//Change the delay to a new random value
		}
	}

	/** Draws the in game labels for score, high score, level and lives **
	 ** Called from the paintComponent when the game is running			**/
	private void drawLabels(Graphics g){
		//Current Score:
		g.setColor(Color.WHITE);
		retro = retro.deriveFont(50f);
		g.setFont(retro);
		g.drawString("Score: "+score, 25, 40);
		//Draws the score in font retro in white size 50pt

		//High Score:
		rwHighScore();
		//Method to read and write the high score from file
		if(konamiActive){
			g.drawString("CAATZZ!!!!", 360, 40);
			//If the Konami code has been typed then don't show the highscore.
		}
		else{
			if(GamePanel.score>Integer.parseInt(highScore)){
				//If the current score is higher than the saved high score...
				g.drawString("High Score: "+score, 330, 40);
				//Update the shown high score to match the current score
			}
			else{
				g.drawString("High Score: "+highScore, 330, 40);
				//Otherwise show the saved high score
			}
		}
		
		//Level and lives:
		g.drawString(("Level: "+level+" /"), 700, 40);
		g.drawString("Lives: "+Player.lives, InvadersGame.width-150, 40);
	}
	
	/** Draws the pause menu labels, called from the paintComponent when the game is paused **/
	private void drawPauseMenuLabels(Graphics g){
		//Paused text:
//		retro = retro.deriveFont(200f);
		g.setFont(retro);
		g.drawString("PAUSED", 310,375);
		
		//Info text:
		retro = retro.deriveFont(50f);
		g.setFont(retro);
		if(saved){
			//If the game has just been saved...
			g.drawString("Game saved!", 440, 450);
			//Tell the user it was a sucess.
		}
		else if(loaded){
			//If the game has just been loaded from file...
			g.drawString("Press [P] to resume game!", 330, 450);
			//The game will be paused, so inform them how to resume.
			//(And prevent saving - This is seen further down)
		}
		else{
			g.drawString("[S]ave game", 440, 450);
			//Otherwise tell the user how to save the game
		}
	}
	
	/** Home screen/Title screen labels **/
	private void drawMenuLabels(Graphics g){
		g.setColor(Color.WHITE);
		if(gameDeath){
			//Death Notifier!
			retro = retro.deriveFont(140f);
			g.setFont(retro);
			g.drawString("GAME OVER!",250,250);
			retro = retro.deriveFont(50f);
			g.setFont(retro);
			g.drawString("Number of deaths: "+deathCount, 350, 550);
		}
		else{
			//Title card:
			retro = retro.deriveFont(180f);
			g.setFont(retro);
			g.drawString("SPACE INVADERS", 50, 250);
		}
		
		//Info text:
		retro = retro.deriveFont(50f);
		g.setFont(retro);
		g.drawString("[N]ew game", 420, 350);
		g.drawString("[L]oad game", 415, 400);
		
		//High score:
		rwHighScore();
		//Prevent IO errors before running next line as above
		if(gameDeath){
			g.drawString("Your Score: "+score, 395, 500);
			g.drawString("Current High Score: "+highScore, 300, 600);
		}
		else{
			g.drawString("Current High Score: "+highScore, 300, 550);
		}
	}
	
	/** Prevent IO errors when reading the high score, called whenever it needs to be read. **/
	public void rwHighScore(){
		try{
			readBase = new FileReader("highscore.txt");
			//Try to open the highscore file
			rIn = new BufferedReader(readBase);
			highScore = rIn.readLine();
			rIn.close();
		} catch (IOException e) {
			//If the file can't be opened...
			try {
				writeBase = new FileWriter("highscore.txt");
				//Create the file...
				wIn = new BufferedWriter(writeBase);
				finalWriter = new PrintWriter(wIn);
				finalWriter.print("0");
				//...and give it a value of zero.
				finalWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Unable to write file!");
				//If the file can't be written, catch the IO error
			}
		}
	}

	/** The paint component method that draws everything in the game **/
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//This line ensures that everything that would usually be drawn by a panel, is.
		frameCount++;
		//Frame counter to prevent player bullet streams because that's effectively cheating!
		g.setFont(retro);
		//Set all fonts to be "retro" font as imported on game start
		if(konamiActive){
			g.drawImage(konamiBackground, 0, 0, this.getWidth(), this.getHeight(), this);
			//If the Konami code has been typed, this "clears" the screen with the cat image.
		}
		else{
			g.setColor(Color.black);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			//This clears the screen by filling it with black colour.
		}
		
		if(menuScreen){
			//If we're in the menu screen...
			drawMenuLabels(g);
			//...then draw the appropriate labelset...
			if(player!= null&&!gameDeath){
				//If the player exists...
				player.draw(g);
				//...draw the player
			}
			//Cause I think it's cool to be able to control the player
			//spaceship in the menu screen! :P
			//Only before the game has started though. Player should be invisible after death.
		}
		else{
			//Otherwise we must be in game
			drawLabels(g);
			//So draw the ingame labels
			if(player!= null&&!gameDeath){
				//And if the player exists
				player.draw(g);
				//Draw the player
			}
	
			if(!player.isActive()){
				//If the player is out of lives
				endGame();
				//Run the endGame method as defined below
			}
	
			Iterator<Enemy> iterator = enemyList.iterator();
			//Sets up an iterator for cycling through the enemyList
			while (iterator.hasNext()) {
				//While there are still enemies left to move and draw
				Enemy e = iterator.next();
				//Set the temp enemy to be the next available enemy in the list
				
				/** PlayerBullet-Enemy collision detection **/
				for(int i=0;i<player.getBulletCount();i++){
					//For each of the bullets on the screen...
					if(player.getBulletBounds(i).intersects(e.getBounds())){
						//...check if it intersects with an enemy.
						e.removeLife();
						player.getBullet(i).destroy();
						//If it is, set the enemy and the bullet to inactive using the destroy() methods.
					}
				}
				
				/** EnemyBullet-Player collision detection **/
				if(e instanceof ShootBack){
					//If the enemy implements the ShootBack method...
					for(int i=0; i<((ShootBack)e).getEnemyBulletCount();i++){
						//Then for each bullet that the current enemy has fired... 
						if (((ShootBack)e).getBulletBounds(i).intersects(player.getBounds())){
							//...Check if it has hit the player...
							player.removeLife();
							((ShootBack)e).getBullet(i).destroy();
							//And if so, remove a life from the player and destroy the bullet.
							hitSound();
						}
					}
				}
				
				/** Player-Enemy collision detection **/
				if(player.getBounds().intersects(e.getBounds())){
					//If the enemy hits the player...
					e.removeLife();
					player.removeLife();
					hitSound();
					//...remove a life from the enemy and the player
					//Note that this could mean that if an enemy has one life left and the player has two,
					//the player will still survive because they'll only lose one life. If the enemy has
					//the same amount of lives or more than the player, the player will die.
				}
				
				Iterator<Shelter> shelterIterate = shelterList.iterator();
				//Sets up an iterator for cycling through the enemyList
				while (shelterIterate.hasNext()) {
					//While there are still enemies left to move and draw
					Shelter sh = shelterIterate.next();
					//Set the temp enemy to be the next available enemy in the list
					
					/** Enemy Bullet-Shelter collision detection **/
					if(e instanceof ShootBack){
						//If the current enemy can shoot
						for(int i=0; i<((ShootBack)e).getEnemyBulletCount();i++){
							//For each of the active bullets the enemy has
							if (((ShootBack)e).getBulletBounds(i).intersects(sh.getBounds())){
								//Check if the bounds intersect the shelter bounds
								if(sh.getNumberOfHits()==0){
									//If there are currently no hits on the shelter
									sh.hit(((ShootBack)e).getBulletPosition(i));
									((ShootBack)e).getBullet(i).destroy();
									//Add a hit point to the shelter and destroy the bullet
								}
								else{
									//If there is at least one hit point
									for(int noh=0;noh<sh.getNumberOfHits();noh++){
										//For each of the hit points
										Point theHit = sh.getHitCoord(noh);
										if(!((ShootBack)e).getBulletBounds(i).contains(theHit.x, theHit.y, 10, 10)){
											//Check if the bullet bounds do not contain the hit coordinate
											Point cbp = ((ShootBack)e).getBulletPosition(i);
											sh.hit(((ShootBack)e).getBulletPosition((i)-10));
											((ShootBack)e).getBullet(i).destroy();
											//If they don't, at a hit point and destroy the bullet
										}
									}
								}
							}
						}
						sh.draw(g);
						sh.hitDraw(g);
					}
					/** Enemy-Shelter collision detection **/
					if(e.getBounds().intersects(sh.getBounds())){
						sh.destroy();
						shelterIterate.remove();
						e.destroy();
					}
					
					sh.draw(g);
				}
				
	
				if(e!=null && e.isActive()){
					//If the enemy exists and hasn't been killed...
					e.move();
					e.draw(g);
					//...move and draw the enemy
					if(e.edgeHit()){
						//If this enemy has hit the edge of the screen, then execute these commands.
						Iterator<Enemy> downwardIterator = enemyList.iterator();
						/* Sets up a second iterator for cycling through the enemyList from the start. If the *
						 * first iterator is used, it won't apply these movement changes to all enemies but   *
						 * only to those after the point it is up to in the .move().draw() cycle              */
						while (downwardIterator.hasNext()) {
							//While there are still enemies left to move...
							Enemy de = downwardIterator.next();
							//...set the temporary enemy modifier/placeholder variable to be the next available enemy...
							de.dropPosition();
							de.changeDirection();
							//...and move the enemy down and change it's direction
						}
					}
				}
				else{
					iterator.remove();
					//This is part of the first iterator.
					//If the enemy is no longer active (i.e. killed/collided with) then remove it from the list.
				}
			}
			if(!containsInstance(enemyList, Invader.class)&&!containsInstance(enemyList, Mothership.class)){
				//This uses the custom class written below that allows the enemyList to be checked for a
				//certain type of class. If there are no invaders and no motherships in the enemylist
				if(mothershipsTrigger&&!gameDeath){
					//and if the motherships trigger has been activated
					destroyerTimer.stop();
					meteorTimer.stop();
					//Stop the specials timers
					purge();
					//Stop currently queued meteors and timers from appearing from off screen
					placeMotherships(GamePanel.gameWidth, GamePanel.gameHeight);
					playMothershipsSound();
					//and place the motherships on screen
					mothershipsTrigger=false;
					//then set the trigger to false
				}
				else if(!mothershipsTrigger&&!gameDeath){
					//If the motherships trigger is off
					destroyerTimer.start();
					meteorTimer.start();
					//Start the specials timers
					GamePanel.level = GamePanel.level + 1;
					//Increase the difficulty
					placeInvaders(GamePanel.gameWidth, GamePanel.gameHeight);
					stopMothershipsSound();
					//and draw the next wave of invaders
					mothershipsTrigger=true;
					//The motherships trigger gets activated only when a wave of motherships is destroyed.
					//This starts off as false and so prevents motherships from being drawn at the very
					//start of the game.
				}
			}
			
			if(GamePanel.paused){
				//If the game has been paused
				drawPauseMenuLabels(g);
				//then draw the pause labels.
				//The actual pausing is dealt with below in the pause method and the action listener on
				//key 'P'
			}
			if(konamiActive){
				//If the Konami code has been typed
				meteorTimer.setDelay(750);
				//Increase the speed of meteors
				destroyerTimer.stop();
				//Stop the destroyers
				GamePanel.score++;
				//And make the score do crazy things... Cause why not?!
				//This score increase doesn't affect the highscore however as can be seen from the endGame
				//method below
			}
		}
	}
	
	private void playMothershipsSound(){
		try {
			ais = AudioSystem.getAudioInputStream(this.getClass().getResource("motherships.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			clip.loop(-1);;
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mothershipSoundPlaying = true;
	}
	
	private void stopMothershipsSound(){
		if(mothershipSoundPlaying){
			clip.stop();
		}
	}
	
	private void hitSound(){
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(this.getClass().getResource("hit.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** This method removes queued enemies which are still situated off screen **/
	private void purge() {
		Iterator<Enemy> purgeIterator = enemyList.iterator();
		//Iterator to cycle through the list
		if(purgeIterator.hasNext()){
			//If there's still enemies to look at
			Enemy purgeEnemy = purgeIterator.next();
			//Update the temp variable
			if(purgeEnemy!=null && purgeEnemy.isActive()){
				//If it exists and hasn't been killed
				if(!((purgeEnemy.getPosition().x>0 && purgeEnemy.getPosition().x<GamePanel.gameWidth)||
						/*And if it's not between x=0 and the width of the GamePanel or...*/
				   (purgeEnemy.getPosition().y>0 && purgeEnemy.getPosition().y<GamePanel.gameHeight))){
						/* between y=0 and the height of the GamePanel
						 * i.e. it's not visible on the screen */
					purgeEnemy.destroy();
					//Destroy the enemy
					System.out.println("Off screen enemy destroyed");
					purgeIterator.remove();
					//And remove it from the array
				}
				
			}
		}
	}
	
	/** Method to pause the game, called from the keyEvent 'P' **/
	public void pauseGame(){
		if(paused){
			//If it's already paused, we want to unpause it
			meteorTimer.restart();
			destroyerTimer.restart();
			//So start the timers again
			Iterator<Enemy> bulletAndCloakStartIterator = enemyList.iterator();
			while (bulletAndCloakStartIterator.hasNext()){
				Enemy e = bulletAndCloakStartIterator.next();
				//And iterate through the enemies to restart the bullets and cloaks
				if (e instanceof ShootBack){
					((ShootBack) e).startBullets();
					//If they belong to the particular inferfaces for shooting...
				}
				if (e instanceof Cloaking){
					((Cloaking) e).startCloakChange();
					//...and cloaking
				}
			}
			paused = false;
			loaded = false;
			saved = false;
			//Game state instantly changes when unpaused so it is no longer saved or loaded!
		}
		else{
			//Otherwise it isn't paused
			meteorTimer.stop();
			destroyerTimer.stop();
			//So stop the specials timers
			Iterator<Enemy> bulletPauseIterator = enemyList.iterator();
			while (bulletPauseIterator.hasNext()){
				//And iterate through the bullets to prevent them from queuing up and all firing at once
				//once the game is unpaused
				Enemy e = bulletPauseIterator.next();
				if (e instanceof ShootBack){
					((ShootBack) e).stopBullets();
				}
				if (e instanceof Cloaking){
					((Cloaking) e).stopCloakChange();
					//Also prevent cloaking so that the game actually looks paused, otherwise the enemies
					//will cloak and uncloak in the background because the redraw timer is still running
					//in order to draw the pause menu labels.
				}
			}
			paused = true;
			//Game is paused.
		}
	}
	
	/** Method to run when the player dies **/
	private void endGame() {
		if(GamePanel.score>Integer.parseInt(highScore)&&!konamiActive){
			//If the score is higher than the saved highScore loaded earlier and we aren't in Konami mode
			try {
				writeBase = new FileWriter("highscore.txt");
				wIn = new BufferedWriter(writeBase);
				finalWriter = new PrintWriter(wIn);
				finalWriter.print(score);
				//Writer the new score to file
				finalWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Unable to write file!");
			}
		}
		boolean explosionPlayed = false;
		if(!explosionPlayed){
			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(this.getClass().getResource("explosion.wav"));
				Clip clip = AudioSystem.getClip();
				clip.open(ais);
				clip.start();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			explosionPlayed = true;
		}
		//Uncomment this to die.
		//Commented to test.
		stopMothershipsSound();
		meteorTimer.stop();
		destroyerTimer.stop();
		enemyList.removeAll(enemyList);
		menuScreen = true;
		gameDeath = true;
		deathCount = deathCount+1;
	}
	
	/** Method to save the game **/
	public void saveGameState(){
		if(konamiActive){
			JOptionPane.showMessageDialog(null, "One does not simply save the Konami!");
			//Game can't be saved if the Konami is running, cause that's just stupid...
		}
		else if(!loaded){
			//If the file hasn't JUST been loaded
			fileChooser = new JFileChooser();
			//Instantiate a file chooser for the user to use
			if (fileChooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
				//Once they've created a save location
				try{
					File fileName = fileChooser.getSelectedFile();
					//Get the file they chose
					FileOutputStream file = new FileOutputStream(fileName);
					//Open an output stream with that file path
					objectOutput = new ObjectOutputStream(file);
					//Open an Object stream within the output stream (for serialisation)
					objectOutput.writeInt(score);
					objectOutput.writeInt(Player.lives);
					objectOutput.writeInt(level);
					objectOutput.writeObject(enemyList);
					objectOutput.writeObject(player);
					//Write the game's current state. Just writing the entire ArrayList as one is easier
					//than iterating through all the enemies.
					objectOutput.close();
					file.close();
					//Close the object output and the output stream
					saved = true;
					//Tell the game it's been saved, so the labels update
				}
				catch(IOException e){
					JOptionPane.showMessageDialog(null, "Can't write to that location!");
					//Warn the user they can't write to a certain location if the file save fails.
					e.printStackTrace();
				}
			}
			//If it has just been leaded then the different error message shows
			//The way that double saves are prevented is dealt with in the KeyListener.
		}
	}
	
	@SuppressWarnings("unchecked")
	//The unchecked warning can be suppressed because we will only be dealing with a very rigid
	//file structure that will be saved and read the same way each time.
	private void loadGameState() {
		fileChooser = new JFileChooser();
		//Instantiate a file chooser
		if (fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
			//Give the user a GUI to choose a file from
			try{
				File fileName = fileChooser.getSelectedFile();
				//Get the file name
				FileInputStream file = new FileInputStream(fileName);
				//And set up an input stream with that file name
				objectInput = new ObjectInputStream(file);
				//With an object input stream nested inside it
				
				score = (int) objectInput.readInt();
				Player.lives = (int) objectInput.readInt();
				level = (int) objectInput.readInt();
				enemyList = (ArrayList<Enemy>) objectInput.readObject();
				player = (Player) objectInput.readObject();
				//Read back in the same order as it was written. Casts are required to ensure correct import
				
				Iterator<Enemy> imgResetIterator = enemyList.iterator();
				while(imgResetIterator.hasNext()){
					Enemy eir = imgResetIterator.next();
					eir.setImg();
					//Because BufferedImages are not serialisable, they are made transient and so aren't
					//saved to the serialised file. As a result of this, when they are reimported, they
					//are drawn without an image.
					//This iterator goes through all the enemies in the ArrayList and reassigns them with
					//the appropriate image for the type of class that they are.
					//This is handled using a setImg() class in each enemy type which is overridden to show
					//the correct image type.
					if(eir instanceof ShootBack){
						((ShootBack)eir).replaceBulletImage();
					}
				}
				player.setImg();
				//Finally, reset the player image
				gameDeath=false;
				menuScreen=false;
				//then leave the menu screen
				paused=true;
				loaded=true;
				//and swap to the loaded pause screen
			}
			catch (InvalidClassException e){
				JOptionPane.showMessageDialog(null, "This is from an old version of Space Invaders\n"
						+ "and is incompatible with this version.");
				enemyList.removeAll(enemyList);
				menuScreen=true;
				loaded=false;
				paused=false;
				e.printStackTrace();
			}
			catch(IOException e){
				JOptionPane.showMessageDialog(null, "Can't open that file!");
				//Error reading file message
				enemyList.removeAll(enemyList);
				menuScreen=true;
				loaded=false;
				paused=false;
				e.printStackTrace();
			}
			catch (ClassNotFoundException e) {
				JOptionPane.showMessageDialog(null, "This is not a valid game save file!");
				//Not a valid file message
				enemyList.removeAll(enemyList);
				menuScreen=true;
				loaded=false;
				paused=false;
				e.printStackTrace();
			}
		}
	}
	
	//This method uses Generics to check if an ArrayList contains a certain Class. This is called above
	//in the if statement which checks if there are any Invaders left on screen.
	@SuppressWarnings("hiding")
	public static <Enemy> boolean containsInstance(ArrayList<Enemy> list, Class<? extends Enemy> clazz) {
	    for (Enemy e : list) {
	    	//For each enemy in the given list
	        if (clazz.isInstance(e)) {
	        	//check if the queried instance (Invader/Mothership/etc) is present in the list
	            return true;
	            //If it is, return true
	        }
	    }
	    return false;
	    //Otherwise return false.
	}

	

	//Code here controls the keyevents
	@Override
	public void keyPressed(KeyEvent event) {
		switch(event.getKeyCode())
		{
		case KeyEvent.VK_RIGHT: player.move(1); if(paused){pauseGame();}
								//If the game has been paused and the player tries to move, unpause the game
		
								//These konamiStage counters increase if the pattern is typed in order
								//and reset to zero if they are typed incorrectly.
								if(konamiStage==5){
									konamiStage++;
								}
								else if(konamiStage==7){
									konamiStage++;
								}
								else{
									konamiStage=0;
								}
								break;
		case KeyEvent.VK_LEFT: player.move(-1); if(paused){pauseGame();}
								if(konamiStage==4){
									konamiStage++;
								}
								else if(konamiStage==6){
									konamiStage++;
								}
								else{
									konamiStage=0;
								}
								break;
		case KeyEvent.VK_SPACE: if(frameCount>15){
									player.fire();
									try {
//										InputStream shootSound = GamePanel.class.getResourceAsStream("playerlaser.wav");
										AudioInputStream ais = AudioSystem.getAudioInputStream(this.getClass().getResource("playerlaser.wav"));
										Clip clip = AudioSystem.getClip();
										clip.open(ais);
										clip.start();
									} catch (UnsupportedAudioFileException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (LineUnavailableException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									frameCount=0;} if(paused){pauseGame();} break;
		case KeyEvent.VK_N: if(menuScreen){
								gameDeath=false;
								Player.lives=3;
								GamePanel.score=0;
								player.setActive();
								//If you're on the menu screen, then listen for the 'N' - New game - command
								placeInvaders(GamePanel.gameWidth,GamePanel.gameHeight);
								//Then place the invaders (because it's a new game)
//								placeShelters(GamePanel.gameWidth, GamePanel.gameHeight);
								//These should only be added when the game starts
								level=1;
								//Set the level to 1.... Because it's a new game
								menuScreen=false;
								//Leave the menu screen
								meteorTimer.start();
								destroyerTimer.start();
								//And start the specials timers.
							}					
							break;
		case KeyEvent.VK_L: if(menuScreen){
								//Again, only do something if on the menu screen
								loadGameState();
								//run the load game state method
							}
		case KeyEvent.VK_S: if(paused&&!saved){
								saveGameState();
								//If the game is paused and not saved, then allow the saveGameState()
								//method to be called. If the game has been saved, don't allow double saving
							}
							break;
		case KeyEvent.VK_P: if(!menuScreen){pauseGame();} break;
								//If not on the menu screen, run the pause toggle method.
								//This prevents pausing from the main title screen
								//That's just weird!
		case KeyEvent.VK_UP:	if(konamiStage==0){
									konamiStage++;
							 	}
							 	else if(konamiStage==1){
							 		konamiStage++;
							 	}
							 	else if(konamiStage!=0||konamiStage!=1){
							 		konamiStage=0;
							 	}
								break;
		case KeyEvent.VK_DOWN:	if(konamiStage==2){
									konamiStage++;
								}
								else if(konamiStage==3){
									konamiStage++;
								}
								else{
									konamiStage=0;
								}
								break;
		case KeyEvent.VK_B:		if(konamiStage==8){
									konamiStage++;
								}
								else{
									konamiStage=0;
								}
								break;
		case KeyEvent.VK_A:		if(konamiStage==9){
									konamiActive=true;
									Player.lives=50;
									//If they finish the Konami sequence, set the Konami state to active
									//and give them 50 lives... Cause why not?!
								}
								else{
									konamiStage=0;
								}
								break;
		}
	}

	//These methods are here to complete the keylistener
	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}
}
