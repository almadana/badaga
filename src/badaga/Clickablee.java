package badaga;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import org.math.plot.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SpringLayout;

public class Clickablee extends JFrame implements ActionListener{

	private Plot2DPanel plot;
	
	public Clickablee() {
		setLayout(new FlowLayout());
		JButton bn1 = new JButton("Start");
		JButton bn2 = new JButton("Ding!");
		bn1.setActionCommand("start");
		bn2.setActionCommand("ding");
		bn1.addActionListener(this);
		bn2.addActionListener(this);
		bn1.setVisible(true);
		bn2.setVisible(true);
		
		plot = new Plot2DPanel();
		JFrame plotframe = new JFrame();
		plotframe.setContentPane(plot);
		plotframe.setVisible(true);
		//getContentPane().add(plot);
		getContentPane().add(bn1);
		getContentPane().add(bn2);	
		plot.setSize(300, 300);
	}
	
	public static void main(String[] args) {
		Clickablee cl = new Clickablee();
		cl.setSize(400,400);
		cl.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getActionCommand()) {
		case "start":
			new Thread(new Runnable(){

				@Override
				public void run() {
					start();
				}
				
			}).start();
			break;
		case "ding":
			System.out.println("Ding!");
			break;
		}
	}
	
	private void start() {
		//for (int i=0; i<50; i++){
			//System.out.println("Number "+i);
			try {
				double[] x={1,2,3,4,5};
				double[] y={1,4,9,16,25};
				plot.addLinePlot("quad", x, y);
				Thread.sleep(2000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
	}

}
