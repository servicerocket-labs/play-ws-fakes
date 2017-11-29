package com.servicerocket.play.ws.fakes

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.ContentTypes.{FORM => FormUrlEncoded, JSON => Json}
import play.api.http.HeaderNames.{CONTENT_TYPE => ContentType}
import play.api.http.{ContentTypeOf, Writeable}
import play.api.libs.iteratee.Enumerator
import play.api.libs.ws._

import scala.concurrent.Future
import scala.concurrent.duration.Duration

/** Fake implementation of WSRequest for testing purposes.
  *
  * Example:
  * {{{
  * val fakeRequest = new FakeWSRequest()
  * .expectMethod(toBe = "POST")
  * .expectContentType(toBe = FormUrlEncoded)
  * .expectBody(toBe = expectedBody)
  * .returning(fakeResponse)
  * }}}
  *
  * Note that by explicitly imposing expectations on this fake, when called with `execute` or `post` or `get`,
  * it will verify these expectations automatically and will throw [[IllegalArgumentException]] if an expectation
  * fails to be verified. By ignoring expect methods and just using `returning` method you can skip this behavior.
  *
  * @author Nader Hadji Ghanbari
  */
class FakeWSRequest() extends WSRequest {

  private var expectedMethod: Option[String] = None
  private var expectedContentType: Option[String] = None
  private var expectedBody: Option[Any] = None

  private var response: WSResponse = FakeWSResponse()
  private var responseHeaders: WSResponseHeaders = FakeWSResponseHeaders()
  private var responseBody: Enumerator[Array[Byte]] = _
  private var responseBodyWithSource: Source[ByteString, _] = _

  /** Adds expected method to this fake class which will be checked later only if needed.
    *
    * @param toBe Expected method.
    * @return This class mutated with new expected method.
    */
  def expectMethod(toBe: String) = {
    expectedMethod = Some(toBe)
    this
  }

  /** Adds expected content type to this fake class which will be checked later only if needed.
    *
    * @param toBe Expected content type.
    * @return This class mutated with new expected content type.
    */
  def expectContentType(toBe: String) = {
    expectedContentType = Some(toBe)
    this
  }

  /** Adds expected body to this fake class which will be checked later only if needed.
    *
    * @param toBe Expected body.
    * @return This class mutated with new expected body.
    */
  def expectBody[B](toBe: B) = {
    expectedBody = Some(toBe)
    this
  }

  /** Sets the response to be returned as result of <code>execute()</p> method which in turn is called by <code>post()</code>
    * and <code>get()</code> methods.
    *
    * @param response Response to be returned if all expectations are passed.
    * @return A new copy of this case class with response populated.
    */
  def returning(response: WSResponse) = {
    this.response = response
    this
  }

  /** Sets the response to be returned as result of <code>execute()</p> method which in turn is called by <code>post()</code>
    * and <code>get()</code> methods.
    *
    * @param response Response to be returned if all expectations are passed.
    * @return A new copy of this case class with response populated.
    */
  def returning(response: WSResponseHeaders, body: Enumerator[Array[Byte]]) = {
    this.responseHeaders = response
    this.responseBody = body
    this
  }

  /** Sets the response to be returned as result of <code>execute()</p> method which in turn is called by <code>post()</code>
    * and <code>get()</code> methods.
    *
    * @param response Response to be returned if all expectations are passed.
    * @return A new copy of this case class with response populated.
    */
  def returning(response: WSResponseHeaders, body: Source[ByteString, NotUsed]) = {
    this.responseHeaders = response
    this.responseBodyWithSource = body
    this
  }

  /** Sets the method. This is the actual place where assertion and verification happens for expected method.
    * <p>If provided method differs from expected, an IllegalStateException will be thrown.</p>
    *
    * @param method Method to be set which will be compared with expected method.
    * @return This case class intact if expectations are met.
    * @throws IllegalStateException In case of failed verifications.
    */
  def withMethod(method: String) =
    expectedMethod match {
      case Some(expected) if expected != method =>
        throw new IllegalStateException( s"""Unexpected method: Expected :$expected, Actual : $method""")
      case _ =>
        this
    }

