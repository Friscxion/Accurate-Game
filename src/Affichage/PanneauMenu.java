package Affichage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PanneauMenu extends JPanel{
	
	private String[] options= {"Partie rapide","Personnalisé","Quitter"};
	private int choix=0;
	private int down=50;
	private BufferedImage image;
	
	/**Constructeur sans paramètres*/
	public PanneauMenu() {
	}
	/** Permet de rafracihir la page*/
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Police/Noodle.ttf")));
		} catch (IOException|FontFormatException e) {
		    e.printStackTrace();
		}
		
		
		try {
			image = ImageIO.read(
				getClass().getResourceAsStream("/Backgrounds/menubg.jpg")
			);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		g.drawImage(image, 0, 0, (int)(getSize().width), (int)(getSize().height),null);
		
		Font font1=new Font("BigNoodleTitling", Font.BOLD, 200);
		FontMetrics metrics1 = g.getFontMetrics(font1);
		
		g.setColor(new Color(255, 181, 0));
        g.setFont(font1);
        g.drawString("Accurate",(int)(getSize().width/2)-metrics1.stringWidth("Accurate")/2, (int)(metrics1.getHeight()));
        for(int i=0;i<options.length;i++) {
        	
        	Font font2=new Font("BigNoodleTitling", Font.PLAIN, 60);
    		FontMetrics metrics2 = g.getFontMetrics(font2);
			g.setColor(new Color(255, 181, 0));
			g.setFont(font2);
		    if(i==choix) {
		    	g.setColor(new Color(242, 243, 244));
		    }
		    g.drawString(options[i],(int)(getSize().width/2)-metrics2.stringWidth(options[i])/2, (int)(getSize().height/2)+down);
		    down+=100;
        }
        down=50;
        System.out.println("updated");
        g.dispose();
	}
	
	/** Incrémente le choix du mode de jeu*/
	public void setChoix(int i) {
		 choix=i;
	}

  /** Retourne le choix du mode de jeu*/
	public int getChoix() {
		return choix;
	}

	/** Retourne le nombre d'options de jeu */
	public int getNbOpts() {
		return options.length;
	}

	
}
