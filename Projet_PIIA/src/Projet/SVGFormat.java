package Projet;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Pour ecrire le canvas en format SVG 
 **/
public class SVGFormat {

    private SVGFormat() {}

    /**
     * @param canvas Le canvas.
     * @param file Le fichier.
     * @throws IOException si l'ecriture a echou�.
     */
    
    public static void write(ObservableList<Shape> listShapes, File file) throws IOException {
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            write(listShapes, writer);
        }
    }

   public static void write(ObservableList<Shape> listShapes, Writer writer) throws IOException {
	   
	   /*String svgHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + "<svg\n"
				+ "    xmlns:svg=\"http://www.w3.org/2000/svg\"\n" + "    xmlns=\"http://www.w3.org/2000/svg\"\n"
+ ">\n";
	   writer.write(svgHeader);
	   
	   */
	   /*
	   Region viewport = (Region) canvas.getParent();
       writer.write(String.format("<svg xmlns=\"http://www.w3.org/2000/svg\"" +
                " xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.1\" width=\"%f\"" +
                " height=\"%f\">\n", viewport.getWidth(), viewport.getHeight()));
	   */
	   
       for (Shape s : listShapes) {
            if (s.getClass()==Line.class) {
                write((Line) s, writer);
            } else if (s.getClass()==Rectangle.class) {
                write((Rectangle) s, writer);
            } else if (s.getClass()==Circle.class) {
                write((Circle) s, writer);
            }
            /* non traite pour polygon
            } else if (s.getClass()==Polygon.class) {
                write((Polygon) s, writer);
            }*/
       }
       writer.write("</svg>\n");

       writer.flush();
    
    }

    private static void write(Line line, Writer writer) throws IOException {
        writer.write(String.format("<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" />\n",
                line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
    }

    private static void write(Rectangle rectangle, Writer writer) throws IOException {
        writer.write(String.format("<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" />\n",
                rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight()));
    }

    private static void write(Circle circle, Writer writer) throws IOException {
        writer.write(String.format("<circle cx=\"%f\" cy=\"%f\" rx=\"%f\" ry=\"%f\" />\n",
                circle.getCenterX(), circle.getCenterY(), circle.getRadius(),
                circle.getRadius()));
    }
    
    /* pas traite pour polygon
    private static void write(Polygon polygon, Writer writer) throws IOException {
    	
    	
    	
        writer.write(String.format("<text x=\"%f\" y=\"%f\" font-size=\"%f\">%s</text>\n",
                text.getX(), text.getY(), text.getFont().getSize(), escape(text.getText())));
    }*/

    /**
     * Escape string for XML.
     *
     * @param string The string to escape.
     * @return The escaped string.
     */
    private static String escape(String string) {
        StringBuilder builder = new StringBuilder();
        int len = string.length();
        for (int i = 0; i < len; i++) {
            char c = string.charAt(i);
            if (c > 0x7F) {
                builder
                        .append("&#")
                        .append(Integer.toString(c, 10))
                        .append(';');
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
