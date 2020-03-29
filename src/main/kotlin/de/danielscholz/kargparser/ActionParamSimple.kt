package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ActionParamSimple(private val name: String, private val callback: () -> Unit) : IParam {

   override fun configure(ignoreCase: Boolean) {
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

   override fun printout(): String {
      return "--$name"
   }
}