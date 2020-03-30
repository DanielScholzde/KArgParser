package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ActionParamSimple(override val name: String, private val description: String?, private val callback: () -> Unit) : IActionParam {

   override fun init(parentArgParser: ArgParser<*>) {
      //
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>, ignoreCase: Boolean): Boolean {
      return arg.equals(name, ignoreCase)
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {
      //
   }

   override fun checkRequired() {
      //
   }

   override fun deferrExec(): Boolean {
      return true
   }

   override fun exec() {
      callback()
   }

   override fun printout(e: ArgParseException?): String {
      return name + (if (description != null) "${ArgParser.descriptionMarker}$description" else "")
   }
}