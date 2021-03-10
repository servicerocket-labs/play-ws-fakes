package com.servicerocket.play.ws.fakes

import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.Status.OK
import play.api.libs.json.{JsNull, JsValue}
import play.api.libs.ws.{WSCookie, WSResponse}

import java.net.URI
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
                          body: String = "",
                          json: JsValue = JsNull,
                          source: Source[ByteString, _] = null,
                          headers: Map[String, Seq[String]] = Map())
  extends WSResponse {

  private var expectedURI: Option[URI] = None

  /** Adds expected method to this fake class which will be checked later only if needed.
    *
    * @param toBe Expected URI.
    * @return This class mutated with new expected method.
    */
  def expectURI(toBe: URI): FakeWSResponse = {
    expectedURI = Some(toBe)
    this
  }

  override def underlying[T]: T = null.asInstanceOf[T]

  override def header(key: String) = allHeaders.get(key).fold(None: Option[String])(xs => Some(xs.head))

  override def cookie(name: String) = None

  override def bodyAsBytes = ByteString(body.getBytes)

  val xml: Elem = null
  val cookies: Seq[WSCookie] = Seq()
  val allHeaders: Map[String, Seq[String]] = headers

  override def bodyAsSource: Source[ByteString, _] = source

  override def uri: URI = expectedURI.get
}

object FakeWSResponse {

  def withStatus(status: Int) = FakeWSResponse(status)

  val FakeSuccessfulResponse = withStatus(OK)

}
