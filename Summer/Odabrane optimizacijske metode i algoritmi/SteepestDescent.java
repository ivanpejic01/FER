
public class SteepestDescent {
	
	private static double alfa = 0.01;		
	private static double GR = (Math.sqrt(5) - 1)/2;
	
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
	
	public static double nextStepX1(double x1, double x2, double alfa) {
		double fx1 = sixHumpCamelFunctionDerivationX1(x1, x2);
		//double fx2 = sixHumpCamelFunctionDerivationX2(x1, x2);
		double nextStepX1 = x1 - alfa * fx1;
		//System.out.println("Sljedeci korak za x1 = " + nextStepX1);
		return nextStepX1;
	}
	
	public static double nextStepX2(double x1, double x2, double alfa) {
		//double fx1 = sixHumpCamelFunctionDerivationX1(x1, x2);
		double fx2 = sixHumpCamelFunctionDerivationX2(x1, x2);
		double nextStepX2 = x2 - alfa * fx2;
		//System.out.println("Sljedeci korak za x2 = " + nextStepX2);
		return nextStepX2;
	}
	
	public static double functionOfAlfa(double x1, double x2, double gradientX1, double gradientX2, double alfa) {
		double newX1 = x1 - alfa * gradientX1;
		double newX2 = x2 - alfa * gradientX2;
		return sixHumpCamelFunction(newX1, newX2);
	}
	
	public static double goldenRatioSearch(double x1, double x2, double gradientX1, double gradientX2, double a, double b, double epsilon) {
		
		double c = a + (b - a) * GR;
		double d = b - (b - a) * GR;
		
		while(Math.abs(c - d) > epsilon) {
			if (functionOfAlfa(x1, x2, gradientX1, gradientX2, c) < functionOfAlfa(x1, x2, gradientX1, gradientX2, d)) {
				
				a = d;
			} else {
				b = c;
			}

			
			c = a + (b - a) * GR;
			d = b - (b - a) * GR;
			//System.out.println("Novi a = " + a + " novi b = " + b);
		}
		
		return (a + b) / 2;
		
	}
	
	

	public static double[] findMinimumGradient(double x1, double x2, double epsilon, int maxIterations) {
		double result[] = new double[4];
		double newx1 = x1;
		double newx2 = x2;
		//System.out.println("FUNKCIJA ZA MINIMUUUUM");
		//System.out.println("X1 i X2 prije for petlje " + x1 + ","+ x2);
		for(int i = 0; i < maxIterations; i++) {
			
			double gradientX1 = sixHumpCamelFunctionDerivationX1(x1, x2);
			double gradientX2 = sixHumpCamelFunctionDerivationX2(x1, x2);
			
			alfa = goldenRatioSearch(x1, x2, gradientX1, gradientX2, 0, 1, epsilon);
			System.out.println("x(k) = (" + x1 + "," + x2 + ")");
			newx1 = nextStepX1(x1, x2, alfa);
			newx2 = nextStepX2(x1, x2, alfa);
			System.out.println("Step alpha(k) = " + alfa);
			System.out.println("x(k + 1) = (" + newx1 + "," + newx2 + ")");
			//System.out.println("vrijednost za x1, x2 = " + newx1 + ", " + newx2 + " = " + sixHumpCamelFunction(newx1, newx2));

			
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
