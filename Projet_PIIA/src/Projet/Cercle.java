package Projet;

import javafx.scene.canvas.GraphicsContext;

public class Cercle extends Formes{
	private double width, heigth;
	
	Cercle(double x, double y, double w, double h) {
		super(x, y);
		this.width=w;
		this.heigth=h;
	}
	double getWidth() {
		return this.width;
	}
	double getHeigth() {
		return this.heigth;
	}
	void setWidth(double w) {
		this.width=w;
	}
	void setHeigth(double h) {
		this.heigth=h;
	}

	@Override
	boolean verifBords(double x, double y) {
		// TODO Auto-generated method stub
		return (x-this.x)<this.width && (x-this.x)>0 && (y-this.y)<this.heigth && (y-this.y)>0;
	}

	@Override
	void fillForm(GraphicsContext gc) {
	}

	@Override
	void draw(GraphicsContext gc) {
		if (this.width > x) {
			if (this.heigth>y) {
				gc.strokeOval(x,y,this.width-x,this.heigth-y);
			}
			else {
				gc.strokeOval(x, this.heigth, this.width-x, y-this.heigth);
			}
		}
		//Cas ou le trace se fait de droite a gauche et :
		else {
			//Cas ou le trace se fait de haut en bas
			if (this.heigth>y) {
				gc.strokeOval(this.width,y,x-this.width,this.heigth-y);
			}
			//Cas ou le trace se fait de bas en haut				
			else {
				gc.strokeOval(this.width, this.heigth, x-this.heigth, y-this.heigth);
			} 
		}
	}
}
