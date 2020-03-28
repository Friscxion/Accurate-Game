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
public class Visualizer extends JPanel implements KeyListener, Runnable {
	private Thread thread;
	ArrayList<Float> spectralFlux;
	float[] lastSpectrum;
	float[] spectrum;
	float[] samples ;
	int time=0;
	FFT fft ;
	WaveDecoder decoder;
	int ne=1;
	int na=1024;
	Clip clip;
	private float x=0;
	
	public Visualizer() {
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
//		    for(int i=0;i<samples.length;i++) {
//		    	System.out.println(samples[i]);
//		    	
//		    }
		    System.out.println(fft.avgSize());
		
		
		
		
		} 
		catch (Exception e) {e.printStackTrace();}
		start();
	}

	
	private synchronized void start() {
		thread=new Thread(this);
		
		thread.start();
		
	}

	public static void main(String [] args) {
		Visualizer m=new Visualizer();
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
    	   g2d.draw(new Line2D.Double(100+(i/5)-x, y3, 100+(i/5)-x, y3-(spectralFlux.get(i))/2));
       }
       if(time>=22) {
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
			time++;
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
