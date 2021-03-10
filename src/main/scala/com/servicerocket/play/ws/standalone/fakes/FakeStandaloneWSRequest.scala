package com.servicerocket.play.ws.standalone.fakes

import java.net.URI

import play.api.http.ContentTypes.{FORM => FormUrlEncoded, JSON => Json}
import play.api.http.HeaderNames.{CONTENT_TYPE => ContentType}
import play.api.libs.ws._

import scala.concurrent.Future
import scala.concurrent.duration.Duration

/** Fake implementation of StandaloneWSRequest for testing purposes.
  *
  * Example:
  * {{{
  * val fakeRequest = new FakeStandaloneWSRequest()
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
class FakeStandaloneWSRequest() extends StandaloneWSRequest {

  type Self = StandaloneWSRequest
  type Response = StandaloneWSResponse

  private var expectedURI: Option[URI] = None
  private var expectedMethod: Option[String] = None
  private var expectedContentType: Option[String] = None
  private var expectedBody: Option[Any] = None

  private var response: StandaloneWSResponse = FakeStandaloneWSResponse()

  /** Adds expected method to this fake class which will be checked later only if needed.
    *
    * @param toBe Expected URI.
    * @return This class mutated with new expected method.
    */
  def expectURI(toBe: URI) = {
    expectedURI = Some(toBe)
    this
  }

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
  def returning(response: StandaloneWSResponse) = {
    this.response = response
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
  def withHttpHeaders(headers: (String, String)*) = {
    this.headers = headers.toMap.view.mapValues(Seq(_)).toMap
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
  }

  /** Sets the body. This is the actual place where assertion and verification happens for expected body.
    * <p>If provided body is not equal to the expected one an error will be thrown.</p>
    *
    * @param body Body of response.
    * @return This case class intact if expectations are met.
    * @throws IllegalStateException In case of failed verifications.
    */
  def withBody[T: BodyWritable](body: T) =
    expectedBody match {
      case Some(expected) if expected != body =>
        throw new IllegalStateException(s"Unexpected body: Expected :$expected, Actual : $body")
      case _ =>
        this
    }

  def withBody(body: WSBody) = this

  def sign(calc: WSSignatureCalculator) = this

  def withAuth(username: String, password: String, scheme: WSAuthScheme) = this

  def withFollowRedirects(follow: Boolean) = this

  def withRequestTimeout(timeout: Int) = this

  def withVirtualHost(vh: String) = this

  def withProxyServer(proxyServer: WSProxyServer) = this

  override def withRequestTimeout(timeout: Duration) = this

  def execute(): Future[StandaloneWSResponse] = Future.successful(response)

  def stream(): Future[Response] = Future.successful(response)

  def withRequestFilter(filter: WSRequestFilter): StandaloneWSRequest = this

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

  var headers: Map[String, Seq[String]] = Map.empty

  override def withQueryStringParameters(parameters: (String, String)*) = this

  override def withCookies(cookie: WSCookie*) = this

  override def get(): Future[StandaloneWSResponse] = execute("GET")

  override def post[T: BodyWritable](body: T): Future[StandaloneWSResponse] = withBody(body).execute("POST")

  override def patch[T: BodyWritable](body: T): Future[StandaloneWSResponse] = withBody(body).execute("PATCH")

  override def put[T: BodyWritable](body: T): Future[StandaloneWSResponse] = withBody(body).execute("PUT")

  override def delete(): Future[StandaloneWSResponse] = execute("DELETE")

  override def head(): Future[StandaloneWSResponse] = execute("HEAD")

  override def options(): Future[StandaloneWSResponse] = execute("OPTIONS")

  override def execute(method: String): Future[StandaloneWSResponse] = Future.successful(response)

  override def uri: URI = expectedURI.get

  override def contentType: Option[String] = expectedContentType

  override def cookies: Seq[WSCookie] = Seq()

  override def withUrl(url: String): StandaloneWSRequest = this
}

/** Companion object containing helper factory methods.
  *
  * Beware that this fake class is not immutable so one cannot simply
  * call a method like `expectMethod` and get a new instance. Therefore we need factory methods and not
  * fixed values in this helper method. In order to impose immutability feeling we are using upper case
  * camel case names for factory methods which is normally used only for constants in Scala. God, forgive us!
  */
object FakeStandaloneWSRequest {

  val FakeGetRequest = new FakeStandaloneWSRequest() expectMethod "GET"
  val FakePostRequest = new FakeStandaloneWSRequest() expectMethod "POST"
  val FakePutRequest = new FakeStandaloneWSRequest() expectMethod "PUT"
  val FakeDeleteRequest = new FakeStandaloneWSRequest() expectMethod "DELETE"
  val FakePatchRequest = new FakeStandaloneWSRequest() expectMethod "PATCH"
  val FakePostFormRequest = FakePostRequest expectContentType FormUrlEncoded
  val FakePostJsonRequest = FakePostRequest expectContentType Json
  val FakePutJsonRequest = FakePutRequest expectContentType Json
  val FakePatchJsonRequest = FakePatchRequest expectContentType Json

}
