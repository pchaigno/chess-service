package com.sun.jersey.DemoPresentation;

public class Move {
	private String nom;
	private int nombreJoue;
	private float pourcentageW;
	
	public Move(String nom, int nombreJoue, float pourcentageW) throws InvalidMoveException {
		super();
		if(nom == null){
			throw new InvalidMoveException();
		}
		if(nom.isEmpty() || nombreJoue <0 || pourcentageW < 0){
			throw new InvalidMoveException();
		}
		this.nom = nom;
		this.nombreJoue = nombreJoue;
		this.pourcentageW = pourcentageW;
	}

	@Override
	public String toString() {
		return "Move [name=" + nom + ", number=" + nombreJoue
				+ ", percentageW=" + pourcentageW + "]\n";
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getNombreJoue() {
		return nombreJoue;
	}

	public void setNombreJoue(int nombreJoue) {
		this.nombreJoue = nombreJoue;
	}

	public float getPourcentageW() {
		return pourcentageW;
	}

	public void setPourcentageW(float pourcentageW) {
		this.pourcentageW = pourcentageW;
	}	
}
