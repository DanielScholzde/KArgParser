package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException

class StringParamWithMapper<T>(private val matcher: Regex = Regex(".*"), private val mapper: (String) -> T, callback: ((T) -> Unit)? = null) : ParamParserBase<T, T?>() {

   override var callback: ((T) -> Unit)? = null
   private var value: T? = null

   init {
      this.callback = callback
   }

   override fun matches(rawValue: String): Boolean {
      return matcher.matches(rawValue)
   }

   override fun assign(rawValue: String) {
      value = mapper(rawValue)
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "value"
   }
}