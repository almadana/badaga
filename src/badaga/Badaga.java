package badaga;

import java.awt.CardLayout;

import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.BoxLayout;
import javax.swing.AbstractButton;
//import javax.sound.sampled.DataLine.*;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.math.plot.*;

import badaga.recursos.CargadorSonidos;

public class Badaga extends JFrame implements ActionListener {

	private JFrame identiFrame, nuevoFrame, discriFrame,continuaFrame,grafiFrame,grafiIdentiFrame, grafiDiscriFrame;
	private JPanel startPaneli,startPaneld,mainPaneli,mainPaneld,pausaDP,pausaP;
	private JButton ba,da,ga,botona,botonb,graficabot,pausaD,pausa;
	private int numEstim,numBlock,discriCorrect,numeroSonidos;
	private int[] soFar;
	private FileWriter salidita;
	private String nombresujeto;
	private JLabel datosronda,datosrondad,sujetoLabel;
	private Thread identiThread, discriThread, grafiThread;
	private Plot2DPanel identiPlot, discriPlot;
	private boolean endThread, appendD,appendI;
	private volatile boolean pauseThread;
	private AudioInputStream[] sonidos;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 12352345L;

	public Badaga() {
		endThread=false;
		pauseThread=false;
		appendI=false;
		appendD=false;
		nombresujeto = "";
		sujetoLabel = new JLabel();
		sujetoLabel.setText(nombresujeto);
		JButton nuevoexp = new JButton("Nuevo experimento");
		JButton contexp = new JButton("Continuar experimento");
		graficabot = new JButton("Graficar");
		nuevoexp.addActionListener(this);
		contexp.addActionListener(this);
		graficabot.addActionListener(this);
		nuevoexp.setActionCommand("nuevo");
		contexp.setActionCommand("continuar");
		graficabot.setActionCommand("graficar");
		graficabot.setEnabled(false);
		JPanel sujetoPanelM = new JPanel();
		sujetoPanelM.add(sujetoLabel);
		sujetoLabel.setVisible(true);
		sujetoPanelM.setVisible(true);
		
		this.setLayout(new FlowLayout());
		this.setTitle("Percepción categórica v.0.1");
		this.setSize(400, 300);
		
		//JPanel botonero = new JPanel();
		//botonero.add(nuevoexp);
		//botonero.add(contexp);
		//botonero.add(graficabot);
		getContentPane().add(nuevoexp);
		getContentPane().add(contexp);
		getContentPane().add(graficabot);
		//getContentPane().add(botonero);
		getContentPane().add(sujetoPanelM);
		
		
		nuevoFrame = new JFrame("Elija la tarea:"){
			private static final long serialVersionUID = 1125345L;
					
		};
		JButton identi = new JButton("identificación");
		JButton discri = new JButton("discriminación");
		identi.setActionCommand("identificar");
		discri.setActionCommand("discriminar");
		identi.addActionListener(this);
		discri.addActionListener(this);
		nuevoFrame.setLayout(new FlowLayout());
		nuevoFrame.getContentPane().add(identi);
		nuevoFrame.getContentPane().add(discri);
		nuevoFrame.setSize(400,400);
		
		
		// identificacion
		identiFrame = new JFrame("Tarea de identificación"){
			private static final long serialVersionUID = 613451L;
		};
		identiFrame.addWindowListener(new WindowAdapter(){
			 public void windowClosing(WindowEvent e) {
				 if (identiThread!=null)
			        endThread=true;
			    }
		});
		
		JLabel instrucciones = new JLabel();
		instrucciones.setText("<html>A continuación, escuchará una serie de sonidos. <br>Deberá identificar cada uno de ellos usando los botones que aparecerán.<br>Tendrá pocos segundos para responder<br>, antes que el próximo sonido aparezca.</html>");
		instrucciones.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
		identiFrame.setLayout(new FlowLayout());
		JButton comenzar = new JButton("Comenzar");
		comenzar.setActionCommand("identistart");
		startPaneli = new JPanel();
		startPaneli.add(instrucciones);
		startPaneli.add(comenzar);
		startPaneli.setVisible(true);
		identiFrame.getContentPane().add(startPaneli);
		comenzar.addActionListener(this);
		identiFrame.setLayout(new CardLayout());
		identiFrame.setSize(500,300);
		identiFrame.setVisible(false);
		
		mainPaneli = new JPanel();
		mainPaneli.setLayout(new FlowLayout());
		datosronda = new JLabel();
		mainPaneli.add(datosronda);
		ba=new JButton("ba");
		da=new JButton("da");
		ga=new JButton("ga");
		JPanel botoneri = new JPanel();
		botoneri.add(ba);
		botoneri.add(da);
		botoneri.add(ga);
		ba.setActionCommand("ba");
		da.setActionCommand("da");		
		ga.setActionCommand("ga");
		ba.addActionListener(this);
		da.addActionListener(this);
		ga.addActionListener(this);
		ba.setEnabled(false);
		da.setEnabled(false);
		ga.setEnabled(false);
		mainPaneli.add(botoneri);
		botoneri.setEnabled(false);
		mainPaneli.setVisible(false);
		identiFrame.add(mainPaneli);
		pausaP = new JPanel();
		pausa = new JButton("pausa");
		pausa.addActionListener(this);
		pausa.setActionCommand("pausa");
		pausaP.add(pausa);
		mainPaneli.add(pausaP);
		pausaP.setVisible(false);
		
		
		
		
		//discriminacion
		discriFrame = new JFrame(){
			private static final long serialVersionUID = 613451L;
		};
		JLabel instruccionesD = new JLabel("<html>A continuación, escuchará tandas de 3 sonidos <br>consecutivos, '1', '2', y 'X'.</html>");
		JLabel instruccionesD2 = new JLabel("<html>Usando los botones que aparecerán abajo <br>deberá decidir si el tercer sonido 'X' <br>es igual al primero o al segundo.<br>Debe responder pronto, tras unos <br> segundos los botones desaparecerán<br>y una nueva tanda será presentada.</html>");
		instruccionesD.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
		instruccionesD2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
		discriFrame.setLayout(new CardLayout());
		startPaneld = new JPanel();
		JButton comenzarD = new JButton("Comenzar");
		comenzarD.setActionCommand("discristart");
		startPaneld.add(instruccionesD);
		startPaneld.add(instruccionesD2);
		startPaneld.add(comenzarD);
		discriFrame.getContentPane().add(startPaneld);
		comenzarD.addActionListener(this);
		discriFrame.setSize(600,300);
		discriFrame.setVisible(false);
		discriFrame.addWindowListener(new WindowAdapter(){
			 public void windowClosing(WindowEvent e) {
			     if (discriThread!=null)   
			    	 endThread=true;
			    }
		});
		
		mainPaneld= new JPanel();
		mainPaneld.setLayout(new FlowLayout());
		datosrondad = new JLabel();
		mainPaneld.add(datosrondad);
		botona=new JButton("Igual al primero");
		botonb=new JButton("Igual al segundo");
		JPanel botonerd = new JPanel();
		botonerd.add(botona);
		botonerd.add(botonb);
		botona.setActionCommand("botona");
		botonb.setActionCommand("botonb");		
		botona.addActionListener(this);
		botonb.addActionListener(this);
		botona.setEnabled(false);
		botonb.setEnabled(false);
		mainPaneld.add(botonerd);
		botonerd.setEnabled(false);
		mainPaneld.setVisible(false);
		discriFrame.add(mainPaneld);
		pausaDP = new JPanel();
		pausaD = new JButton("pausa");
		pausaD.addActionListener(this);
		pausaD.setActionCommand("pausa");
		pausaDP.add(pausaD);
		mainPaneld.add(pausaDP);
		//pausaDP.setVisible(false);
		
		// Graficar
		grafiFrame = new JFrame("Graficar resultados");
		grafiFrame.setSize(300,300);
		JPanel sujetoPanel = new JPanel();
		JButton cambiarSujeto = new JButton("Cambiar Sujeto");
		cambiarSujeto.setActionCommand("cambiarSujeto");
		cambiarSujeto.addActionListener(this);
		
		//sujetoPanel.add(sujetoLabel);
		sujetoPanel.add(cambiarSujeto);
		grafiFrame.add(sujetoPanel);
		
		//JPanel graficas = new JPanel();
		//  grafiquitas
		identiPlot = new Plot2DPanel();
		identiPlot.addLegend("SOUTH");
		grafiIdentiFrame = new JFrame("Gráfica: Identificación");
		grafiIdentiFrame.setSize(300,300);
		grafiIdentiFrame.setVisible(false);
		grafiIdentiFrame.setContentPane(identiPlot);		
		
		discriPlot = new Plot2DPanel();
		discriPlot.addLegend("SOUTH");
		grafiDiscriFrame = new JFrame("Gráfica: Discriminación");
		grafiDiscriFrame.setSize(300, 300);
		grafiDiscriFrame.setVisible(false);
		grafiDiscriFrame.setContentPane(discriPlot);
		
		//graficas.add(identiPlot);
		//graficas.add(discriPlot);
		//grafiFrame.add(graficas);
		//SVGPanel
		//sujetoPanel.add(graficas);
	
		// cargar sonidos
		numeroSonidos = 13;
		sonidos = new AudioInputStream[numeroSonidos];
		try {
			for (int i=0; i<numeroSonidos; i++) {
				sonidos[i] = CargadorSonidos.cargarSonido("bada"+(i+1)+".wav");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al cargar los archivos de sonido.");
			System.exit(0);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Badaga bdg = new Badaga();
		bdg.setVisible(true);
		bdg.addWindowListener(new WindowAdapter(){
			 public void windowClosing(WindowEvent e) {
			        System.exit(0);
			    }
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		File[] archivosBadaga;
		// TODO Auto-generated method stub
		switch (arg0.getActionCommand()) {
		case "nuevo":
			nombresujeto = JOptionPane.showInputDialog("Introduzca el nombre del participante");
			if (nombresujeto==null)
				break;
			nuevoFrame.setVisible(true);
			appendD=false;
			appendI=false;
			break;
			//this.setVisible(false);
		case "continuar":
			while (true) {
				nombresujeto = JOptionPane.showInputDialog("Introduzca el nombre del participante");
				if (nombresujeto==null)
					break;

				archivosBadaga = new File(".").listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir,String name) {
						return name.contains(nombresujeto) && name.endsWith(".badaga");
					}
				});
				if (archivosBadaga!=null) {
					appendD = true;
					appendI= true;
					graficabot.setEnabled(true);
					break;
				}
				JOptionPane.showMessageDialog(this, "No se han encontrado archivos para el participante: "+nombresujeto+".");
			}
			break;
		case "identificar":
			identiFrame.setVisible(true);
			startPaneli.setVisible(true);
			mainPaneli.setVisible(false);
			//nuevoFrame.setVisible(false);
			//discriFrame.setVisible(false);
			System.out.println("Es ahora");
			break;
		case "discriminar":
			discriFrame.setVisible(true);
			//nuevoFrame.setVisible(false);
			startPaneld.setVisible(true);
			mainPaneld.setVisible(false);
			break;
		case "identistart":
			startPaneli.setVisible(false);
			mainPaneli.setVisible(true);
			pausaP.setVisible(true);
			identiThread =new Thread(new Runnable() {

			@Override
			public void run() {
				startIdenti();
			}});
			identiThread.start();
			break;
		case "discristart":
			startPaneld.setVisible(false);
			mainPaneld.setVisible(true);
			//pausaDP.setVisible(true);
			discriThread = new Thread(new Runnable(){

				@Override
				public void run() {
					startDiscri();
				}
				
			});
			discriThread.start();
			break;
		case "ba":
			soFar[numEstim-1]=1;
			break;
		case "da":
			soFar[numEstim-1]=2;
			break;
		case "ga":
			soFar[numEstim-1]=3;
			break;
		case "botona":
			if (discriCorrect==1)
				soFar[numEstim-1]=1;
			else
				soFar[numEstim-1]=0;
			break;
		case "botonb":
			if (discriCorrect==1)
				soFar[numEstim-1]=0;
			else
				soFar[numEstim-1]=1;
			break;
		case "graficar":
			grafiFrame.setVisible(true);
			grafiThread = new Thread(new Runnable(){

				@Override
				public void run() {
					grafiStart();
				}});
			grafiThread.start();
			break;
		case "cambiarSujeto":
			nombresujeto = JOptionPane.showInputDialog("Introduzca el nombre del participante");
			if (nombresujeto==null)
				break;
			sujetoLabel.setText(nombresujeto);
			grafiThread.start();
		case "pausa":
			pauseThread=!pauseThread;
			break;
		}
		
	}
	
	

	private synchronized void playSound(final AudioInputStream ais) {
		  //new Thread(new Runnable() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    //public void run() {
		      try {
		//        AudioInputStream ais = AudioSystem.getAudioInputStream(
		  //        new File(file));
		        //System.out.println(ais.getFormat());
		        DataLine.Info dli = new DataLine.Info(Clip.class, ais.getFormat());
		        Clip clip = (Clip) AudioSystem.getLine(dli);
		        clip.open(ais);
		        //System.out.println(clip.getFormat().toString());
		        //ba.setEnabled(true);
				//da.setEnabled(true);
				//ga.setEnabled(true);
		        clip.start();
		        clip.drain();
		        clip.close();
		        clip=null;
		        //while (clip.isActive()) {
		        //	Thread.yield();
		        //}
				///ba.setEnabled(false);
				///da.setEnabled(false);
				///ga.setEnabled(false);

		        //clip.close();
		      } catch (Exception e) {
		        e.printStackTrace();
		      }
//		    }
		  //}).start();
	}
	
	private synchronized void startIdenti(){
		try {
			endThread=false;
			pauseThread=false;
			numBlock=-1;
			salidita = new FileWriter(nombresujeto+"_identi"+".badaga",appendI);
			datosronda.setVisible(true);
			datosronda.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,24));
			
			Iterator<Integer> iter;
			
			//Thread sonido, respuesta;
			boolean seguir= true;
			int sigueono;
			//Sonidero sonidero;
			//Integer[] orden;
			//datosronda.setVisible(true);
			String arxivo;
			while (seguir && !endThread) {
				datosronda.setText("Preparado...");
				Thread.sleep(3500);
				numBlock++;
				iter = dameN(13).iterator();
				soFar = new int[13];
				datosronda.setText("Bloque: "+(numBlock+1));
				while (iter.hasNext() && !endThread) {
					/*sonido=new Thread(new Sonidero(iter.next()));
					/*respuesta=new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
						
						}});
					sonido.run();
					System.out.println(sonido.getState());*/
//					while (sonido.isAlive()) {
					numEstim = ((Integer) iter.next());
					//arxivo = "bada"+numEstim+".wav";
					ba.setEnabled(true);
					da.setEnabled(true);
					ga.setEnabled(true);
					
					// para pausar la thread
					while (pauseThread) {
						Thread.yield();
					}
					playSound(sonido(numEstim));
					
	//				}
					//System.out.println("Ahora: "+arxivo);
					//while sonido.is
					while (pauseThread) {
						Thread.yield();
					}
					
					Thread.sleep(2500);
					//System.out.println("Que pasa acá?");
					ba.setEnabled(false);
					//System.out.println("Dsps BA");

					da.setEnabled(false);
					//System.out.println("Dsps DA");

					ga.setEnabled(false);
					//System.out.println("Dsps GA");

					while (pauseThread) {
						Thread.yield();
					}
					
					Thread.sleep(1000);
					//System.out.println("Que pasa acááaa?");

					//ba.setEnabled(false);
					//da.setEnabled(false);
					//ga.setEnabled(false);
		//			System.out.println(sonido.getState());

					//Thread.yield();
					//sonido.yield();
				}
				if (endThread) {
					endThread=false;
					salidita.close();
					return;					
				}

				for (int i=0; i<soFar.length; i++) {
					salidita.write(""+soFar[i]+"\r\n");	
				}
				sigueono = JOptionPane.showConfirmDialog(identiFrame, "Desea continuar con otro bloque?","Confirma continuar", JOptionPane.YES_NO_OPTION); 
				seguir = (sigueono==JOptionPane.YES_OPTION);
			}
			//String mensaje="Se han respondido "+numBlock+" bloques de 13 estímulos";
			//int rps = JOptionPane.showConfirmDialog(identiFrame, mensaje,"Éxito", JOptionPane.OK_OPTION);
			//mainPaneli.setVisible(false);

			identiFrame.setVisible(false);
			salidita.close();
			ba.setEnabled(false);
			da.setEnabled(false);
			ga.setEnabled(false);
			graficabot.setEnabled(true);
			
			startPaneli.setVisible(true);
			mainPaneli.setVisible(false);
			pausaP.setVisible(false);
			
			appendI=true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private synchronized void startDiscri() {
		// TODO Auto-generated method stub
		try {
			endThread=false;
			pauseThread=false;
			numBlock=-1;
			salidita = new FileWriter(nombresujeto+"_discri"+".badaga",appendD);
			datosrondad.setVisible(true);
			datosrondad.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,18));
			
		
			// uno para elegir posicion, el otro para elegir cual va primero, y el otro para saber quien es el tercero (X)
			Iterator<Integer> iter,iterOrden;
			//Thread sonido, respuesta;
			boolean seguir= true;
			int sigueono;
			//Sonidero sonidero;
			//Integer[] orden;
			//datosronda.setVisible(true);
			String arxivo,arxivo2,arxivo3,informacion;
			//informacion = new String();
			ga.setEnabled(true);ga.setEnabled(true);
			int numEstim2,numEstim1;
			
			
			while (seguir && !endThread) {
				datosrondad.setText("Preparado...");
				Thread.sleep(3500);
				numBlock++;
				informacion = new String("Bloque: "+(numBlock+1));
				iter = dameN(12).iterator();
				iterOrden = dameN(11).iterator();				
				soFar = new int[11];
				datosrondad.setText(informacion);
				while (iter.hasNext() && !endThread) {
					botona.setEnabled(false);
					botonb.setEnabled(false);

					/*sonido=new Thread(new Sonidero(iter.next()));
					/*respuesta=new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
						
						}});
					sonido.run();
					System.out.println(sonido.getState());*/
//					while (sonido.isAlive()) {
					numEstim = ((Integer) iter.next());
					
	
					// la mitad de las veces A B B, la otra mintad A B A
					if ((iterOrden.next().intValue() % 2)==0) {
						numEstim1 = numEstim;
						numEstim2 = numEstim+2;
						discriCorrect=1;
					}
					else {
						numEstim2 = numEstim;
						numEstim1 = numEstim+2;
						discriCorrect = 2;
					}
					//arxivo = "bada"+numEstim1+".wav";
					//arxivo2 = "bada"+numEstim2+".wav";
					//arxivo3 = "bada"+numEstim+".wav";
					
					datosrondad.setText(new String(informacion+ " | Sonido: 1"));
					
					while (pauseThread) {
						Thread.yield();
					}
					
					playSound(sonido(numEstim1));
					//System.out.println("Ahora: "+arxivo);
					Thread.sleep(800);
					
					while (pauseThread) {
						Thread.yield();
					}

					datosrondad.setText(new String(informacion+ " | Sonido: 2"));
					playSound(sonido(numEstim2));
					//System.out.println("Ahora: "+arxivo2);
					Thread.sleep(800);
					botona.setEnabled(true);
					botonb.setEnabled(true);
					while (pauseThread) {
						Thread.yield();
					}

					datosrondad.setText(new String(informacion+ " | Sonido: X?"));
					playSound(sonido(numEstim));
					//System.out.println("Ahora: "+arxivo3);
					while (pauseThread) {
						Thread.yield();
					}
					
					Thread.sleep(3000);
					//ba.setEnabled(false);
					//da.setEnabled(false);
					//ga.setEnabled(false);
		//			System.out.println(sonido.getState());

					//Thread.yield();
					//sonido.yield();
				}
				for (int i=0; i<soFar.length; i++) {
					salidita.write(""+soFar[i]+"\r\n");
				}
				sigueono = JOptionPane.showConfirmDialog(discriFrame, "Desea continuar con otro bloque?","Confirma continuar", JOptionPane.YES_NO_OPTION); 
				seguir = (sigueono==JOptionPane.YES_OPTION);
			}
			
			if (endThread) {
				endThread=false;
				salidita.close();
				return;
			}
			//String mensaje="Se han respondido "+numBlock+" bloques de 13 estímulos";
			//int rps = JOptionPane.showConfirmDialog(identiFrame, mensaje,"Éxito", JOptionPane.OK_OPTION);
			//mainPaneli.setVisible(false);

			// resetear interfaz al comienzo
			
			discriFrame.setVisible(false);
			salidita.close();
			botona.setEnabled(false);
			botonb.setEnabled(false);
			
			startPaneld.setVisible(true);
			mainPaneld.setVisible(false);
			pausaDP.setVisible(false);
			
			appendD=true;
			
			graficabot.setEnabled(true);
			//ga.setEnabled(false);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private AudioInputStream sonido(int numEstim) {
		return sonidos[numEstim-1];
	}

