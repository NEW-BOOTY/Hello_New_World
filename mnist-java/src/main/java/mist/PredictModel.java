/*
 * Copyright © 2024 Devin B. Royal.
 * All Rights Reserved.
 */

package mnist;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.translate.TranslatorFactory;
import ai.djl.util.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class PredictModel {

    public static void main(String[] args) {
        try (Model model = Model.newInstance(Paths.get("model"), "mnist_model")) {
            NDManager manager = NDManager.newBaseManager();

            // Load an example image
            Image img = Image.create(Paths.get("test_digit.png"));
            img.getTransform().add(new ToTensor());

            // Convert to NDArray
            NDArray input = img.toNDArray(manager).reshape(new Shape(1, 28, 28, 1));

            // Predict
            NDArray result = model.getPredictor(new MnistTranslator()).predict(input);
            System.out.println("Predicted Digit: " + result.argmax().toIntArray()[0]);

        } catch (IOException | ModelException | TranslateException e) {
            e.printStackTrace();
        }
    }
}
