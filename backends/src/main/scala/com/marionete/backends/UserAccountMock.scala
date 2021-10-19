package com.marionete.backends

import com.twitter.finagle.{Failure, Http, Service, http}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future}

object UserAccountMock {

  def start(): Unit = {
    val port = "8898"
    println(s"Starting UserAccountMock service in port $port")
    val server = Http.serve(":" + port, service)
    Await.ready(server)
  }

  val service: Service[Request, Response] = (req: http.Request) => {
    req.path match {
      case "/marionete/user/" => processUserEndpoint(req)
      case _ =>
        Future.exception(Failure.rejected("no endpoint found"))
    }
  }

  private def processUserEndpoint(req: Request): Future[Response] = {
    req.headerMap.get("Authorization") match {
      case Some(token) =>
        println(s"[UserAccountMock] Request with $token valid. Returning account info.")
        val response =
          """
            {
                "name":"John",
                "surname":"Doe",
                "sex":"male",
                 "age": 32
             }
            |""".stripMargin
        val rep = Response(com.twitter.finagle.http.Status.Ok)
        rep.setContentString(response)
        Future.value(rep)

      case None =>
        Future.exception(Failure.rejected("Token not found in header"))
    }
  }

}
