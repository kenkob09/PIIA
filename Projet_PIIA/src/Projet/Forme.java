package Projet;

public class Forme {
	
	private double strokeWidth;
	private String paint;
	
	public Forme() {
		
	}

	public Forme(double strokeWidth, String paint) {
		this.strokeWidth = strokeWidth;
		this.paint = paint;
	}

	public double getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public String getPaint() {
		return paint;
	}

	public void setPaint(String paint) {
		this.paint = paint;
	}
	

}
