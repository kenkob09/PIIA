package Projet;

public class Polygone extends Forme implements java.io.Serializable{
	private double[] xPoints;
	private double[] yPoints;
	private double strokeWidth;
	private String paint;
	
	public Polygone() {
		
	}
	
	public Polygone(double[] xPoints, double[] yPoints, double strokeWidth, String paint) {
		super(strokeWidth,paint);
		this.xPoints = xPoints;
		this.yPoints = yPoints;
	}


	public double[] getXPoints() {
		return xPoints;
	}


	public void setXPoints(double[] xPoints) {
		this.xPoints = xPoints;
	}


	public double[] getYPoints() {
		return yPoints;
	}


	public void setYPoints(double[] yPoints) {
		this.yPoints = yPoints;
	}
}
