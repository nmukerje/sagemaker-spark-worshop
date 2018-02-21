package com.test.app

import ml.combust.bundle.BundleFile
import resource._
import ml.combust.mleap.runtime.MleapSupport._
import ml.combust.mleap.runtime.serialization.{BuiltinFormats, FrameReader}
import ml.combust.mleap.runtime.frame.Transformer


import org.scalatra._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class InferenceServer extends ScalatraServlet with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/ping") {
    Ok()
  }

  post("/invocations") {
      //println (request.body)
      val s= predict(request.body)
      (s)
    }

  error {
    case e: java.nio.file.NoSuchFileException => "Model not found."
    case e: spray.json.JsonParser.ParsingException => "JSON Parsing Exception : "+(e.toString)
    case e => "Unexpected Exception : "+(e.toString)
  }

  //val model_path = "jar:file:/opt/ml/model"
  val model_path = "jar:file:/mnt"


  def init_model() : Transformer = {
    val mleapTransformer = (for(bf <- managed(BundleFile(model_path+"/model.zip"))) yield {
      bf.loadMleapBundle().get.root
    }).tried.get

    return mleapTransformer
  }

  def predict( input: String ) : String = {
    val frame = FrameReader(BuiltinFormats.json).fromBytes(input.getBytes).get

    val frame2 = mleapTransformer.transform(frame).get
    //println (frame2.schema)
    val frame3=frame2.select("prediction").get
    val bytes = frame3.writer("ml.combust.mleap.json").toBytes()
    return new String(bytes.get)
  }

  lazy val mleapTransformer=init_model()

}
