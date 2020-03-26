package de.danielscholz.kargparser

interface IValueParamParser<T> {

   var callback: ((T) -> Unit)?

   fun seperateValueArgs(): IntRange?

   fun matches(rawValue: String): Boolean

   fun assign(rawValue: String)

   fun exec()

}