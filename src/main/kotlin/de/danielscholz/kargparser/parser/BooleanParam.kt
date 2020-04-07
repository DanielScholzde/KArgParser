package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException

class BooleanParam(acceptedValues: Set<String> = setOf("true", "false", "yes", "no", "y", "n", "0", "1"),
                   acceptedValuesWithMeaningTrue: Set<String> = setOf("true", "yes", "y", "1"),
                   additionalAcceptedValues: Set<String> = setOf(),
                   additionalAcceptedValuesWithMeaningTrue: Set<String> = setOf(),
                   private val defaultValue: Boolean = true,
                   callback: ((Boolean) -> Unit)? = null) : ParamParserBase<Boolean>() {

   private val allValues = acceptedValues + additionalAcceptedValues
   private val allValuesLowercase = allValues.map { it.toLowerCase() }
   private val trueValuesLowercase = (acceptedValuesWithMeaningTrue + additionalAcceptedValuesWithMeaningTrue).map { it.toLowerCase() }

   override var callback: ((Boolean) -> Unit)? = null
   private var value: Boolean? = null

   init {
      this.callback = callback
   }

   override fun numberOfSeperateValueArgsToAccept(): IntRange? {
      return null
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue == "" || rawValue.toLowerCase() in allValuesLowercase
   }

   override fun assign(rawValue: String) {
      if (rawValue != "") {
         value = rawValue.toLowerCase() in trueValuesLowercase
      }
   }

   override fun exec() {
      callback?.invoke(value ?: defaultValue) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "[:" + allValues.joinToString("|") + "]"
   }
}