	private synchronized void grafiStart() {
		try {
			sujetoLabel.setText(nombresujeto);
			boolean nohayDiscri = true;
			boolean nohayIdenti = true;
			BufferedReader bf = new BufferedReader(new FileReader(nombresujeto+"_discri.badaga"));
			String linea = bf.readLine();
			int contador=0;
			int sesiones=0;
			int[] loquesalio= new int[12];
			while (linea!=null){
				loquesalio[contador]=loquesalio[contador]+Integer.parseInt(linea);
				contador++;
				if (contador==12) {
					contador=0;
					sesiones++;
				}
				linea=bf.readLine();
			}
			bf.close();
			
			if (sesiones!=0) {
				nohayDiscri=false;
				double[] discriProfile = new double[12];
				for (int i=0; i<12; i++) {
					discriProfile[i]=loquesalio[i] * Math.pow(sesiones, -1);
					System.out.println("discri"+i+": "+discriProfile[i]);
				}
				
				double[] xprofile = {1,2,3,4,5,6,7,8,9,10,11,12};
				discriPlot.addLinePlot("Discriminación", xprofile,discriProfile);
				grafiDiscriFrame.setVisible(true);
				System.out.println("Che!");
			}
			bf = new BufferedReader(new FileReader(nombresujeto+"_identi.badaga"));
			linea = bf.readLine();
			contador=0;
			sesiones=0;
			int[] salioBA= new int[13];
			int[] salioDA= new int[13];
			int[] salioGA= new int[13];
			while (linea!=null){
				switch (Integer.parseInt(linea)){
					case 1:
						salioBA[contador]++;
						break;
					case 2:
						salioDA[contador]++;
						break;
					case 3:
						salioGA[contador]++;
						break;
				}
				contador++;
				if (contador==13) {
					contador=0;
					sesiones++;
				}
				linea=bf.readLine();

			}
			bf.close();
			
			if (sesiones!=0) {
				nohayIdenti=false;
				double[] identiBA = new double[13];
				double[] identiDA = new double[13];
				double[] identiGA = new double[13];
				
				for (int i=0; i<13; i++) {
					identiBA[i]=salioBA[i] * Math.pow(sesiones,-1);
					identiDA[i]=salioDA[i] * Math.pow(sesiones,-1);
					identiGA[i]=salioGA[i] * Math.pow(sesiones,-1);
	
				}
				
				double[] xprofilei = {1,2,3,4,5,6,7,8,9,10,11,12,13};
				//identiPlot.addLinePlot("BA", xprofilei,xprofilei);
			//	identiPlot.setVisible(true);
	
				identiPlot.addLinePlot("BA", xprofilei,identiBA);
				identiPlot.addLinePlot("DA", xprofilei,identiDA);
				identiPlot.addLinePlot("GA", xprofilei,identiGA);
				grafiIdentiFrame.setVisible(true);
				System.out.println("CHE");
			}
			if (nohayIdenti || nohayDiscri)
				JOptionPane.showMessageDialog(grafiFrame, new String("Faltan datos de alguna de las pruebas"), new String("Datos faltantes"), JOptionPane.WARNING_MESSAGE);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private List<Integer> dameN(int n) {
		ArrayList<Integer> unoa13 = new ArrayList<Integer>();
		for (int i=0;i<n;i++) {
			unoa13.add(new Integer(i+1));
		}
		Collections.shuffle(unoa13);
		return unoa13;
	}

}/*
class Sonidero implements Runnable {
	
	Object archivo;
	
	Sonidero(Object archivo){
		this.archivo=archivo;
	}
	
	private static synchronized void playSound(final String file) {
		  //new Thread(new Runnable() {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    //public void run() {
		      try {
		        AudioInputStream ais = AudioSystem.getAudioInputStream(
		          new File(file));
		        //System.out.println(ais.getFormat());
		        DataLine.Info dli = new DataLine.Info(Clip.class, ais.getFormat());
		        Clip clip = (Clip) AudioSystem.getLine(dli);
		        clip.open(ais);
		        //System.out.println(clip.getFormat().toString());
		        clip.start();
		        while (clip.isRunning()) {
		        	Thread.yield();
		        }
		        //clip.close();
		      } catch (Exception e) {
		        e.printStackTrace();
		      }
//		    }
		  //}).start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		playSound("bada"+archivo+".wav");							

	}
	
}*/
