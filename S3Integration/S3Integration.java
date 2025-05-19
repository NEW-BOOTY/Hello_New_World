/** Copyright © 2024 Devin B. Royal. All Rights Reserved. */

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Integration {
    public static void main(String[] args) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("access_key_id", "secret_key");
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        s3Client.putObject("my-bucket", "my-object", new File("file_path"));
        System.out.println("File uploaded to S3.");
    }
}

// <!-- Maven Dependency -->
//<dependency>
//    <groupId>com.google.cloud</groupId>
//    <artifactId>google-cloud-storage</artifactId>
    //<version>1.111.0</version>
//</dependency>


//<!-- Maven Dependency -->
//<dependency>
  //  <groupId>com.amazonaws</groupId>
    //<artifactId>aws-java-sdk</artifactId>
    //<version>1.11.837</version>
//</dependency>

