package de.danielscholz.kargparser

interface IValueParamParser<T> {

   fun init(parentArgParser: ArgParser<*>, config: Config)

   var callback: ((T) -> Unit)?

   fun numberOfSeperateValueArgsToAccept(): IntRange?

   fun matches(rawValue: String): Boolean

   fun assign(rawValue: String)

   fun exec()

   fun printout(): String

}