package diffEqSolver;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Math.*;
public class Integration
{
	public static void main(String[] args)
	{
		
		DoubleTrinaryOperator f = (t, x, v) -> -x / sqrt(x*x + 1) / (x*x + 1) -v / 10;
		DoubleUnaryOperator fu = x -> -x;
		double x = 0, v = 1;
		double expected = sin(1);	//order2(f, x, v, 0, 1.0 / (1 << 22));
		double temp 	= 	order2(f, x, v, 0, 1.0 / (1 << 5)) - expected;
		for(int i = 6; i <= 16; i++)
		{
			double error =  RK4O2(fu, x, v, 1.0 / (1 << i)) - expected;//order2(f, x, v, 0, 1.0 / (1 << i)) - expected;
			System.out.println(temp / error);
			temp = error;
		}
		System.out.println();
		System.out.println(temp);
		System.out.println(expected);
		
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
			double delta =  h * (k1 + 2 * (k2 + k3) + k4) / 6 - yC; // Change in y, corrected for past lost low-order bits
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
		return h * (k1 + 2 * (k2 + k3) + k4) / 6;
	}
	
	public static double order2(DoubleTrinaryOperator accel, double x, double dxdt, double t, double h)
	{
		
		for(int i = 0; i < 1.0 / h; i++)
		{
			double a = accel.apply(t, x, dxdt);
			double gx = x + h * dxdt / 2 + h * h * a / 8;
			double gv = dxdt + a * h / 2;
			
			double a2 = accel.apply(t + h / 2, gx, gv);
			
			double gx2 = x + h * ((2*a + a2)*h / 2 + 6*dxdt) / 12;//x + h * dxdt / 2 + h * h * a2 / 8;
			double gv2 = dxdt + h*(a + a2) / 4;//dxdt + a2 * h / 2;
			
			double a3 = accel.apply(t + h / 2, gx2, gv2);
			
			double gx3 = x + h * ((2*a + a3)*h + 6*dxdt) / 6;//x + h * dxdt + h * h * a3 / 2;
			double gv3 = dxdt + h*(a + a3) / 2;//dxdt + a3 * h;
			
			double a4 = accel.apply(t + h, gx3, gv3);
			
			
			//a2 = 2 * a2 - a;
			x  += dxdt * h + h*h*(a + a2 + a3) / 6;// h * (2*a*h + a2*h + 6*dxdt) / 6;
			dxdt += h * (a + 2 * (a2 + a3) + a4) / 6;
			
			t += h;
		}
		//System.out.println(sin(t));
		return x;
		
		
		
	}
	
	public static double RK4O2(DoubleUnaryOperator accel, double x, double dxdt, double h)
	{
		for(int i = 0; i < 1.0 / h; i++)
		{
			double halfStep = h / 2;
			double vk1 = accel.applyAsDouble(x);
			double rk1 = dxdt;
			
			double vk2 = accel.applyAsDouble(x + halfStep * rk1);
			double rk2 = dxdt + halfStep * vk1;
			
			double vk3 = accel.applyAsDouble(x + halfStep * rk2);
			double rk3 = dxdt + vk2 * halfStep;
			
			double vk4 = accel.applyAsDouble(x + h * rk3);
			double rk4 = dxdt + vk3 * h;
			
			dxdt += h * (vk1 + 2 *(vk2 + vk3) + vk4) / 6;
			x += h * (rk1 + 2*(rk2 + rk3) + rk4) / 6;
		}
		return x;	
	}
	
	
	
	
	
}
