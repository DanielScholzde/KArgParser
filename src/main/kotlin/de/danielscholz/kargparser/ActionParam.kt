package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class  ActionParam<T>(private val name: String, private val argParser: ArgParser<T>, private val callback: ArgParser<T>.() -> Unit) : IParam {

   init {
      if (!argParser.isSubParser()) throw RuntimeException("Parser muss ein SubParser sein!")
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>): Boolean {
      return arg == name
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {
      argParser.parseArgs(allArguments)
   }

   override fun deferrExec(): Boolean {
      return true
   }

   override fun exec() {
      argParser.exec()
      argParser.callback()
   }
}