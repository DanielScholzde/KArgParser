package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.IValueParamParser

class StringValueParamParser(callback: ((String) -> Unit)? = null) : IValueParamParser<String> {

   override var callback: ((String) -> Unit)? = null
   private var value: String? = null

   init {
      this.callback = callback
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
      callback?.invoke(value!!) ?: throw RuntimeException("callback must be specified!")
   }

   override fun printout(): String {
      return "value"
   }
}