# Sagemaker-Spark-Workshop

## Productionizing a Spark Model to serve a near real-time Inference API hosted on Amazon Sagemaker.

This workshop shows how to train a Spark Model using Amazon Sagemaker pointed to Apache Livy running on an Amazon EMR Spark cluster. The Spark model is then serialized to an Mleap bundle and hosted on Amazon Sagemaker to serve an Inference API. The Inference API should be able to accept JSON data as input and return a prediction on the dataset within milliseconds.

This uses a small car prices dataset to predict the price of a car given certain attributes using the Gradient Boosting Trees Regression algorithm. But this approach can be used to host any Spark Pipeline Model on Amazon Sagemaker. As the training is done on Amazon EMR, this approach can be used to train models on very large datasets.

### Step 1: Building the Spark Pipeline model. 

The Notebook 'Predict-Car-Prices.ipynb' trains the Spark model on the dataset and saves the model to an S3 location.

### Step 2: Converting the Spark model to the MLeap format.

The Notebook 'Scala-Mleap-Serialize-Model.ipynb' covers the steps to convert the Spark model to an Mleap bundle.

### Step 3: Building the Inteference Server Docker application and pushing the image to ECR.

To build the Inference Server docker application:
(please install scala, sbt and docker on your local machine)

```
$> cd inference-server
$> sbt
sbt> assembly
sbt> exit
$ > ./build_and_push inference-server
```
You can now view the inference-server image in the repository tab on the ECR (Elastic Container Registry) console.

### Step 4: Creating the Sagemaker Model and Endpoint, and testing the Endpoint.

The notebook 'SageMaker-Model-Deployment.ipynb' covers the steps to deploy the SageMaker model and create and test the SageMaker endpoint.

