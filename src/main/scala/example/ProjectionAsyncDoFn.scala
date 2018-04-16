package example

import java.net.URI

import com.google.common.collect.ImmutableSet
import com.spotify.scio.transforms.{DoFnWithResource, ScalaAsyncDoFn}

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProjectionAsyncDoFn
    extends ScalaAsyncDoFn[Modification, Set[Statement], BigtableClient] {

  override def processElement(input: Modification): Future[Set[Statement]] = {
    val client = this.getResource

    client
      .lookup(ImmutableSet.of(URI.create("urn:artist:abc")), "artist")
      .toScala
      .map(entities => {
        entities.asScala
          .map(entity => Statement("uri", "kind", "field", "value"))
          .toSet
      })
  }

  override def createResource(): BigtableClient = {
    new BigtableClient
  }

  override def getResourceType: DoFnWithResource.ResourceType =
    DoFnWithResource.ResourceType.PER_INSTANCE
}
