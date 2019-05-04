package Projet;

public class Rectangle_ extends Forme implements java.io.Serializable{
	private double x;
	private double y;
	private double width;
	private double height;
	
	public Rectangle_() {
		
	}
	
	public Rectangle_(double x, double y, double width, double height, double strokeWidth, String paint) {
		super(strokeWidth,paint);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}


	public double getX() {
		return x;
	}


	public void setX(double x) {
		this.x = x;
	}


	public double getY() {
		return y;
	}


	public void setY(double y) {
		this.y = y;
	}


	public double getWidth() {
		return width;
	}


	public void setWidth(double width) {
		this.width = width;
	}


	public double getHeight() {
		return height;
	}


	public void setHeight(double height) {
		this.height = height;
	}
	
}
