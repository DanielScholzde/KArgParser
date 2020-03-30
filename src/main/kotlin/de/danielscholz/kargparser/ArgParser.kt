package de.danielscholz.kargparser

import java.lang.RuntimeException
import kotlin.reflect.KMutableProperty

class ArgParser<T> private constructor(val data: T, internal var ignoreCase: Boolean) {

   class Argument(val value: String, var matched: Boolean)

   interface BuildSimple {
      fun build(): ArgParser<Any>
      fun addNamelessLast(parser: IValueParamParser<*>, description: String? = null, required: Boolean = false): BuildSimple
      fun <R> addNamelessLast(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false): BuildSimple
   }

   class ArgParserBuilderSimple(ignoreCase: Boolean = false) : BuildSimple {

      private val argParser: ArgParser<Any> = ArgParser(Object(), ignoreCase)

      fun buildWith(init: () -> Unit): ArgParser<Any> {
         init()
         return build()
      }

      fun add(param: IParam): ArgParserBuilderSimple {
         argParser.params.add(param)
         return this
      }

      fun add(name: String, parser: IValueParamParser<*>, description: String? = null, required: Boolean = false): ArgParserBuilderSimple {
         argParser.params.add(ValueParam(name, description, required).addParser(parser))
         return this
      }

      fun <R> add(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false): ArgParserBuilderSimple {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam(property.name, description, required).addParser(parser))
         return this
      }

      override fun addNamelessLast(parser: IValueParamParser<*>, description: String?, required: Boolean): BuildSimple {
         argParser.params.add(ValueParam(null, description, required).addParser(parser))
         return this
      }

      override fun <R> addNamelessLast(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String?, required: Boolean): BuildSimple {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam(null, description, required).addParser(parser))
         return this
      }

      fun addActionParser(name: String, description: String? = null, callback: () -> Unit): ArgParserBuilderSimple {
         argParser.params.add(ActionParamSimple(name, description, callback))
         return this
      }

      fun <U> addActionParser(name: String, subArgParser: ArgParser<U>, description: String? = null, callback: ArgParser<U>.() -> Unit): ArgParserBuilderSimple {
         argParser.params.add(ActionParam(name, description, subArgParser, callback))
         return this
      }

      override fun build(): ArgParser<Any> {
         argParser.init(null)
         return argParser
      }
   }

   class ArgParserBuilder<T>(val data: T, ignoreCase: Boolean = false) {

      private val argParser = ArgParser(data, ignoreCase)

      fun buildWith(init: ArgParserBuilder<T>.() -> Unit): ArgParser<T> {
         init()
         return build()
      }

      fun add(param: IParam) {
         argParser.params.add(param)
      }

      fun add(name: String, parser: IValueParamParser<out Any>, description: String? = null, required: Boolean = false) {
         argParser.params.add(ValueParam(name, description, required).addParser(parser))
      }

      fun <R> add(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false) {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam(property.name, description, required).addParser(parser))
      }

      fun addNamelessLast(parser: IValueParamParser<out Any>, description: String? = null, required: Boolean = false) {
         argParser.params.add(ValueParam(null, description, required).addParser(parser))
      }

      fun <R> addNamelessLast(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false) {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam(null, description, required).addParser(parser))
      }

      fun addActionParser(name: String, description: String? = null, callback: () -> Unit) {
         argParser.params.add(ActionParamSimple(name, description, callback))
      }

      fun <U> addActionParser(name: String, subArgParser: ArgParser<U>, description: String? = null, callback: ArgParser<U>.() -> Unit) {
         argParser.params.add(ActionParam(name, description, subArgParser, callback))
      }

      private fun build(): ArgParser<T> {
         argParser.init(null)
         return argParser
      }
   }

   companion object {
      const val descriptionMarker = ":DESCRIPTION:"
   }

   internal var parent: ArgParser<*>? = null

   private val params: MutableList<IParam> = mutableListOf()
   private val matchedParams: MutableList<IParam> = mutableListOf()

   internal var argsToParse: Array<String>? = null


   internal fun init(parentArgParser: ArgParser<*>?) {
      parent = parentArgParser
      params.forEach { it.init(this) }

      val list = params.filterIsInstance<IActionParam>()
      if (list.map { it.name }.distinct().size != list.size) {
         throw RuntimeException("There are action commands that are registered with the same name!")
      }

      val list1 = params.filterIsInstance<ValueParam>().dropWhile { !it.nameless() }.filter { !it.nameless() }
      if (list1.isNotEmpty()) {
         throw RuntimeException("There are named parameter after nameless parameter: ${list1.joinToString(", ") { it.name ?: "" }}")
      }
   }

   fun parseArgs(args: Array<String>) {
      if (parent != null) throw RuntimeException("Method parseArgs() should not be called on a subparser")

      argsToParse = args

      val arguments = args.map { Argument(it, false) }

      parseArgs(arguments)

      val list = arguments.filter { !it.matched }
      if (list.isNotEmpty()) {
         throw ArgParseException(list.joinToString(prefix = "Unassigned arguments: ") { it.value }, this)
      }

      checkRequired()

      exec()
   }

   internal fun parseArgs(arguments: List<Argument>) {
      for (param in params) {
         var i = -1
         for (arg in arguments) {
            i++
            if (arg.matched) continue

            if (param.matches(arg.value, i, arguments, ignoreCase)) {
               matchedParams.add(param)
               arg.matched = true // must be set before the assign!
               param.assign(arg.value, i, arguments)
            }
         }
      }
   }

   internal fun checkRequired() {
      params.filterIsInstance<ValueParam>().forEach { it.checkRequired() }
      matchedParams.filterIsInstance<IActionParam>().forEach { it.checkRequired() }
   }

   internal fun exec() {
      for (param in matchedParams) {
         if (!param.deferrExec()) param.exec()
      }
      for (param in matchedParams) {
         if (param.deferrExec()) param.exec()
      }
   }

   fun printout(e: ArgParseException? = null, rawOutput: Boolean = false): String {

      fun rightPad(str: String, len: Int): String {
         var s = str
         while (s.length < len) s += " "
         return s
      }

      var str = params.map { it.printout(e) }.filter { it.isNotEmpty() }.joinToString("\n")

      if (parent != null) {
         str = "   " + str.replace(Regex("\n"), "\n   ")
      }

      if (parent == null && str.contains(descriptionMarker)) {
         val maxRowLen = str.splitToSequence('\n')
               .map { if (it.contains(descriptionMarker)) it.substring(0, it.indexOf(descriptionMarker)).length else it.length }
               .max() ?: 0

         str = str.splitToSequence('\n')
               .map {
                  if (it.contains(descriptionMarker)) {
                     rightPad(it.substring(0, it.indexOf(descriptionMarker)), maxRowLen + 1) +
                           it.substring(it.indexOf(descriptionMarker) + descriptionMarker.length)
                  } else {
                     it
                  }
               }
               .joinToString("\n")
      }

      if (parent == null && !rawOutput) {
         str = if (e != null) {
            "An error has occurred while processing the parameters: ${e.message}\nAll supported parameters are:\n$str"
         } else {
            "All supported parameters are:\n$str"
         }
      }
      return str
   }
}