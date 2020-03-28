package Cible;

import ModeJeux.PanneauJeu;
import ModeJeux.PanneauJeuPerso;

public class CibleDontTouch extends Cible {

  /** Constructeur d'une cible avec un panneau jeu simple et un temps de disparition */
	public CibleDontTouch(PanneauJeu jeu, double timedesp) {
		super(jeu, timedesp);
	}
	/** Constructeur d'une cible avec un panneau jeu personnalisé et un temps de disparition */
	public CibleDontTouch(PanneauJeuPerso jeu, double speed) {
		super(jeu, speed);
	}
	/** Retourne si il y a collision entre la cible et les coordonées en paramètre et change l'état de mort */
	public boolean isCollide(int xp,int yp) {
		float distX=(float) (xp-x);
		float distY=(float) (yp-y);
		float distance=(float) (0.1-RAYON);
		boolean res=distX*distX +distY*distY <= distance*distance;
		if(res) {
			changeDead(true);
			return true;
		}
		else{
			return false;
		}
		
	}
	
	/** Décremente la vie de la cible et la fait disparaitre */
	public void minorTimeToDespawn() {
		timeToDespawn-=1;
		if(timeToDespawn<0) {
			isVisible=false;
		}
	}

}
