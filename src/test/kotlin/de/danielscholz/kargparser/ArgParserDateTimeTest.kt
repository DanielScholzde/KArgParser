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
      var value: LocalDateTime? = null

      ArgParserBuilderSimple().buildWith {
         add("param1", DateTimeParam { value = it })
      }.parseArgs(arrayOf("--param1", "2020-01-31T10:20:30"))

      assertEquals(LocalDateTime.of(2020, 1, 31, 10, 20, 30), value)
   }

   @Test
   fun test2() {
      var value: LocalDate? = null

      ArgParserBuilderSimple().buildWith {
         add("param1", DateParam { value = it })
      }.parseArgs(arrayOf("--param1", "2020-01-31"))

      assertEquals(LocalDate.of(2020, 1, 31), value)
   }

   @Test
   fun test3() {
      var value: LocalTime? = null

      ArgParserBuilderSimple().buildWith {
         add("param1", TimeParam { value = it })
      }.parseArgs(arrayOf("--param1", "10:20:30"))

      assertEquals(LocalTime.of(10, 20, 30), value)
   }
}