
public class Main {
	
	public static void main(String[] args) {

		//minimumi
		//1. (x1,x2)=(-0.0898,0.7126) => f(x1,x2) = -1.0316
		//2. (x1,x2)=(0.0898,-0.7126) => f(x1,x2) = -1.0316
		//3. (x1,x2)=(-1.7036,0.7961) => f(x1,x2) = -0.2155
		//4. (x1,x2)=(1.7036,-0.7961) => f(x1,x2) = -0.2155
		//5. (x1,x2)=(1.6071,0.5687) => f(x1,x2) = 2.1043
		//5. (x1,x2)=(-1.6071,-0.5687) => f(x1,x2) = 2.1043
		//za (-1,1) matrica nije pozitivno definitna!!
		double x1 = 2;
		double x2 = 2;
		double epsilon = 0.0001;
		int maxIterations = 100;
		double newtonResult[] = new double[4];
		double gradientResult[] = new double[4];
		double steepestDescentResult[] = new double[4];
		
		System.out.println("NEWTONOVA");
		newtonResult = NewtonMethod.findMinimum(x1, x2, epsilon, maxIterations);
		if (newtonResult != null) {
			System.out.println("Newtonova metoda nasla je minimum u tocki (" + newtonResult[0] + "," + newtonResult[1] + ") "
					+ "i iznosi " + newtonResult[2] + ", a broj iteracija je " + (int)newtonResult[3]);
		}
		else {
			System.out.println("Newtonova metoda nije mogla naci minimum");
		}
		System.out.println();
		System.out.println("GRADIJENTNA");
		gradientResult = GradientMethod.findMinimumGradient(x1, x2, epsilon, maxIterations);
		if (gradientResult[3] == maxIterations) {
			if (Double.valueOf(gradientResult[0]).isNaN() || Double.valueOf(gradientResult[1]).isNaN()) {
				System.out.println("Gradijentna metoda dosegla je maksimalni broj iteracija i divergirala je");
			}
			else {
			System.out.println("Gradijentna metoda dosegla je maksimalni broj iteracija, a rjesenje do "
					+ "kojeg je dosla je u tocki (" + gradientResult[0] + "," + gradientResult[1] + ") i iznosi " +
					gradientResult[2]);
			}
		}
		else {
			System.out.println("Gradijentna metoda nasla je minimum u tocki (" + gradientResult[0] + "," + gradientResult[1] + ") "
					+ "i iznosi " + gradientResult[2] + ", a broj iteracija je " + (int)gradientResult[3]);
			
		}
		System.out.println();
		System.out.println("STEEPEST DESCENT");
		steepestDescentResult = SteepestDescent.findMinimumGradient(x1, x2, epsilon, maxIterations);
		if (steepestDescentResult[3] == maxIterations) {
			if (Double.valueOf(steepestDescentResult[0]).isNaN() || Double.valueOf(steepestDescentResult[1]).isNaN()) {
				System.out.println("Steepest descent metoda dosegla je maksimalni broj iteracija i divergirala je");
			}
			else {
			System.out.println("Steepest descent metoda dosegla je maksimalni broj iteracija, a rjesenje do "
					+ "kojeg je dosla je u tocki (" + steepestDescentResult[0] + "," + steepestDescentResult[1] + ") i iznosi " +
					steepestDescentResult[2]);
			}
		}
		
		else {
			System.out.println("Steepest descent metoda nasla je minimum u tocki (" + steepestDescentResult[0] + "," + steepestDescentResult[1] + ") "
					+ "i iznosi " + steepestDescentResult[2] + ", a broj iteracija je " + (int)steepestDescentResult[3]);
			
		}
			
	}

}
