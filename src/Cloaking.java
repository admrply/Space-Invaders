
public interface Cloaking {
	
	public boolean cloaked = false;
	
	public void changeCloak();
	public boolean isCloaked();
	
	void stopCloakChange();
	void startCloakChange();
	//Pause the cloaking when the game is paused

}
