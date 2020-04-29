package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException

class IntParam(callback: ((Int) -> Unit)? = null) : ParamParserBase<Int, Int?>() {

   override var callback: ((Int) -> Unit)? = null
   private var value: Int? = null

   init {
      this.callback = callback
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue.matches(Regex("[+-]?[0-9]+"))
   }

   override fun assign(rawValue: String) {
      value = Integer.parseInt(rawValue)
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "integer"
   }
}