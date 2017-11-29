package com.servicerocket.play.ws.fakes

import play.api.http.Status._
import play.api.libs.ws.WSResponseHeaders

/** Fake WSResponseHeaders for testing purposes.
  *
  * The default will be a successful response headers with 200 OK status.
  */
case class FakeWSResponseHeaders(status: Int = OK) extends WSResponseHeaders {

  val headers: Map[String, Seq[String]] = Map[String, Seq[String]]()

}

object FakeWSResponseHeaders {

  def withStatus(status: Int) = FakeWSResponseHeaders(status)

  val FakeSuccessfulResponseHeaders = withStatus(OK)

}
