import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;
import org.tensorflow.*;

public class NeuralNetworkApp {

    private static JTextArea outputArea;
    private static JTextField inputField;
    private static JLabel modelLabel;
    private static String modelPath;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NeuralNetworkApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Neural Network Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadModelButton = new JButton("Load Model");
        modelLabel = new JLabel("No model selected");

        loadModelButton.addActionListener(e -> chooseModelFile());
        topPanel.add(loadModelButton);
        topPanel.add(modelLabel);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel inputLabel = new JLabel("Input Data: ");
        inputField = new JTextField(40);
        JButton runButton = new JButton("Run Neural Network");

        runButton.addActionListener(e -> executeNeuralNetwork());
        inputPanel.add(inputLabel);
        inputPanel.add(inputField);
        inputPanel.add(runButton);

        outputArea = new JTextArea(20, 80);
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);

        JPanel graphPanel = new JPanel();
        graphPanel.setBorder(BorderFactory.createTitledBorder("Output Visualization"));
        graphPanel.setPreferredSize(new Dimension(800, 300));

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(outputScroll, BorderLayout.SOUTH);
        frame.add(graphPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private static void chooseModelFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            modelPath = selectedFile.getAbsolutePath();
            modelLabel.setText("Model: " + selectedFile.getName());
        }
    }

    private static void executeNeuralNetwork() {
        if (modelPath == null) {
            outputArea.setText("Please select a model file first.");
            return;
        }

        String inputData = inputField.getText().trim();
        if (inputData.isEmpty()) {
            outputArea.setText("Please provide input data.");
            return;
        }

        outputArea.setText("Running neural network...");

        CompletableFuture.runAsync(() -> {
            try {
                byte[] graphDef = extractTarGz(modelPath);
                Graph graph = new Graph();
                graph.importGraphDef(graphDef);

                try (Session session = new Session(graph)) {
                    Tensor<String> inputTensor = Tensor.create(inputData.getBytes("UTF-8"), String.class);
                    Tensor<?> outputTensor = session.runner()
                            .feed("input_tensor", inputTensor)
                            .fetch("output_tensor")
                            .run()
                            .get(0);

                    String output = new String(outputTensor.bytesValue(), "UTF-8");
                    SwingUtilities.invokeLater(() -> outputArea.setText("Output: " + output));
                }

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> outputArea.setText("Error: " + ex.getMessage()));
            }
        });
    }

    private static byte[] extractTarGz(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        try (FileInputStream fis = new FileInputStream(file);
             GZIPInputStream gis = new GZIPInputStream(fis);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new IOException("Error extracting tar.gz file: " + e.getMessage());
        }
    }
}
