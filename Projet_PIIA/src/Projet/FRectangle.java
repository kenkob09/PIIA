package Projet;

import javafx.scene.canvas.GraphicsContext;

public class FRectangle extends Formes{
	private double width, heigth;
	
	FRectangle(double x, double y, double width, double heigth) {
		super(x,y);
		this.width=width;
		this.heigth=heigth;
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
		// TODO Auto-generated method stub
		gc.fillRect(this.x,this.y,this.width,this.heigth);
	}

	@Override
	void draw(GraphicsContext gc) {
		// TODO Auto-generated method stub
		gc.strokeLine(x, y, width, y);
		gc.strokeLine(x, y, x, heigth);
		gc.strokeLine(width, y, width, heigth);
		gc.strokeLine(x, heigth, width, heigth);
	}
}
