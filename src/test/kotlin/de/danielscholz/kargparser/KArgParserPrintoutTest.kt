package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.ArgParserBuilderSimple
import de.danielscholz.kargparser.parser.*
import org.junit.Test
import kotlin.test.assertEquals

class KArgParserPrintoutTest {

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