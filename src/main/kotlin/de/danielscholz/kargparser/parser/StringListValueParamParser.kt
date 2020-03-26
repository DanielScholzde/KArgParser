package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.IValueParamParser

class StringListValueParamParser(private val numberOfStrings: IntRange = 1..Int.MAX_VALUE,
                                 private val regex: Regex? = null,
                                 private val mapper: ((String) -> String)? = null,
                                 callback: ((List<String>) -> Unit)? = null) : IValueParamParser<List<String>> {

   override var callback: ((List<String>) -> Unit)? = null
   private var valueList: MutableList<String> = mutableListOf()

   init {
      this.callback = callback
   }

   override fun seperateValueArgs(): IntRange? {
      return numberOfStrings
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue != "" && (regex == null || rawValue.matches(regex))
   }

   override fun assign(rawValue: String) {
      var str = rawValue
      if (mapper != null) {
         str = mapper.invoke(str)
      }
      valueList.add(str)
   }

   override fun exec() {
      callback?.invoke(valueList) ?: throw RuntimeException("callback wurde nicht definiert!")
   }
}