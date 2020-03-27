package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.ArgParserBuilderSimple
import de.danielscholz.kargparser.parser.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KArgParserComplexBuilderTest {

   @Rule
   @JvmField
   var thrown: ExpectedException = ExpectedException.none()

   @Test
   fun test1_1() {
      class Data(var value: Int = 0)

      val data = Data()

      ArgParserBuilderSimple()
            .add(data::value, IntValueParamParser())
            .build()
            .parseArgs(arrayOf("--value:5"))

      assertEquals(5, data.value, "Fehler")
   }

   @Test
   fun testRange5_0() {
      class Test {
         var files: List<File> = listOf()
      }

      val argParser = ArgParser.ArgParserBuilder(Test()).buildWith {
         addNamelessLast(data::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("a", "b"))

      assertEquals(2, argParser.data.files.size)
      assertEquals("a", argParser.data.files[0].toString())
      assertEquals("b", argParser.data.files[1].toString())
   }

   @Test
   fun testRange5_1() {
      class Test {
         var test = false
         var files: List<File> = listOf()
      }

      val argParser = ArgParser.ArgParserBuilder(Test()).buildWith {
         add(data::test, BooleanValueParamParser())
         addNamelessLast(data::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("--test", "a", "b"))

      assertTrue(argParser.data.test)
      assertEquals(2, argParser.data.files.size)
      assertEquals("a", argParser.data.files[0].toString())
      assertEquals("b", argParser.data.files[1].toString())
   }

   @Test
   fun testRange5_2() {
      thrown.expectMessage("Parameterwert konnte nicht verarbeitet werden: --c")

      class Test {
         var test = false
         var files: List<File> = listOf()
      }

      val argParser = ArgParser.ArgParserBuilder(Test()).buildWith {
         add(data::test, BooleanValueParamParser())
         addNamelessLast(data::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("--test", "a", "b", "--c"))
   }


}