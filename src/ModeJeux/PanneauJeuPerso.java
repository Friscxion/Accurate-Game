package ModeJeux;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;

import javax.imageio.ImageIO;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

//import org.jaudiotagger.audio.AudioFile;
//import org.jaudiotagger.audio.AudioFileIO;
//import org.jaudiotagger.tag.Tag;


import com.badlogic.audio.analysis.FFT;

import com.badlogic.audio.io.WaveDecoder;



import Cible.Cible;
import Cible.CibleDontTouch;
import Cible.CibleTank;



@SuppressWarnings({ "serial", "unused" })
public class PanneauJeuPerso extends JPanel implements MouseListener, Runnable{
	ArrayList<Integer> tableaupasse=new ArrayList<Integer>();
	ArrayList<Color> couleurcible=new ArrayList<Color>();
	ArrayList<Color> couleurcercle=new ArrayList<Color>();
	
	//Partie Visualizer/ Analyse de la musique
	ArrayList<Float> spectralFlux;
	ArrayList<Float> saveSpectralFlux;
	float[] lastSpectrum;
	float[] spectrum;
	float[] samples;
	FFT fft ;
	WaveDecoder decoder;
	int ne=1;
	int na=1024;
	int m=0;
	int tempo=0;
	int nbselectcible;
	String file;
	String message="Bravo";
	int timeMusique=0;
	//Partie affichage et jeu
	private BufferedImage image;
	protected ArrayList<Cible> cibles;
	protected int score=0;
	protected boolean running=false;
	protected double speed=5000;
	protected boolean end=false;
	protected double probaSpawn=20;
	protected Clip clip;
	private Thread thread;
	private String nomFichier;
	protected boolean ended=false;
	
	
	/** Constructeur d'un panneau avec un nombre de cible et un nom de fichier */
	public PanneauJeuPerso(int nb,String fi) {
		cibles=new ArrayList<Cible>();
		addMouseListener(this);
		nbselectcible=nb;
		file=fi;
		start();
		
	}
	
