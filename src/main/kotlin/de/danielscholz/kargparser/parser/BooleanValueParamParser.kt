package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.IValueParamParser

class BooleanValueParamParser(private val defaultValue: Boolean = true, callback: ((Boolean) -> Unit)? = null) : IValueParamParser<Boolean> {

   override var callback: ((Boolean) -> Unit)? = null
   private var value: Boolean? = null

   init {
      this.callback = callback
   }

   override fun seperateValueArgs(): IntRange? {
      return null
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue.toLowerCase() in setOf("", "true", "false", "yes", "no", "y", "n", "j", "0", "1")
   }

   override fun assign(rawValue: String) {
      if (rawValue != "") {
         value = rawValue in setOf("true", "yes", "y", "j", "1")
      }
   }

   override fun exec() {
      callback?.invoke(value ?: defaultValue) ?: throw RuntimeException("callback wurde nicht definiert!")
   }
}