package com.servicerocket.play.ws.standalone.fakes

import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.Status.OK
import play.api.libs.json.{JsNull, JsValue}
import play.api.libs.ws.{StandaloneWSResponse, WSCookie}

import scala.xml.Elem

/** Fake StandaloneWSResponse for testing purposes.
  *
  * The default will be a successful response with 200 OK status with empty body.
  *
  * @param status         Status.
  * @param statusText     Status text.
  * @param withBody       Body.
  * @param withJson       Json object.
  * @param withBodySource Body source stream.
  * @author Nader Hadji Ghanbari
  */
case class FakeStandaloneWSResponse(status: Int = OK,
                                    statusText: String = "Success",
                                    withBody: Option[String] = None,
                                    withJson: JsValue = JsNull,
                                    withBodySource: Source[ByteString, _] = null,
                                    headers: Map[String, Seq[String]] = Map())
  extends StandaloneWSResponse {

  override def underlying[T]: T = null.asInstanceOf[T]

  override def header(key: String) = allHeaders.get(key).fold(None: Option[String])(xs => Some(xs.head))

  override def cookie(name: String) = None

  override def bodyAsBytes = ByteString(body.getBytes)

  val xml: Elem = null
  val cookies: Seq[WSCookie] = Seq()
  val allHeaders: Map[String, Seq[String]] = headers

  override def bodyAsSource: Source[ByteString, _] = withBodySource

  def body: String = withBody.getOrElse(withJson.toString())
}

object FakeStandaloneWSResponse {

  def withStatus(status: Int) = FakeStandaloneWSResponse(status)

  val FakeSuccessfulResponse = withStatus(OK)

}
