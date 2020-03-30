package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException
import de.danielscholz.kargparser.ArgParser
import de.danielscholz.kargparser.IValueParamParser

class IntRangeValueParamParser(callback: ((IntRange) -> Unit)? = null) : IValueParamParser<IntRange> {

   private var argParser: ArgParser<*>? = null

   override var callback: ((IntRange) -> Unit)? = null
   private var value: IntRange? = null

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
      return rawValue.matches(Regex("[+-]?[0-9]+-[+-]?[0-9]+"))
   }

   override fun assign(rawValue: String) {
      val split = rawValue.split("-")
      value = split[0].toInt()..split[1].toInt()
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "integer-integer"
   }
}