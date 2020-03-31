package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException
import de.danielscholz.kargparser.ArgParser
import de.danielscholz.kargparser.IValueParamParser

class StringSetValueParamParser(private val numberOfValuesToAccept: IntRange = 1..Int.MAX_VALUE,
                                private val regex: Regex? = null,
                                private val mapper: ((String) -> String)? = null,
                                callback: ((Set<String>) -> Unit)? = null) : IValueParamParser<Set<String>> {

   private var argParser: ArgParser<*>? = null

   override var callback: ((Set<String>) -> Unit)? = null
   private var valueSet: MutableSet<String> = mutableSetOf()

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
      valueSet.add(str)
   }

   override fun exec() {
      callback?.invoke(valueSet) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "value1 value2 ..."
   }
}