package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.ArgParserBuilder
import de.danielscholz.kargparser.ArgParser.ArgParserBuilderSimple
import de.danielscholz.kargparser.parser.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArgParserComplexBuilderTest {

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

      assertEquals(5, data.value)
   }

   @Test
   fun testRange5_0() {
      class Test {
         var files: List<File> = listOf()
      }

      val test = Test()
      val argParser = ArgParserBuilder(test).buildWith {
         addNamelessLast(data::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("a", "b"))

      assertEquals(2, test.files.size)
      assertEquals("a", test.files[0].toString())
      assertEquals("b", test.files[1].toString())
   }

   @Test
   fun testRange5_1() {
      class MainParams {
         var test = false
         var files: List<File> = listOf()
      }

      val mainParams = MainParams()
      val argParser = ArgParserBuilder(mainParams).buildWith {
         add(data::test, BooleanValueParamParser())
         addNamelessLast(data::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("--test", "a", "b"))

      assertTrue(mainParams.test)
      assertEquals(2, mainParams.files.size)
      assertEquals("a", mainParams.files[0].toString())
      assertEquals("b", mainParams.files[1].toString())
   }

   @Test
   fun testRange5_2() {
      thrown.expectMessage("Unassigned arguments: a, b, --c")

      class Test {
         var test = false
         var files: List<File> = listOf()
      }

      val argParser = ArgParserBuilder(Test()).buildWith {
         add(data::test, BooleanValueParamParser())
         addNamelessLast(data::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("--test", "a", "b", "--c"))
   }

   @Test
   fun testRange5_3() {
      thrown.expectMessage("Unassigned arguments: a, b, --c")

      class Test {
         var test = false
         var files: List<File> = listOf()
      }

      val argParser = ArgParserBuilder(Test()).buildWith {
         addNamelessLast(data::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("a", "b", "--c"))
   }

   @Test
   fun testSubParser() {
      class MainParams {
         var foo = false
         var action = false
      }

      class SubParams {
         var files: List<File> = listOf()
      }

      val mainParams = MainParams()
      val subParams = SubParams()

      val parser = ArgParserBuilder(mainParams).buildWith {
         add(data::foo, BooleanValueParamParser())
         addActionParser("compare_files", ArgParserBuilder(subParams).buildWith {
            addNamelessLast(data::files, FilesValueParamParser(1..2))
         }) {
            mainParams.action = true
         }
      }

      parser.parseArgs(arrayOf("--foo", "compare_files", "a", "b"))

      assertTrue(mainParams.foo)
      assertTrue(mainParams.action)
      assertEquals(2, subParams.files.size)
      assertEquals("a", subParams.files[0].toString())
      assertEquals("b", subParams.files[1].toString())
   }
}