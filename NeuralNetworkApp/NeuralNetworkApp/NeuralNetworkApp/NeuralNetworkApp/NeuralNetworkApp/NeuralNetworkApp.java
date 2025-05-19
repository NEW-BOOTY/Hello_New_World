/*
 * Copyright © 2024 Devin B. Royal. All Rights Reserved.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.framework.GraphDef;
import org.tensorflow.framework.SessionDef;
import org.tensorflow.saved_model.SavedModelBundle;

public class NeuralNetworkApp {

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    // Method to create and display the GUI
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Neural Network Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        frame.add(panel, BorderLayout.NORTH);

        JButton runButton = new JButton("Run Neural Network");
        runButton.addActionListener(e -> {
            try {
                runNeuralNetwork(outputArea);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });
        panel.add(runButton);

        JButton askButton = new JButton("Ask AI Assistant");
        askButton.addActionListener(e -> {
            try {
                runAIAssistant(outputArea);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });
        panel.add(askButton);

        frame.setVisible(true);
    }

    // Method to run the neural network
    private static void runNeuralNetwork(JTextArea outputArea) throws IOException {
        outputArea.append("Running Feedforward Neural Network...\n");
        runFeedforwardNN(outputArea);
        outputArea.append("Running Convolutional Neural Network...\n");
        runConvolutionalNN(outputArea);
        outputArea.append("Running Recurrent Neural Network...\n");
        runRecurrentNN(outputArea);
        outputArea.append("Running Generative Adversarial Network...\n");
        runGenerativeAdversarialNetwork(outputArea);
    }

    // Method to run a Feedforward Neural Network (FNN)
    private static void runFeedforwardNN(JTextArea outputArea) throws IOException {
        String filePath = getUserFilePath("Select FNN Model File");
        byte[] graphDef = extractTarGz(filePath);
        try (Graph graph = new Graph()) {
            graph.importGraphDef(graphDef);
            try (Session session = new Session(graph)) {
                float[][] inputData = new float[][]{{1.0f, 2.0f, 3.0f}};
                Tensor<Float> inputTensor = Tensor.create(inputData);
                Tensor<?> outputTensor = session.runner()
                        .feed("input_tensor", inputTensor)
                        .fetch("output_tensor")
                        .run()
                        .get(0);
                outputArea.append("Feedforward NN output: " + outputTensor.toString() + "\n");
            }
        }
    }

    // Method to run a Convolutional Neural Network (CNN) for image recognition
    private static void runConvolutionalNN(JTextArea outputArea) throws IOException {
        String filePath = getUserFilePath("Select CNN Model File");
        byte[] graphDef = extractTarGz(filePath);
        try (Graph graph = new Graph()) {
            graph.importGraphDef(graphDef);
            try (Session session = new Session(graph)) {
                BufferedImage img = ImageIO.read(new File(getUserFilePath("Select Image File")));
                float[][][][] imageData = preprocessImage(img);
                Tensor<Float> inputTensor = Tensor.create(imageData);
                Tensor<?> outputTensor = session.runner()
                        .feed("input_tensor", inputTensor)
                        .fetch("output_tensor")
                        .run()
                        .get(0);
                outputArea.append("CNN output: " + outputTensor.toString() + "\n");
            }
        }
    }

    // Preprocess image with normalization and resizing
    private static float[][][][] preprocessImage(BufferedImage img) throws IOException {
        int targetHeight = 224;
        int targetWidth = 224;
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        float[][][][] imageData = new float[1][targetHeight][targetWidth][3];
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int rgb = resized.getRGB(x, y);
                imageData[0][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0f;
                imageData[0][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0f;
                imageData[0][y][x][2] = (rgb & 0xFF) / 255.0f;
            }
        }
        return imageData;
    }

    // Improved AI Assistant with dynamic input
    private static void runAIAssistant(JTextArea outputArea) throws IOException {
        String filePath = getUserFilePath("Select AI Assistant Model File");
        byte[] graphDef = extractTarGz(filePath);
        try (Graph graph = new Graph()) {
            graph.importGraphDef(graphDef);
            try (Session session = new Session(graph)) {
                String inputPrompt = JOptionPane.showInputDialog("Enter your question:");
                Tensor<String> inputTensor = Tensor.create(inputPrompt.getBytes("UTF-8"), String.class);
                Tensor<?> outputTensor = session.runner()
                        .feed("input_tensor", inputTensor)
                        .fetch("output_tensor")
                        .run()
                        .get(0);
                String response = new String(outputTensor.bytesValue(), "UTF-8");
                outputArea.append("AI Assistant: " + response + "\n");
            }
        }
    }

    // Dynamic file path selection
    private static String getUserFilePath(String dialogTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            throw new RuntimeException("File selection cancelled.");
        }
    }
}
