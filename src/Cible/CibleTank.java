package Cible;

import ModeJeux.PanneauJeu;
import ModeJeux.PanneauJeuPerso;

public class CibleTank extends Cible {

	private int life;
	
	/** Constructeur d'une cible avec un panneau jeu simple et un temps de disparition */
	public CibleTank(PanneauJeu jeu, double timedesp,int life) {
		super(jeu, timedesp);
		this.life=life;
	}
	/** Constructeur d'une cible avec un panneau jeu personnalisé et un temps de disparition */
	public CibleTank(PanneauJeuPerso jeu, double speed, int n) {
		super(jeu, speed);
		this.life=n;
	}
	
	/** Retourne si il y a collision entre la cible et les coordonées en paramètre et décrémente la vie */
	public boolean isCollide(int xp,int yp) {
		float distX=(float) (xp-x);
		float distY=(float) (yp-y);
		float distance=(float) (0.1-RAYON);
		boolean res=distX*distX +distY*distY <= distance*distance;
		if(res) {
			//System.out.println("collide");
			life--;
			if(life<=0) {
				isVisible=false;
			}
			return true;
		}
		else{
			return false;
		}
		
	}
	/** Retourne la vie restante de la cible tank*/
	public int getLife() {
		return life;
	}
	

	
}
