package net.invalidkeyword

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    }~
    path("services") {
      get {
        complete {
          """
            |{
            |"services":[ "CLOSED", "LISTEN", "SYN RCVD", "SYN SENT","ESTAB", "FINWAIT-1", "CLOSE WAIT", "FINWAIT-2","CLOSING", "LAST-ACK", "TIME WAIT" ],
            |"edges":[
            | { "u": "CLOSED",     "v": "LISTEN",     "value": { "label": "open" } },
            | { "u": "LISTEN",     "v": "SYN RCVD",   "value": { "label": "rcv SYN" } },
            | { "u": "LISTEN",     "v": "SYN SENT",   "value": { "label": "send" } },
            | { "u": "LISTEN",     "v": "CLOSED",     "value": { "label": "close" } },
            | { "u": "SYN RCVD",   "v": "FINWAIT-1",  "value": { "label": "close" } },
            | { "u": "SYN RCVD",   "v": "ESTAB",      "value": { "label": "rcv ACK of SYN" } },
            | { "u": "SYN SENT",   "v": "SYN RCVD",   "value": { "label": "rcv SYN" } },
            | { "u": "SYN SENT",   "v": "ESTAB",      "value": { "label": "rcv SYN, ACK" } },
            | { "u": "SYN SENT",   "v": "CLOSED",     "value": { "label": "close" } },
            | { "u": "ESTAB",      "v": "FINWAIT-1",  "value": { "label": "close" } },
            | { "u": "ESTAB",      "v": "CLOSE WAIT", "value": { "label": "rcv FIN" } },
            | { "u": "FINWAIT-1",  "v": "FINWAIT-2",  "value": { "label": "rcv ACK of FIN" } },
            | { "u": "FINWAIT-1",  "v": "CLOSING",    "value": { "label": "rcv FIN" } },
            | { "u": "CLOSE WAIT", "v": "LAST-ACK",   "value": { "label": "close" } },
            | { "u": "FINWAIT-2",  "v": "TIME WAIT",  "value": { "label": "rcv FIN" } },
            | { "u": "CLOSING",    "v": "TIME WAIT",  "value": { "label": "rcv ACK of FIN" } },
            | { "u": "LAST-ACK",   "v": "CLOSED",     "value": { "label": "rcv ACK of FIN" } },
            | { "u": "TIME WAIT",  "v": "CLOSED",     "value": { "label": "timeout=2MSL" } }
            | ]
            |}
          """.stripMargin
        }
      }
    }~
    pathPrefix("static") {
      getFromDirectory("src/main/resources/static/")
    }
}

