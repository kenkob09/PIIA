package Projet;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;


public class Controleur {
    
	//Attributs FXML
	@FXML private Canvas Canvas;
	@FXML private Button Sauver_Button;
	@FXML private Button Undo_Button;
	@FXML private Button Redo_Button;
	@FXML private Button Pinceau_Button;
	@FXML private TextField Pinceau_Size_Champs;
	@FXML private Button Gomme_Button;
	@FXML private Button Sceau_Button;
	@FXML private Button Ligne_Button;
	@FXML private Button Rectangle_Button;
	@FXML private Button Cercle_Button;
	@FXML private Button Polygone_Button; //faire un affichage explicatif pour le polygone
	@FXML private ColorPicker Color_Picker;	
	@FXML private Button Start_Button;
	@FXML private Pane Start_Pane;
	@FXML private GridPane Whole_Grid_Pane;
	@FXML private Button Deplacer_Button;
	@FXML private Button Redim_Button;
	
	/***************************************************************** Attributs de classe ******************************************************************/
	
	private Button last_button; //dernier bouton actif
	private GraphicsContext gc;
  	
	//Coordonnees de dessin
	private double xStart;
	private double xEnd;
	private double yStart;
	private double yEnd;
	
	//x,y pour les traces au pinceau
	private double xMid;
	private double yMid;
	
	//Liste de points pour polygone
	private ArrayList<Double> xPoints;
	private ArrayList<Double> yPoints;	
	
	//booleen pour le deplacement / redimenssionnement
	private boolean first_time;
	private boolean last_time;
	
	//liste de toutes les formes dessinees
	//necessaires pour faire le redimensionnement et le deplacement
	private ObservableList<Shape> listShapes = FXCollections.observableArrayList();;
	private int indexShape;
    
    //Canvas
	private Line line = new Line();
	private Rectangle rect = new Rectangle();
	private Circle cercle = new Circle();
	private Polygon polygone = new Polygon();
	private double size;
	private Color color;
  	
    //Undo/Redo Historique 
	private Stack<Shape> undoHistory = new Stack<Shape>();
	private  Stack<Shape> redoHistory = new Stack<Shape>();
    
    //Pinceau
	private Map<Rectangle,Rectangle> brushMap = new HashMap<>(); //contient key: le premier rectangle du pinceau, value: le dernier rectangle  
	private Rectangle rectDebut;
	private Stack<Rectangle> undoPinceauRect = new Stack<Rectangle>(); //liste des rectangle qui ont servi au dessin avec pinceau
	private Stack<Rectangle> redoPinceauRect = new Stack<Rectangle>();
      

    
    /***************************************************************** Constructeur *****************************************************************/	

  	public Controleur() {
  		this.xPoints = new ArrayList<>();
  		this.yPoints = new ArrayList<>();
  	}
    
    //Initialisation
    public void initialize() {    
    	//On initialise la couleur
    	Color_Picker.setValue(Color.BLACK);
    	//Par defaut, le pinceau est selectionne
    	this.last_button = Pinceau_Button;
    	//On recupere le contexte graphique
        gc = Canvas.getGraphicsContext2D();
        //Lorsqu'on clique sur le canvas
        Canvas.setOnMousePressed(e -> {begin_trace(e, gc);});
        //Lorsqu'on relache sur le canvas
        Canvas.setOnMouseReleased(e -> {end_trace(e, gc);});
        //Lorsqu'on drag sur le canvas
        Canvas.setOnMouseDragged(e -> {drag_trace(e, gc);});
    }
    
    
    
    /***************************************************************** COMMENTAIRES *****************************************************************/
    
    //PROBLEME : LA TAILLE DU CANVAS NE S'AJUSTE PAS A LA FENETRE
    //SCEAU:  bug quand on clique deux fois sur la meme zones avec la meme couleur
    //REDO : Quand on trace une ligne a 10, qu'on en trace une autre a 100, qu'on undo les deux, et qu'on redo les deux: les deux sont a 100 
    //DEPLACER : Quand on clique dans le vide, ca deplace qd mm 
    //DEPLACER : Quand on deplace plusieurs fois, la largeur de la figure change. (tracer une figure. Tracer une autre figure en changeant la taille. ca bug)
    //DEPLACER : Ne marche pas bien avec undo/redo
    //DEPLACER : Animations ? 
    //REDIM : Meme pb que deplacement
    //PINCEAU : On fait la meme fonction mais avec des lignes
    //LASTSTROKEWIDTH ?
    //BOUTONS : CHANGER APPARENCE
    //INTERFACE : PLUS DE COULEUR
    //BOUTON HELP !!
    //ENLEVER LES COMMENTAIRES INUTILES
    
    //LOGO?
    

    /**************           Soucis        ***********/
    /* effacer les points qui auront servi au dessin du polygone car �a cree 
     * un probleme sur la taille de la forme qui le precede */
    /*****************************************************/
    
    
    
    /***************************************************************** Fonctions associees aux actions avec la souris *****************************************************************/
    
    //Fonction appelee lorsqu'on clique sur le canvas avec la souris
	public void begin_trace(MouseEvent e, GraphicsContext gc){
		System.out.println("begin_trace");
		//On recupere de la position
		xStart = e.getX();
		yStart = e.getY();
		xEnd= xStart;
		yEnd= yStart;
		//On recupere la couleur
        color = Color_Picker.getValue();
        gc.setStroke(color);
        //On recupere la taille 
        size = Double.parseDouble(Pinceau_Size_Champs.getText());
        gc.setLineWidth(size);
        //Si on veut dessiner au pinceau
        if(this.last_button==Pinceau_Button) {
        	tracer_pinceau(true,false);
		}
        else {
        	if(this.last_button == Gomme_Button) {
    			gc.clearRect(xStart-size / 2, yStart-size / 2, size, size);
    		}
    		else if(this.last_button == Sceau_Button) {
    			remplissage();
    		}	
    		else if(this.last_button == Ligne_Button) {
    			tracer_ligne();
    		}
    		else if(this.last_button == Rectangle_Button) {
    			tracer_rectangle();
    		}
    		else if(this.last_button == Cercle_Button) {
    			tracer_cercle();
    		}
    		else if(this.last_button == Deplacer_Button) {
    			first_time = true;
    			last_time = false;
    			forme_visee();
    		} 
    		else if(this.last_button == Redim_Button) {
    			first_time = true;
    			last_time = false;
    			forme_visee();
    		}
        }
	}
	
