package diffEqSolver;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


/**
 * This class was supposed to be more involved, but got paired back as i was working on the project
 * Evaluates the solution to an equation, as equation is in the form dy/dx=, it gives the slope for some (x,y)
 * @author Hank
 */
public class EquationParser {

	private String equation;

	public EquationParser(String eq) {
		this.equation = eq;
		e = new ExpressionBuilder(equation).variables("x", "y").build();
	}

	/**
	 * evaluates the equation at x, y
	 * indirecly produces the slope at that point, if the equation equals dy/dx
	 * @param x
	 * @param y
	 * @return the solution (slope) at x,y
	 */
	private Expression e;
	public double getSlopeAtCoord(double x, double y) 
	{
		e.setVariable("x", x).setVariable("y", y);
		try
		{
			return e.evaluate();
		}
		catch(ArithmeticException e)
		{
			return Double.NaN;
		}
		
	}
}
