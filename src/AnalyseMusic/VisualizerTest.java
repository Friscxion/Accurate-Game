package AnalyseMusic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.io.BufferedInputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.io.WaveDecoder;





@SuppressWarnings("serial")
public class VisualizerTest extends JPanel implements KeyListener, Runnable {
	private Thread thread;
	ArrayList<Float> spectralFlux;
	ArrayList<Float> saveSpectralFlux;
	float[] lastSpectrum;
	float[] spectrum;
	float[] samples ;
	float time=0;
	FFT fft ;
	WaveDecoder decoder;
	int ne=1;
	int na=1024;
	int m=0;
	Clip clip;
	private float x=0;
	
	public VisualizerTest() {
		try {
			BufferedInputStream url=  new BufferedInputStream(getClass().getResourceAsStream("/Sons/chopin.wav"));
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
	        clip= AudioSystem.getClip();
	        clip.open(audioIn);
	        while(ne<clip.getBufferSize()) {
	        	ne=ne*2;
	        }
	        BufferedInputStream url2= new BufferedInputStream(getClass().getResourceAsStream("/Sons/chopin.wav"));
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
		    
		    int nbcibles=300;
		    int count=2000;
		    int imax=0;
		    int iimax=0;
		    while (nbcibles<count) {
		    	count=0;
		    	iimax+=5;
		    	imax=iimax;
		    	System.out.println(iimax);
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
			    System.out.println(count);
		    
		    
		}
		    

		    
		    
		    
		   
		} 
		catch (Exception e) {e.printStackTrace();}
		start();
	}

	
	private synchronized void start() {
		thread=new Thread(this);
		
		thread.start();
		
	}

	public static void main(String [] args) {
		VisualizerTest m=new VisualizerTest();
		JFrame fen=new JFrame();
		fen.setPreferredSize(new Dimension(1920,1080)); 
		fen.setFocusable(true);
		fen.setContentPane(m);
		fen.pack();
		fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fen.addKeyListener(m);
		fen.setVisible(true);
		
	}

	public void paintComponent(Graphics g)
    { 
       super.paintComponent(g);
       int y3=500;
       Graphics2D g2d=(Graphics2D)g;
      
       for (int i=0;i< spectralFlux.size();i++) {
    	   g2d.draw(new Line2D.Double(100+(i/5)-x, y3, 100+(i/5)-x, y3-(spectralFlux.get(i))));
       }
       
       if(time>=11) {
    	   if(spectralFlux.get(m)>10) {
    		   System.out.println(spectralFlux.get(m));
    	   }
    	   m++;
    	   time=0;
    	   x+=0.2;
       }
      
       g2d.setColor(Color.RED);
       g2d.draw(new Line2D.Double(100,0, 100, 1000));
    }



	@SuppressWarnings("static-access")
	@Override
	public void run() {
		try {
			clip.start();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		while(true) {
			try {
				thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time+=0.5;
			repaint();
		}
		
		
	}

	
	@Override
	public void keyPressed(KeyEvent arg) {
		if(arg.getKeyCode()==KeyEvent.VK_ENTER) {
			System.exit(0);
		}
		if(arg.getKeyCode()==KeyEvent.VK_SPACE) {
			
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
