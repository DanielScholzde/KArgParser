package de.danielscholz.kargparser

import de.danielscholz.kargparser.parser.BooleanParam
import de.danielscholz.kargparser.parser.FileListParam
import de.danielscholz.kargparser.parser.FileParam
import de.danielscholz.kargparser.parser.IntParam
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.File

class ArgParserSimpleBuilderNegTest {

   @Rule
   @JvmField
   var thrown: ExpectedException = ExpectedException.none()

   class Params(var i1: Int? = null, var b1: Boolean? = null, var file: File? = null, var files: List<File>? = null)

   @Test
   fun test1() {
      thrown.expectMessage("Number of parameter values (0) is too few for parameter 'i1'. 1 parameter values are expected.")

      ArgParserBuilder(Params()).buildWith {
         add(paramValues::i1, IntParam())
      }.parseArgs(arrayOf("--i1", "5a"))
   }

   @Test
   fun test2() {
      thrown.expectMessage("Value for parameter 'b1' could not be processed: tru")

      ArgParserBuilder(Params()).buildWith {
         add(paramValues::b1, BooleanParam())
      }.parseArgs(arrayOf("--b1:tru"))
   }

   @Test
   fun test3() {
      thrown.expectMessage("Unassigned arguments: test")

      ArgParserBuilder(Params()).buildWith {
         add(paramValues::b1, BooleanParam())
      }.parseArgs(arrayOf("test", "--b1"))
   }

   @Test
   fun testRange1() {
      thrown.expectMessage("Unassigned arguments: b")

      ArgParserBuilder(Params()).buildWith {
         add(paramValues::files, FileListParam(1..1))
      }.parseArgs(arrayOf("--files", "a", "b"))
   }

   @Test
   fun testRange2() {
      thrown.expectMessage("Unassigned arguments: c")

      ArgParserBuilder(Params()).buildWith {
         add(paramValues::files, FileListParam(1..2))
      }.parseArgs(arrayOf("--files", "a", "b", "c"))
   }

   @Test
   fun testRange3() {
      thrown.expectMessage("Unassigned arguments: a")

      ArgParserBuilder(Params()).buildWith {
         add(paramValues::files, FileListParam(1..2))
      }.parseArgs(arrayOf("a", "--files", "b"))
   }

   @Test
   fun testRange5() {
      thrown.expectMessage("Number of parameter values (1) is too few for parameter 'files'. 2 parameter values are expected.")

      ArgParserBuilder(Params()).buildWith {
         add(paramValues::files, FileListParam(2..2))
      }.parseArgs(arrayOf("--files", "a"))
   }

   @Test
   fun testRange6() {
      thrown.expectMessage("Number of parameter values (0) is too few for parameter 'files'. 1 to 2 parameter values are expected.")

      ArgParserBuilder(Params()).buildWith {
         add(paramValues::files, FileListParam(1..2))
      }.parseArgs(arrayOf("--files"))
   }

   @Test
   fun testSubParser2() {
      thrown.expectMessage("Value for parameter 'b1' could not be processed: K")

      val argParser = ArgParserBuilder(Params()).buildWith {
         addActionParser("action",
               ArgParserBuilder(Params()).buildWith {
                  add(paramValues::b1, BooleanParam())
               }) { }
         addNamelessLast(paramValues::files, FileListParam())
      }

      argParser.parseArgs(arrayOf("--action", "--b1:K"))
   }

   @Test
   fun testRequired1() {
      thrown.expectMessage("There are required nameless parameter after not required nameless parameter: Description2")

      ArgParserBuilder(Params()).buildWith {
         addNamelessLast(paramValues::file, FileParam(), "Description1", false)
         addNamelessLast(paramValues::file, FileParam(), "Description2", true)
      }
   }

   @Test
   fun testRequired2() {
      thrown.expectMessage("There are required nameless parameter after not required nameless parameter: Description2")

      ArgParserBuilder(Params()).buildWith {
         addActionParser("action",
               ArgParserBuilder(Params()).buildWith {
                  addNamelessLast(paramValues::file, FileParam(), "Description1", false)
               }, "") { }
         addNamelessLast(paramValues::file, FileParam(), "Description2", true)
      }
   }

}