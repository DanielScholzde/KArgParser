package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

interface IParam {

   fun configure(ignoreCase: Boolean)

   fun matches(arg: String, idx: Int, allArguments: List<Argument>, ignoreCase: Boolean): Boolean

   fun assign(arg: String, idx: Int, allArguments: List<Argument>)

   fun deferrExec(): Boolean

   fun exec()

   fun printout(): String

}