	//Fonction appelee lorsqu'on drag la souris sur le canvas
	public void drag_trace(MouseEvent e, GraphicsContext gc) {
		xEnd = e.getX();
		yEnd = e.getY();
	    
		//Si on veut desinner au pinceau
		if (this.last_button == Pinceau_Button) {
			tracer_pinceau(false,false);
		}
		//Si on veut effacer
		else if (this.last_button == Gomme_Button) {
			gc.clearRect(xEnd-size / 2, yEnd-size / 2, size, size);
	    }

		//Si on veut tracer une ligne
		else if (this.last_button == Ligne_Button) {
			Shape l = undoHistory.lastElement();
			if (l.getClass()==Line.class) {
				Line line = (Line)l;
				//Si on a bouge la souris, alors on retrace la ligne
				if ( (line.getEndX()!=xEnd)||(line.getEndY()!=yEnd) ) {
					onUndo();
					refresh();
					tracer_ligne();
				}
			}
		}
		//Si on veut tracer un rectangle
		else if (this.last_button == Rectangle_Button) {
			Shape r = undoHistory.lastElement();
			if (r.getClass()==Rectangle.class) {
				Rectangle rect = (Rectangle)r;
				//Si on a bouge la souris, alors on retrace le rectangle
				if ( (rect.getWidth()!=Math.abs(xEnd-xStart))||(rect.getHeight()!=Math.abs(yEnd-yStart)) ) {
					onUndo();
					refresh();
					tracer_rectangle();
				}
			}
		}
		//Si on veut tracer un cercle
		else if (this.last_button == Cercle_Button) {
			Shape c = undoHistory.lastElement();
			if (c.getClass()==Circle.class) {
				Circle cercle = (Circle)c;
				//Si on a bouge la souris, alors on retrace le cercle
				if ( (cercle.getRadius()!=Math.abs(xEnd-xStart))||(cercle.getRadius()!=Math.abs(yEnd-yStart)) ) {
					onUndo();
					refresh();
					tracer_cercle();
				}
			}
		}
		
		else if (this.last_button == Deplacer_Button) {
			//On deplace
			deplacement(xEnd,yEnd,indexShape);			
			//On met a jour xStart et yStart
			xStart=xEnd;
			yStart=yEnd;
		}
		
		else if (this.last_button == Redim_Button) {
			redim(xEnd,yEnd,indexShape);
			xStart=xEnd;
			yStart=yEnd;
		}
	}		
	
	//Fonction appelee lorsqu'on relache la souris sur le canvas
	public void end_trace(MouseEvent e, GraphicsContext gc){
		//double lastStrokeWidth = 1;
		xEnd = e.getX();
		yEnd = e.getY();
        
        //Si on veut dessiner au pinceau
      	if (this.last_button == Pinceau_Button) {
      		tracer_pinceau(false,true);
			//System.out.println("\nmap "+brushMap.toString());
			//System.out.println("rectStart"+rectDebut+"rectFin"+r);
      	}
        //Si on veut tracer une ligne
		if (this.last_button == Ligne_Button) {
			//on enleve la ligne trace dans drag_trace
			onUndo();
			refresh();
			tracer_ligne();
		}
		
		//Si on veut tracer un rectangle
		else if (this.last_button == Rectangle_Button) {
			//on enleve le rectangle trace dans drag_trace
			onUndo();
			refresh();
			tracer_rectangle();
		}
		
		//Si on veut tracer un cercle
		else if (this.last_button == Cercle_Button) {
			//on enleve le rectangle trace dans drag_trace
			onUndo();
			refresh();
			tracer_cercle();
		}
		
		//Si on veut tracer un polygone
		else if (this.last_button == Polygone_Button) {
			//lastStrokeWidth = gc.getLineWidth();
			tracer_polygone();
		}
		
		//Si on veut deplacer
		else if (this.last_button == Deplacer_Button){
			System.out.println("end_trace");
			//Shape temp = listShapes.get(indexShape);
			last_time = true;
			deplacement(xEnd,yEnd,indexShape);			
		}
		
		//Si on veut redimensionner
		else if (this.last_button == Redim_Button){
			System.out.println("redim !");
			last_time = true;
			redim(xEnd,yEnd,indexShape);
		}		
		
		//Gestion de la pile Undo/Redo
		redoHistory.clear();
		//recuperer le dernier element du stock
		if (undoHistory.size() !=0) {
			Shape lastUndo = undoHistory.lastElement();
			if(lastUndo.getClass() != Polygon.class) {
				lastUndo.setStroke(gc.getStroke());
				lastUndo.setStrokeWidth(gc.getLineWidth());	
				
			}else {
				lastUndo.setStroke(gc.getStroke());
			}
		}	
	}    
	
	/***************************************************************** Fonctions de dessin *****************************************************************/
	
	public void tracer_pinceau(boolean first_time, boolean last_time) {
		if (first_time) {
			xMid = xStart;
			yMid = yStart;
		}
		line.setStartX(xMid);
		line.setEndX(xEnd);
		line.setStartY(yMid);
		line.setEndY(yEnd);
		gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
		xMid = xEnd;
		yMid = yEnd;
		Line l = new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
		l.setFill(color);
        l.setStrokeWidth(size);
        l.setStroke(color);
        //On ajoute le rectangle dessine dans les differentes piles
    	listShapes.add(l); 
    	undoHistory.push(l);
	}
	
