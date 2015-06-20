package input;
import java.util.function.DoubleBinaryOperator;
import static java.lang.Math.*;
public class Expression implements DoubleBinaryOperator
{
	public double ln(double x)
	{
		return log(x);
	}
	public double applyAsDouble(double x, double y)
	{
		return log(y) * x;
	}
}
