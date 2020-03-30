package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ActionParam<T>(private val name: String,
                     private val description: String?,
                     private val subArgParser: ArgParser<T>,
                     private val callback: ArgParser<T>.() -> Unit) : IParam {

   override fun init(parentArgParser: ArgParser<*>) {
      subArgParser.ignoreCase = parentArgParser.ignoreCase
      subArgParser.init(parentArgParser)
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>, ignoreCase: Boolean): Boolean {
      return arg.equals(name, ignoreCase)
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {
      subArgParser.parseArgs(allArguments)
   }

   override fun checkRequired() {
      subArgParser.checkRequired()
   }

   override fun deferrExec(): Boolean {
      return true
   }

   override fun exec() {
      subArgParser.exec()
      subArgParser.callback()
   }

   override fun printout(e: ArgParseException?): String {

      fun findInHierarchie(e: ArgParseException): Boolean {
         var parser = e.source
         do {
            if (parser == subArgParser) return true
            parser = parser.parent ?: break
         } while (true)
         return false
      }

      if (e != null && !findInHierarchie(e)) return ""
      val printout = subArgParser.printout(e)
      return "--$name" +
            (if (description != null) "${ArgParser.descriptionMarker}$description" else "") +
            (if (printout.isEmpty()) "" else "\n$printout")
   }
}