package kotlin

import scala.math.Ordering

case class KotlinVersion(versionString: String) extends AnyVal

object KotlinVersion {
  implicit val versionOrdering: Ordering[KotlinVersion] =
    Ordering.by { _.versionString.split("[.-]").map(_.toInt).toIterable }
}