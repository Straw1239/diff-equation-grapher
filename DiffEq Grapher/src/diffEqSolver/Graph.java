package diffEqSolver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.function.DoubleBinaryOperator;

import javax.swing.JPanel;

/**
 * Produces the JPanel that represents the graph, paints onto the panel using
 * Graphics Because it paints, it might make scrolling more difficult
 * 
 * @author Hank
 *
 */

@SuppressWarnings("serial")
public class Graph extends JPanel {

	/**
	 * line length is the approximate length of the slope vectors 
	 * pixels per coord is how many pixels there are between the points (0,0) and (1,0)
	 * step length is associated with the resolution of the lines VA means
	 * vertical asymptote, this value tells the slope arrows when to point
	 * directly vertical, part of standardizing arrows length
	 */
	public static final int LINE_LENGTH = 30;
	public static final int PIXELS_PER_COORDINATE = 100;
	public static final int STEP_LENGTH = 1;
	public static final int VA_THRESHOLD_SLOPE = 30;

	/**
	 * stepLength here has to do with the horizontal component of the slope arrows, TODO: change name
	 * scale is the number of points in one dimension of one quadrant, for instance if it equals 6, the 1st quadrant goes from (0,0) to (6,6)
	 * eq is the equation
	 * equation is the instance of EquationParser that parses eq
	 */
	private int stepLength;
	private int scale;
	private String eq;
	private DoubleBinaryOperator equation;

	/**
	 * 
	 * @param xMin not used yet, will be part of non-zero-based graphs
	 * @param yMin see above
	 * @param scale the scale of the graph, explanation above
	 */
	public Graph(int xMin, int yMin, int scale) {
		setBackground(Color.WHITE);
		this.scale = 6;
	}

	public int getScale() {
		return scale;
	}

	public void setEquation(String eq) 
	{
		this.eq = eq;
		equation = Expressions.compile(eq);
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
	/**
	 * paints the JPanel, calls a metho to make the curves, then draws the axes, then the slope arrows
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Drawing solutions below slope vectors to make slopes more visible
		for (int x = -scale; x <= scale; x++) {
			for (int y = -scale; y <= scale; y++) {
				drawSolutions(x, y, g);// -3,3
			}
		}

		// Drawing the Axis
		g.setColor(Color.BLACK);
		g.drawLine(0, (scale * PIXELS_PER_COORDINATE),
				(2 * scale * PIXELS_PER_COORDINATE),
				(scale * PIXELS_PER_COORDINATE));
		g.drawLine((scale * PIXELS_PER_COORDINATE), 0,
				(scale * PIXELS_PER_COORDINATE),
				(2 * scale * PIXELS_PER_COORDINATE));

		// Drawing the slope vectors
		g.setColor(Color.BLACK);
		for (int i = 0; i < (2 * scale); i++) {
			for (int j = 0; j < (2 * scale); j++) {
				double m = equation.applyAsDouble(i - scale, -j + scale);

				int startX = i * PIXELS_PER_COORDINATE;
				int startY = j * PIXELS_PER_COORDINATE;
				int endX = i * PIXELS_PER_COORDINATE;
				int endY = j * PIXELS_PER_COORDINATE;
				// normal conditions
				if (m < VA_THRESHOLD_SLOPE && m > -VA_THRESHOLD_SLOPE) {
					stepLength = (int) (LINE_LENGTH * Math.cos(Math.atan(m)));
					endX = i * PIXELS_PER_COORDINATE + stepLength;
					endY = (int) (j * PIXELS_PER_COORDINATE - stepLength * m);
				}
				// Vertical asymptote where slopes are going upward
				else if (m >= VA_THRESHOLD_SLOPE) {
					endX = i * PIXELS_PER_COORDINATE;
					endY = j * PIXELS_PER_COORDINATE - LINE_LENGTH;
				}
				// Vertical asymptote where slopes are going downward
				else if (m < -VA_THRESHOLD_SLOPE) {
					endX = i * PIXELS_PER_COORDINATE;
					endY = j * PIXELS_PER_COORDINATE + LINE_LENGTH;
				}
				// g.drawString("" + m, startX, startY);
				// three drawlines make the line bold
				g.drawLine(startX, startY, endX, endY);
				g.drawLine(startX + 1, startY, endX + 1, endY);
				g.drawLine(startX - 1, startY, endX - 1, endY);
				g.fillOval(endX - 5, endY - 5, 10, 10);
			}
		}

	}
	
	/**
	 * Method to draw a solution curve about a point in space
	 * @param startX x coordinate of the solution curve start point
	 * @param startY y coordinate of the solution curve start point
	 * @param g Graphics object to actually do the drawing
	 */
	/*
	 * start at x,y get slope for that x,y draw a line to x + step, y + m*step x
	 * = x + step y = y + m*step
	 * 
	 * to translate a coord x,y to drawing x,y (x+3)*100 (y+3)*100
	 */
	public void drawSolutions(double startX, double startY, Graphics g) {
		int drawingX = (int) ((startX + scale) * PIXELS_PER_COORDINATE);
		int drawingY = (int) (-(startY - scale) * PIXELS_PER_COORDINATE);
		// Drawing the Solution curves
		g.setColor(Color.RED);
		double tempStartX = startX;
		double tempStartY = startY;
		// Draw Lines forward from the start point
		for (int i = 0; i < (2 * PIXELS_PER_COORDINATE * scale) / STEP_LENGTH; i++) {
			double m = 0;
			m = equation.applyAsDouble(tempStartX, tempStartY);
			drawingX = (int) ((tempStartX + scale) * PIXELS_PER_COORDINATE);
			drawingY = (int) (-(tempStartY - scale) * PIXELS_PER_COORDINATE);
			g.drawLine(drawingX, drawingY, drawingX + STEP_LENGTH,
					(int) (drawingY - m * STEP_LENGTH));
			tempStartX += STEP_LENGTH / (double) PIXELS_PER_COORDINATE;
			tempStartY += (m) / (double) PIXELS_PER_COORDINATE;
		}
		// Draw Lines backward from start point
		for (int i = 0; i < (2 * PIXELS_PER_COORDINATE * scale) / STEP_LENGTH; i++) {
			double m = 0;
			m = equation.applyAsDouble(startX
					- (STEP_LENGTH / PIXELS_PER_COORDINATE), startY
					- (m / (double) PIXELS_PER_COORDINATE));
			drawingX = (int) ((startX + scale) * PIXELS_PER_COORDINATE);
			drawingY = (int) (-(startY - scale) * PIXELS_PER_COORDINATE);
			g.drawLine(drawingX + STEP_LENGTH, (int) (drawingY - m
					* STEP_LENGTH), drawingX, drawingY);
			startX -= (STEP_LENGTH / (double) PIXELS_PER_COORDINATE);
			startY -= m / (double) PIXELS_PER_COORDINATE;
		}
	}

	//vestige of scrolling, not used yet
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);

	}

}