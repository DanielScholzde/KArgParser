package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException
import de.danielscholz.kargparser.ArgParser
import de.danielscholz.kargparser.IValueParamParser

class StringValueParamParser(callback: ((String) -> Unit)? = null) : IValueParamParser<String> {

   private var argParser: ArgParser<*>? = null

   override var callback: ((String) -> Unit)? = null
   private var value: String? = null

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