	public void tracer_ligne() {
		line.setEndX(xEnd);
        line.setEndY(yEnd);
        line.setStartX(xStart);
        line.setStartY(yStart);
		gc.strokeLine(xStart,yStart,xEnd,yEnd);
		
		//ajout a la liste des dessins
		Line l = new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
		l.setStrokeWidth(gc.getLineWidth());
		listShapes.add(l);
		//ajout a l'historique des retours en arriere
		undoHistory.push(l);
	}
	
	public void tracer_rectangle() {
		//calcul de la largeur du rectangle
		rect.setWidth(Math.abs((xEnd - xStart)));
        rect.setHeight(Math.abs((yEnd - yStart)));
        //Il faut distinguer les differents 4 cas de traces (de haut a gauche a en bas a droite, ...)
		rect.setX(Math.min(xStart, xEnd));
		rect.setY(Math.min(yStart, yEnd));
        gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        //ajout a la liste des dessins
        Rectangle r = new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        r.setStrokeWidth(gc.getLineWidth());
        listShapes.add(r); 
        undoHistory.push(r);
	}
	
	public void tracer_cercle() {
		//Calcul du rayon du cercle trace
		cercle.setRadius(Math.max(Math.abs(xEnd-xStart),Math.abs(yEnd-yStart)));
		//Il faut distinguer les differents 4 cas de traces
		double circle_xStart = Math.min(xEnd, xStart);
		double circle_yStart = Math.min(yEnd, yStart);
		gc.strokeOval(circle_xStart, circle_yStart, cercle.getRadius(), cercle.getRadius());
		//ajout a la liste des dessins
		Circle c = new Circle(circle_xStart, circle_yStart, cercle.getRadius());
		c.setStrokeWidth(gc.getLineWidth());
		listShapes.add(c);
		undoHistory.push(c);
	}
	
	public void tracer_polygone() {		
		//Si on a finit de tracer le polygone (on est quasiment revenu sur le point de depart)
		if ((this.xPoints.size()>1)&&(this.yPoints.size()>1)&&(Math.abs(xStart-this.xPoints.get(0))<5)&&(Math.abs(yStart-this.yPoints.get(0))<5)) {
			//On trace le polygone
			gc.strokePolygon(double_array_to_list(xPoints), double_array_to_list(yPoints), xPoints.size());
            //ajout dans le undoHistory
            Polygon poly = new Polygon();
            int x = 0;
            while (x < xPoints.size()){
                poly.getPoints().add(xPoints.get(x));
                poly.getPoints().add(yPoints.get(x));
                x++;
            }
            poly.setStrokeWidth(gc.getLineWidth());
            listShapes.add(poly);
            undoHistory.push(poly);
			//On vide les deux lists une fois qu'on a trace le polygone
			xPoints.clear();
			yPoints.clear();
		}
		else {
			//On ajoute les coordonnees du nouveau point dans le tableau
			gc.setLineWidth(3);
			gc.strokeOval(xStart, yStart, 3, 3);
			this.xPoints.add(xStart);
			this.yPoints.add(yStart);
			gc.setLineWidth(Double.parseDouble(Pinceau_Size_Champs.getText()));
		}
	}
	
	
	/***************************************************************** Fonctions de deplacement *****************************************************************/

	//Fonction qui regarde la forme qui est visee par la souris
	public void forme_visee() {
		System.out.println("deplacement");
		if(!listShapes.isEmpty()) {
			System.out.println("listeShapes not empty");
			int i =0;
			Shape s = listShapes.get(i);
			while(!s.contains(xStart, yStart) && i < listShapes.size()-1) {
				i++;
				s = listShapes.get(i);
			}
			if(i>= listShapes.size()) {
				/************ point non reconnu, reclicker ***********/
			}else {
				indexShape = i;
			}
		}else {
			/*** aucune forme a deplacer ***/
		}
	}
	
	//Fonction pour le deplacement de formes
	public void deplacement(double xE, double yE, int index ){
		if (index!=-1) {
	        Shape s = listShapes.get(index);
	       
	    	double distanceX = xE-xStart;
	    	double distanceY = yE-yStart;
	        
	        if(s.getClass() == Line.class) {
	        	Line tempLine = (Line) s;
	        	deplacer_ligne(tempLine,index,distanceX,distanceY);
	        }
	        else if(s.getClass() == Rectangle.class) {
	        	Rectangle rect = (Rectangle) s;
	        	deplacer_rectangle(rect,index,distanceX,distanceY);
	        }
	        else if(s.getClass() == Circle.class) {
	        	Circle cerc = (Circle) s;
	        	deplacer_cercle(cerc,index,distanceX,distanceY);
	        }
	        else if(s.getClass() == Polygon.class){
	    		Polygon poly= (Polygon) s;
	    		deplacer_polygone(poly,index,distanceX,distanceY);
	        }
	        refresh();
		}
	}
	
	//Fonction pour deplacer une ligne
	public void deplacer_ligne(Line l, int index, double distanceX, double distanceY) {
		double newXStart=0,newYStart=0, newXEnd=0, newYEnd=0; 
		//recuperer les nouvelles coordonnees
    	newXStart = l.getStartX()+distanceX;
    	newXEnd = l.getEndX()+distanceX;
    	newYStart = l.getStartY()+distanceY;
    	newYEnd = l.getEndY()+distanceY;
    	//creer la nouvelle ligne (on ne modifie pas directement l'ancienne car 
    	//les modifications s'appliquerront sur la liste des undos, et donc on ne pourra plus annuler le deplacement )
    	Line temp = new Line();
		temp.setStartX(newXStart);
        temp.setStartY(newYStart);
        temp.setEndX(newXEnd);
        temp.setEndY(newYEnd);
        temp.setStroke(l.getStroke());
        temp.setStrokeWidth(l.getStrokeWidth());
    	listShapes.set(index,temp);
    	if (first_time) {
        	undoHistory.push(temp);
        	first_time = false;
    	}        	
    	if (last_time) {
        	undoHistory.push(temp);
        	last_time = false;
    	}
	}
	
