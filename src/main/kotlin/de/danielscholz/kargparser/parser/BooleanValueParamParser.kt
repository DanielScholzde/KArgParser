package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException

class BooleanValueParamParser(private val defaultValue: Boolean = true, callback: ((Boolean) -> Unit)? = null) : BaseParser<Boolean>() {

   private val allValues = setOf("true", "false", "yes", "no", "y", "n", "j", "0", "1")
   private val trueValues = setOf("true", "yes", "y", "j", "1")

   override var callback: ((Boolean) -> Unit)? = null
   private var value: Boolean? = null

   init {
      this.callback = callback
   }

   override fun numberOfSeperateValueArgsToAccept(): IntRange? {
      return null
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue == "" || rawValue.toLowerCase() in allValues
   }

   override fun assign(rawValue: String) {
      if (rawValue != "") {
         value = rawValue in trueValues
      }
   }

   override fun exec() {
      callback?.invoke(value ?: defaultValue) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "[:" + allValues.joinToString("|") + "]"
   }
}