package ModeJeux;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

import Cible.Cible;
import Cible.CibleDontTouch;
import Cible.CibleTank;


@SuppressWarnings("serial")
public class PanneauJeu extends JPanel implements MouseListener, Runnable{
	private BufferedImage image;
	protected ArrayList<Cible> cibles;
	protected int score=0;
	protected double time = 0;
	protected boolean running=false;
	protected double speed=500;
	protected int timespliter=0;
	protected int timespliterre=0;
	protected boolean end=false;
	protected double probaSpawn=60;
	protected double probaSpawnTank=30;
	protected double probaSpawnDT=20;
	protected Clip clip;
	protected boolean ended=false;
	
	
	private Thread thread;
	
	public PanneauJeu() {
		cibles=new ArrayList<Cible>();
		addMouseListener(this);
		start();
	}
	
	/** Génère une cible */
	public void generateNewCible() {
		//System.out.println(System.nanoTime());
		//System.out.println(speed +" "+probaSpawn);
		int i=(int) (Math.random()*100);
		if(i<probaSpawnTank) {
			int n=(int) (Math.random()*4)+2;
			cibles.add(new CibleTank(this,speed,n));
		}
		else if(i>probaSpawnTank && i<probaSpawnTank+probaSpawnDT) {
			cibles.add(new CibleDontTouch(this,speed));
		}
		else {
			cibles.add(new Cible(this,speed));
		}
		
	}
	
