package de.danielscholz.kargparser

import de.danielscholz.kargparser.parser.BooleanParam
import de.danielscholz.kargparser.parser.FileListParam
import de.danielscholz.kargparser.parser.IntParam
import org.junit.Test
import java.io.File
import kotlin.test.*

class ArgParserSimpleBuilderTest {

   class Params(var i1: Int? = null, var b1: Boolean? = null, var b2: Boolean? = null, var file: File? = null, var files: List<File>? = null)

   @Test
   fun test1() {
      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(params::i1, IntParam())
      }.parseArgs(arrayOf("--i1", "5"))

      assertEquals(5, params.i1)
   }

   @Test
   fun test1_1() {
      class Data(var value: Int = 0)

      val data = Data()

      ArgParserBuilder(Params()).buildWith {
         add(data::value, IntParam())
      }.parseArgs(arrayOf("--value", "5"))

      assertEquals(5, data.value)
   }

   @Test
   fun test2() {
      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(params::b1, BooleanParam())
      }.parseArgs(arrayOf("--b1:yes"))

      assertTrue(params.b1 ?: fail())
   }

   @Test
   fun test2_2() {
      val params = Params()
      params.b1 = true

      ArgParserBuilder(params).buildWith {
         add(params::b1, BooleanParam())
      }.parseArgs(arrayOf("--b1:no"))

      assertFalse(params.b1 ?: fail())
   }

   @Test
   fun testRange1() {
      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(params::files, FileListParam(1..1))
      }.parseArgs(arrayOf("--files", "a"))

      assertEquals(1, params.files?.size)
   }

   @Test
   fun testRange2() {
      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(params::files, FileListParam(1..2))
      }.parseArgs(arrayOf("--files", "a"))

      assertEquals(1, params.files?.size)
   }

   @Test
   fun testRange3() {
      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(params::files, FileListParam(1..2))
      }.parseArgs(arrayOf("--files", "a", "b"))

      assertEquals(2, params.files?.size)
      assertEquals("a", params.files?.get(0).toString())
      assertEquals("b", params.files?.get(1).toString())
   }

   @Test
   fun testSubParser1() {
      var actionCalled = false
      val params = Params()

      ArgParserBuilder(params).buildWith {
         add(paramValues::b1, BooleanParam())
         addActionParser("action",
               ArgParserBuilder(params).buildWith {
                  add(paramValues::b2, BooleanParam())
               }) {
            actionCalled = true
         }
      }.parseArgs(arrayOf("--action", "--b1", "--b2"))

      assertTrue(params.b1 ?: fail())
      assertTrue(params.b2 ?: fail())
      assertTrue(actionCalled)
   }

}