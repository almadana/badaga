package badaga.recursos;


import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CargadorSonidos {

	private static CargadorSonidos cgs = new CargadorSonidos();
	
	//public CargadorSonidos(){
		
		public static AudioInputStream cargarSonido(String s) throws UnsupportedAudioFileException, IOException {
			return AudioSystem.getAudioInputStream(cgs.getClass().getResource("sonidos/"+s));
		}
	
}
