package Projet;

import java.util.ArrayList;

public class Liste_Forme {
	
	private ArrayList<Forme> liste;
	
	
	public Liste_Forme() {
		
	}
	
	public Liste_Forme(ArrayList<Forme> l ) {
		this.liste=l;
	}

	public ArrayList<Forme> getListe() {
		return liste;
	}

	public void setListe(ArrayList<Forme> liste) {
		this.liste = liste;
	}
	
}
