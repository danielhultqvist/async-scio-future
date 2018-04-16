package example

import java.util.function

import com.spotify.scio.transforms.BaseAsyncDoFn

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.collection.JavaConverters._

abstract class FutureAsyncDoFn[InputT, OutputT, ResourceT]
    extends BaseAsyncDoFn[InputT, OutputT, ResourceT, Future[OutputT]] {

  override def waitForFutures(
      futures: java.lang.Iterable[Future[OutputT]]): Unit = {
    val future: Future[Iterable[OutputT]] = Future.sequence(futures.asScala)
    Await.ready(future, 60.seconds)
  }

  override def addCallback(
      future: Future[OutputT],
      onSuccess: function.Function[OutputT, Void],
      onFailure: function.Function[Throwable, Void]): Future[OutputT] = {
    future.onSuccess[Void](successCallback(onSuccess))
    future.onFailure[Void](failureCallback(onFailure))
    future
  }

  private def successCallback(callback: function.Function[OutputT, Void]) =
    new PartialFunction[OutputT, Void] {
      override def isDefinedAt(x: OutputT): Boolean = true

      override def apply(value: OutputT): Void = callback.apply(value)
    }

  private def failureCallback(callback: function.Function[Throwable, Void]) =
    new PartialFunction[Throwable, Void] {
      override def isDefinedAt(x: Throwable): Boolean = true

      override def apply(value: Throwable): Void = callback.apply(value)
    }
}
