package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ActionParam<T>(private val name: String,
                     private val description: String?,
                     private val argParser: ArgParser<T>,
                     private val callback: ArgParser<T>.() -> Unit) : IParam {

   init {
      if (!argParser.isSubParser()) throw ArgParseException("Parser has to be a Subparser!")
   }

   override fun configure(ignoreCase: Boolean) {
      argParser.ignoreCase = ignoreCase
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>, ignoreCase: Boolean): Boolean {
      return arg.equals(name, ignoreCase)
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {
      argParser.parseArgs(allArguments)
   }

   override fun checkRequired() {
      argParser.checkRequired()
   }

   override fun deferrExec(): Boolean {
      return true
   }

   override fun exec() {
      argParser.exec()
      argParser.callback()
   }

   override fun printout(): String {
      val printout = argParser.printout()
      return "--$name" +
            (if (description != null) "${ArgParser.descriptionMarker}$description" else "") +
            (if (printout.isEmpty()) "" else "\n$printout")
   }
}