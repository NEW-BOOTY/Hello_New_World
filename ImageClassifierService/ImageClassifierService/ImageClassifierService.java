/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 *
 * Unauthorized use, distribution, or reproduction of this software and code is strictly prohibited without written consent from the author.
 */

package com.devinroyal.imageclassifier;

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
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageClassifierService {

    private static final Logger logger = LoggerFactory.getLogger(ImageClassifierService.class);
    private static final String MODEL_DIR = "models/image-classifier";
    private static ExecutorService executor = Executors.newCachedThreadPool();

    // Prometheus Metrics
    private static final Gauge requestLatency = Gauge.build()
        .name("image_classifier_request_latency")
        .help("Time taken to process a request")
        .register();
    private static final Gauge errorCount = Gauge.build()
        .name("image_classifier_error_count")
        .help("Number of errors encountered")
        .register();

    public static void main(String[] args) {
        startMonitoring();
        startServer();
    }

    private static void startMonitoring() {
        try {
            new HTTPServer(9091); // Prometheus metrics on port 9091
            logger.info("Prometheus monitoring started on port 9091");
        } catch (IOException e) {
            logger.error("Failed to start Prometheus server", e);
        }
    }

    private static void startServer() {
        Javalin app = Javalin.create().start(7000);
        logger.info("Image Classifier Service started on port 7000");

        app.post("/predict", ctx -> {
            UploadedFile file = ctx.uploadedFile("image");
            if (file == null) {
                ctx.result("No file uploaded").status(400);
                return;
            }

            executor.submit(() -> {
                Gauge.Timer timer = requestLatency.startTimer(); // Start timing
                try {
                    String prediction = predictImage(file);
                    ctx.result("Prediction: " + prediction);
                } catch (Exception e) {
                    errorCount.inc(); // Increment error counter
                    logger.error("Prediction failed", e);
                    ctx.result("Error processing image").status(500);
                } finally {
                    timer.close(); // End timing
                }
            });
        });
    }

    private static String predictImage(UploadedFile file) throws IOException, ModelException, TranslateException {
        Path modelPath = Paths.get(MODEL_DIR);
        try (Model model = Model.newInstance(modelPath, "image-classifier-model")) {
            Image img = ImageFactory.getInstance().fromInputStream(file.getContent());
            Translator<Image, Classifications> translator = new Translator<>() {
                @Override
                public NDList processInput(TranslatorContext ctx, Image input) {
                    input.getTransform().add(new Resize(224, 224)).add(new ToTensor());
                    return new NDList(input.toNDArray(ctx.getNDManager()));
                }

                @Override
                public Classifications processOutput(TranslatorContext ctx, NDList list) {
                    List<String> labels = List.of("Cat", "Dog", "Bird", "Other");
                    return new Classifications(labels, list.singletonOrThrow());
                }
            };

            try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
                Classifications result = predictor.predict(img);
                return result.best().getClassName();
            }
        }
    }
}

/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 *
 * Unauthorized use, distribution, or reproduction of this software and code is strictly prohibited without written consent from the author.
 */


/*
 * Key Features in This Program
 *
 * Modular Design:
 * The prediction logic is isolated within predictImage() for better maintainability.
 *
 * Optimized Input Handling:
 * Images are resized to 224x224, which is standard for many pre-trained models.
 *
 * Error Handling:
 * Provides proper HTTP status codes and logs detailed error messages.
 *
 * Scalability:
 * The lightweight Javalin server can easily be extended for new endpoints.
 *
 * Logging Metrics:
 * Prometheus tracks:
 * - image_classifier_request_latency: Measures processing time per request.
 * - image_classifier_error_count: Counts prediction errors.
 *
 * Parallelized Requests:
 * Used Executors.newCachedThreadPool() for a flexible and scalable thread pool.
 *
 * Advanced Model Management:
 * Supports dynamic loading of models by allowing seamless loading from the MODEL_DIR.
 */
