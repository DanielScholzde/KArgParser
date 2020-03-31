package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException
import de.danielscholz.kargparser.ArgParser
import de.danielscholz.kargparser.IValueParamParser

class StringListValueParamParser(private val numberOfValuesToAccept: IntRange = 1..Int.MAX_VALUE,
                                 private val regex: Regex? = null,
                                 private val mapper: ((String) -> String)? = null,
                                 callback: ((List<String>) -> Unit)? = null) : IValueParamParser<List<String>> {

   private var argParser: ArgParser<*>? = null

   override var callback: ((List<String>) -> Unit)? = null
   private var valueList: MutableList<String> = mutableListOf()

   init {
      this.callback = callback
   }

   override fun init(parentArgParser: ArgParser<*>) {
      argParser = parentArgParser
   }

   override fun numberOfSeperateValueArgsToAccept(): IntRange? {
      return numberOfValuesToAccept
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
      callback?.invoke(valueList) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "value1 value2 ..."
   }
}