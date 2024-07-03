
public class NewtonMethod {
	
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
	
	public static double sixHumpCamelFunctionDerivationX1X1 (double x1, double x2) {
		return 10 * Math.pow(x1, 4) - 25.2 * Math.pow(x1, 2) + 8;
	}
	public static double sixHumpCamelFunctionDerivationX1X2 (double x1, double x2) {
		return 1;
	}
	public static double sixHumpCamelFunctionDerivationX2X1 (double x1, double x2) {
		return 1;
	}
	public static double sixHumpCamelFunctionDerivationX2X2 (double x1, double x2) {
		return 48 * Math.pow(x2, 2) - 8;
	}
	
	public static boolean definiteMatrix(double x1, double x2) {
		double fx1x1 = sixHumpCamelFunctionDerivationX1X1(x1, x2);
		double fx1x2 = sixHumpCamelFunctionDerivationX1X2(x1, x2);
		double fx2x1 = sixHumpCamelFunctionDerivationX2X1(x1, x2);
		double fx2x2 = sixHumpCamelFunctionDerivationX2X2(x1, x2);
		
		if (fx1x2 != fx2x1) {
			return false;
		}
		
		if (fx1x1 * fx2x2 - fx1x2 * fx2x1 < 0) {
			return false;
		}
		
		if (fx1x1 < 0) {
			return false;
		}
		
		if (fx2x2 < 0) {
			return false;
		}
		return true;
	}
	
	public static double nextStepX1(double x1, double x2) {
		double fx1 = sixHumpCamelFunctionDerivationX1(x1, x2);
		double fx2 = sixHumpCamelFunctionDerivationX2(x1, x2);
		double fx1x1 = sixHumpCamelFunctionDerivationX1X1(x1, x2);
		double fx1x2 = sixHumpCamelFunctionDerivationX1X2(x1, x2);
		double fx2x1 = sixHumpCamelFunctionDerivationX2X1(x1, x2);
		double fx2x2 = sixHumpCamelFunctionDerivationX2X2(x1, x2);
		double determinant = fx1x1 * fx2x2 - fx1x2 * fx2x1;
		//System.out.println("fx1 = " + fx1 + " fx2 = " + fx2 + " fx1x1 = " + fx1x1 + " fx2x1 = " + fx2x1);
		double nextStepX1 = x1 - Math.abs((1/determinant)) * (fx2x2 * fx1 - fx1x2 * fx2);
		//System.out.println("Sljedeci korak x1 = " + nextStepX1);
		return nextStepX1;
	}
	
	public static double nextStepX2(double x1, double x2) {
		double fx1 = sixHumpCamelFunctionDerivationX1(x1, x2);
		double fx2 = sixHumpCamelFunctionDerivationX2(x1, x2);
		double fx1x1 = sixHumpCamelFunctionDerivationX1X1(x1, x2);
		double fx1x2 = sixHumpCamelFunctionDerivationX1X2(x1, x2);
		double fx2x1 = sixHumpCamelFunctionDerivationX2X1(x1, x2);
		double fx2x2 = sixHumpCamelFunctionDerivationX2X2(x1, x2);
		double determinant = fx1x1 * fx2x2 - fx1x2 * fx2x1;
		//System.out.println("fx1 = " + fx1 + " fx2 = " + fx2 + " fx1x2 = " + fx1x2 + " fx2x2 = " + fx2x2);
		double nextStepX2 = x2 - Math.abs((1/determinant)) * (- fx2x1 * fx1 + fx1x1 * fx2);
		//System.out.println("Sljedeci korak x2 = " + nextStepX2);
		return nextStepX2;
	}
	
	public static double[] findMinimum(double x1, double x2, double epsilon, int maxIterations) {
		double result[] = new double[4];
		for(int i = 0; i < maxIterations; i++) {
			
			boolean definite = definiteMatrix(x1, x2);
			if (!definite) {
				System.out.println("Matrica nije pozitivno definitna");
				return null;
			}
			double newx1 = nextStepX1(x1, x2);
			double newx2 = nextStepX2(x1, x2);
			System.out.println("x(k + 1) = (" + newx1 + "," + newx2 + ")");

			
			if (Math.abs(x1 - newx1) < epsilon && Math.abs(x2 - newx2) < epsilon) {
				result[0] = newx1;
				result[1] = newx2;
				result[2] = sixHumpCamelFunction(newx1, newx2);
				result[3] = (double)i;
				return result;
			}
			
			x1 = newx1;
			x2 = newx2;
			
		}
		
		return null;
		
		
	}
	
	
	

}
