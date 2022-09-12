package caloree.logging

import doobie.util.log
import doobie.util.log.LogHandler

import scala.concurrent.duration.FiniteDuration

import org.log4s.getLogger
import org.typelevel.log4cats.LoggerFactory

object DoobieLogger {
  private val logger = getLogger

  private def onSuccess(e1: FiniteDuration, e2: FiniteDuration): String =
    s"Successful Statement Execution: " +
      s"elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (${(e1 + e2).toMillis} ms total)"

  private def onProcessFailure(e1: FiniteDuration, e2: FiniteDuration): String =
    s"Failed Resultset Processing: " +
      s"elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (failed) (${(e1 + e2).toMillis} ms total)"

  private def onExecFailure(e1: FiniteDuration): String =
    s"Failed Statement Execution: elapsed = ${e1.toMillis} ms exec (failed)"

  def apply: LogHandler = {
    LogHandler {
      case log.Success(_, _, e1, e2)              => logger.info(onSuccess(e1, e2))
      case log.ProcessingFailure(_, _, e1, e2, t) => logger.error(t)(onProcessFailure(e1, e2))
      case log.ExecFailure(_, _, e, t)            => logger.error(t)(onExecFailure(e))
    }
  }

}
