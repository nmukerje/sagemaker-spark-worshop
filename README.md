# Sagemaker-Spark-Workshop

## Deploying a Spark Model to serve a real-time Inference API hosted on Amazon Sagemaker.

This workshop shows how to train a Spark Model using Amazon Sagemaker pointed to Apache Livy running on an Amazon EMR Spark cluster. The Spark model is then serialized to an MLeap bundle and hosted on Amazon Sagemaker to serve an Inference API. The Inference API accepts JSON data as input and returns predictions on the input dataset within milliseconds.

This uses a small car prices dataset to predict the price of a car given certain attributes using the Gradient Boosted Trees Regression algorithm. But this approach can be used to host any Spark Pipeline Model on Amazon Sagemaker. As the training is done on Spark on Amazon EMR, this approach can be used to train models on very large datasets.


![Overview-Diagram](https://raw.githubusercontent.com/nmukerje/sagemaker-spark-worshop/master/SageMaker-SparkOnEMR.png)

## Pre-Requisites

1. Launch an EMR Spark Cluster selecting Apache Livy to be installed on it. (A single node cluster is enough for this workshop)
```
aws emr create-cluster --auto-scaling-role EMR_AutoScaling_DefaultRole \
--applications Name=Hadoop Name=Livy Name=Hive Name=Spark --ebs-root-volume-size 10 \
--ec2-attributes '{"KeyName":"<EC2 keypair>","InstanceProfile":"EMR_EC2_DefaultRole","SubnetId":"<subnet id>","EmrManagedSlaveSecurityGroup":"<security group>","EmrManagedMasterSecurityGroup":"<security group>"}' \
--service-role EMR_DefaultRole --release-label emr-5.12.0 --name 'Spark Livy Cluster' \
--instance-groups '[{"InstanceCount":1,"InstanceGroupType":"MASTER","InstanceType":"m3.xlarge","Name":"Master - 1"}]' \
--region us-west-2
```

The cluster needs a bootstrap script to have mleap and other dependencies installed:
```
#!/bin/bash
sudo pip install boto3;
sudo pip install jip;
sudo pip install mleap;
jip install ml.combust.mleap:mleap-spark_2.11:0.9.0;
```
jip installs the java dependencies in /home/hadoop/javalib which needs to be added to the config 'spark.driver.extraClassPath' in the file '/etc/spark/conf/spark-defaults.conf'.

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
