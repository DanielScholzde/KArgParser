package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException

class StringParam(callback: ((String) -> Unit)? = null) : ParamParserBase<String>() {

   override var callback: ((String) -> Unit)? = null
   private var value: String? = null

   init {
      this.callback = callback
   }

   override fun matches(rawValue: String): Boolean {
      return true
   }

   override fun assign(rawValue: String) {
      value = rawValue
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "value"
   }
}