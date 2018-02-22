# Sagemaker-Spark-Workshop

## Productionizing a Spark Model to serve a near real-time Inference API hosted on Amazon Sagemaker.

This workshop shows how to train a Spark Model using Amazon Sagemaker pointed to Apache Livy running on an Amazon EMR Spark cluster. The Spark model is then serialized to an Mleap bundle and hosted on Amazon Sagemaker to serve an Inference API.

This uses a small car prices dataset to predict the price of a car given certain attributes using the GBT Regressor algorithm. But this approach can be however used to host any Spark Pipeline Model on Amazon Sagemaker. As the training is done on Amazon EMR, you can use this approach to train models on very large datasets.

### Step 1: Building the Spark Pipeline model. 

The Notebook 'Predict-Car-Prices.ipynb' builds the Spark model from the dataset and saves it to an S3 location.

### Step 2: Converting the Spark model to the MLeap format.

The Notebook 'Scala-Mleap-Serialize-Model.ipynb' covers the steps to convert the Spark model to an Mleap bundle.

### Step 3: Building the Inteference Server Docker application and pushing the image to ECR..

To build the Inference Server docker application:
(please install scala, sbt and docker on your local machine)

```
$> sbt
sbt> assembly
sbt> exit
$ > ./build_and_push inference-server
```
You can navigate the the ECR (Elastic Container Registry) console to view the container image in the repository.

### Step 4: Creating the Sagemaker Model and Endpoint, and testing the Endpoint.

The notebook 'SageMaker-Model-Deployment.ipynb' covers the steps to deploy the SageMaker model and create and test the SageMaker endpoint.

