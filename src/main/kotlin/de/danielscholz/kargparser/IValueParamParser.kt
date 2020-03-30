package de.danielscholz.kargparser

interface IValueParamParser<T> {

   fun configure(parentArgParser: ArgParser<*>)

   var callback: ((T) -> Unit)?

   fun seperateValueArgs(): IntRange?

   fun matches(rawValue: String): Boolean

   fun assign(rawValue: String)

   fun exec()

   fun printout(): String
}