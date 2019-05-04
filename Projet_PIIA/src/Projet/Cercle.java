package Projet;

public class Cercle extends Forme implements java.io.Serializable{
	private double centerX;
	private double centerY;
	private double radius;
	
	public Cercle() {
		
	}
	
	public Cercle(double centerX, double centerY, double radius, double strokeWidth, String paint) {
		super(strokeWidth,paint);
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
	}


	public double getCenterX() {
		return centerX;
	}


	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}


	public double getCenterY() {
		return centerY;
	}


	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}


	public double getRadius() {
		return radius;
	}


	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	
}
