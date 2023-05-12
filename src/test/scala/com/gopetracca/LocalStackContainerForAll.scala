package com.gopetracca

import com.dimafeng.testcontainers.GenericContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.gopetracca.testUtils.S3Utils
import org.scalatest.funsuite.AnyFunSuite
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region

import java.net.URI


trait LocalStackContainerForAll extends AnyFunSuite with TestContainerForAll {

  var localstackPort: Int = _ // Will be reassigned when container starts

  override val containerDef = GenericContainer.Def(
    dockerImage = "localstack/localstack",
    exposedPorts = Seq(4566),
    env = Map("SOMEVAR" -> "SOMEVALUE"),
    command = Seq("touch /tmp/hello"),

  )

  override def afterContainersStart(container: Containers): Unit = {
    super.afterContainersStart(container)
    println("loading data into localstack S3..")

    withContainers { s3Container =>

      this.localstackPort = s3Container.mappedPort(4566)

      // s3Container.containerIpAddress}:${s3Container.mappedPort(4566)
      val region = Region.US_EAST_1
      val bucketName = "fake-bucket-testing"
      val directoryPath = "src/test/resources/data/"
      val endpoint = URI.create(s"http://localhost:${s3Container.mappedPort(4566)}")

      // Create the credentials provider
      val credentialsProvider = DefaultCredentialsProvider.create()

      // Create the S3 client
      val s3Client = S3Utils.createS3Client(region, credentialsProvider, Some(endpoint))

      // Create a bucket
      S3Utils.createBucket(s3Client, bucketName)

      // Upload the contents of a directory to S3
      S3Utils.uploadDirectoryToS3(s3Client, directoryPath, bucketName, "")

    }

  }


}