package diffEqSolver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import security.ExpressionLoader;

public class Expressions 
{
	public static DoubleBinaryOperator compile(String expression)
	{
		expression = javify(expression);
		File temp;
		temp = new File("input\\Expression.java");
		if(!temp.getParentFile().exists()) temp.mkdirs();
		try(PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(temp))))
		{
			out.println("package input;");
			out.println("import java.util.function.DoubleBinaryOperator;");
			out.println("import static java.lang.Math.*;");
			out.println("public class Expression implements DoubleBinaryOperator");
			out.println("{");
			out.println("\tpublic double ln(double x)");
			out.println("\t{");
			out.println("\t\treturn log(x);");
			out.println("\t}");
			out.println("\tpublic double applyAsDouble(double x, double y)");
			out.println("\t{");
			out.printf("\t\treturn %s;\n", expression);
			out.println("\t}");
			out.println("}");
			out.flush();
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = javac.getStandardFileManager(diagnostics, null, null);
		
		Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(temp));
		
		
		List<String> options = new ArrayList<>();
		
		
		JavaCompiler.CompilationTask task = javac.getTask(
				null, 
				fileManager, 
				diagnostics, 
				options, 
				null, 
				compilationUnit);
		if(task.call())
		{
			try(ExpressionLoader classLoader = new ExpressionLoader(new URL[]{new File("./").toURI().toURL()}))
			{
	            // Load the class from the classloader by name....
	            Class<?> loadedClass = classLoader.loadClass("input.Expression");
	            // Create a new instance...
	            Object obj = loadedClass.newInstance();
	            if(obj instanceof DoubleBinaryOperator)
	            {
	            	return (DoubleBinaryOperator) obj;
	            }
	            else throw new RuntimeException();
			}
			catch(ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e)
			{
				throw new RuntimeException(e);
			}	
		}
		throw new RuntimeException();
	}

	private static String javify(String expression)
	{
		return expression;
	}
	
}