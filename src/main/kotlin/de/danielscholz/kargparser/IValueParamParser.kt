package de.danielscholz.kargparser

interface IValueParamParser<T> {

   fun init(parentArgParser: ArgParser<*>, config: ArgParserConfig)

   var callback: ((T) -> Unit)?

   fun numberOfSeparateValueArgsToAccept(): IntRange?

   fun matches(rawValue: String): Boolean

   fun assign(rawValue: String)

   fun exec()

   fun printout(): String

}