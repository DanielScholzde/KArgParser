package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.ArgParserBuilderSimple
import de.danielscholz.kargparser.parser.BooleanValueParamParser
import de.danielscholz.kargparser.parser.FilesValueParamParser
import de.danielscholz.kargparser.parser.IntValueParamParser
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.File
import java.lang.RuntimeException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ArgParserTest {

   @Rule
   @JvmField
   var thrown: ExpectedException = ExpectedException.none()

   @Test
   fun test1() {
      var value = 0

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("param1").addParser(IntValueParamParser { value = it }))
         .build()

      argParser.parseArgs(arrayOf("--param1:5"))

      Assert.assertEquals("Fehler", 5, value)
   }

   @Test
   fun test1_1() {
      class Data(var value: Int? = 0)

      val data = Data()

      val argParser = ArgParserBuilderSimple()
         .add(data::value, IntValueParamParser())
         .build()

      argParser.parseArgs(arrayOf("--value:5"))

      Assert.assertEquals("Fehler", 5, data.value)
   }

   @Test
   fun test2() {
      var value = false

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("param1").addParser(BooleanValueParamParser { value = it }))
         .build()

      argParser.parseArgs(arrayOf("--param1:1"))

      Assert.assertTrue("Fehler", value)
   }

   @Test
   fun test3() {
      thrown.expect(RuntimeException::class.java)
      thrown.expectMessage("Nicht gematchte Argumente: test")

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("param1").addParser(BooleanValueParamParser { }))
         .build()

      argParser.parseArgs(arrayOf("test", "--param1"))
   }

   @Test
   fun testRange1() {
      var files: List<File>? = null

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("files").addParser(FilesValueParamParser(1..1) { files = it }))
         .build()

      argParser.parseArgs(arrayOf("--files", "a"))

      assertNotNull(files)
      assertEquals(1, files!!.size)
   }

   @Test
   fun testRange2() {
      var files: List<File>? = null

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("files").addParser(FilesValueParamParser(1..2) { files = it }))
         .build()

      argParser.parseArgs(arrayOf("--files", "a"))

      assertNotNull(files)
      assertEquals(1, files!!.size)
   }

   @Test
   fun testRange3() {
      var files: List<File>? = null

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("files").addParser(FilesValueParamParser(1..2) { files = it }))
         .build()

      argParser.parseArgs(arrayOf("--files", "a", "b"))

      assertNotNull(files)
      assertEquals(2, files!!.size)
      assertEquals("a", files!![0].toString())
      assertEquals("b", files!![1].toString())
   }

   @Test
   fun testRange4() {
      thrown.expect(RuntimeException::class.java)
      thrown.expectMessage("Nicht gematchte Argumente: c")

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("files").addParser(FilesValueParamParser(1..2) {  }))
         .build()

      argParser.parseArgs(arrayOf("--files", "a", "b", "c"))
   }

   @Test
   fun testRange5() {
      thrown.expect(RuntimeException::class.java)
      thrown.expectMessage("Anzahl an Parameterwerten (1) ist zu wenig für Parameter files. Erwartet werden 2..2 Parameterwerte.")

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("files").addParser(FilesValueParamParser(2..2) { throw RuntimeException() }))
         .build()

      argParser.parseArgs(arrayOf("--files", "a"))
   }

   @Test
   fun testRange5_0() {
      class Test {
         var files: List<File> = listOf()
      }

      val argParser = ArgParser.ArgParserBuilder(Test()).buildWith {
         add(data::files, FilesValueParamParser(2..2), true)
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
         add(data::files, FilesValueParamParser(2..2), true)
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
         add(data::files, FilesValueParamParser(2..2), true)
      }

      argParser.parseArgs(arrayOf("--test", "a", "b", "--c"))
   }

   @Test
   fun testSubParser1() {
      var value1 = false
      var value2 = false

      var actionCalled = false

      val argParser = ArgParserBuilderSimple()
         .add(ValueParam("param1").addParser(BooleanValueParamParser { value1 = it }))
         .addActionParser("action",
                          ArgParserBuilderSimple()
                             .add(ValueParam("param2").addParser(BooleanValueParamParser { value2 = it }))
                             .build(),
                          { actionCalled = true })
         .build()

      argParser.parseArgs(arrayOf("--param1", "--param2", "action"))

      Assert.assertTrue("Fehler1", value1)
      Assert.assertTrue("Fehler2", value2)
      Assert.assertTrue("Fehler Action", actionCalled)
   }
}