	/** Affichage du panneau aspect graphique */
	public void paintComponent(Graphics g) {
		removeAll();
		if(ended) {
			finDePartie();
			return;
		}
		super.paintComponent(g);

		try {
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,getClass().getResourceAsStream("/Police/Noodle.ttf")));
		} catch (IOException|FontFormatException e) {
		    e.printStackTrace();
		}
		
		
		try {
			image = ImageIO.read(
					getClass().getResourceAsStream("/Backgrounds/gamebg.jpg")
				);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		Font font1=new Font("BigNoodleTitling", Font.BOLD, 30);
		FontMetrics metrics1 = g.getFontMetrics(font1);
		g.drawImage(image, 0, 0, (int)(getSize().width), (int)(getSize().height),null);
		
		g.setColor(new Color(255, 181, 0));
        g.setFont(font1);
		g.drawString("Time : "+clip.getMicrosecondPosition()/1000000, this.getWidth()/2-metrics1.stringWidth("Time : "+clip.getMicrosecondPosition()/1000000)/2,30);
		
		
		g.setColor(new Color(255, 181, 0));
		
		for(int i=0;i<cibles.size();i++) {
			if(cibles.get(i).isVisible()) {
				
				if(cibles.get(i) instanceof CibleDontTouch) {
					g.setColor(new Color(255, 0, 0));
					g.fillOval(cibles.get(i).getX()-Cible.RAYON, cibles.get(i).getY()-Cible.RAYON, Cible.RAYON*2, Cible.RAYON*2);
				}
				else {
					g.setColor(new Color(255, 181, 0));
					g.fillOval(cibles.get(i).getX()-Cible.RAYON, cibles.get(i).getY()-Cible.RAYON, Cible.RAYON*2, Cible.RAYON*2);
					
				}
				
				g.setColor(new Color(242, 243, 244));
				Graphics2D g2d=(Graphics2D)g;
				g2d.setStroke(new BasicStroke(3));
				g2d.drawOval(cibles.get(i).getX()-(int)(Cible.RAYON)-(int)cibles.get(i).getTimeToDespawn()/2, 
							cibles.get(i).getY()-(int)(Cible.RAYON)-(int)cibles.get(i).getTimeToDespawn()/2, 
							(Cible.RAYON*2)+(int)cibles.get(i).getTimeToDespawn(), 
							(Cible.RAYON*2)+(int)cibles.  get(i).getTimeToDespawn());
				if(cibles.get(i) instanceof CibleTank) {
					CibleTank c=(CibleTank)cibles.get(i);
					String n="";
					if(c.getLife()!=1) {
						n+=c.getLife();
					}
					g.drawString(n,cibles.get(i).getX()-8, cibles.get(i).getY()+7);
				}
			}
		}
		
		
		
		
		g.dispose();
	}

	/** Run de l'interface runnable (lance en boucle le programme) */
	@SuppressWarnings("static-access")
	public void run() {
		init();
		while(running) {
			this.update();
			this.render();
			try {
				thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stop();
	}
	
	/** Initialisation des données */
	protected void init() {
		try {
	           BufferedInputStream url= new BufferedInputStream(getClass().getResourceAsStream("/Sons/bgsongdef.wav"));
	           AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
	           clip= AudioSystem.getClip();
	           clip.open(audioIn);
	           clip.start();
	       }
		catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Start, préparation du Thread de jeu */
	public synchronized void start() {
		if(running) {
			return;
		}
		running=true;
		thread =new Thread(this);
		thread.start();
	}
	
	/** Stop fermeture du thread et lancement de l'affichage de fin */
	@SuppressWarnings("static-access")
	public synchronized void stop() {
		if(!running) {
			return;
		}
		running=false;
		finDePartie();
		try {
			thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		finDePartie();
		finDePartie();
		end=true;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

	/** Affichage de fin */
	private void finDePartie() {
		ended=true;
		Graphics g=getGraphics();
		super.paintComponent(g);
		removeAll();
		
		Font font1=new Font("BigNoodleTitling", Font.BOLD, 200);
		FontMetrics metrics1 = g.getFontMetrics(font1);
		
		g.drawImage(image, 0, 0, (int)(getSize().width), (int)(getSize().height),null);
		g.setColor(new Color(255, 181, 0));
		g.setFont(font1);
		g.drawString("Fin de la partie", this.getWidth()/2-metrics1.stringWidth("Fin de la partie")/2,metrics1.getHeight());
	
		Font font2=new Font("BigNoodleTitling", Font.BOLD, 50);
		FontMetrics metrics2 = g.getFontMetrics(font2);
		
		g.setFont(font2);
		g.drawString("Durée de la partie : "+clip.getMicrosecondPosition()/1000000, this.getWidth()/2-metrics2.stringWidth("Durée de la partie : "+(int)clip.getMicrosecondLength()/1000000)/2,400);
		g.drawString("Nombre de cibles touchées : "+score, this.getWidth()/2-metrics2.stringWidth("Nombre de cibles touchées : "+score)/2,500);
		g.setColor(new Color(242, 243, 244));
		g.drawString("Espace pour continuer",this.getWidth()/2-metrics2.stringWidth("Espace pour continuer")/2,(this.getHeight()/4)*3+100);
		try {
			clip.stop();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/** Fonction d'update de la fenetre */
	protected void update() {
		int microPos =(int)(clip.getFramePosition()/1000);
		if(microPos>time+100) {
			time+=100;
		}
		if(microPos>timespliterre+40) {
			timespliterre+=40;
			double n= Math.random()*100;
			if(n<probaSpawn) {
				generateNewCible();
			}
		}
		if(microPos>timespliter+50) {
			timespliter+=50;
			if(speed<4) {
				speed-=0.1;
			}
			else {
				speed-=6;
				probaSpawn+=2;
				probaSpawnTank+=0.5;
			}
		}
		
		for(int i=0;i<cibles.size();i++) {
			if(cibles.get(i).isVisible())
				cibles.get(i).minorTimeToDespawn();
		}
		for(int i=0;i<cibles.size();i++) {
			if(cibles.get(i).isDead())
				stop();
		}
		
	}
	
	/** Fonction de rendu de la fenetre */
	protected void render() {
		repaint();
	}

	/** Retourne la fin ou non */
	public boolean getEnd() {
		return end;
	}
	
	/** Retourne si le programme est en marche */
	public boolean getRunning() {
		return running;
	}
	
	
	public void mouseClicked(MouseEvent arg) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg) {
		boolean touched=false;
		for(int i=cibles.size()-1;i>=0;i--) {
			if(cibles.get(i).isVisible() && cibles.get(i).isCollide(arg.getX(), arg.getY())){
				score++;
				touched=true;
				return;
			}
		}
		if(!touched) {
			this.generateNewCible();
		}
	}
	public void mouseReleased(MouseEvent arg0) {}
	
	/** Retourne si le jeu est terminé */
	public boolean isEnded() {
		return ended;
	}
}
