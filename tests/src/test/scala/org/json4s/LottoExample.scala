/*
 * Copyright 2009-2011 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.json4s

import org.specs.Specification
import org.json4s.LottoExample.{Winner, Lotto}
import text.Document

object NativeLottoExample extends LottoExample[Document]("Native") with native.JsonMethods {
  import native._
  implicit val formats = DefaultFormats
  def extractWinner(jv: _root_.org.json4s.JValue): Winner = jv.extract[Winner]

  def extractLotto(jv: _root_.org.json4s.JValue): Lotto = jv.extract[Lotto]
}


/**
 * System under specification for Lotto Examples.
 */
abstract class LottoExample[T](mod: String) extends Specification(mod + " Lotto Examples") with JsonMethods[T] {
  import LottoExample._

  compact(render(json)) mustEqual """{"lotto":{"id":5,"winning-numbers":[2,45,34,23,7,5,3],"winners":[{"winner-id":23,"numbers":[2,45,34,23,3,5]},{"winner-id":54,"numbers":[52,3,12,11,18,22]}]}}"""

  extractWinner((json \ "lotto" \ "winners")(0)) mustEqual Winner(23, List(2, 45, 34, 23, 3, 5))

  extractLotto(json \ "lotto") mustEqual lotto

  json.values mustEqual Map("lotto" -> Map("id" -> 5, "winning-numbers" -> List(2, 45, 34, 23, 7, 5, 3), "draw-date" -> None, "winners" -> List(Map("winner-id" -> 23, "numbers" -> List(2, 45, 34, 23, 3, 5)), Map("winner-id" -> 54, "numbers" -> List(52, 3, 12, 11, 18, 22)))))


  def extractWinner(jv: JValue): Winner
  def extractLotto(jv: JValue): Lotto

}
object LottoExample extends Specification("Lotto Examples") {
  import JsonDSL._

  case class Winner(`winner-id`: Long, numbers: List[Int])
  case class Lotto(id: Long, `winning-numbers`: List[Int], winners: List[Winner], 
                   `draw-date`: Option[java.util.Date])

  val winners = List(Winner(23, List(2, 45, 34, 23, 3, 5)), Winner(54, List(52, 3, 12, 11, 18, 22)))
  val lotto = Lotto(5, List(2, 45, 34, 23, 7, 5, 3), winners, None)

  val json: JObject =
    ("lotto" ->
      ("id" -> lotto.id) ~
      ("winning-numbers" -> lotto.`winning-numbers`) ~
      ("draw-date" -> lotto.`draw-date`.map(_.toString)) ~
      ("winners" ->
        lotto.winners.map { w =>
          (("winner-id" -> w.`winner-id`) ~
           ("numbers" -> w.numbers))}))

}