package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ActionParam<T>(override val name: String,
                     private val description: String?,
                     private val subArgParser: ArgParser<T>,
                     private val callback: ArgParser<T>.() -> Unit) : IActionParam {

   private var config: ArgParser.Config = ArgParser.defaultConfig

   override fun init(argParser: ArgParser<*>, config: ArgParser.Config) {
      this.config = config
      subArgParser.init(argParser, config)
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>): Boolean {
      return arg.equals(calcName(), config.ignoreCase)
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

   override fun reset() {
      subArgParser.reset()
   }

   override fun printout(e: ArgParseException?): String {

      fun findInArguments(e: ArgParser<*>): Boolean {
         var parser = e
         do {
            parser = parser.parent ?: break
         } while (true)
         return parser.argsToParse!!.any { it.equals(calcName(), config.ignoreCase) }
      }

      if (e != null && !findInArguments(e.source)) return ""
      val printout = subArgParser.printout(e)
      return calcName() +
            (if (description != null) "${ArgParser.descriptionMarker}$description" else "") +
            (if (printout.isEmpty()) "" else "\n$printout")
   }

   private fun calcName() = if (config.noPrefixForActionParams) name else "--$name"

}