	/** Génère une cible */
	public void generateNewCible() {
		float h=(float) Math.random();
		float s=1f;
		float b=1f;
		couleurcible.add(Color.getHSBColor(h, s, b));
		couleurcercle.add(Color.getHSBColor(h, s, b-0.2f));
		cibles.add(new Cible(this,speed));

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

		g.drawImage(image, 0, 0, (int)(getSize().width), (int)(getSize().height),null);
		g.setColor(new Color(255, 181, 0));
		
		Font font1=new Font("BigNoodleTitling", Font.BOLD, 30);
		FontMetrics metrics1 = g.getFontMetrics(font1);
		
        g.setFont(font1);
		g.drawString("Time : "+(int)clip.getMicrosecondPosition()/1000000 , this.getWidth()/2-metrics1.stringWidth("Time : "+(int)clip.getMicrosecondPosition()/1000000)/2,30);
		g.drawString(nomFichier , 0,30);
		
		
		g.setColor(new Color(255, 181, 0));
		
		for(int i=0;i<cibles.size();i++) {
			if(cibles.get(i).isVisible()) {
				
				if(cibles.get(i) instanceof CibleDontTouch) {
					g.setColor(new Color(255, 0, 0));
					g.fillOval(cibles.get(i).getX()-Cible.RAYON, cibles.get(i).getY()-Cible.RAYON, Cible.RAYON*2, Cible.RAYON*2);
				}
				else {
					
					g.setColor(couleurcible.get(i));
					g.fillOval(cibles.get(i).getX()-Cible.RAYON, cibles.get(i).getY()-Cible.RAYON, Cible.RAYON*2, Cible.RAYON*2);
					
				}
				
				
				
				g.setColor(couleurcercle.get(i));
				Graphics2D g2d=(Graphics2D)g;
				g2d.setStroke(new BasicStroke(3));
				g2d.drawOval(cibles.get(i).getX()-(int)(Cible.RAYON)-(int)cibles.get(i).getTimeToDespawn()/2/20, 
							cibles.get(i).getY()-(int)(Cible.RAYON)-(int)cibles.get(i).getTimeToDespawn()/2/20, 
							(Cible.RAYON*2)+(int)cibles.get(i).getTimeToDespawn()/20, 
							(Cible.RAYON*2)+(int)cibles.  get(i).getTimeToDespawn()/20);
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
				thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stop();
	}

	/** Initialisation des données */
	protected void init() {
		try {
			File f=new File(file);
			long fileSize = f.length();
			int frameSize = 4;
			long numFrames = fileSize / frameSize;
			AudioFormat audioFormat = new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, frameSize, 50, true);
			nomFichier=f.getName();
	    BufferedInputStream audioIn =new BufferedInputStream( new AudioInputStream(new FileInputStream(f),audioFormat, numFrames));	        
	    AudioInputStream audioI=AudioSystem.getAudioInputStream(audioIn);
	    clip= AudioSystem.getClip();
	    clip.open(audioI);
	    while(ne<clip.getBufferSize()) {
	      ne=ne*2;
	    }  
	    BufferedInputStream url2 =new BufferedInputStream(new AudioInputStream(new FileInputStream(f),audioFormat, numFrames));
			decoder = new WaveDecoder(url2);
			fft= new FFT(na, 1);
			samples= new float[na];
			spectrum= new float[na / 2 + 1];
		    lastSpectrum= new float[na / 2 + 1];
		    spectralFlux= new ArrayList<Float>();
		    while (decoder.readSamples(samples) > 0) {
		        fft.forward(samples);
		        System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);
		        System.arraycopy(fft.getSpectrum(), 0, spectrum, 0, spectrum.length);
		        float flux = 0;
		        for (int i = 0; i < spectrum.length; i++)
		            flux += (spectrum[i] - lastSpectrum[i]);
		        spectralFlux.add(flux);
		    }
		    
		    saveSpectralFlux= new ArrayList<Float>();
		    saveSpectralFlux.addAll(spectralFlux);
		    
		    for(int i=1;i<spectralFlux.size()-1;i++) {
		    	float last=spectralFlux.get(i-1);
		    	float here=spectralFlux.get(i);
		    	if(last<here) {
		    		spectralFlux.set(i-1, 0f);
		    	}
		    	else if(last>here) {
		    		spectralFlux.set(i, 0f);
		    	}	
		    }
		    
		    double sec=clip.getMicrosecondLength()/1000000;
		    double minute=sec/60;
		    int nbcibles=(int) (nbselectcible*minute);
		    System.out.println("nbcibles : "+nbcibles);
		    int count=2000;
		    int imax=0;
		    int iimax=0;
		    while (nbcibles<count) {
		    	count=0;
		    	iimax+=5;
		    	imax=iimax;
			    for(int i=0;i<spectralFlux.size()-1;i+=imax) {
			    	float max=spectralFlux.get(i);
			    	int maxnum=0;
			    	float min;
			    	if(i+imax>spectralFlux.size()) {
			    		imax=spectralFlux.size()-i;
			    	}
			    	for(int n=i;n<i+imax;n++) {
			    		min=spectralFlux.get(n);
			    		if(min>max) {
			    			max=min;
			    			maxnum=n;
			    		}
			    	}
			    	for(int m=i;m<i+imax;m++) {
			    		if(m!=maxnum) {
			    			spectralFlux.set(m, 0f);
			    		}
			    	}
			    	
			    	
			    }
			    for(int i=0;i<spectralFlux.size();i++) {
			    	
			    	float n=spectralFlux.get(i);
			    	
			    	if(n!=0f) {
			    		count++;
			    	}
			    	
			    }
		    } 
		    System.out.println(count);
		}
		catch (Exception e) {
			System.out.println("Seuleument les fichiers .wav sont autorisés");
			e.printStackTrace();
			finDePartie();
			
			return;
			}
		clip.start();
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
		end=true;
		Graphics g=getGraphics();
		super.paintComponent(g);
		removeAll();
		
		Font font1=new Font("BigNoodleTitling", Font.BOLD, 200);
		FontMetrics metrics1 = g.getFontMetrics(font1);
		
		g.drawImage(image, 0, 0, (int)(getSize().width), (int)(getSize().height),null);
		g.setColor(new Color(255, 181, 0));
		g.setFont(font1);
		g.drawString("Fin de la partie", this.getWidth()/2-metrics1.stringWidth("Fin de la partie")/2,metrics1.getHeight());
		g.drawString(message, this.getWidth()/2-metrics1.stringWidth("Bravo")/2,metrics1.getHeight()*2);
		Font font2=new Font("BigNoodleTitling", Font.BOLD, 50);
		FontMetrics metrics2 = g.getFontMetrics(font2);
		
		g.setFont(font2);
		g.drawString("Durée de la partie : "+(int)clip.getMicrosecondLength()/1000000, this.getWidth()/2-metrics2.stringWidth("Durée de la partie : "+(int)clip.getMicrosecondLength()/1000000)/2,(int)(metrics1.getHeight()*2.5));
		g.drawString("Nombre de cibles touchées : "+score, this.getWidth()/2-metrics2.stringWidth("Nombre de cibles touchées : "+score)/2,(int)(metrics1.getHeight()*2.5 +100));
		g.setColor(new Color(242, 243, 244));
		g.drawString("Espace pour continuer",this.getWidth()/2-metrics2.stringWidth("Espace pour continuer")/2,(int)(metrics1.getHeight()*2.5 +250));
		try {
			clip.stop();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		ended=true;
		end=true;
		
		
		
	}

	/** Fonction d'update de la fenetre */
	protected void update() {
		int microPos =(int)(clip.getFramePosition()/1000);
	    
	    if(microPos>spectralFlux.size()-2) {
	    	
	    }
	    else if(spectralFlux.get(microPos)>10 && !tableaupasse.contains(microPos)) {
	    	tableaupasse.add(microPos);
	    	generateNewCible();
	
	    }
	    	   
	    if (microPos>(clip.getFrameLength()/1000)-20){
	    	stop();
	    }
	    
	    if(microPos>timeMusique+100) {
	    	timeMusique+=100;
	    	if(speed<2) {
				speed-=0.01;
			}
			else {
				speed-=1;
			}
	    }
	    
		for(int i=0;i<cibles.size();i++) {
			if(cibles.get(i).isVisible())
				cibles.get(i).minorTimeToDespawn();
		}
		for(int i=0;i<cibles.size();i++) {
			if(cibles.get(i).isDead()) {
				message="Perdu";
				stop();
			}
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
