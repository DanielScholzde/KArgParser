package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ActionParamSimple(private val name: String, private val callback: () -> Unit) : IParam {

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>): Boolean {
      return arg == name
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {

   }

   override fun deferrExec(): Boolean {
      return true
   }

   override fun exec() {
      callback()
   }
}