	//Fonction pour deplacer un rectangle
	public void deplacer_rectangle(Rectangle r, int index, double distanceX, double distanceY) {
		double newXStart=0,newYStart=0; 
    	//recuperer les nouvelles coordonnees
    	newXStart = r.getX()+distanceX;
    	newYStart = r.getY()+distanceY;
    	//creer le nouveau rectangle
    	Rectangle rect = new Rectangle(); 
		rect.setX(newXStart);
        rect.setY(newYStart);
        rect.setHeight(r.getHeight());
        rect.setWidth(r.getWidth());
        rect.setStroke(r.getStroke());
    	rect.setStrokeWidth(r.getStrokeWidth());
    	listShapes.set(index,rect);
    	if (first_time) {
        	undoHistory.push(rect);
        	first_time = false;
    	}        	
    	if (last_time) {
        	undoHistory.push(rect);
        	last_time = false;
    	}
    }
	
	//Fonction pour deplacer un cercle
	public void deplacer_cercle(Circle c, int index, double distanceX, double distanceY) {
		double newXStart=0,newYStart=0;
    	newXStart = c.getCenterX()+distanceX;
    	newYStart = c.getCenterY()+distanceY;
    	Circle cerc = new Circle();
    	//nouveau cercle a la nouvelle position
		cerc.setCenterX(newXStart);
        cerc.setCenterY(newYStart);
        cerc.setRadius(c.getRadius());
        cerc.setStroke(c.getStroke());
    	cerc.setStrokeWidth(c.getStrokeWidth());
        listShapes.set(index,cerc);
    	if (first_time) {
        	undoHistory.push(cerc);
        	first_time = false;
    	}        	
    	if (last_time) {
        	undoHistory.push(cerc);
        	last_time = false;
    	}
	}
	
	//Fonction pour deplacer un polygone
	public void deplacer_polygone(Polygon p, int index, double distanceX, double distanceY) {
		//recuperation des points + calcul des nouvelles positions
		List<Double> points = p.getPoints();
	    int          taille = p.getPoints().size() / 2;
	    double[]     pointsX = new double[taille];
	    double[]     pointsY = new double[taille];
	    int          count = 0;
	    for (int j = 0 ; j < points.size() ; j++) {
	        if (j % 2 == 0) {
	            pointsX[count] = points.get(j)+distanceX;
	        } else {
	            pointsY[count] = points.get(j)+distanceY;
	            count++;
	        }
	    }
	    //ajout dans la liste
        Polygon poly = new Polygon();
        int iter = 0;
        while (iter < taille){
            poly.getPoints().add(pointsX[iter]);
            poly.getPoints().add(pointsY[iter]);
            iter++;
        }		
        poly.setStroke(p.getStroke());
    	poly.setStrokeWidth(p.getStrokeWidth());
        listShapes.set(index,poly);
    	if (first_time) {
        	undoHistory.push(poly);
        	first_time = false;
    	}        	
    	if (last_time) {
        	undoHistory.push(poly);
        	last_time = false;
    	}
	}
	
	
	/***************************************************************** Fonctions de redimenssionnement *****************************************************************/

	
	public void redim(double xE, double yE, int index ){
		if (index!=-1) {
	        Shape s = listShapes.get(index);
	    	double distanceX = xE-xStart;
	    	double distanceY = yE-yStart;
	    	//onUndo();
	        if(s.getClass() == Line.class) {
	        	Line tempLine = (Line) s;
	        	redim_ligne(tempLine,index,distanceX,distanceY);
	        }
	        else if(s.getClass() == Rectangle.class) {
	        	Rectangle rect = (Rectangle) s;
	        	redim_rectangle(rect,index,distanceX,distanceY);
	        }
	        else if(s.getClass() == Circle.class) {
	        	Circle cerc = (Circle) s;
	        	redim_cercle(cerc,index,distanceX,distanceY);
	        }
	        else if(s.getClass() == Polygon.class){
	    		Polygon poly= (Polygon) s;
	    		redim_polygone(poly,index,distanceX,distanceY);
	        }
	        refresh();
		}
	}
	
	public void redim_ligne(Line l, int index, double distanceX, double distanceY) {
		double l_xMoy = (l.getStartX()+l.getEndX())/2;
		Line temp = new Line();
		//Si on a clique sur la premiere moitie de la ligne, alors on modifie les coordonnees du debut de ligne
		if (xStart < l_xMoy) {
			temp.setStartX(l.getStartX() + distanceX);
	        temp.setStartY(l.getStartY() + distanceY);
	        temp.setEndX(l.getEndX());
	        temp.setEndY(l.getEndY());
		}
		//Si on a clique sur la deuxieme moitie de la ligne, alors on modifie les coordonnees de fin de ligne
		else {
			temp.setStartX(l.getStartX());
	        temp.setStartY(l.getStartY());
	        temp.setEndX(l.getEndX() + distanceX);
	        temp.setEndY(l.getEndY() + distanceY);
		}
        temp.setStroke(l.getStroke());
        temp.setStrokeWidth(l.getStrokeWidth());
    	listShapes.set(index,temp);
    	if (first_time) {
        	undoHistory.push(temp);
        	first_time = false;
    	}        	
    	if (last_time) {
        	undoHistory.push(temp);
        	last_time = false;
    	}	}
	
