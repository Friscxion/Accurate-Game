package Affichage;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ModeJeux.PanneauJeu;
import ModeJeux.PanneauJeuPerso;


@SuppressWarnings("serial")
public class Menu extends JFrame implements KeyListener{
	private PanneauMenu pan;
	private PanneauJeu pang;
	private PanneauJeuPerso panp;
	private boolean ingame=false;
	private boolean space=false;
	
	/** Constructeur du Menu */
	public Menu() {
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setFocusable(true);
		pack();
		pan=(new PanneauMenu());
		setContentPane(pan);
		setTitle("Accurate");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(this);
		setVisible(true);
	}
	
	
	
	
	public static void main(String [] args) {
		new Menu();
		
	}
	
	/** Selection d'une partie simple */
	public void selectNormalGame() {
		this.getContentPane().removeAll();
		pang=new PanneauJeu();
		setContentPane(pang);
		validate();
		repaint();
	}
	/** Selection d'une partie personnalisé */
	public void selectPersoGame() {
		JFileChooser dialogue = new JFileChooser();
        dialogue.showOpenDialog(null);
        String file=dialogue.getSelectedFile().toString();
		JFrame fenselect=new JFrame();
		fenselect.setLayout(new GridLayout(3,1));
		fenselect.setPreferredSize(new Dimension(400,200));
        JLabel label=new JLabel("Entrer un nombre de cibles par minute (100 par defaut) : ");
        fenselect.add(label);
        JTextField textField = new JTextField(20);
        
        fenselect.add(textField);
        JButton button=new JButton("Valider");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int nbselectcible=100;
            	nbselectcible=100;
            	String t=textField.getText();
            	int num;
            	boolean numounon;
            	try {
                    Integer.parseInt(t);
                    numounon= true;
                 } catch (NumberFormatException ee) {
                	 numounon =false;
                 }
            	
            	if(t==null || !numounon) {
            		num=100;
            	}
            	else {
            		num=Integer.parseInt(t);
            	}
				
            	if(num>0) {
            		nbselectcible=num;
            	}
            	
            	panp=new PanneauJeuPerso(nbselectcible,file);
                
        		getContentPane().removeAll();
        		setContentPane(panp);
        		validate();
        		repaint();
        		fenselect.setVisible(false);
            }
        });
        fenselect.pack();
        fenselect.add(button);
        fenselect.setVisible(true);
        
	}
	/** Selection du menu */
	private void selectMenu() {
		this.getContentPane().removeAll();
		pan=new PanneauMenu();
		setContentPane(pan);
		validate();
		repaint();
		
	}
	
	@Override
	public void keyPressed(KeyEvent arg) {
		boolean p=false;
		boolean g=false;
		if(panp !=null) {
			p=!panp.isEnded();
		}
		if(pang !=null) {
			g=!pang.isEnded();
		}
		
		ingame=p||g;
		//System.out.println(ingame);
		if(arg.getKeyCode()==KeyEvent.VK_UP) {
			int choix=pan.getChoix();
			int nbopt=pan.getNbOpts();
			if(choix-1<0) {
				choix=nbopt-1;
			}
			else {
				choix=choix-1;
			}
			pan.setChoix(choix);
			pan.repaint();
			//System.out.println("up");
		}
		if(arg.getKeyCode()==KeyEvent.VK_DOWN) {
			int choix=pan.getChoix();
			int nbopt=pan.getNbOpts();
			if(choix+1>nbopt-1) {
				choix=0;
			}
			else {
				choix=choix+1;
			}
			System.out.println(choix);
			pan.setChoix(choix);
			pan.repaint();
			//System.out.println("down");
		}
		if(arg.getKeyCode()==KeyEvent.VK_ENTER) {
			int choix=pan.getChoix();
			if(!space && !ingame && choix==2) {
				System.exit(0);
			}
			if(!space &&  !ingame && choix==0) {
				space=true;
				ingame=true;
				selectNormalGame();
				System.out.println("normal");
				
			}
			if(!space &&  !ingame && choix==1) {
				space=true;
				ingame=true;
				selectPersoGame();
				System.out.println("perso");
				
			}
			
			
		}
		if(!ingame && arg.getKeyCode()==KeyEvent.VK_SPACE) {
			if(pang!=null) {
				if(pang.getEnd()||pang.isEnded()) {
					selectMenu();
					repaint();
					System.out.println("ouais");
					space=false;
				}
			}
			if(panp!=null) {
				if(panp.getEnd()||panp.isEnded()) {
					selectMenu();
					repaint();
					System.out.println("ouais");
					space=false;
				}
			}
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
