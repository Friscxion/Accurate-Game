package Cible;

import ModeJeux.PanneauJeu;
import ModeJeux.PanneauJeuPerso;

public class Cible {

	public final static int RAYON = 50;
	protected double timeToDespawn;
	protected boolean isVisible;
	protected boolean isTouched;
	protected boolean isDead;
	protected double x;
	protected double y;
	
	/** Constructeur d'une cible avec un panneau jeu simple et un temps de disparition */
	public Cible(PanneauJeu jeu,double timedesp) {
		timeToDespawn=timedesp;
		isTouched=false;
		isDead=false;
		isVisible=true;
		x=Math.random()*jeu.getWidth();
		y=Math.random()*jeu.getHeight();
		
		while(x<100 || x>jeu.getWidth()-100) {
			x=Math.random()*jeu.getWidth();
		}
		while(y<100 || y>jeu.getHeight()-100) {
			y=Math.random()*jeu.getHeight();
		}
	}
	/** Constructeur d'une cible avec un panneau jeu personnalisé et un temps de disparition */
	public Cible(PanneauJeuPerso jeu, double speed) {
		timeToDespawn=speed;
		isTouched=false;
		isDead=false;
		isVisible=true;
		x=Math.random()*jeu.getWidth();
		y=Math.random()*jeu.getHeight();
	}

	/** Retourne le temps de vie de la cible*/
	public double getTimeToDespawn() {
		return timeToDespawn;
	}
	
	/** Décremente le temps de vie de la cible */
	public void minorTimeToDespawn() {
		timeToDespawn-=1;
		if(timeToDespawn<0) {
			isVisible=false;
			isDead=true;
			System.out.println("deadtime");
		}
	}
	/** Retourne un boolean si la cible est morte ou non (partie perdu ou non)*/
	public boolean isDead() {
		return isDead;
	}
	
	/** Retourne si il y a collision entre la cible et les coordonées en paramètre*/
	public boolean isCollide(int xp,int yp) {
		float distX=(float) (xp-x);
		float distY=(float) (yp-y);
		float distance=(float) (0.1-RAYON);
		boolean res=distX*distX +distY*distY <= distance*distance;
		if(res) {
			isVisible=false;
			return true;
		}
		else{
			return false;
		}
		
	}
	



	/**Retourne un boolean si la cible et visible ou non*/
	public boolean isVisible() {
		return isVisible;
	}
	/** Permet de changer la visibilité de la cible*/
	public void changeVisible(boolean v) {
		isVisible=v;
	}
	/** Permet de changer l'état de mort de la cible */
	public void changeDead(boolean v) {
		isDead=v;
	}
	
	/** Retourne x*/
	public int getX() {
		return (int) x;
	}
	
	/** Retourne y*/
	public int getY() {
		return (int) y;
	}
	
}
