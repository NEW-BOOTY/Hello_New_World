/*
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 */
 
import java.util.Random;

/**
 * QuantumState3Qubit class to model and normalize a 3-qubit quantum state with randomized coefficients.
 */
public class QuantumState3Qubit {

    public static void main(String[] args) {
        // Generate random coefficients for the 3-qubit state
        double[] coefficients = generateRandomCoefficients();

        // Normalize the coefficients
        normalizeCoefficients(coefficients);

        // Display the normalized state
        displayState(coefficients);
    }

    /**
     * Generate random coefficients for the 3-qubit state.
     *
     * @return an array of random coefficients
     */
    private static double[] generateRandomCoefficients() {
        Random random = new Random();
        double[] coefficients = new double[8];
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = random.nextDouble();
        }
        return coefficients;
    }

    /**
     * Normalize the coefficients of the 3-qubit state.
     *
     * @param coefficients the coefficients of the 3-qubit state
     */
    private static void normalizeCoefficients(double[] coefficients) {
        double sumOfSquares = 0.0;
        for (double coefficient : coefficients) {
            sumOfSquares += coefficient * coefficient;
        }

        double normalizationFactor = Math.sqrt(sumOfSquares);
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] /= normalizationFactor;
        }
    }

    /**
     * Display the 3-qubit state with normalized coefficients.
     *
     * @param coefficients the normalized coefficients
     */
    private static void displayState(double[] coefficients) {
        String[] basisStates = {"|000⟩", "|001⟩", "|010⟩", "|011⟩", "|100⟩", "|101⟩", "|110⟩", "|111⟩"};

        System.out.println("Normalized 3-Qubit State:");
        for (int i = 0; i < coefficients.length; i++) {
            System.out.printf("%f %s%n", coefficients[i], basisStates[i]);
        }
    }
}
