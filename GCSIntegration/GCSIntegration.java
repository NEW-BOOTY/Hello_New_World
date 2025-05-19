/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GCSIntegration {
    public static void main(String[] args) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of("my-bucket", "my-object");
        Blob blob = storage.get(blobId);
        Path path = Paths.get("file_path");
        blob.downloadTo(path);
        System.out.println("File downloaded from GCS.");
    }
}
