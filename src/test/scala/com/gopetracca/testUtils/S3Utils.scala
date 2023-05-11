package com.gopetracca.testUtils

import software.amazon.awssdk.auth.credentials.{AwsCredentialsProvider, DefaultCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{CreateBucketRequest, PutObjectRequest, S3Exception, StorageClass}

import java.io.File
import java.net.URI
import java.nio.file.Paths



object S3Utils {

  def createS3Client(region: Region, credentialsProvider: AwsCredentialsProvider, endpoint: Option[URI] = None) = {

    val s3ClientBuilder = S3Client.builder()
      .region(region)
      .credentialsProvider(credentialsProvider)
      .forcePathStyle(true)

    endpoint match {
      case Some(endpoint) => s3ClientBuilder.endpointOverride(endpoint)
      case None => // Do nothing
    }

    s3ClientBuilder.build()
  }

  def createBucket(s3Client: S3Client, bucketName: String) = {
      // Create the bucket if it doesn't already exist
      try {
        val createBucketRequest = CreateBucketRequest.builder()
          .bucket(bucketName)
          .build()
        s3Client.createBucket(createBucketRequest)
      } catch {
        case e: S3Exception =>
          println(e.awsErrorDetails().errorMessage())
          if (!e.awsErrorDetails().errorCode().equalsIgnoreCase("Bucket already exists!")) {
            println(e.awsErrorDetails().errorMessage())
          }
      }
    }

  def uploadDirectoryToS3(s3Client: S3Client, directoryPath: String, bucketName: String, prefix: String): Unit = {

    val directory = new File(directoryPath)
    if (directory.isDirectory) {
      val files = directory.listFiles()
      files.foreach(file => {
        if (file.isDirectory) {
          // Recurse into subdirectories
          val subDirectoryPath = if (prefix.isEmpty) file.getName else s"$prefix/${file.getName}"
          uploadDirectoryToS3(s3Client, file.getAbsolutePath, bucketName, subDirectoryPath)
        } else {
          // Upload individual files to S3
          val key = if (prefix.isEmpty) file.getName else s"$prefix/${file.getName}"
          val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .storageClass(StorageClass.STANDARD)
            .build()
          s3Client.putObject(request, Paths.get(file.getAbsolutePath))
        }
      })
    }
  }

  def main(args: Array[String]): Unit = {
    // Example usage
    val accessKey = "your-access-key"
    val secretKey = "your-secret-key"
    val region = Region.US_EAST_1
    val bucketName = "fake-bucket"
    val directoryPath = "src/test/resources/data/"
    val endpoint = URI.create("http://localhost:4566")

    // Create the credentials provider
    //val credentials = AwsBasicCredentials.create(accessKey, secretKey)
    //val credentialsProvider = StaticCredentialsProvider.create(credentials)
    val credentialsProvider = DefaultCredentialsProvider.create()

    // Create the S3 client
    val s3Client = createS3Client(region, credentialsProvider, Some(endpoint))

    // Create a bucket
    createBucket(s3Client, bucketName)

    // Upload the contents of a directory to S3
    uploadDirectoryToS3(s3Client, directoryPath, bucketName, "")

    // Close connection
    s3Client.close()
  }


}