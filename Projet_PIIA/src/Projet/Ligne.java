package Projet;
import javafx.scene.shape.Line;


public class Ligne {
	
	public static void tracer_ligne() {
		Controleur.line.setEndX(Controleur.xEnd);
		Controleur.line.setEndY(Controleur.yEnd);
		Controleur.line.setStartX(Controleur.xStart);
		Controleur.line.setStartY(Controleur.yStart);
		Controleur.gc.strokeLine(Controleur.xStart,Controleur.yStart,Controleur.xEnd,Controleur.yEnd);
		
		//ajout a la liste des dessins
		Line l = new Line(Controleur.line.getStartX(), Controleur.line.getStartY(), Controleur.line.getEndX(), Controleur.line.getEndY());
		l.setStrokeWidth(Controleur.gc.getLineWidth());
		Controleur.listShapes.add(l);
		//ajout a l'historique des retours en arriere
		Controleur.undoHistory.push(l);
	}
	
}