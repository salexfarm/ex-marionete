package com.marionete.backends

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, ListeningServer, Service, http}
import com.twitter.util.Future

import scala.collection.mutable


object AccountInfoMock {

  private val statuses: mutable.Queue[http.Status] = mutable.Queue[http.Status](
    http.Status.TooManyRequests,
    http.Status.TooManyRequests,
    http.Status.Ok,
    http.Status.Ok,
    http.Status.Ok,
    http.Status.Ok,
    http.Status.Ok,
    http.Status.Ok,
    http.Status.Ok,
    http.Status.Ok
  )

  def start(): ListeningServer = {
    val port = "8899"
    println(s"Starting AccountInfoMock service in port $port")
    Http.serve(":" + port, service)
  }

  private val service: Service[Request, Response] = (req: http.Request) => {
    req.path match {
      case "/marionete/account/" => processAccountEndpoint(req)
      case _ =>
        val rep = Response(http.Status.NotFound)
        Future.value(rep)
    }
  }

  private def processAccountEndpoint(req: Request): Future[Response] = {
    req.headerMap.get("Authorization") match {
      case Some(token) =>
        println(s"[AccountInfoMock] Request with $token valid. Returning account info.")
        val response =
          """
                {
                   "account_number":12345
                }
            |""".stripMargin
        val rep = Response(statuses.dequeue())
        rep.setContentString(response)
        Future.value(rep)

      case None =>
        val rep = Response(http.Status.InternalServerError)
        Future.value(rep)
    }
  }

}