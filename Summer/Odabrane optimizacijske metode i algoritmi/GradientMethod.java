
public class GradientMethod {
	
	private static double alfa = 0.0005;
	
	
	
	public static double sixHumpCamelFunction(double x1, double x2) {
		return (4 - 2.1 * Math.pow(x1, 2) + 
				Math.pow(x1, 4) / 3) * Math.pow(x1, 2) + 
				x1 * x2 + (- 4 + 4 * Math.pow(x2, 2)) * Math.pow(x2, 2);
	}
	
	public static double sixHumpCamelFunctionDerivationX1(double x1, double x2) {
		return 2 * (Math.pow(x1, 5) - 4.2 * Math.pow(x1, 3) + 4 * x1 + 0.5 * x2);
	}
	
	public static double sixHumpCamelFunctionDerivationX2 (double x1, double x2) {
		return x1 + 16 * Math.pow(x2, 3) - 8 * x2;
	}
	
	public static double nextStepX1(double x1, double x2) {
		double fx1 = sixHumpCamelFunctionDerivationX1(x1, x2);
		//double fx2 = sixHumpCamelFunctionDerivationX2(x1, x2);
		double nextStepX1 = x1 - alfa * fx1;
		//System.out.println("Sljedeci korak za x1 = " + nextStepX1);
		return nextStepX1;
	}
	
	public static double nextStepX2(double x1, double x2) {
		//double fx1 = sixHumpCamelFunctionDerivationX1(x1, x2);
		double fx2 = sixHumpCamelFunctionDerivationX2(x1, x2);
		double nextStepX2 = x2 - alfa * fx2;
		//System.out.println("Sljedeci korak za x2 = " + nextStepX2);
		return nextStepX2;
	}

	public static double[] findMinimumGradient(double x1, double x2, double epsilon, int maxIterations) {
		double result[] = new double[4];
		

		
		for(int i = 0; i < maxIterations; i++) {
			
			double newx1 = nextStepX1(x1, x2);
			double newx2 = nextStepX2(x1, x2);
			System.out.println("x(k + 1) = (" + newx1 + "," + newx2 + ")");

			
			if ((Math.abs((sixHumpCamelFunction(newx1, newx2) - sixHumpCamelFunction(x1, x2)) 
					/ sixHumpCamelFunction(x1, x2)) < epsilon) || 
					(Math.abs(sixHumpCamelFunctionDerivationX1(newx1, newx2)) < epsilon && 
						Math.abs(sixHumpCamelFunctionDerivationX2(newx1, newx2)) < epsilon)) {
				result[0] = newx1;
				result[1] = newx2;
				result[2] = sixHumpCamelFunction(newx1, newx2);
				result[3] = (double)i;
				return result;
			}
			
			x1 = newx1;
			x2 = newx2;
			
		}
		result[0] = x1;
		result[1] = x2;
		result[2] = sixHumpCamelFunction(x1, x2);
		result[3] = maxIterations;
		return result;
		
	}
}
