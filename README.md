# sagemaker-spark-worshop
Productionizing a Spark Model to serve a Real-Time Inference API hosted on Amazon Sagemaker.

This workshop shows how to build a Spark Model using Amazon Sagemaker pointed to Apache Livy running on an Amazon EMR Spark cluster. The Spark model is then serialized to an Mleap bundle and hosted on Amazon Sagemaker to serve a RestFul Inference API.

Step 1: Building the Spark Pipeline model. 

The Notebook 'Predict-Car-Prices.ipynb' builds the Spark model and saves it to an S3 location.

Step 2: Converting the Spark model to the MLeap format.

The Notebook 'Scala-Mleap-Serialize-Model.ipynb' covers the steps to convert the Spark model to an Mleap bundle.

Step 3: Building the Inteference Server Application.

To build the Inference Server docker application:

$> sbt
sbt> assembly
sbt> exit
$ > ./build_and_push .


Step 4: Creating the Sagemaker Model and Endpoint, and testing the Endpoint.

The notebook 'SageMaker-Model-Deployment.ipynb' convers the steps to deploy the SageMaker model and create and test the SageMaker endpoint.

