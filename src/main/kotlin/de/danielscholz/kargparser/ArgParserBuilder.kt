package de.danielscholz.kargparser

import kotlin.reflect.KMutableProperty0

class ArgParserBuilder<T>(val paramValues: T) : BaseArgParserBuilder() {

   private var createdArgParser: ArgParser<T>? = null

   fun buildWith(config: ArgParserConfig = ArgParserConfig(), init: ArgParserBuilder<T>.() -> Unit): ArgParser<T> {
      init()
      val argParser = ArgParser(paramValues, params)
      argParser.init(null, config) // parentArgParser will be set later if it is a subparser
      createdArgParser = argParser
      return argParser
   }

   fun printout(): String {
      return createdArgParser?.printout() ?: ""
   }
}

class ArgParserBuilderSimple : BaseArgParserBuilder() {

   private var createdArgParser: ArgParser<Unit>? = null

   fun buildWith(config: ArgParserConfig = ArgParserConfig(), init: ArgParserBuilderSimple.() -> Unit): ArgParser<Unit> {
      init()
      val argParser = ArgParser(Unit, params)
      argParser.init(null, config) // parentArgParser will be set later if it is a subparser
      createdArgParser = argParser
      return argParser
   }

   fun printout(): String {
      return createdArgParser?.printout() ?: ""
   }
}

open class BaseArgParserBuilder {

   protected val params = mutableListOf<IParam>()

   fun add(param: IParam) {
      params.add(param)
   }

   fun add(name: String, parser: IValueParamParser<out Any>, description: String? = null, required: Boolean = false) {
      params.add(ValueParam(name, description, required).addParser(parser))
   }

   fun <R> add(property: KMutableProperty0<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false) {
      if (parser.callback == null) parser.callback = { property.setter.call(it) }
      params.add(ValueParam(property.name, description, required).addParser(parser))
   }

   fun addNamelessLast(parser: IValueParamParser<out Any>, description: String? = null, required: Boolean = false) {
      params.add(ValueParam(null, description, required).addParser(parser))
   }

   fun <R> addNamelessLast(property: KMutableProperty0<R>, parser: IValueParamParser<out R>, description: String? = null, required: Boolean = false) {
      if (parser.callback == null) parser.callback = { property.setter.call(it) }
      params.add(ValueParam(null, description, required).addParser(parser))
   }

   fun addActionParser(name: String, description: String? = null, callback: () -> Unit) {
      params.add(ActionParamSimple(name, description, callback))
   }

   fun <U> addActionParser(name: String, subArgParser: ArgParser<U>, description: String? = null, callbackBeforeSubParameterParsing: () -> Unit = {}, callback: ArgParser<U>.() -> Unit) {
      params.add(ActionParam(name, description, subArgParser, callbackBeforeSubParameterParsing, callback))
   }
}
