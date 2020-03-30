package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ActionParam<T>(override val name: String,
                     private val description: String?,
                     private val subArgParser: ArgParser<T>,
                     private val callback: ArgParser<T>.() -> Unit) : IActionParam {

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

      fun findInArguments(e: ArgParser<*>): Boolean {
         var parser = e
         do {
            parser = parser.parent ?: break
         } while (true)
         return parser.argsToParse!!.any { it.equals(name, subArgParser.ignoreCase) }
      }

      if (e != null && !findInArguments(e.source)) return ""
      val printout = subArgParser.printout(e)
      return name +
            (if (description != null) "${ArgParser.descriptionMarker}$description" else "") +
            (if (printout.isEmpty()) "" else "\n$printout")
   }
}