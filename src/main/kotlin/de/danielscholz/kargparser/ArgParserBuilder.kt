package de.danielscholz.kargparser

import java.lang.IllegalStateException
import kotlin.reflect.KMutableProperty0

class ArgParserBuilder<T>(val paramValues: T) {

   private var createdArgParser: ArgParser<T>? = null
   private val params = mutableListOf<IParam>()

   fun buildWith(config: ArgParserConfig = ArgParserConfig(), init: ArgParserBuilder<T>.() -> Unit): ArgParser<T> {
      init()
      val argParser = ArgParser(paramValues, params)
      argParser.init(null, config) // parentArgParser will be set later if it is a subparser
      createdArgParser = argParser
      return argParser
   }

   fun add(param: IParam) {
      params.add(param)
   }

   fun <S, R : S> add(property: KMutableProperty0<S>, parser: IValueParamParser<out R, S>, description: String? = null, required: Boolean = false) {
      if (parser.callback == null) parser.callback = { property.setter.call(it) } else throw IllegalStateException("Parser ${parser::class} must nor be initialized with a callback, because a property (${property.name}) was already specified!")
      params.add(ValueParam(property.name, description, required, parser.convertToStr(property.getter.call())).addParser(parser))
   }

   fun <S, R : S> addNamelessLast(property: KMutableProperty0<S>, parser: IValueParamParser<out R, S>, description: String? = null, required: Boolean = false) {
      if (parser.callback == null) parser.callback = { property.setter.call(it) } else throw IllegalStateException("Parser ${parser::class} must nor be initialized with a callback, because a property (${property.name}) was already specified!")
      params.add(ValueParam(null, description, required, parser.convertToStr(property.getter.call())).addParser(parser))
   }

   fun addActionParser(name: String, description: String? = null, callback: () -> Unit) {
      params.add(ActionParamSimple(name, description, callback))
   }

   fun <U> addActionParser(name: String, subArgParser: ArgParser<U>, description: String? = null, callbackBeforeSubParameterParsing: () -> Unit = {}, callback: ArgParser<U>.() -> Unit) {
      params.add(ActionParam(name, description, subArgParser, callbackBeforeSubParameterParsing, callback))
   }

   fun printout(): String {
      return createdArgParser?.printout() ?: ""
   }
}