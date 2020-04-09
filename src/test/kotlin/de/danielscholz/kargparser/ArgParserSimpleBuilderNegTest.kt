package de.danielscholz.kargparser

import de.danielscholz.kargparser.parser.BooleanParam
import de.danielscholz.kargparser.parser.FileListParam
import de.danielscholz.kargparser.parser.FileParam
import de.danielscholz.kargparser.parser.IntParam
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class ArgParserSimpleBuilderNegTest {

   @Rule
   @JvmField
   var thrown: ExpectedException = ExpectedException.none()

   @Test
   fun test1() {
      thrown.expectMessage("Number of parameter values (0) is too few for parameter 'param1'. 1 parameter values are expected.")

      ArgParserBuilderSimple().buildWith {
         add("param1", IntParam { })
      }.parseArgs(arrayOf("--param1", "5a"))
   }

   @Test
   fun test2() {
      thrown.expectMessage("Value for parameter 'param1' could not be processed: tru")

      ArgParserBuilderSimple().buildWith {
         add("param1", BooleanParam { })
      }.parseArgs(arrayOf("--param1:tru"))
   }

   @Test
   fun test3() {
      thrown.expectMessage("Unassigned arguments: test")

      ArgParserBuilderSimple().buildWith {
         add("param1", BooleanParam { })
      }.parseArgs(arrayOf("test", "--param1"))
   }

   @Test
   fun testRange1() {
      thrown.expectMessage("Unassigned arguments: b")

      ArgParserBuilderSimple().buildWith {
         add("files", FileListParam(1..1) { })
      }.parseArgs(arrayOf("--files", "a", "b"))
   }

   @Test
   fun testRange2() {
      thrown.expectMessage("Unassigned arguments: c")

      ArgParserBuilderSimple().buildWith {
         add("files", FileListParam(1..2) { })
      }.parseArgs(arrayOf("--files", "a", "b", "c"))
   }

   @Test
   fun testRange3() {
      thrown.expectMessage("Unassigned arguments: a")

      ArgParserBuilderSimple().buildWith {
         add("files", FileListParam(1..2) { })
      }.parseArgs(arrayOf("a", "--files", "b"))
   }

   @Test
   fun testRange5() {
      thrown.expectMessage("Number of parameter values (1) is too few for parameter 'files'. 2 parameter values are expected.")

      ArgParserBuilderSimple().buildWith {
         add("files", FileListParam(2..2) { throw RuntimeException("Fail") })
      }.parseArgs(arrayOf("--files", "a"))
   }

   @Test
   fun testRange6() {
      thrown.expectMessage("Number of parameter values (0) is too few for parameter 'files'. 1 to 2 parameter values are expected.")

      ArgParserBuilderSimple().buildWith {
         add("files", FileListParam(1..2) { })
      }.parseArgs(arrayOf("--files"))
   }

   @Test
   fun testSubParser2() {
      thrown.expectMessage("Value for parameter 'b2' could not be processed: K")

      val argParser = ArgParserBuilderSimple().buildWith {
         addActionParser("action",
               ArgParserBuilderSimple().buildWith {
                  add("b2", BooleanParam { })
               }) { }
         addNamelessLast(FileListParam { })
      }

      argParser.parseArgs(arrayOf("--action", "--b2:K"))
   }

   @Test
   fun testRequired1() {
      thrown.expectMessage("There are required nameless parameter after not required nameless parameter: Description2")

      ArgParserBuilderSimple().buildWith {
         addNamelessLast(FileParam { }, "Description1", false)
         addNamelessLast(FileParam { }, "Description2", true)
      }
   }

   @Test
   fun testRequired2() {
      thrown.expectMessage("There are required nameless parameter after not required nameless parameter: Description2")

      ArgParserBuilderSimple().buildWith {
         addActionParser("action",
               ArgParserBuilderSimple().buildWith {
                  addNamelessLast(FileParam { }, "Description1", false)
               }, "") { }
         addNamelessLast(FileParam { }, "Description2", true)
      }
   }

}