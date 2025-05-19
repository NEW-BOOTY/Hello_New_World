/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 *
 * Unauthorized use, distribution, or reproduction of this software and code is strictly prohibited without written consent from the author.
 */

package com.devinroyal.mnist;

import ai.djl.Application;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.NDList;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.training.Trainer;
import ai.djl.training.dataset.Batch;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.dataset.Mnist;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.loss.Loss;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.listener.TrainingListener;
import ai.djl.util.Utils;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MnistClassifier {

    private static final Logger logger = LoggerFactory.getLogger(MnistClassifier.class);
    private static final String MODEL_DIR = "models/mnist";
    private static final Gauge modelAccuracyGauge = Gauge.build()
        .name("mnist_model_accuracy")
        .help("MNIST Model Accuracy Tracking")
        .register();
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try {
            trainModel();
            evaluateModel();
            startMonitoring();
            startServer();
        } catch (IOException | ModelException | TranslateException e) {
            logger.error("Error initializing application", e);
        }
    }

    private static void trainModel() throws IOException, ModelException {
        Mnist mnist = Mnist.builder().setSampling(128, true).build();
        mnist.prepare();

        DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
            .addEvaluator(new Accuracy())
            .optDevices(ai.djl.Device.getDevices(1))
            .addTrainingListeners(TrainingListener.Defaults.logging());

        try (Model model = Model.newInstance()) {
            model.setBlock(ai.djl.nn.Blocks.resNet18());
            try (Trainer trainer = model.newTrainer(config)) {
                trainer.initialize(new ai.djl.ndarray.types.Shape(1, 3, 28, 28));
                for (Batch batch : trainer.iterateDataset(mnist)) {
                    trainer.trainBatch(batch);
                    trainer.step();
                }
            }
            Path modelDir = Paths.get(MODEL_DIR);
            model.save(modelDir, "mnist-model");
        }
    }

    private static void evaluateModel() throws IOException, ModelException, TranslateException {
        Mnist mnist = Mnist.builder().setSampling(128, true).build();
        mnist.prepare();

        Path modelPath = Paths.get(MODEL_DIR);
        try (Model model = Model.newInstance(modelPath, "mnist-model")) {
            Trainer trainer = model.newTrainer(new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss()));
            trainer.initialize(new ai.djl.ndarray.types.Shape(1, 3, 28, 28));
            double accuracy = trainer.validate(mnist).getValidateEvaluation("Accuracy");
            modelAccuracyGauge.set(accuracy);
            logger.info("Model accuracy: " + accuracy);
        }
    }

    private static void startServer() {
        Javalin app = Javalin.create().start(7000);
        app.post("/predict", ctx -> {
            UploadedFile file = ctx.uploadedFile("image");
            if (file == null) {
                ctx.result("No file uploaded");
                return;
            }
            executor.submit(() -> {
                try {
                    processPrediction(file, ctx);
                } catch (Exception e) {
                    logger.error("Error processing prediction request", e);
                    ctx.result("Error processing image");
                }
            });
        });
    }

    private static void processPrediction(UploadedFile file, io.javalin.http.Context ctx) throws IOException, ModelException, TranslateException {
        Path modelPath = Paths.get(MODEL_DIR);
        try (Model model = Model.newInstance(modelPath, "mnist-model")) {
            Image img = ImageFactory.getInstance().fromInputStream(file.getContent());
            Translator<Image, Classifications> translator = new Translator<>() {
                @Override
                public NDList processInput(TranslatorContext ctx, Image input) {
                    input.getTransform().add(new Resize(28, 28)).add(new ToTensor());
                    return new NDList(input.toNDArray(ctx.getNDManager()));
                }
                @Override
                public Classifications processOutput(TranslatorContext ctx, NDList list) {
                    List<String> labels = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                    return new Classifications(labels, list.singletonOrThrow());
                }
            };
            Predictor<Image, Classifications> predictor = model.newPredictor(translator);
            Classifications result = predictor.predict(img);
            ctx.result("Predicted digit: " + result.best().getClassName());
        }
    }

    private static void startMonitoring() {
        try {
            new HTTPServer(9091);
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    evaluateModel();
                } catch (IOException | ModelException | TranslateException e) {
                    logger.error("Error updating model accuracy gauge", e);
                }
            }, 0, 5, TimeUnit.MINUTES);
            logger.info("Prometheus monitoring started on port 9091");
        } catch (IOException e) {
            logger.error("Error starting Prometheus HTTP server", e);
        }
    }
}


To further elevate your AI service and make it truly one of a kind, here are some advanced features and enhancements you could implement:
1. Model Optimization and Performance Enhancements

    Quantization and Pruning: Reduce model size and improve inference speed by using TensorFlow Lite or DJL model optimization techniques.
    Hardware Acceleration: Add GPU and TPU support to increase training and inference speed.
    AutoML Integration: Implement automated hyperparameter tuning and neural architecture search to dynamically optimize model performance.

2. Advanced AI Capabilities

    Ensemble Learning: Combine multiple models for more accurate predictions.
    Transfer Learning: Allow users to fine-tune the MNIST model for custom datasets.
    Self-Learning AI: Implement reinforcement learning to let the AI adapt to new handwritten styles over time.
    Explainable AI (XAI): Integrate model interpretability features to explain predictions visually.

3. Scalability and Deployment Enhancements

    Microservices Architecture: Deploy the AI model as a distributed service using Kubernetes or Docker Swarm.
    Edge AI Compatibility: Optimize the model for edge computing devices (e.g., Raspberry Pi, Jetson Nano).
    Serverless Inference: Implement inference on cloud platforms like AWS Lambda, Google Cloud Functions, or Azure Functions.
    Multi-Model Deployment: Allow dynamic model switching for different datasets and tasks.

4. Security and Compliance

    Federated Learning: Train models across decentralized devices while preserving user privacy.
    Secure Model Hosting: Implement encrypted model storage and authentication for access control.
    GDPR & HIPAA Compliance: Ensure privacy policies for handling user data.

5. Enhanced API and User Features

    REST & GraphQL API Support: Offer flexible API options for better integration.
    Web Dashboard for Visualization: Build an interactive UI using React, Vue, or Angular to track model performance.
    Multi-Language Support: Extend support beyond digits to letters, symbols, and different scripts.
    Real-Time Prediction Streaming: Implement WebSockets or Kafka for live AI-powered applications.
    Integration with Messaging Platforms: Allow predictions via Telegram, Discord, Slack, etc.

6. Self-Healing & Auto-Maintenance

    Automated Model Retraining: Periodically retrain the model with new data.
    AI-Based Error Detection & Correction: Implement anomaly detection for incorrect predictions.
    Self-Diagnosing & Auto-Healing System: Detect and fix system failures automatically.