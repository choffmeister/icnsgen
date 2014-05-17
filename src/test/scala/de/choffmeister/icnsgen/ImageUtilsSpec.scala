package de.choffmeister.icnsgen

import de.choffmeister.icnsgen.ImageUtils._
import java.io._
import org.specs2.mutable._
import scala.language.reflectiveCalls

class ImageUtilsSpec extends Specification {
  "ImageUtils" should {
    "centerSquareImage" in {
      val input = loadImage("apple-684x840.png")

      println("Generating .icns file...")
      val f = tempFile(".icns")
      using(new FileOutputStream(f)) { fs ⇒
        generateICNS(input, fs)
      }
      println("Done.")

      ok
    }
  }

  private def tempFile(suffix: String): File = {
    val res = File.createTempFile("icnsgen_", suffix)
    println(res)
    res
  }

  private def using[A <: { def close(): Unit }, B](closable: A)(inner: A ⇒ B): B = {
    try {
      inner(closable)
    }
    finally {
      closable.close()
    }
  }
}
