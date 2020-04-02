package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.ArgParserBuilderSimple
import de.danielscholz.kargparser.parser.*
import org.junit.Test
import kotlin.test.assertEquals

class ArgParserPrintoutTest {

   @Test
   fun testSubParserPrintout1() {
      val argParser = ArgParserBuilderSimple()
            .add("b1", BooleanValueParamParser { })
            .addActionParser("action",
                  ArgParserBuilderSimple()
                        .add("b2", BooleanValueParamParser { })
                        .add("f1", FileValueParamParser { })
                        .build()) { }
            .build()

      assertEquals("All supported parameters are:\n" +
            "--b1[:true|false|yes|no|y|n|j|0|1]\n" +
            "--action\n" +
            "   --b2[:true|false|yes|no|y|n|j|0|1]\n" +
            "   --f1 file",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout2() {
      val argParser = ArgParserBuilderSimple()
            .add("b1", BooleanValueParamParser { })
            .addActionParser("action",
                  ArgParserBuilderSimple()
                        .add("b2", BooleanValueParamParser { })
                        .add("i1", IntValueParamParser { })
                        .add("ir1", IntRangeValueParamParser { })
                        .addNamelessLast(FilesValueParamParser { })
                        .build()) { }
            .build()

      assertEquals("All supported parameters are:\n" +
            "--b1[:true|false|yes|no|y|n|j|0|1]\n" +
            "--action\n" +
            "   --b2[:true|false|yes|no|y|n|j|0|1]\n" +
            "   --i1 integer\n" +
            "   --ir1 integer-integer\n" +
            "   file1 file2 ...",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout3() {
      val argParser = ArgParserBuilderSimple()
            .add("b1", BooleanValueParamParser { })
            .addActionParser("action",
                  ArgParserBuilderSimple()
                        .add("b2", BooleanValueParamParser { })
                        .build()) { }
            .addNamelessLast(FilesValueParamParser { })
            .build()

      assertEquals("All supported parameters are:\n" +
            "--b1[:true|false|yes|no|y|n|j|0|1]\n" +
            "--action\n" +
            "   --b2[:true|false|yes|no|y|n|j|0|1]\n" +
            "file1 file2 ...",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout4() {
      val argParser = ArgParserBuilderSimple()
            .addActionParser("action",
                  ArgParser.ArgParserBuilder(Unit).buildWith {
                     add("i1", IntValueParamParser { }, "Description for i1", true)
                     add("ir1", IntRangeValueParamParser { }, "Description for ir1")
                     addNamelessLast(FileValueParamParser { }, "Description for file")
                     addNamelessLast(FilesValueParamParser { }, "Description for files")
                  },
                  "Description for action") { }
            .build()

      assertEquals("All supported parameters are:\n" +
            "--action                   Description for action\n" +
            "   --i1 integer (required) Description for i1\n" +
            "   --ir1 integer-integer   Description for ir1\n" +
            "   file                    Description for file\n" +
            "   file1 file2 ...         Description for files",
            argParser.printout())
   }

   @Test
   fun testSubParserPrintout5() {
      val argParser = ArgParserBuilderSimple()
            .add("b1", BooleanValueParamParser { })
            .addActionParser("action1",
                  ArgParserBuilderSimple()
                        .add("b2", BooleanValueParamParser { })
                        .build()) { }
            .addActionParser("action2",
                  ArgParserBuilderSimple()
                        .add("b3", BooleanValueParamParser { })
                        .build()) { }
            .build()

      var txt = ""
      try {
         argParser.parseArgs(arrayOf("--action1", "--b2:K"))
      } catch (e: ArgParseException) {
         txt = argParser.printout(e)
      }

      assertEquals("An error has occurred while processing the parameters: Value for parameter 'b2' could not be processed: K\n" +
            "All supported parameters are:\n" +
            "--b1[:true|false|yes|no|y|n|j|0|1]\n" +
            "--action1\n" +
            "   --b2[:true|false|yes|no|y|n|j|0|1]",
            txt)
   }
}