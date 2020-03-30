package de.danielscholz.kargparser

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
         return argParser
      }

      fun add(param: IParam): ArgParserBuilderSimple {
         argParser.params.add(param)
         return this
      }

      fun add(name: String, parser: IValueParamParser<*>, description: String? = null, required: Boolean = false): ArgParserBuilderSimple {
         argParser.params.add(ValueParam(name, description, required).addParser(parser))
         return this
      }

      override fun addNamelessLast(parser: IValueParamParser<*>, description: String?, required: Boolean): BuildSimple {
         argParser.params.add(ValueParam("", description, required).addParser(parser))
         return this
      }

      fun <R> add(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false): ArgParserBuilderSimple {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam(property.name, description, required).addParser(parser))
         return this
      }

      override fun <R> addNamelessLast(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String?, required: Boolean): BuildSimple {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam("", description, required).addParser(parser))
         return this
      }

      fun addActionParser(name: String, description: String? = null, callback: () -> Unit): ArgParserBuilderSimple {
         argParser.params.add(ActionParamSimple(name, description, callback))
         return this
      }

      fun <U> addActionParser(name: String, subParser: ArgParser<U>, description: String? = null, callback: ArgParser<U>.() -> Unit): ArgParserBuilderSimple {
         subParser.subParser = true
         argParser.params.add(ActionParam(name, description, subParser, callback))
         return this
      }

      override fun build(): ArgParser<Any> {
         return argParser
      }
   }

   class ArgParserBuilder<T>(val data: T, ignoreCase: Boolean = false) {

      private val argParser = ArgParser(data, ignoreCase)

      fun buildWith(init: ArgParserBuilder<T>.() -> Unit): ArgParser<T> {
         init()
         return argParser
      }

      fun add(param: IParam): ArgParserBuilder<T> {
         argParser.params.add(param)
         return this
      }

      fun add(name: String, parser: IValueParamParser<out Any>, description: String? = null, required: Boolean = false): ArgParserBuilder<T> {
         argParser.params.add(ValueParam(name, description, required).addParser(parser))
         return this
      }

      fun addNamelessLast(parser: IValueParamParser<out Any>, description: String? = null, required: Boolean = false): ArgParserBuilder<T> {
         argParser.params.add(ValueParam("", description, required).addParser(parser))
         return this
      }

      fun <R> add(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false): ArgParserBuilder<T> {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam(property.name, description, required).addParser(parser))
         return this
      }

      fun <R> addNamelessLast(property: KMutableProperty<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false): ArgParserBuilder<T> {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam("", description, required).addParser(parser))
         return this
      }

      fun addActionParser(name: String, description: String? = null, callback: () -> Unit): ArgParserBuilder<T> {
         argParser.params.add(ActionParamSimple(name, description, callback))
         return this
      }

      fun <U> addActionParser(name: String, subParser: ArgParser<U>, description: String? = null, callback: ArgParser<U>.() -> Unit): ArgParserBuilder<T> {
         subParser.subParser = true
         argParser.params.add(ActionParam(name, description, subParser, callback))
         return this
      }
   }

   companion object {
      const val descriptionMarker = ":DESCRIPTION:"
   }

   internal var parent: ArgParser<*>? = null

   private var subParser: Boolean = false

   private val params: MutableList<IParam> = mutableListOf()
   private val matchedParams: MutableList<IParam> = mutableListOf()

   fun parseArgs(strings: Array<String>) {
      if (subParser) throw ArgParseException("Method parseArgs() should not be called on a subparser", this)

      configure()

      val args = strings.map { Argument(it, false) }

      parseArgs(args)

      val list = args.filter { !it.matched }
      if (list.isNotEmpty()) {
         throw ArgParseException(list.joinToString(prefix = "Unassigned arguments: ") { it.value }, this)
      }

      checkRequired()

      exec()
   }

   internal fun configure() {
      params.forEach { it.configure(this) }
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

   fun checkRequired() {
      params.forEach { it.checkRequired() }
   }

   internal fun exec() {
      for (param in matchedParams) {
         if (!param.deferrExec()) param.exec()
      }
      for (param in matchedParams) {
         if (param.deferrExec()) param.exec()
      }
   }

   internal fun isSubParser() = subParser

   fun printout(e: ArgParseException? = null): String {

      fun rightPad(str: String, len: Int): String {
         var s = str
         while (s.length < len) s += " "
         return s
      }

      var str = params.joinToString("\n") { it.printout(e) }

      if (subParser) {
         str = "   " + str.replace(Regex("\n"), "\n   ")
      }

      if (!subParser && str.contains(descriptionMarker)) {
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
      return str
   }
}