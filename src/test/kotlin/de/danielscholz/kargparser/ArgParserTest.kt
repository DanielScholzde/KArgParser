package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.ArgParserBuilderSimple
import de.danielscholz.kargparser.parser.*
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

      ArgParserBuilderSimple()
            .add(ValueParam("param1").addParser(IntValueParamParser { value = it }))
            .build()
            .parseArgs(arrayOf("--param1:5"))

      assertEquals(5, value, "Fehler")
   }

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
   fun test2() {
      var value = false

      ArgParserBuilderSimple()
            .add(ValueParam("param1").addParser(BooleanValueParamParser { value = it }))
            .build()
            .parseArgs(arrayOf("--param1:1"))

      assertTrue(value, "Fehler")
   }

   @Test
   fun test3() {
      thrown.expectMessage("Nicht gematchte Argumente: test")

      ArgParserBuilderSimple()
            .add(ValueParam("param1").addParser(BooleanValueParamParser { }))
            .build()
            .parseArgs(arrayOf("test", "--param1"))
   }

   @Test
   fun testRange1() {
      var files: List<File> = listOf()

      ArgParserBuilderSimple()
            .add(ValueParam("files").addParser(FilesValueParamParser(1..1) { files = it }))
            .build()
            .parseArgs(arrayOf("--files", "a"))

      assertEquals(1, files.size)
   }

   @Test
   fun testRange2() {
      var files: List<File> = listOf()

      ArgParserBuilderSimple()
            .add(ValueParam("files").addParser(FilesValueParamParser(1..2) { files = it }))
            .build()
            .parseArgs(arrayOf("--files", "a"))

      assertEquals(1, files.size)
   }

   @Test
   fun testRange3() {
      var files: List<File> = listOf()

      ArgParserBuilderSimple()
            .add(ValueParam("files").addParser(FilesValueParamParser(1..2) { files = it }))
            .build()
            .parseArgs(arrayOf("--files", "a", "b"))

      assertEquals(2, files.size)
      assertEquals("a", files[0].toString())
      assertEquals("b", files[1].toString())
   }

   @Test
   fun testRange4() {
      thrown.expectMessage("Nicht gematchte Argumente: c")

      ArgParserBuilderSimple()
            .add(ValueParam("files").addParser(FilesValueParamParser(1..2) { }))
            .build()
            .parseArgs(arrayOf("--files", "a", "b", "c"))
   }

   @Test
   fun testRange5() {
      thrown.expectMessage("Anzahl an Parameterwerten (1) ist zu wenig für Parameter files. Erwartet werden 2..2 Parameterwerte.")

      ArgParserBuilderSimple()
            .add(ValueParam("files").addParser(FilesValueParamParser(2..2) { throw RuntimeException() }))
            .build()
            .parseArgs(arrayOf("--files", "a"))
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

   @Test
   fun testSubParser1() {
      var value1 = false
      var value2 = false

      var actionCalled = false

      ArgParserBuilderSimple()
            .add(ValueParam("param1").addParser(BooleanValueParamParser { value1 = it }))
            .addActionParser("action",
                  ArgParserBuilderSimple()
                        .add(ValueParam("param2").addParser(BooleanValueParamParser { value2 = it }))
                        .build()) {
               actionCalled = true
            }
            .build()
            .parseArgs(arrayOf("--param1", "--param2", "action"))

      assertTrue(value1, "Fehler1")
      assertTrue(value2, "Fehler2")
      assertTrue(actionCalled, "Fehler Action")
   }

   @Test
   fun testSubParserPrintout1() {
      val argParser = ArgParserBuilderSimple()
            .add(ValueParam("b1").addParser(BooleanValueParamParser { }))
            .addActionParser("action",
                  ArgParserBuilderSimple()
                        .add(ValueParam("b2").addParser(BooleanValueParamParser { }))
                        .add(ValueParam("f1").addParser(FileValueParamParser { }))
                        .build()) { }
            .build()

      assertEquals("" +
            "--b1[:true|false|yes|no|y|n|j|0|1]\n" +
            "--action\n" +
            "   --b2[:true|false|yes|no|y|n|j|0|1]\n" +
            "   --f1 file",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout2() {
      val argParser = ArgParserBuilderSimple()
            .add(ValueParam("b1").addParser(BooleanValueParamParser { }))
            .addActionParser("action",
                  ArgParserBuilderSimple()
                        .add(ValueParam("b2").addParser(BooleanValueParamParser { }))
                        .add(ValueParam("i1").addParser(IntValueParamParser { }))
                        .add(ValueParam("ir1").addParser(IntRegionValueParamParser { }))
                        .add(ValueParam().addParser(FilesValueParamParser { }))
                        .build()) { }
            .build()

      assertEquals("" +
            "--b1[:true|false|yes|no|y|n|j|0|1]\n" +
            "--action\n" +
            "   --b2[:true|false|yes|no|y|n|j|0|1]\n" +
            "   --i1:integer\n" +
            "   --ir1:integer-integer\n" +
            "   file1 file2 ...",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout3() {
      val argParser = ArgParserBuilderSimple()
            .add(ValueParam("b1").addParser(BooleanValueParamParser { }))
            .addActionParser("action",
                  ArgParserBuilderSimple()
                        .add(ValueParam("b2").addParser(BooleanValueParamParser { }))
                        .build()) { }
            .add(ValueParam().addParser(FilesValueParamParser { }))
            .build()

      assertEquals("" +
            "--b1[:true|false|yes|no|y|n|j|0|1]\n" +
            "--action\n" +
            "   --b2[:true|false|yes|no|y|n|j|0|1]\n" +
            "file1 file2 ...",
            argParser.printout())
   }
}