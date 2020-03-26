package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.IValueParamParser

class IntValueParamParser(callback: ((Int) -> Unit)? = null) : IValueParamParser<Int> {

   override var callback: ((Int) -> Unit)? = null
   private var value: Int? = null

   init {
      this.callback = callback
   }

   override fun seperateValueArgs(): IntRange? {
      return null
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue.matches(Regex("[0-9]+"))
   }

   override fun assign(rawValue: String) {
      value = Integer.parseInt(rawValue)
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw RuntimeException("callback wurde nicht definiert!")
   }
}