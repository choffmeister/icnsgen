package de.choffmeister.icnsgen

import java.awt.geom.AffineTransform
import java.awt.Image
import java.awt.image.{ AffineTransformOp, BufferedImage }
import java.io._
import java.nio._
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object ImageUtils {
  /** See http://en.wikipedia.org/wiki/Apple_Icon_Image_format */
  def generateICNS(input: Image, output: OutputStream): Unit = {
    val w = input.getWidth(null)
    val h = input.getHeight(null)
    val d = Math.max(w, h)

    def writeMagicBytes(): Unit = {
      output.write(Array[Byte](0x69, 0x63, 0x6e, 0x73))
    }

    def writeInt32(i: Int): Unit = {
      val bufRaw = new Array[Byte](4)
      val buf = ByteBuffer.wrap(bufRaw)
      buf.order(ByteOrder.BIG_ENDIAN)
      buf.putInt(0, i)
      output.write(bufRaw)
    }

    def writeString(s: String): Unit = {
      val bytes = s.getBytes("ASCII")
      output.write(bytes)
    }

    def writeBytes(b: Array[Byte]): Unit = {
      output.write(b)
    }

    val pngs = icnsResolutions.filter(_._2 <= d).map {
      case (typ, size) ⇒
        val bytes = new ByteArrayOutputStream()
        val scale = size.toDouble / d.toDouble
        val image = scaleImage(centerSquareImage(input), scale)
        ImageIO.write(image, "png", bytes)
        (typ, size, bytes.toByteArray)
    }

    writeMagicBytes()
    writeInt32(pngs.foldLeft(8)((len, png) ⇒ len + 8 + png._3.length))
    pngs.foreach(png ⇒ {
      writeString(png._1)
      writeInt32(8 + png._3.length)
      writeBytes(png._3)
    })
  }

  def scaleImage(input: BufferedImage, scale: Double): BufferedImage = {
    val w = input.getWidth(null)
    val h = input.getHeight(null)
    val output = new BufferedImage(Math.round(w * scale).toInt, Math.round(h * scale).toInt, BufferedImage.TYPE_INT_ARGB)
    val transform = new AffineTransform()
    transform.scale(scale, scale)
    val transformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC)
    transformOp.filter(input, output)
    output
  }

  def centerSquareImage(input: Image): BufferedImage = {
    val w = input.getWidth(null)
    val h = input.getHeight(null)
    val d = Math.max(w, h)
    val output = new BufferedImage(d, d, BufferedImage.TYPE_INT_ARGB)
    val graphics = output.getGraphics
    graphics.drawImage(input, (d - w) / 2, (d - h) / 2, null)
    graphics.dispose()
    output
  }

  def loadImage(path: String): Image =
    new ImageIcon(getClass.getClassLoader.getResource(path)).getImage

  val icnsResolutions = Map(
    "icp4" -> 16,
    "icp5" -> 32,
    "icp6" -> 64,
    "ic07" -> 128,
    "ic08" -> 256,
    "ic09" -> 512,
    "ic10" -> 1024,
    "ic11" -> 32,
    "ic12" -> 64,
    "ic13" -> 256,
    "ic14" -> 512
  )
}
