package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ActionParamSimple(override val name: String, private val description: String?, private val callback: () -> Unit) : IActionParam {

   private var config: Config = ArgParser.defaultConfig

   override fun init(argParser: ArgParser<*>, config: Config) {
      this.config = config
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>): Boolean {
      return arg.equals(calcName(), config.ignoreCase)
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

   override fun reset() {
      //
   }

   override fun printout(e: ArgParseException?): String {
      return calcName() + (if (description != null) "${ArgParser.descriptionMarker}$description" else "")
   }

   private fun calcName() = if (config.noPrefixForActionParams) name else "${config.prefixStr}$name"

}