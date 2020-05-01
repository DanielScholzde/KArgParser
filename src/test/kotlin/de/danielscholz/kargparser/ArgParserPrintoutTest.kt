package de.danielscholz.kargparser

import de.danielscholz.kargparser.parser.*
import org.junit.Test
import java.io.File
import kotlin.test.*

class ArgParserPrintoutTest {

   @Test
   fun testSubParserPrintout1() {
      class P(var b1: Boolean? = null, var b2: Boolean? = null, var f1: File? = null)

      val argParser = ArgParserBuilder(P()).buildWith {
         add(paramValues::b1, BooleanParam())
         addActionParser("action",
               ArgParserBuilder(P()).buildWith {
                  add(paramValues::b2, BooleanParam())
                  add(paramValues::f1, FileParam())
               }) { }
      }

      assertEquals("All supported parameters are:\n" +
            "--b1[:yes|no]\n" +
            "--action\n" +
            "   --b2[:yes|no]\n" +
            "   --f1 file",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout2() {
      class P(var b1: Boolean? = null, var b2: Boolean? = null, var i1: Int? = null, var ir1: IntRange? = null, var files: List<File>? = null)

      val argParser = ArgParserBuilder(P()).buildWith {
         add(paramValues::b1, BooleanParam())
         addActionParser("action",
               ArgParserBuilder(P()).buildWith {
                  add(paramValues::b2, BooleanParam())
                  add(paramValues::i1, IntParam())
                  add(paramValues::ir1, IntRangeParam())
                  addNamelessLast(paramValues::files, FileListParam())
               }) { }
      }

      assertEquals("All supported parameters are:\n" +
            "--b1[:yes|no]\n" +
            "--action\n" +
            "   --b2[:yes|no]\n" +
            "   --i1 integer\n" +
            "   --ir1 integer-integer\n" +
            "   file1 file2 ...",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout3() {
      class P(var b1: Boolean? = null, var b2: Boolean? = null, var files: List<File>? = null)

      val argParser = ArgParserBuilder(P()).buildWith {
         add(paramValues::b1, BooleanParam())
         addActionParser("action",
               ArgParserBuilder(P()).buildWith {
                  add(paramValues::b2, BooleanParam())
               }) { }
         addNamelessLast(paramValues::files, FileListParam())
      }

      assertEquals("All supported parameters are:\n" +
            "--b1[:yes|no]\n" +
            "--action\n" +
            "   --b2[:yes|no]\n" +
            "file1 file2 ...",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout4() {
      data class Test(var i1: Int = 1,
                      var ir1: IntRange = 0..2,
                      var file: File? = File("a"),
                      var files: List<File>? = listOf(File("a"), File("b")))

      val argParser = ArgParserBuilder(Test()).buildWith {
         val parent = paramValues
         addActionParser("action",
               ArgParserBuilder(Unit).buildWith {
                  add(parent::i1, IntParam(), "Description for i1", true)
                  add(parent::ir1, IntRangeParam(), "Description for ir1")
                  addNamelessLast(parent::file, FileParam(), "Description for file")
                  addNamelessLast(parent::files, FileListParam(), "Description for files")
               },
               "Description for action") { }
      }

      assertEquals("All supported parameters are:\n" +
            "--action                       Description for action\n" +
            "   --i1 integer (required) (1) Description for i1\n" +
            "   --ir1 integer-integer (0-2) Description for ir1\n" +
            "   file (a)                    Description for file\n" +
            "   file1 file2 ... (a, b)      Description for files",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout5() {
      class P(@Description("my description 1\nbla bla")
              var b1: Boolean? = null,
              @Description("my description 2")
              var b2: Boolean? = null,
              var b3: Boolean? = null)

      val argParser = ArgParserBuilder(P()).buildWith {
         add(paramValues::b1, BooleanParam())
         addActionParser("action1",
               ArgParserBuilder(P()).buildWith {
                  add(paramValues::b2, BooleanParam())
               }) { }
         addActionParser("action2",
               ArgParserBuilder(P()).buildWith {
                  add(paramValues::b3, BooleanParam())
               }) { }
      }

      var txt = ""
      try {
         argParser.parseArgs(arrayOf("--action1", "--b2:K"))
      } catch (e: ArgParseException) {
         txt = argParser.printout(e)
      }

      assertEquals("An error has occurred while processing the parameters: Value for parameter 'b2' could not be processed: K\n" +
            "All supported parameters are:\n" +
            "--b1[:yes|no]    my description 1\n" +
            "                 bla bla\n" +
            "--action1\n" +
            "   --b2[:yes|no] my description 2",
            txt)
   }
}