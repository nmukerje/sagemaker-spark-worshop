# Sagemaker-Spark-Workshop

## Deploying a Spark Model to serve a real-time Inference API hosted on Amazon Sagemaker.

This workshop shows how to train a Spark Model using Amazon Sagemaker pointed to Apache Livy running on an Amazon EMR Spark cluster. The Spark model is then serialized to an MLeap bundle and hosted on Amazon Sagemaker to serve an Inference API. The Inference API accepts JSON data as input and returns predictions on the input dataset within milliseconds.

This uses a small car prices dataset to predict the price of a car given certain attributes using the Gradient Boosted Trees Regression algorithm. But this approach can be used to host any Spark Pipeline Model on Amazon Sagemaker. As the training is done on Spark on Amazon EMR, this approach can be used to train models on very large datasets.


![Overview-Diagram](https://raw.githubusercontent.com/nmukerje/sagemaker-spark-worshop/master/SageMaker-SparkOnEMR.png)

## Pre-Requisites

1. Launch an EMR Spark Cluster selecting Apache Livy to be installed on it. (A single node cluster is enough for this workshop)
```
aws emr create-cluster --applications Name=Hadoop Name=Spark Name=Hive Name=Livy \
--ec2-attributes  '{"KeyName":"<EC2 keypair>","InstanceProfile":"EMR_EC2_DefaultRole","SubnetId":"<VPC Subnet>"}' \
--release-label emr-5.11.1  --instance-type m3.xlarge --instance-count 1  \
--configurations '[{"Classification":"spark-defaults","Properties":{"spark.driver.extraClassPath":"/home/hadoop/javalib/*:/usr/lib/hadoop-lzo/lib/*:/usr/lib/hadoop/hadoop-aws.jar:/usr/share/aws/aws-java-sdk/*:/usr/share/aws/emr/emrfs/conf:/usr/share/aws/emr/emrfs/lib/*:/usr/share/aws/emr/emrfs/auxlib/*:/usr/share/aws/emr/security/conf:/usr/share/aws/emr/security/lib/*:/usr/share/aws/hmclient/lib/aws-glue-datacatalog-spark-client.jar:/usr/share/java/Hive-JSON-Serde/hive-openx-serde.jar:/usr/share/aws/sagemaker-spark-sdk/lib/sagemaker-spark-sdk.jar"},"Configurations":[]}]' \
--auto-scaling-role EMR_AutoScaling_DefaultRole \
--bootstrap-actions '[{"Path":"<Bootstrap script path in S3>","Name":"Custom action"}]'  \
--service-role EMR_DefaultRole --name 'Spark Livy cluster' --region us-west-2
```

The cluster needs a bootstrap script to have mleap and other dependencies installed:
```
#!/bin/bash
sudo pip install boto3;
sudo pip install jip;
sudo pip install mleap;
(cd /home/hadoop && /usr/local/bin/jip install ml.combust.mleap:mleap-spark_2.11:0.9.0)
```
jip installs the java dependencies in /home/hadoop/javalib which is added to the config 'spark.driver.extraClassPath' in the file '/etc/spark/conf/spark-defaults.conf' by the custom configuration specific in the cluster launch step.

2. Launch a SageMaker Notebook instance in the same region and VPC.
3. Open the SageMaker Notebook instance once the status is 'InService' and click on 'Upload' to upload the notebook below in Step #1.

## Step 1: Training the Spark Pipeline model. 

The Notebook ['Predict-Car-Prices.ipynb'](https://github.com/nmukerje/sagemaker-spark-worshop/blob/master/Predict-Car-Prices.ipynb) trains the Spark model on the dataset and saves the model to an S3 location, converts the Spark pipeline model to an MLeap bundle and saves the MLeap Bundle to S3.

## Step 2: Building the Inteference Server Docker application and pushing the image to ECR.

The Inference Server Docker application uses a light-weight web micro-framework called ['Scalatra'](http://scalatra.org/) to serve the http application. To build and deploy the Inference Server docker application:
(please install scala, sbt and docker on your local machine)

```
$> cd inference-server
$> sbt assembly
$> ../build_and_push inference-server
```
You can now view the inference-server image in the repository tab on the ECR (Elastic Container Registry) console.

## Step 3: Creating the Sagemaker Model and Endpoint, and testing the Endpoint.

The notebook ['SageMaker-Model-Deployment.ipynb'](https://github.com/nmukerje/sagemaker-spark-worshop/blob/master/SageMaker-Model-Deployment.ipynb) covers the steps to deploy the SageMaker model and create and test the SageMaker endpoint.

The SageMaker Endpoint supports 2 http endpoints:
* /ping - used for healthchecks by SageMaker
* /invocations - returns predictions when a JSON payload is POSTed.
