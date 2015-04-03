package com.ambantis.matcher

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

case class Strand(name: String, head: String, tail: String)

case class Solution(lefts: Seq[Strand], rights: Seq[Strand]) {
  def output =
    for {
      l <- lefts
      r <- rights
      if l != r
    } yield l.name -> r.name
}

object UltimateAnswer {

  def main(args: Array[String]): Unit = {
    if (args.length != 2)
      println("error, 2 parameters needed: (1) file name, (2) length, as an integer")
    else (args(0), args(1)) match { case (fileName, n) =>
      Try(new FileOpener(fileName, n.toInt).getStrands) match {
        case Success(data) =>
          findSolutions(data.toSeq)
        case Failure(e) =>
          println(s"sorry, had an error ${e.getMessage}")
          for (ste <- e.getStackTrace) println()
      }
    }
  }

  def findSolutions(strands: Seq[Strand]): Unit = {
    val leftStrands: Map[String, Seq[Strand]] = strands.groupBy(_.tail)
    val rightStrands: Map[String, Seq[Strand]] = strands.groupBy(_.head)

    val solutions: Seq[(String, String)] =
      for {
        key <- leftStrands.keys.toSeq intersect rightStrands.keys.toSeq
        l = leftStrands(key)
        r = rightStrands(key)
        pair <- Solution(l, r).output
      } yield pair

    solutions foreach { case (left, right) => println(s"[$left -> $right]") }
  }
}