  /** Sets the headers. This is the actual place where assertion and verification happens for expected content type.
    * <p>If provided headers does not include the exact content type and error will be thrown.</p>
    *
    * @param headers Headers to be set.
    * @return This case class intact if expectations are met.
    * @throws IllegalStateException In case of failed verifications.
    */
  def withHeaders(headers: (String, String)*) =
    expectedContentType match {
      case None =>
        this
      case Some(expected) =>
        headers.filter(_._1 == ContentType) match {
          case (_, contentType) :: _ if expected != contentType =>
            throw new IllegalStateException(s"Unexpected content type: Expected :$expected, Actual : $contentType")
          case _ =>
            this
        }
    }

  /** Sets the body. This is the actual place where assertion and verification happens for expected body.
    * <p>If provided body is not equal to the expected one an error will be thrown.</p>
    *
    * @param body Body of response.
    * @return This case class intact if expectations are met.
    * @throws IllegalStateException In case of failed verifications.
    */
  def withBody[T](body: T)(implicit wrt: Writeable[T], ct: ContentTypeOf[T]) =
    expectedBody match {
      case Some(expected) if expected != body =>
        throw new IllegalStateException(s"Unexpected body: Expected :$expected, Actual : $body")
      case _ =>
        this
    }

  def withBody(body: WSBody) = this

  def sign(calc: WSSignatureCalculator) = this

  def withAuth(username: String, password: String, scheme: WSAuthScheme) = this

  def withQueryString(parameters: (String, String)*) = this

  def withFollowRedirects(follow: Boolean) = this

  def withRequestTimeout(timeout: Int) = this

  def withVirtualHost(vh: String) = this

  def withProxyServer(proxyServer: WSProxyServer) = this

  override def withRequestTimeout(timeout: Duration) = this

  def execute(): Future[WSResponse] = Future.successful(response)

  def stream(): Future[StreamedResponse] = Future.successful(StreamedResponse(responseHeaders, responseBodyWithSource))

  def streamWithEnumerator(): Future[(WSResponseHeaders, Enumerator[Array[Byte]])] = Future.successful(responseHeaders, responseBody)

  def withRequestFilter(filter: WSRequestFilter): WSRequest = this

  val url = ""
  val calc = None
  val queryString = Map[String, Seq[String]]()
  val followRedirects = None
  val requestTimeout = None
  val virtualHost = None
  val proxyServer = None
  val auth = None
  val body: WSBody = EmptyBody
  val method = ""
  val headers = Map[String, Seq[String]]()

}

/** Companion object containing helper factory methods.
  *
  * Beware that this fake class is not immutable so one cannot simply
  * call a method like `expectMethod` and get a new instance. Therefore we need factory methods and not
  * fixed values in this helper method. In order to impose immutability feeling we are using upper case
  * camel case names for factory methods which is normally used only for constants in Scala. God, forgive us!
  */
object FakeWSRequest {

  val FakeGetRequest = new FakeWSRequest() expectMethod "GET"
  val FakePostRequest = new FakeWSRequest() expectMethod "POST"
  val FakePutRequest = new FakeWSRequest() expectMethod "PUT"
  val FakeDeleteRequest = new FakeWSRequest() expectMethod "DELETE"
  val FakePatchRequest = new FakeWSRequest() expectMethod "PATCH"
  val FakePostFormRequest = FakePostRequest expectContentType FormUrlEncoded
  val FakePostJsonRequest = FakePostRequest expectContentType Json
  val FakePutJsonRequest = FakePutRequest expectContentType Json
  val FakePatchJsonRequest = FakePatchRequest expectContentType Json

}
