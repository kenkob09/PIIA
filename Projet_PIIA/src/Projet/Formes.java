package Projet;

import javafx.scene.canvas.GraphicsContext;

public abstract class Formes {
	protected double x,y;
	
	Formes(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	double getX() {return x;}
	
	double getY() {return y;}
	
	void setX(double x) {this.x=x;}
	
	void setY(double y) {this.y=y;}
	
	abstract void tracer(GraphicsContext gc);
	
	//abstract void fillForm(GraphicsContext gc);
	
}
