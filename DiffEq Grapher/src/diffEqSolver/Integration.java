package diffEqSolver;

import java.util.function.DoubleBinaryOperator;

public class Integration
{
	public static void main(String[] args)
	{
		DoubleBinaryOperator f = (x, y) -> y;
		int steps = 1 << 12;
		System.out.println(RK4(f, 0, 1, 1.0 / steps, steps)[steps] - Math.E);
	}
	public static double[] RK4(DoubleBinaryOperator f, double x, double y, double h, int steps)
	{
		double[] result = new double[steps + 1];
		result[0] = y;
		/**
		 * Floating point round off error collector, used for Kahan summation
		 */
		double yC = 0;
		for(int i = 1; i <= steps; i++)
		{
			//Runge-Kutta 4rth Order method
			double stepX = x + (i - 1) * h;
			double halfStep = h / 2;
			double k1 = f.applyAsDouble(stepX, y);
			double k2 = f.applyAsDouble(stepX + halfStep, y + halfStep * k1);
			double k3 = f.applyAsDouble(stepX + halfStep, y + halfStep * k2);
			double k4 = f.applyAsDouble(stepX + h, y + h * k3);
			double delta =  (h / 6) * (k1 + 2 * (k2 + k3) + k4) - yC; // Change in y, corrected for past lost low-order bits
			//Kahan summation for reduced round-off error for small delta but large y
			double t = y + delta;
			yC = (t - y) - delta;
			y = t;
			result[i] = y;
		}
		return result;
	}
	
	public static double adaptiveRK4(DoubleBinaryOperator f, double x, double y, double h, double targetX)
	{
		/**
		 * Floating point round off error collector, used for Kahan summation
		 */
		double yC = 0;
		double xC = 0;
		while(x < targetX)
		{
			//Runge-Kutta 4rth Order method
			
			double halfStep = h / 2;
			double k1 = f.applyAsDouble(x, y);
			double k2 = f.applyAsDouble(x + halfStep, y + halfStep * k1);
			double k3 = f.applyAsDouble(x + halfStep, y + halfStep * k2);
			double k4 = f.applyAsDouble(x + h, y + h * k3);
			double delta =  (h / 6) * (k1 + 2 * (k2 + k3) + k4) - yC; // Change in y, corrected for past lost low-order bits
			//Kahan summation for reduced round-off error for small delta but large y
			double t = y + delta;
			yC = (t - y) - delta;
			y = t;
			double deltaX = h - xC;
			double tx = x + deltaX;
			xC = (tx - x) - deltaX;
			x = tx;
			
		}
		
	}
	
	public static double RK4(DoubleBinaryOperator f, double x, double y, double h)
	{
		double halfStep = h / 2;
		double k1 = f.applyAsDouble(x, y);
		double k2 = f.applyAsDouble(x + halfStep, y + halfStep * k1);
		double k3 = f.applyAsDouble(x + halfStep, y + halfStep * k2);
		double k4 = f.applyAsDouble(x + h, y + h * k3);
		return y + (h / 6) * (k1 + 2 * (k2 + k3) + k4);
	}
	
	public static double RK4DeltaY(DoubleBinaryOperator f, double x, double y, double h)
	{
		double halfStep = h / 2;
		double k1 = f.applyAsDouble(x, y);
		double k2 = f.applyAsDouble(x + halfStep, y + halfStep * k1);
		double k3 = f.applyAsDouble(x + halfStep, y + halfStep * k2);
		double k4 = f.applyAsDouble(x + h, y + h * k3);
		return (h / 6) * (k1 + 2 * (k2 + k3) + k4);
	}
	
	
}
