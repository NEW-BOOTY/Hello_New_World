/*
 * Copyright © 2024 Devin B. Royal. All rights reserved.
 *
 * Quantum Fourier Transform (QFT) with Visualization and External Simulation Libraries.
 */

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.Random;

public class QuantumFourierTransformVisualization {
    public static void main(String[] args) {
        try {
            int n = 3; // Number of qubits

            // Generate and normalize random 3-qubit state
            double[] coefficients = QuantumState3Qubit.generateRandomCoefficients();
            QuantumState3Qubit.normalizeCoefficients(coefficients);
            System.out.println("Initialized 3-Qubit State:");
            QuantumState3Qubit.displayState(coefficients);

            // Perform QFT
            FieldMatrix<Complex> qftMatrix = qft(n);
            FieldMatrix<Complex> state = initializeState(n, coefficients);
            FieldMatrix<Complex> qftResult = qftMatrix.multiply(state);

            // Measure state probabilistically
            System.out.println("Post-QFT State:");
            double[] probabilities = measureState(qftResult);

            // Visualize quantum state probabilities
            visualizeQuantumState(probabilities);

            // Integration with external library (e.g., Strange or Python Qiskit)
            integrateWithExternalLibrary(coefficients, n);

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate QFT Matrix
    private static FieldMatrix<Complex> qft(int n) {
        FieldMatrix<Complex> qftMatrix = new Array2DRowFieldMatrix<>(new Complex[n][n]);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double angle = 2.0 * Math.PI * i * j / n;
                qftMatrix.setEntry(i, j, new Complex(Math.cos(angle), Math.sin(angle)));
            }
        }
        return qftMatrix;
    }

    // Initialize Quantum State
    private static FieldMatrix<Complex> initializeState(int n, double[] coefficients) {
        Complex[][] stateVector = new Complex[coefficients.length][1];
        for (int i = 0; i < coefficients.length; i++) {
            stateVector[i][0] = new Complex(coefficients[i]);
        }
        return new Array2DRowFieldMatrix<>(stateVector);
    }

    // Probabilistic Measurement Simulation
    private static double[] measureState(FieldMatrix<Complex> state) {
        double[] probabilities = new double[state.getRowDimension()];
        double totalProbability = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = Math.pow(state.getEntry(i, 0).abs(), 2);
            totalProbability += probabilities[i];
        }

        if (Math.abs(totalProbability - 1.0) > 1e-6) {
            throw new IllegalStateException("State probabilities do not sum to 1. Possible numerical error.");
        }

        System.out.println("Measurement probabilities:");
        for (int i = 0; i < probabilities.length; i++) {
            System.out.printf("|%d⟩: %.6f%n", i, probabilities[i]);
        }

        return probabilities;
    }

    // Visualize Quantum State
    private static void visualizeQuantumState(double[] probabilities) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < probabilities.length; i++) {
            dataset.addValue(probabilities[i], "Probability", "|" + i + "⟩");
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Quantum State Probabilities",
                "Basis State",
                "Probability",
                dataset);

        JFrame chartFrame = new JFrame("Quantum State Visualization");
        chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chartFrame.getContentPane().add(new ChartPanel(barChart));
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    // Integration with External Library
    private static void integrateWithExternalLibrary(double[] coefficients, int n) {
        System.out.println("Integration with external quantum libraries (placeholder).");
        // Example: Send state vector to Python via sockets or a Java-Python bridge like Py4J
        // Example: Simulate using Strange library or Qiskit Python-based tool
    }
}

/**
 * QuantumState3Qubit class to model and normalize a 3-qubit quantum state with randomized coefficients.
 */
class QuantumState3Qubit {
    public static double[] generateRandomCoefficients() {
        Random random = new Random();
        double[] coefficients = new double[8];
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] = random.nextDouble();
        }
        return coefficients;
    }

    public static void normalizeCoefficients(double[] coefficients) {
        double sumOfSquares = 0.0;
        for (double coefficient : coefficients) {
            sumOfSquares += coefficient * coefficient;
        }

        double normalizationFactor = Math.sqrt(sumOfSquares);
        for (int i = 0; i < coefficients.length; i++) {
            coefficients[i] /= normalizationFactor;
        }
    }

    public static void displayState(double[] coefficients) {
        String[] basisStates = {"|000⟩", "|001⟩", "|010⟩", "|011⟩", "|100⟩", "|101⟩", "|110⟩", "|111⟩"};

        for (int i = 0; i < coefficients.length; i++) {
            System.out.printf("%f %s%n", coefficients[i], basisStates[i]);
        }
    }
}
