package Projet;


public class Ligne extends Forme implements java.io.Serializable {
	private double xStart;
	private double yStart;
	private double xEnd;
	private double yEnd;

	public Ligne() {
		
	}
	
	public Ligne(double xStart, double yStart, double xEnd, double yEnd, double strokeWidth, String paint) {
		super(strokeWidth,paint);
		this.xStart = xStart;
		this.yStart = yStart;
		this.xEnd = xEnd;
		this.yEnd = yEnd;
	}


	public double getXStart() {
		return xStart;
	}


	public void setXStart(double xStart) {
		this.xStart = xStart;
	}


	public double getYStart() {
		return yStart;
	}


	public void setYStart(double yStart) {
		this.yStart = yStart;
	}


	public double getXEnd() {
		return xEnd;
	}


	public void setXEnd(double xEnd) {
		this.xEnd = xEnd;
	}


	public double getYEnd() {
		return yEnd;
	}


	public void setYEnd(double yEnd) {
		this.yEnd = yEnd;
	}
	
}
