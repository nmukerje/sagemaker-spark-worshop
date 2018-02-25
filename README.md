# Sagemaker-Spark-Workshop

## Deploying a Spark Model to serve a near real-time Inference API hosted on Amazon Sagemaker.

This workshop shows how to train a Spark Model using Amazon Sagemaker pointed to Apache Livy running on an Amazon EMR Spark cluster. The Spark model is then serialized to an MLeap bundle and hosted on Amazon Sagemaker to serve an Inference API. The Inference API accepts JSON data as input and returns predictions on the input dataset within milliseconds.

This uses a small car prices dataset to predict the price of a car given certain attributes using the Gradient Boosted Trees Regression algorithm. But this approach can be used to host any Spark Pipeline Model on Amazon Sagemaker. As the training is done on Spark on Amazon EMR, this approach can be used to train models on very large datasets.

## Pre-Requisites

1. Launch an EMR Spark Cluster selecting Apache Livy to be installed on it.
```
aws emr create-cluster --auto-scaling-role EMR_AutoScaling_DefaultRole \
--applications Name=Hadoop Name=Livy Name=Hive Name=Spark --ebs-root-volume-size 10 \
--ec2-attributes '{"KeyName":"<EC2 keypair>","InstanceProfile":"EMR_EC2_DefaultRole","SubnetId":"<subnet id>","EmrManagedSlaveSecurityGroup":"<security group>","EmrManagedMasterSecurityGroup":"<security group>"}' \
--service-role EMR_DefaultRole --release-label emr-5.12.0 --name 'Spark Livy Cluster' \
--instance-groups '[{"InstanceCount":1,"EbsConfiguration":{"EbsBlockDeviceConfigs":[{"VolumeSpecification":{"SizeInGB":32,"VolumeType":"gp2"},"VolumesPerInstance":1}]},"InstanceGroupType":"MASTER","InstanceType":"m4.large","Name":"Master - 1"},{"InstanceCount":2,"EbsConfiguration":{"EbsBlockDeviceConfigs":[{"VolumeSpecification":{"SizeInGB":32,"VolumeType":"gp2"},"VolumesPerInstance":1}]},"InstanceGroupType":"CORE","InstanceType":"m4.large","Name":"Core - 2"}]' \
--region us-west-2
```
2. Launch a SageMaker Notebook instance in the same region and VPC.
3. Open the SageMaker Notebook instance once the status is 'InService'.

## Step 1: Training the Spark Pipeline model. 

The Notebook ['Predict-Car-Prices.ipynb'](https://github.com/nmukerje/sagemaker-spark-worshop/blob/master/Predict-Car-Prices.ipynb) trains the Spark model on the dataset and saves the model to an S3 location, converts the Spark pipeline model to an MLeap bundle and saves the MLeap Bundle to S3.

## Step 2: Building the Inteference Server Docker application and pushing the image to ECR.

To build the Inference Server docker application:
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