	public void redim_rectangle(Rectangle r, int index, double distanceX, double distanceY) {
		double r_xMoy = (2*r.getX()+r.getWidth())/2;
		double r_yMoy = (2*r.getY()+r.getHeight())/2;
		Rectangle temp = new Rectangle();
		//Distinguer les 4 coins du rectangle
		//Coin haut gauche
		if (xStart<r_xMoy && yStart<r_yMoy) {
			temp.setX(r.getX() + distanceX);
			temp.setY(r.getY() + distanceY);
			temp.setWidth(r.getWidth() - distanceX);
			temp.setHeight(r.getHeight() - distanceY);
		}
		//Coin haut droit
		else if (xStart>=r_xMoy && yStart<r_yMoy) {
			temp.setX(r.getX());
			temp.setY(r.getY() + distanceY);
			temp.setWidth(r.getWidth() + distanceX);
			temp.setHeight(r.getHeight() - distanceY);
		}
		//Coin bas gauche
		else if (xStart<r_xMoy && yStart>=r_yMoy) {
			temp.setX(r.getX() + distanceX);
			temp.setY(r.getY());
			temp.setWidth(r.getWidth() - distanceX);
			temp.setHeight(r.getHeight() + distanceY);
		}
		//Coin bas droit
		else {
			temp.setX(r.getX());
			temp.setY(r.getY());
			temp.setWidth(r.getWidth() + distanceX);
			temp.setHeight(r.getHeight() + distanceY);
		}
        temp.setStroke(r.getStroke());
        temp.setStrokeWidth(r.getStrokeWidth());
    	listShapes.set(index,temp);
    	if (first_time) {
        	undoHistory.push(temp);
        	first_time = false;
    	}        	
    	if (last_time) {
        	undoHistory.push(temp);
        	last_time = false;
    	}	}
	
	
	public void redim_cercle(Circle c, int index, double distanceX, double distanceY) {
		//Distinguer les 4 coins du cercle
		//Coin haut gauche
		Circle temp = new Circle();
		double centerX = c.getCenterX()+c.getRadius()/2;
		double centerY = c.getCenterY()+c.getRadius()/2;
		if (xStart<centerX && yStart<centerY) {
			System.out.println("haut gauche");
			temp.setRadius(c.getRadius() - distanceX - distanceY);
			temp.setCenterX(centerX-temp.getRadius()/2);
			temp.setCenterY(centerY-temp.getRadius()/2);
		}
		//Coin haut droit
		else if (xStart>=centerX && yStart<centerY) {
			System.out.println("haut droit");
			temp.setRadius(c.getRadius() + distanceX - distanceY);
			temp.setCenterX(centerX-temp.getRadius()/2);
			temp.setCenterY(centerY-temp.getRadius()/2);
		}
		//Coin bas gauche
		else if (xStart<centerX && yStart>=centerY) {
			System.out.println("bas gauche");
			temp.setRadius(c.getRadius() - distanceX + distanceY);
			temp.setCenterX(centerX-temp.getRadius()/2);
			temp.setCenterY(centerY-temp.getRadius()/2);

		}
		//Coin bas droit
		else {
			System.out.println("bas droit");
			temp.setRadius(c.getRadius() + distanceX + distanceY);
			temp.setCenterX(centerX-temp.getRadius()/2);
			temp.setCenterY(centerY-temp.getRadius()/2);
		}
        temp.setStroke(c.getStroke());
        temp.setStrokeWidth(c.getStrokeWidth());
    	listShapes.set(index,temp);
    	if (first_time) {
        	undoHistory.push(temp);
        	first_time = false;
    	}        	
    	if (last_time) {
        	//undoHistory.push(temp);
        	last_time = false;
    	}	}
	
	public void redim_polygone(Polygon p, int index, double distanceX, double distanceY) {
		//recuperation des point
		List<Double> points = p.getPoints();
		int          taille = p.getPoints().size() / 2;
	    double[]     pointsX = new double[taille];
	    double[]     pointsY = new double[taille];
	    int          count = 0;
	    for (int j = 0 ; j < points.size() ; j++) {
	        if (j % 2 == 0) {
	            pointsX[count] = points.get(j);
	        } else {
	            pointsY[count] = points.get(j);
	            count++;
	        }
	    }
	    //On recherche le points du polygone le plus proche de la ou on a clique avec la souris
	    int indice=0;
	    double distance = Math.abs(pointsX[0]-xStart)+Math.abs(pointsY[0]-yStart);
	    for (int i=0; i<taille; i++) {
	    	if ( Math.abs(pointsX[i]-xStart)+Math.abs(pointsY[i]-yStart) < distance ) {
	    		indice=i;
	    		distance = Math.abs(pointsX[i]-xStart)+Math.abs(pointsY[i]-yStart);
	    	}
	    }
	    //On decale le point concerne
	    pointsX[indice]=pointsX[indice]+distanceX;
	    pointsY[indice]=pointsY[indice]+distanceY;
	    //ajout dans la liste
        Polygon poly = new Polygon();
        int iter = 0;
        while (iter < taille){
            poly.getPoints().add(pointsX[iter]);
            poly.getPoints().add(pointsY[iter]);
            iter++;
        }		
        poly.setStroke(p.getStroke());
    	poly.setStrokeWidth(p.getStrokeWidth());
        listShapes.set(index,poly);
    	if (first_time) {
        	undoHistory.push(poly);
        	first_time = false;
    	}        	
    	if (last_time) {
        	undoHistory.push(poly);
        	last_time = false;
    	}	
	}
	
	/***************************************************************** Fonctions de remplissage *****************************************************************/
	
