package de.danielscholz.kargparser

import kotlin.reflect.KMutableProperty

class ArgParser<T> private constructor(val data: T) {

   class Argument(val value: String, var matched: Boolean)

   class ArgParserBuilderSimple {

      private val argParser: ArgParser<Any> = ArgParser(Object())

      fun add(param: IParam): ArgParserBuilderSimple {
         argParser.params.add(param)
         return this
      }

      fun add(name: String, parser: IValueParamParser<*>): ArgParserBuilderSimple {
         argParser.params.add(ValueParam(name).addParser(parser))
         return this
      }

      fun <R> add(property: KMutableProperty<R>, parser: IValueParamParser<out R>, namelessLastParameter: Boolean = false): ArgParserBuilderSimple {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam(if (namelessLastParameter) "" else property.name).addParser(parser))
         return this
      }

      fun addActionParser(name: String, callback: () -> Unit): ArgParserBuilderSimple {
         argParser.params.add(ActionParamSimple(name, callback))
         return this
      }

      fun <U> addActionParser(name: String, subParser: ArgParser<U>, callback: ArgParser<U>.() -> Unit): ArgParserBuilderSimple {
         subParser.subParser = true
         argParser.params.add(ActionParam(name, subParser, callback))
         return this
      }

      fun build(): ArgParser<Any> {
         return argParser
      }
   }

   class ArgParserBuilder<T>(val data: T) {

      private val argParser = ArgParser(data)

      fun buildWith(init: ArgParserBuilder<T>.() -> Unit): ArgParser<T> {
         init()
         return argParser
      }

      fun add(param: IParam): ArgParserBuilder<T> {
         argParser.params.add(param)
         return this
      }

      fun add(name: String, parser: IValueParamParser<out Any>): ArgParserBuilder<T> {
         argParser.params.add(ValueParam(name).addParser(parser))
         return this
      }

      fun <R> add(property: KMutableProperty<R>, parser: IValueParamParser<out R>, namelessLastParameter: Boolean = false): ArgParserBuilder<T> {
         if (parser.callback == null) parser.callback = { property.setter.call(it) }
         argParser.params.add(ValueParam(if (namelessLastParameter) "" else property.name).addParser(parser))
         return this
      }

      fun addActionParser(name: String, callback: () -> Unit): ArgParserBuilder<T> {
         argParser.params.add(ActionParamSimple(name, callback))
         return this
      }

      fun <U> addActionParser(name: String, subParser: ArgParser<U>, callback: ArgParser<U>.() -> Unit): ArgParserBuilder<T> {
         subParser.subParser = true
         argParser.params.add(ActionParam(name, subParser, callback))
         return this
      }
   }

   private var subParser: Boolean = false

   private val params: MutableList<IParam> = ArrayList()
   private val matchedParams: MutableList<IParam> = ArrayList()

   fun parseArgs(strings: Array<String>) {
      if (subParser) throw RuntimeException()

      val args = strings.map { Argument(it, false) }

      parseArgs(args)

      val list = args.filter { !it.matched }
      if (list.isNotEmpty()) {
         throw RuntimeException(list.joinToString(prefix = "Nicht gematchte Argumente: ") { it.value })
      }

      exec()
   }

   internal fun parseArgs(arguments: List<Argument>) {
      for (param in params) {
         var i = -1
         for (arg in arguments) {
            i++
            if (arg.matched) continue

            if (param.matches(arg.value, i, arguments)) {
               matchedParams.add(param)
               arg.matched = true // muss vor dem assign gesetzt werden!
               param.assign(arg.value, i, arguments)
            }
         }
      }
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
}