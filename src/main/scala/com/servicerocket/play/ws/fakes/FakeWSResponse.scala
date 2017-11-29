package com.servicerocket.play.ws.fakes

import akka.util.ByteString
import play.api.http.Status.OK
import play.api.libs.json.{JsNull, JsValue}
import play.api.libs.ws.{WSCookie, WSResponse}

import scala.xml.Elem

/** Fake WSResponse for testing purposes.
  *
  * The default will be a successful response with 200 OK status with empty body.
  *
  * @param status     Status.
  * @param statusText Status text.
  * @param body       Body.
  * @param json       Json object.
  * @author Nader Hadji Ghanbari
  */
case class FakeWSResponse(status: Int = OK,
                          statusText: String = "Success",
                          body: String = "", json:
                          JsValue = JsNull)
  extends WSResponse {

  override def underlying[T]: T = null.asInstanceOf[T]

  override def header(key: String) = allHeaders.get(key).fold(None: Option[String])(xs => Some(xs.head))

  override def cookie(name: String) = None

  override def bodyAsBytes = ByteString(body.getBytes)

  val xml: Elem = null
  val cookies: Seq[WSCookie] = Seq()
  val allHeaders: Map[String, Seq[String]] = Map[String, Seq[String]]()

}

object FakeWSResponse {

  def withStatus(status: Int) = FakeWSResponse(status)

  val FakeSuccessfulResponse = withStatus(OK)

}