	public void remplissage() {
		//Classe pixel qui va nous servir pour la fonction
		
		class Pixel {
			private int x;
			private int y;
			public Pixel(int x, int y) {
				this.x=x;
				this.y=y;
			}
			public int get_x() {
				return this.x;
			}
			public int get_y() {
				return this.y;
			}
			public void set_x(int x2) {
				this.x=x2;
			}
			public void set_y(int y2) {
				this.y=y2;
			}
			public Pixel copy(){
				return new Pixel(this.x,this.y);
			}
		}
		
		//Pixelwriter necessaire pour colorier les pixels individuellement
		PixelWriter p = gc.getPixelWriter();
		WritableImage snap = gc.getCanvas().snapshot(null, null);
		int x_start = (int) xStart;
		int y_start = (int) yStart;
		//La couleur de base du pixel sur lequel on a clique
		Color colorStart = snap.getPixelReader().getColor(x_start, y_start);
		//La couleur qu'on va affecter aux pixels de la zone
		Color colorChange = Color_Picker.getValue();
		
		//On declare une pile qui va nous servir pour la fonction
		Stack<Pixel> stack = new Stack<>();
		//On empile le pixel sur lequel on a clique
		stack.push(new Pixel(x_start,y_start));
		while (!stack.isEmpty()) {
			Pixel pixel = stack.pop();
			//Si le pixel depile est de la meme couleur que colorStart
			if ( (snap.getPixelReader().getColor(pixel.get_x(), pixel.get_y())).equals(colorStart) ){
				//On instancie deux copies du pixel concerne qui vont aller jusqu'a la frontiere gauche et droite
				Pixel west = pixel.copy();
				Pixel east = pixel.copy();
				//Deplacer west vers l'ouest jusqu'a� ce que couleur(west) different de colorStart
				while ( ( (snap.getPixelReader().getColor(west.get_x(), west.get_y())).equals(colorStart) ) && (west.get_x()>1)){
					west.set_x(west.get_x()-1);
				}
				//Deplacer east vers l'est jusqu'a� ce que couleur(east) different de colorStart
				while ( ( (snap.getPixelReader().getColor(east.get_x(), east.get_y())).equals(colorStart) ) && (east.get_x()<(int)Canvas.getWidth()-1) ){
					east.set_x(east.get_x()+1);
				}
				//On colorie les pixels entre les deux frontieres
				for (int i=west.get_x();i<=east.get_x();i++){
					p.setColor(i, pixel.get_y(), colorChange);
					//On empile tous les pixels adjacents verticalement aux pixels colories, si ceux ci sont de la meme couleur que colorStart et qu'ils ne sortent pas du canvas
					if ( (pixel.get_y()<Canvas.getHeight()-1) && (pixel.get_y()>0) ) {
						if ( (snap.getPixelReader().getColor(i, pixel.get_y()-1)).equals(colorStart) ) {
							stack.push(new Pixel(i,pixel.get_y()-1));
						}
						if ( (snap.getPixelReader().getColor(i, pixel.get_y()+1)).equals(colorStart) ) {
							stack.push(new Pixel(i,pixel.get_y()+1));
						}
					}
				}
				//On met a jour le snap a l'issue du coloriage
				snap = gc.getCanvas().snapshot(null, null);
			}
		}
	}
	
	
	//Fonction qui convertit une ArrayList de double en un tableau de double
	public double[] double_array_to_list (ArrayList<Double> a ) {
		double[] res = new double[a.size()];
		for (int i=0; i<a.size(); i++) {
			res[i]=a.get(i);
		}
		return res;
	}
    
	
	
	/***************************************************************** Fonctions pour les boutons *****************************************************************/
	
	//Fonction pour le bouton start 
	@FXML
    private void handle_button_action(ActionEvent event) {
    	Start_Pane.setVisible(false);
    	Whole_Grid_Pane.setVisible(true);
    }
	
	//Fonction appelee lorsqu'on appuie sur le bouton pinceau
	public void set_pinceau() {
		this.last_button = Pinceau_Button;
	}
	
	//Fonction appelee lorsqu'on appuie sur le bouton eraser
    public void set_eraser() {
    	this.last_button = Gomme_Button;
    }
    
    //Fonction appelee lorsqu'on appuie sur le bouton ligne
    public void set_line() {
    	this.last_button = Ligne_Button;
    }
    
    //Fonction appelee lorsqu'on appuie sur le bouton rectangle
    public void set_rectangle() {
    	this.last_button = Rectangle_Button;
    }    
    
    //Fonction appelee lorsqu'on appuie sur le bouton cercle
    public void set_cercle() {
    	this.last_button = Cercle_Button;
    }
    
    //Fonction appelee lorsqu'on appuie sur le bouton rectangle
    public void set_polygone() {
    	this.last_button = Polygone_Button;
    }    
    
    //Fonction appelee lorsqu'on appuie sur le bouton sceau
    public void set_sceau() {
    	this.last_button = Sceau_Button;
    }

    //Fonction appelee lorsqu'on appuie sur le bouton deplacement
    public void on_Move() {
        this.last_button = Deplacer_Button;
        /***** clicker sur un point de la forme a deplacer ******/
    }
    
    //Fonction appelee lorsqu'on appuie sur le bouton redimensionner  
    public void set_redim() {
    	this.last_button = Redim_Button;
    }
    
