package de.danielscholz.kargparser

import de.danielscholz.kargparser.parser.DateParam
import de.danielscholz.kargparser.parser.DateTimeParam
import de.danielscholz.kargparser.parser.TimeParam
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.*

class ArgParserDateTimeTest {

   @Test
   fun test1() {
      class Params(var param1: LocalDateTime? = null)

      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(params::param1, DateTimeParam())
      }.parseArgs(arrayOf("--param1", "2020-01-31T10:20:30"))

      assertEquals(LocalDateTime.of(2020, 1, 31, 10, 20, 30), params.param1)
   }

   @Test
   fun test2() {
      class Params(var value: LocalDate? = null)

      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(paramValues::value, DateParam())
      }.parseArgs(arrayOf("--value", "2020-01-31"))

      assertEquals(LocalDate.of(2020, 1, 31), params.value)
   }

   @Test
   fun test3() {
      class Params(var value: LocalTime? = null)

      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(paramValues::value, TimeParam())
      }.parseArgs(arrayOf("--value", "10:20:30"))

      assertEquals(LocalTime.of(10, 20, 30), params.value)
   }
}