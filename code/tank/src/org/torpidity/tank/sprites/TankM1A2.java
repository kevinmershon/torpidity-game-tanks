package org.torpidity.tank.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

/**
 * This TankImage is an M1A2. The decriptions for the parts that are drawn are
 * based on my poor understanding of cars, as I know absolutely nothing about
 * tanks.
 * 
 * @author Kevin Mershon
 */
public class TankM1A2 extends TankImage {
	
	// Tester
	public static void main(String[] args) throws Exception {
		FileOutputStream fos = new FileOutputStream("C:/Documents and Settings/Administrator/Desktop/tank.png");
		TankM1A2 img = new TankM1A2(new Color[] { new Color(0, 200, 200),
				new Color(0, 170, 170), new Color(0, 140, 140) });
		ImageIO.write(img.image, "png", fos);
		fos.close();
	}
	
	/**
	 * Make a new M1A2 with the color set {c1, c2, c3}. Then compile the image
	 * into a BufferedImage so it doesn't need to be drawn again.
	 * 
	 * @param c1
	 *            the first color
	 * @param c2
	 *            the second color
	 * @param c3
	 *            the third color
	 */
	public TankM1A2(Color[] colors) {
		super(colors, "M1A2 Abram");
		center = new Point(35, 22);
		compile();
	}
	
	/**
	 * Compile the image
	 */
	private void compile() {
		image = new BufferedImage(88, 45, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.addRenderingHints(new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON));
		g2.translate(87, 0);
		g2.rotate(Math.PI / 2);
				
		/**
		 * XXX Body
		 */
		Rectangle2D.Double body = new Rectangle2D.Double(0, 15, 44, 70);
		g2.setColor(myColor);
		g2.fill(body);
		g2.setColor(Color.black);
		g2.draw(body);
		
		// Left running line
		g2.draw(new Line2D.Double(8, 15, 8, 85));
		// Right running line
		g2.draw(new Line2D.Double(36, 15, 36, 85));
		
		// Rear spoiler thing
		Rectangle2D.Double rearSpoiler = new Rectangle2D.Double(9, 84, 26, 3);
		g2.setColor(myColor3);
		g2.fill(rearSpoiler);
		g2.setColor(Color.black);
		g2.draw(rearSpoiler);
				
		// Left fender 
		g2.setColor(myColor3);
		g2.fill(new Rectangle2D.Double(0, 15, 8, 12));
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(0, 15, 8, 12));
		// Right fender
		g2.setColor(myColor3);
		g2.fill(new Rectangle2D.Double(36, 15, 8, 12));
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(36, 15, 8, 12));
		
		/**
		 * XXX Turret Base
		 */
		Polygon tb = new Polygon(new int[]{18, 4, 5, 39, 40, 26}, new int[]{32, 38, 76, 76, 38, 32}, 6);
		g2.setColor(myColor3);
		g2.fillPolygon(tb);
		g2.setColor(Color.black);
		g2.drawPolygon(tb);
		
		/**
		 * XXX Cannon Parts
		 */
		// Cannon
		Rectangle2D.Double cannon = new Rectangle2D.Double(20, 0, 4, 34);
		g2.setColor(myColor);
		g2.fill(cannon);
		g2.setColor(Color.black);
		g2.draw(cannon);
				
		// Cannon piece 1
		g2.setColor(myColor2);
		g2.fill(new Rectangle2D.Double(19, 0, 6, 2));
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(19, 0, 6, 2));		
		// Cannon piece 2
		g2.setColor(myColor2);
		g2.fill(new Rectangle2D.Double(19, 6, 6, 12));
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(19, 6, 6, 12));
		// Cannon piece 3
		g2.setColor(myColor2);
		g2.fill(new Rectangle2D.Double(19, 22, 6, 4));
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(19, 22, 6, 4));
		
		/**
		 * XXX Turret Top
		 */
		Polygon tt = new Polygon(new int[]{19, 8, 9, 35, 36, 25}, new int[]{34, 42, 74, 74, 42, 34}, 6);
		g2.setColor(myColor);
		g2.fillPolygon(tt);
		g2.setColor(Color.black);
		g2.drawPolygon(tt);
		// Connector lines
		g2.draw(new Line2D.Double(5, 39, 8, 41));
		g2.draw(new Line2D.Double(39, 39, 35, 41));
		g2.draw(new Line2D.Double(6, 75, 8, 74));
		g2.draw(new Line2D.Double(38, 75, 33, 74));
		
		/**
		 * XXX Hatches
		 */
		g2.setColor(myColor2);
		g2.fill(new Ellipse2D.Double(21, 48, 9, 9));
		g2.fill(new Ellipse2D.Double(12, 49, 8, 8));
		g2.fill(new Ellipse2D.Double(12, 43, 4, 4));
		g2.setColor(Color.black);
		g2.draw(new Ellipse2D.Double(21, 48, 9, 9));
		g2.draw(new Ellipse2D.Double(12, 49, 8, 8));
		g2.draw(new Ellipse2D.Double(12, 43, 4, 4));
		g2.draw(new Line2D.Double(26, 49, 27, 43));
		g2.draw(new Line2D.Double(14, 51, 10, 47));

		/**
		 * XXX Back panels
		*/
		g2.setColor(myColor2);
		g2.fill(new Rectangle2D.Double(13, 60, 8, 12));
		g2.fill(new Rectangle2D.Double(24, 60, 8, 12));
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(12, 60, 8, 12));
		g2.draw(new Rectangle2D.Double(24, 60, 8, 12));
		
		// Back Ridges
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(6, 76, 32, 2));
		g2.draw(new Rectangle2D.Double(6, 78, 32, 2));
		
		
		// Area
		boundingArea.add(new Area(body));
		boundingArea.add(new Area(rearSpoiler));
		boundingArea.add(new Area(cannon));
		AffineTransform rotated = new AffineTransform();
		rotated.rotate(Math.PI / 2);
		rotated.translate(-22, -52);
		boundingArea = boundingArea.createTransformedArea(rotated);
	}
	
	/**
	 * Draw this M1A2 to a graphics context at the specified coordinates and angle
	 * 
	 * @param g2 graphics
	 * @param xPos xPos
	 * @param yPos yPos
	 * @param radians angle
	 */
	public void draw(Graphics2D g2, double xPos, double yPos, double radians) {
		g2.addRenderingHints(new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON));
		
		AffineTransform orig = new AffineTransform(g2.getTransform());
		AffineTransform rotated = new AffineTransform(g2.getTransform());
		rotated.rotate(-radians, xPos, yPos);
		g2.setTransform(rotated);
		g2.drawImage(image, null, (int)xPos-center.x, (int)yPos-center.y);
		g2.setTransform(orig);
	}

}