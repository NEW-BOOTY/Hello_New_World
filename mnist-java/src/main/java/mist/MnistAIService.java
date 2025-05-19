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
import ai.djl.quantization.Quantization;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.Trainer;
import ai.djl.training.dataset.Batch;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.dataset.Mnist;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.loss.Loss;
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

public class MnistAIService {

    private static final Logger logger = LoggerFactory.getLogger(MnistAIService.class);
    private static final String MODEL_DIR = "models/mnist";
    private static final Gauge modelAccuracyGauge = Gauge.build()
        .name("mnist_model_accuracy")
        .help("MNIST Model Accuracy Tracking")
        .register();

    public static void main(String[] args) {
        try {
            trainOptimizedModel();
            startMonitoring();
            startServer();
        } catch (IOException | ModelException | TranslateException e) {
            logger.error("Error initializing application", e);
        }
    }

    private static void trainOptimizedModel() throws IOException, ModelException {
        Mnist mnist = Mnist.builder().setSampling(32, true).build();
        mnist.prepare();

        DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
            .addEvaluator(new Accuracy())
            .optDevices(ai.djl.Device.getDevices(1))
            .addTrainingListeners(TrainingListener.Defaults.logging());

        try (Model model = Model.newInstance()) {
            model.setBlock(ai.djl.nn.Blocks.batchFlattenBlock(28 * 28));
            Quantization.quantize(model);

            try (Trainer trainer = model.newTrainer(config)) {
                trainer.initialize(new ai.djl.ndarray.types.Shape(1, 28 * 28));
                for (Batch batch : trainer.iterateDataset(mnist)) {
                    trainer.trainBatch(batch);
                    trainer.step();
                }
            }

            Path modelDir = Paths.get(MODEL_DIR);
            model.save(modelDir, "mnist-model");
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
            } catch (IOException | ModelException | TranslateException e) {
                logger.error("Error processing prediction request", e);
                ctx.result("Error processing image");
            }
        });
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

    private static void evaluateModel() throws IOException, ModelException, TranslateException {
        Mnist mnist = Mnist.builder().setSampling(32, true).build();
        mnist.prepare();

        Path modelPath = Paths.get(MODEL_DIR);
        try (Model model = Model.newInstance(modelPath, "mnist-model")) {
            Trainer trainer = model.newTrainer(new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss()));
            trainer.initialize(new ai.djl.ndarray.types.Shape(1, 28 * 28));
            double accuracy = trainer.validate(mnist).getValidateEvaluation("Accuracy");
            modelAccuracyGauge.set(accuracy);
            logger.info("Model accuracy: " + accuracy);
        }
    }
}
