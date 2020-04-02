package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.ArgParserBuilder
import de.danielscholz.kargparser.ArgParser.ArgParserBuilderSimple
import de.danielscholz.kargparser.parser.BooleanValueParamParser
import de.danielscholz.kargparser.parser.FileValueParamParser
import de.danielscholz.kargparser.parser.FilesValueParamParser
import de.danielscholz.kargparser.parser.IntValueParamParser
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.File
import kotlin.test.*

class ArgParserComplexBuilderTest {

   @Rule
   @JvmField
   var thrown: ExpectedException = ExpectedException.none()


   @Test
   fun testRange5_0() {
      class Test(var files: List<File> = listOf())

      val test = Test()

      val argParser = ArgParserBuilder(test).buildWith {
         addNamelessLast(paramValues::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("a", "b"))

      assertEquals(2, test.files.size)
      assertEquals("a", test.files[0].toString())
      assertEquals("b", test.files[1].toString())
   }

   @Test
   fun testRange5_1() {
      class MainParams(var test: Boolean = false, var files: List<File> = listOf())

      val mainParams = MainParams()

      val argParser = ArgParserBuilder(mainParams).buildWith {
         add(paramValues::test, BooleanValueParamParser())
         addNamelessLast(paramValues::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("--test", "a", "b"))

      assertTrue(mainParams.test)
      assertEquals(2, mainParams.files.size)
      assertEquals("a", mainParams.files[0].toString())
      assertEquals("b", mainParams.files[1].toString())
   }

   @Test
   fun testRange5_2() {
      thrown.expectMessage("Unassigned arguments: a, b, --c")

      class Test(var test: Boolean = false, var files: List<File> = listOf())

      val argParser = ArgParserBuilder(Test()).buildWith {
         add(paramValues::test, BooleanValueParamParser())
         addNamelessLast(paramValues::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("--test", "a", "b", "--c"))
   }

   @Test
   fun testRange5_3() {
      thrown.expectMessage("Unassigned arguments: a, b, --c")

      class Test(var files: List<File> = listOf())

      val argParser = ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::files, FilesValueParamParser(2..2))
      }

      argParser.parseArgs(arrayOf("a", "b", "--c"))
   }

   @Test
   fun testSubParser() {
      class MainParams(var foo: Boolean = false, var action: Boolean = false)
      class SubParams(var files: List<File> = listOf())

      val mainParams = MainParams()
      val subParams = SubParams()

      val parser = ArgParserBuilder(mainParams).buildWith {
         add(paramValues::foo, BooleanValueParamParser())
         addActionParser("compare_files",
               ArgParserBuilder(subParams).buildWith {
                  addNamelessLast(paramValues::files, FilesValueParamParser(1..2), required = true)
               }) {
            mainParams.action = true
         }
      }

      parser.parseArgs(arrayOf("--compare_files", "--foo", "a", "b"))

      assertTrue(mainParams.foo)
      assertTrue(mainParams.action)
      assertEquals(2, subParams.files.size)
      assertEquals("a", subParams.files[0].toString())
      assertEquals("b", subParams.files[1].toString())
   }

   @Test
   fun testSubParser2() {
      class MainParams(var foo: Boolean = false, var action: Boolean = false)
      class SubParams(var file1: File? = null, var file2: File? = null)

      val mainParams = MainParams()
      val subParams = SubParams()

      val parser = ArgParserBuilder(mainParams).buildWith {
         add(paramValues::foo, BooleanValueParamParser())
         addActionParser("compare_files",
               ArgParserBuilder(subParams).buildWith {
                  addNamelessLast(paramValues::file1, FileValueParamParser(), required = true)
                  addNamelessLast(paramValues::file2, FileValueParamParser(), required = true)
               }) {
            mainParams.action = true
         }
      }

      parser.parseArgs(arrayOf("--compare_files", "--foo", "a", "b"))

      assertTrue(mainParams.foo)
      assertTrue(mainParams.action)
      assertNotNull(subParams.file1)
      assertNotNull(subParams.file2)
      assertEquals("a", subParams.file1.toString())
      assertEquals("b", subParams.file2.toString())
   }

   @Test
   fun testSubParser3() {
      class MainParams(var foo: Boolean = false, var action1: Boolean = false, var action2: Boolean = false)
      class SubParams1(var file1: File? = null)
      class SubParams2(var file1: File? = null)

      val mainParams = MainParams()
      val subParams1 = SubParams1()
      val subParams2 = SubParams2()

      val parser = ArgParserBuilder(mainParams).buildWith {
         add(paramValues::foo, BooleanValueParamParser())
         addActionParser("compare_files",
               ArgParserBuilder(subParams1).buildWith {
                  addNamelessLast(paramValues::file1, FileValueParamParser(), required = true)
               }) {
            mainParams.action1 = true
         }
         addActionParser("sync_files",
               ArgParserBuilder(subParams2).buildWith {
                  addNamelessLast(paramValues::file1, FileValueParamParser(), required = true)
               }) {
            mainParams.action2 = true
         }
      }

      parser.parseArgs(arrayOf("--compare_files", "--foo", "a"))

      assertTrue(mainParams.foo)
      assertTrue(mainParams.action1)
      assertFalse(mainParams.action2)
      assertNotNull(subParams1.file1)
      assertNull(subParams2.file1)
      assertEquals("a", subParams1.file1.toString())
      assertNull(subParams2.file1)
   }

   @Test
   fun testFailure1() {
      thrown.expectMessage("There are named parameter after nameless parameter: test")

      ArgParserBuilder(Unit).buildWith {
         addNamelessLast(FilesValueParamParser(2..2) {})
         add("test", BooleanValueParamParser() {})
      }
   }
}