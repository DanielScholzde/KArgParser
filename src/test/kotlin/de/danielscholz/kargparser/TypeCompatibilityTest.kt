package de.danielscholz.kargparser;

import de.danielscholz.kargparser.parser.*
import org.junit.Test;

import java.io.File;
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TypeCompatibilityTest {

   @Test
   fun testTargetTypesCompileFileList1a() {
      class Test(var value: MutableList<File>? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, FileListParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileFileList1b() {
      class Test(var value: MutableList<File> = mutableListOf())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, FileListParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileFileList1c() {
      class Test(var value: Collection<File>? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, FileListParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileFileList1d() {
      class Test(var value: Collection<File> = listOf())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, FileListParam(2..2))
      }
   }

   //////////////////////

   @Test
   fun testTargetTypesCompileStringList1a() {
      class Test(var value: MutableList<String>? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringListParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileStringList1b() {
      class Test(var value: MutableList<String> = mutableListOf())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringListParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileStringList2a() {
      class Test(var value: Collection<String>? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringListParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileStringList2b() {
      class Test(var value: Collection<String> = listOf())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringListParam(2..2))
      }
   }

   //////////////////////

   @Test
   fun testTargetTypesCompileStringSet1a() {
      class Test(var value: MutableSet<String>? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringSetParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileStringSet1b() {
      class Test(var value: MutableSet<String> = mutableSetOf())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringSetParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileStringSet2a() {
      class Test(var value: Collection<String>? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringSetParam(2..2))
      }
   }

   @Test
   fun testTargetTypesCompileStringSet2b() {
      class Test(var value: Collection<String> = listOf())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringSetParam(2..2))
      }
   }

   //////////////////////

   @Test
   fun testTargetTypesCompileLocalTime1a() {
      class Test(var value: LocalTime = LocalTime.now())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, TimeParam())
      }
   }

   @Test
   fun testTargetTypesCompileLocalTime1b() {
      class Test(var value: LocalTime? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, TimeParam())
      }
   }

   @Test
   fun testTargetTypesCompileLocalDate1a() {
      class Test(var value: LocalDate = LocalDate.now())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, DateParam())
      }
   }

   @Test
   fun testTargetTypesCompileLocalDate1b() {
      class Test(var value: LocalDate? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, DateParam())
      }
   }

   @Test
   fun testTargetTypesCompileLocalDateTime1a() {
      class Test(var value: LocalDateTime = LocalDateTime.now())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, DateTimeParam())
      }
   }

   @Test
   fun testTargetTypesCompileLocalDateTime1b() {
      class Test(var value: LocalDateTime? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, DateTimeParam())
      }
   }

   @Test
   fun testTargetTypesCompileString1a() {
      class Test(var value: String = "")

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringParam())
      }
   }

   @Test
   fun testTargetTypesCompileString1b() {
      class Test(var value: String? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringParam())
      }
   }

   @Test
   fun testTargetTypesCompileInt1a() {
      class Test(var value: Int = 0)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, IntParam())
      }
   }

   @Test
   fun testTargetTypesCompileInt1b() {
      class Test(var value: Int? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, IntParam())
      }
   }

   @Test
   fun testTargetTypesCompileIntRange1a() {
      class Test(var value: IntRange = 0..1)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, IntRangeParam())
      }
   }

   @Test
   fun testTargetTypesCompileIntRange1b() {
      class Test(var value: IntRange? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, IntRangeParam())
      }
   }

   @Test
   fun testTargetTypesCompileFile1a() {
      class Test(var value: File = File("a"))

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, FileParam())
      }
   }

   @Test
   fun testTargetTypesCompileFile1b() {
      class Test(var value: File? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, FileParam())
      }
   }

   @Test
   fun testTargetTypesCompileBoolean1a() {
      class Test(var value: Boolean = false)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, BooleanParam())
      }
   }

   @Test
   fun testTargetTypesCompileBoolean1b() {
      class Test(var value: Boolean? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, BooleanParam())
      }
   }

   @Test
   fun testTargetTypesCompileStringWithMapper1a() {
      class Test(var value: Boolean = false)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringParamWithMapper(mapper = { _: String -> true }, toStringMapper = { it: Boolean? -> it.toString() }))
      }
   }

   @Test
   fun testTargetTypesCompileStringWithMapper1b() {
      class Test(var value: Boolean? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringParamWithMapper(mapper = { _: String -> true }, toStringMapper = { it: Boolean? -> it.toString() }))
      }
   }

   @Test
   fun testTargetTypesCompileStringWithMapper2a() {
      class Test(var value: Collection<String>? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringParamWithMapper(mapper = { it: String -> mutableListOf<String>(it) }, toStringMapper = { it: Collection<String>? -> it.toString() }))
      }
   }

   @Test
   fun testTargetTypesCompileStringWithMapper2b() {
      class Test(var value: Collection<String> = listOf())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringParamWithMapper(mapper = { it: String -> mutableListOf<String>(it) }, toStringMapper = { it: Collection<String>? -> it.toString() }))
      }
   }

   @Test
   fun testTargetTypesCompileStringWithMapper2c() {
      class Test(var value: MutableList<String>? = null)

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringParamWithMapper(mapper = { it: String -> mutableListOf<String>(it) }, toStringMapper = { it: Collection<String>? -> it.toString() }))
      }
   }

   @Test
   fun testTargetTypesCompileStringWithMapper2d() {
      class Test(var value: MutableList<String> = mutableListOf())

      ArgParserBuilder(Test()).buildWith {
         addNamelessLast(paramValues::value, StringParamWithMapper(mapper = { it: String -> mutableListOf<String>(it) }, toStringMapper = { it: Collection<String>? -> it.toString() }))
      }
   }
}
