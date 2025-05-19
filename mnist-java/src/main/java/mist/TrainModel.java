/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package mnist;

import ai.djl.Application;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.basicdataset.cv.classification.Mnist;
import ai.djl.engine.Engine;
import ai.djl.metric.Metrics;
import ai.djl.modality.Classifications;
import ai.djl.modality.Classifications.Classification;
import ai.djl.modality.Classifications.ClassificationBatch;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.ToTensorTransform;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.transform.Transforms;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Block;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.core.Flatten;
import ai.djl.nn.core.Dropout;
import ai.djl.nn.pooling.GlobalAvgPool2d;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.Batch;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.dataset.Dataset.Usage;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.dataset.Batch;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Adam;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.translate.TranslatorFactory;
import ai.djl.translate.TranslateException;
import ai.djl.translate.TranslatorFactory;
import ai.djl.util.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TrainModel {

    public static void main(String[] args) {
        try (Model model = Model.newInstance("mnist", Engine.getInstance())) {
            NDManager manager = NDManager.newBaseManager();

            // Define model architecture
            SequentialBlock block = new SequentialBlock()
                    .add(Flatten.builder().build())  // Flatten input
                    .add(Linear.builder().setUnits(128).build())
                    .add(Linear.builder().setUnits(64).build())
                    .add(Linear.builder().setUnits(10).build());

            model.setBlock(block);

            // Load MNIST dataset
            Mnist dataset = Mnist.builder()
                    .setSampling(32, true)
                    .optUsage(Usage.TRAIN)
                    .build();
            dataset.prepare();

            // Configure training
            DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                    .optOptimizer(Adam.builder().build())
                    .addEvaluator(new Accuracy())
                    .addTrainingListeners(TrainingListener.Defaults.logging());

            try (Trainer trainer = model.newTrainer(config)) {
                trainer.setMetrics(new Metrics());

                // Training loop
                int epochs = 10;
                for (int epoch = 0; epoch < epochs; epoch++) {
                    for (Batch batch : trainer.iterateDataset(dataset)) {
                        EasyTrain.trainBatch(trainer, batch);
                        trainer.step();
                        batch.close();
                    }
                }

                // Save model
                model.save(Paths.get("model"), "mnist_model");
                System.out.println("Model training completed and saved.");
            }

        } catch (IOException | ModelException | TranslateException e) {
            e.printStackTrace();
        }
    }
}
