package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException
import de.danielscholz.kargparser.ArgParser
import de.danielscholz.kargparser.IValueParamParser

class IntValueParamParser(callback: ((Int) -> Unit)? = null) : IValueParamParser<Int> {

   private var argParser: ArgParser<*>? = null

   override var callback: ((Int) -> Unit)? = null
   private var value: Int? = null

   init {
      this.callback = callback
   }

   override fun configure(parentArgParser: ArgParser<*>) {
      argParser = parentArgParser
   }

   override fun seperateValueArgs(): IntRange? {
      return null
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