    //Fonction de sauvegarde d'image
    public void on_save() {
        try {
            Image snapshot = Canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("paint.png"));
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde de l'image: " + e);
        }
    }
    
    //Fonction pour quitter
    public void on_exit() {
        Platform.exit();
    }
	
    
    /***************** Menu **********************/
	
    @FXML
	protected void onCreateFile() {
		
	}
	
	@FXML
	protected void onOpenFile() {
		FileChooser openFile = new FileChooser();
        openFile.setTitle("Open File");
        File file = openFile.showOpenDialog(Canvas.getScene().getWindow());
        if (file != null) {
            try {
                InputStream io = new FileInputStream(file);
                Image img = new Image(io);
                gc.drawImage(img, 0, 0);
            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }		
	}
	
	
	@FXML
	protected void onSave(ActionEvent event) {

	}
	
	/*
	@FXML
	protected void onSave(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Save file");
	    fileChooser.setInitialFileName(".");
	    fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(
	               "Scalable Vector Graphics (*.svg)", "*.svg"));
	    File file = fileChooser.showSaveDialog(Canvas.getScene().getWindow());

	    if (file != null) {
	    	// Add .svg extension if none.
	        if (!file.getName().contains(".")) {
	        	file = new File(file.getPath() + ".svg");
	        }
	        try {
	            SVGFormat.write(Canvas, file);
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	    }
	}*/
	
	@FXML
	protected void onExit() {
	        System.out.println("Exiting program");
	        Platform.exit();
	}
	
	/***************************************************************** Fonctions associees aux boutons Undo/Redo *****************************************************************/	
	
	//Fonction appelle lorsqu'on appuie sur le bouton undo
	@FXML
	protected void onUndo() {
        if(!undoHistory.empty()){
            //recuperer la derniere forme 
            Shape removedShape = undoHistory.lastElement();
            
            //supprimer de la listes des dessins
            if(!listShapes.isEmpty())
            	listShapes.remove(listShapes.size()-1);
            
            undoShape(removedShape);
            
            //suprimer de l'historique des annulations
            undoHistory.pop();
            //redessiner le canvas 
            redessinerCanvas(); 
            //refresh();
        } else {
        	System.out.println("there is no action to undo");
        } 
	}  
	
	protected void undoShape(Shape removedShape) {
		if(removedShape.getClass() == Line.class) {
			boolean pinceau = false;
            Line tempLine = (Line) removedShape;
            //mise a jour du stroke (trait de dessin)
            tempLine.setStroke(gc.getStroke());
            tempLine.setStrokeWidth(gc.getLineWidth());
            //si la forme d'avant est une ligne aussi, on verifie si sa fin est 
            //le debut de la ligne actuelle, alors on est dans le cas d'un dessin 
            //avec pinceau
            int temptaille =  undoHistory.size()-2;
            //System.out.println("taille"+undoHistory.size()+", temptaille "+temptaille);
            while( (temptaille > 0) && (undoHistory.get(temptaille).getClass() == Line.class)) {
            	pinceau = true;
            	Line actual = (Line) undoHistory.lastElement();  
                Line precedant = (Line) undoHistory.get(temptaille);
            	if(precedant.getEndX() == actual.getStartX() &&  precedant.getEndY() == actual.getStartY()) {
            		//System.out.println("precedant.fin = "+precedant.getEndX()+","+precedant.getEndY()+" actual.debut "+actual.getStartX()+", "+actual.getStartY());
        			//ajout dans la liste des suivants
                    redoHistory.push(actual);
                    //supprimer de la listes des dessins
                    if(!listShapes.isEmpty())
                    	listShapes.remove(listShapes.size()-1);
                    //suprimer de l'historique des annulations                       
                    undoHistory.pop();
                    //System.out.println(undoHistory.toString());
                    temptaille = undoHistory.size()-2;	
        		}  
            	else {
            		break;
            	}
            }
            if(!pinceau) {
            	//ajout dans la liste des suivants
                redoHistory.push(tempLine);
            }
        }
        else if(removedShape.getClass() == Rectangle.class) {
        	Rectangle tempRect = (Rectangle) removedShape;
            tempRect.setStroke(gc.getStroke());
            tempRect.setStrokeWidth(gc.getLineWidth());
            //ajout dans la liste des suivants
            redoHistory.push(tempRect);    		
        	   
        }
        else if(removedShape.getClass() == Circle.class) {
            Circle tempCirc = (Circle) removedShape;
            tempCirc.setStroke(gc.getStroke());
            tempCirc.setStrokeWidth(gc.getLineWidth());
            redoHistory.push(tempCirc);
            
            //redoHistory.push(new Circle(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius()));
        }
        else if(removedShape.getClass() == Polygon.class) {
        	Polygon tempPolygon = (Polygon) removedShape;
        	tempPolygon.setStrokeWidth(gc.getLineWidth());
        	tempPolygon.setStroke(gc.getStroke());
            
			//System.out.println("temppoly.getPoints = "+tempPolygon.getPoints());
			redoHistory.push(tempPolygon);
			
        }
		if(!redoHistory.isEmpty()) {
			//recuperer le dernier element de la liste des suivants
	        Shape lastRedo = redoHistory.lastElement();
	        //mettre a jour le stroke
	        lastRedo.setStroke(removedShape.getStroke());
	        lastRedo.setStrokeWidth(removedShape.getStrokeWidth());
		}
          
    }
	
	//Fonction appelle lorsqu'on appuie sur le bouton redo
	@FXML
	protected void onRedo() {
        if(!redoHistory.empty()) {
            Shape shape = redoHistory.lastElement();
            //mettre a jour
            gc.setLineWidth(shape.getStrokeWidth());
            gc.setStroke(shape.getStroke());
            //ajouter a la liste des dessins
            listShapes.add(shape);
            redoShape(shape);
            //supprimer de la liste des suivants 
            redoHistory.pop();
        } else {
            System.out.println("there is no action to redo");
        }
	}
	
	protected void redoShape(Shape shape) {
		if(shape.getClass() == Line.class) {
			boolean pinceau = false;
            Line tempLine = (Line) shape;
            int temptaille =  redoHistory.size()-2;
            //System.out.println("taille"+redoHistory.size()+", temptaille "+temptaille);
            while( (temptaille > 0) && (redoHistory.get(temptaille).getClass() == Line.class)) {
            	//System.out.println("while");
            	pinceau = true;
            	Line actual = (Line) redoHistory.lastElement();  
                Line precedant = (Line) redoHistory.get(temptaille);
            	if(actual.getEndX() == precedant.getStartX() &&  actual.getEndY() == precedant.getStartY()) {
            		//System.out.println("precedant.fin = "+actual.getEndX()+","+actual.getEndY()+" actual.debut "+precedant.getStartX()+", "+precedant.getStartY());
        			//dessin
            		gc.strokeLine(actual.getStartX(), actual.getStartY(), actual.getEndX(), actual.getEndY());
            		//ajout dans la liste des suivants
                    undoHistory.push(actual);
                    //ajouter a la liste des dessins
                    listShapes.add(actual);
                    //suprimer de l'historique des annulations                       
                    redoHistory.pop();
                    //System.out.println(undoHistory.toString());
                    temptaille = redoHistory.size()-2;	
        		}  
            	else {
            		break;
            	}
            }
            if(!pinceau) {
            	gc.strokeLine(tempLine.getStartX(), tempLine.getStartY(), tempLine.getEndX(), tempLine.getEndY());
            	//ajout a la l'historique des annulations
            	undoHistory.push(tempLine);
            }
        }
        else if(shape.getClass() == Rectangle.class) {
            Rectangle tempRect = (Rectangle) shape;
            //dessiner
        	gc.strokeRect(tempRect.getX(), tempRect.getY(), tempRect.getWidth(), tempRect.getHeight());
            //ajout a la l'historique des annulations
            undoHistory.push(tempRect);	
        }
               
        else if(shape.getClass() == Circle.class) {
            Circle tempCirc = (Circle) shape;
            //gc.fillOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
            gc.strokeOval(tempCirc.getCenterX(), tempCirc.getCenterY(), tempCirc.getRadius(), tempCirc.getRadius());
            undoHistory.push(tempCirc);
        }
        /*else if(shape.getClass() == Ellipse.class) {
            Ellipse tempElps = (Ellipse) shape;
            //gc.fillOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());
            gc.strokeOval(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY());            
            undoHistory.push(new Ellipse(tempElps.getCenterX(), tempElps.getCenterY(), tempElps.getRadiusX(), tempElps.getRadiusY()));
        }*/
    	else if(shape.getClass() == Polygon.class){
    		Polygon tempPoly= (Polygon) shape;
    		List<Double> points = tempPoly.getPoints();
		    int          taille = tempPoly.getPoints().size() / 2;
		    double[]     pointsX = new double[taille];
		    double[]     pointsY = new double[taille];
		    int          count = 0;
		    for (int j = 0 ; j < points.size() ; j++) {
		        if (j % 2 == 0) {
		            pointsX[count] = points.get(j);
		        } else {
		            pointsY[count] = points.get(j);
		            count++;
		        }
		    }
		    //System.out.println("redotempPoly = "+tempPoly.getPoints());
			gc.strokePolygon(pointsX, pointsY, taille);
    		//gc.strokePolygon(double_array_to_list(xPoints), double_array_to_list(yPoints), xPoints.size());
    		
    		undoHistory.push(tempPoly);
    	}
        Shape lastUndo = undoHistory.lastElement();
        lastUndo.setStroke(gc.getStroke());
        lastUndo.setStrokeWidth(gc.getLineWidth());	 	
	}
	
	
	//Fonction qui redessine le Canvas apres avoir fait un undo
	protected void redessinerCanvas() {
		System.out.println("redessinerCanvas from undo ");
		//nettoyer le canvas
    	gc.clearRect(0, 0, Canvas.getWidth(), Canvas.getHeight());
    	
    	//redessiner les shapes
    	for(int i=0; i <  undoHistory.size(); i++) {
        	Shape shape = undoHistory.elementAt(i);
        	//System.out.println("\n"+shape.toString());
        	redessiner_shape(shape);
        	//System.out.println("redrow "+shape.toString());
    	}
	}
	
	//Fonction qui "rafrachie" le canvas apres chaque trace
	public void refresh() {
		System.out.println("refreshCanvas from listShapes");
		//nettoyer le canvas
    	gc.clearRect(0, 0, Canvas.getWidth(), Canvas.getHeight());
    	
    	for(int i=0; i <  listShapes.size(); i++) {
        	Shape shape = listShapes.get(i);
        	//System.out.println("\n"+shape.toString());
            //System.out.println("new size "+shape.getStrokeWidth());
        	redessiner_shape(shape);
        	//System.out.println("refresh"+shape.toString());
    	}
	}
	
	//Fonction qui prend une forme en parametre et qui la trace sur le canvas
	protected void redessiner_shape(Shape shape) {
		gc.setLineWidth(shape.getStrokeWidth());
        gc.setStroke(shape.getStroke());
            if(shape.getClass() == Line.class) {
                Line temp = (Line) shape;                
                gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
            }
            else if(shape.getClass() == Rectangle.class) {
                Rectangle temp = (Rectangle) shape;
                gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
            }
            else if(shape.getClass() == Circle.class) {
                Circle temp = (Circle) shape;
                gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
            }
            else if(shape.getClass() == Polygon.class) {
            	Polygon temp = (Polygon) shape;	
    			List<Double> points = temp.getPoints();
    		    int          taille = temp.getPoints().size() / 2;
    		    double[]     pointsX = new double[taille];
    		    double[]     pointsY = new double[taille];
    		    int          count = 0;
    		    for (int j = 0 ; j < points.size() ; j++) {
    		        if (j % 2 == 0) {
    		            pointsX[count] = points.get(j);
    		        } else {
    		            pointsY[count] = points.get(j);
    		            count++;
    		        }
    		    }
    			gc.strokePolygon(pointsX, pointsY, taille);
    			}
            else {
            	System.out.println("aucun match avec les Shapes");
            }
            //On remet la taille et la couleur du pinceau a la normale
            gc.setStroke(color);
            gc.setLineWidth(size);
	}  
}