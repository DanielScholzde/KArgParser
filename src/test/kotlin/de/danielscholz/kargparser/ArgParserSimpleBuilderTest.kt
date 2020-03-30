package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.ArgParserBuilderSimple
import de.danielscholz.kargparser.parser.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.File
import java.lang.RuntimeException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArgParserSimpleBuilderTest {

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

      assertEquals(5, value)
   }

   @Test
   fun test2() {
      var value = false

      ArgParserBuilderSimple()
            .add(ValueParam("param1").addParser(BooleanValueParamParser { value = it }))
            .build()
            .parseArgs(arrayOf("--param1:1"))

      assertTrue(value)
   }

   @Test
   fun test3() {
      thrown.expectMessage("Unassigned arguments: test")

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
      thrown.expectMessage("Unassigned arguments: c")

      ArgParserBuilderSimple()
            .add(ValueParam("files").addParser(FilesValueParamParser(1..2) { }))
            .build()
            .parseArgs(arrayOf("--files", "a", "b", "c"))
   }

   @Test
   fun testRange5() {
      thrown.expectMessage("Number of parameter values (1) is too few for parameter files. 2 parameter values are expected.")

      ArgParserBuilderSimple()
            .add(ValueParam("files").addParser(FilesValueParamParser(2..2) { throw RuntimeException("Fail") }))
            .build()
            .parseArgs(arrayOf("--files", "a"))
   }

   @Test
   fun testRange6() {
      thrown.expectMessage("Number of parameter values (0) is too few for parameter files. 1 to 2 parameter values are expected.")

      ArgParserBuilderSimple()
            .add(ValueParam("files").addParser(FilesValueParamParser(1..2) { }))
            .build()
            .parseArgs(arrayOf("--files"))
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
            .parseArgs(arrayOf("action", "--param1", "--param2"))

      assertTrue(value1)
      assertTrue(value2)
      assertTrue(actionCalled)
   }

}