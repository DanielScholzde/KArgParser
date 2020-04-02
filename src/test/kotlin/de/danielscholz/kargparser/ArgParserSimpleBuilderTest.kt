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

   @Test
   fun test1() {
      var value = 0

      ArgParserBuilderSimple()
            .add("param1", IntParam { value = it })
            .build()
            .parseArgs(arrayOf("--param1", "5"))

      assertEquals(5, value)
   }

   @Test
   fun test1_1() {
      class Data(var value: Int = 0)

      val data = Data()

      ArgParserBuilderSimple()
            .add(data::value, IntParam())
            .build()
            .parseArgs(arrayOf("--value", "5"))

      assertEquals(5, data.value)
   }

   @Test
   fun test2() {
      var value = false

      ArgParserBuilderSimple()
            .add("param1", BooleanParam { value = it })
            .build()
            .parseArgs(arrayOf("--param1:true"))

      assertTrue(value)
   }

   @Test
   fun testRange1() {
      var files: List<File> = listOf()

      ArgParserBuilderSimple()
            .add("files", FileListParam(1..1) { files = it })
            .build()
            .parseArgs(arrayOf("--files", "a"))

      assertEquals(1, files.size)
   }

   @Test
   fun testRange2() {
      var files: List<File> = listOf()

      ArgParserBuilderSimple()
            .add("files", FileListParam(1..2) { files = it })
            .build()
            .parseArgs(arrayOf("--files", "a"))

      assertEquals(1, files.size)
   }

   @Test
   fun testRange3() {
      var files: List<File> = listOf()

      ArgParserBuilderSimple()
            .add("files", FileListParam(1..2) { files = it })
            .build()
            .parseArgs(arrayOf("--files", "a", "b"))

      assertEquals(2, files.size)
      assertEquals("a", files[0].toString())
      assertEquals("b", files[1].toString())
   }

   @Test
   fun testSubParser1() {
      var value1 = false
      var value2 = false
      var actionCalled = false

      ArgParserBuilderSimple()
            .add("param1", BooleanParam { value1 = it })
            .addActionParser("action",
                  ArgParserBuilderSimple()
                        .add("param2", BooleanParam { value2 = it })
                        .build()) {
               actionCalled = true
            }
            .build()
            .parseArgs(arrayOf("--action", "--param1", "--param2"))

      assertTrue(value1)
      assertTrue(value2)
      assertTrue(actionCalled)
   }

}