/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package com.royal.ai;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.translate.TranslatorFactory;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslatorOptions;
import ai.djl.util.Utils;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ScalableAIAPI {

    private static final Logger logger = LoggerFactory.getLogger(ScalableAIAPI.class);

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        
        app.post("/predict", ctx -> {
            UploadedFile file = ctx.uploadedFile("image");
            if (file == null) {
                ctx.status(400).result("No file uploaded");
                return;
            }
            
            try {
                byte[] imageData = file.getContent().readAllBytes();
                String prediction = predict(imageData);
                ctx.json(prediction);
            } catch (IOException | ModelException | TranslateException e) {
                logger.error("Error processing image", e);
                ctx.status(500).result("Error processing image");
            }
        });
    }
    
    private static String predict(byte[] imageData) throws IOException, ModelException, TranslateException {
        Model model = Model.newInstance("resnet50");
        Translator<Image, Classifications> translator = new CustomTranslator();
        try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
            Image img = Image.fromInputStream(Utils.toInputStream(imageData));
            Classifications classifications = predictor.predict(img);
            return classifications.toString();
        }
    }
}

class CustomTranslator implements Translator<Image, Classifications> {
    @Override
    public Batchifier getBatchifier() {
        return null;
    }

    @Override
    public Classifications processOutput(TranslatorContext ctx, ai.djl.ndarray.NDList list) {
        return new Classifications(List.of("Class1", "Class2"), list.singletonOrThrow());
    }

    @Override
    public ai.djl.ndarray.NDList processInput(TranslatorContext ctx, Image input) {
        return new ai.djl.ndarray.NDList(input.toTensor());
    }
}
