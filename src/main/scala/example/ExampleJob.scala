package example

import java.net.URI

import com.spotify.scio._
import org.apache.beam.sdk.transforms.ParDo

object ExampleJob {
  def main(cmdlineArgs: Array[String]): Unit = {
    val (sc, args) = ContextAndArgs(cmdlineArgs)
    val output = args("output")

    sc.parallelize(1 to 200)
      .map(i => Modification(URI.create("urn:artist:a"), "kind", "field"))
      .applyTransform(ParDo.of(new ProjectionAsyncDoFn()))
      .map((s: Set[Statement]) => "a")
      .saveAsTextFile(output)

    sc.close().waitUntilFinish()
  }
}
