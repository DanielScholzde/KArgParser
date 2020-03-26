package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ValueParam(val name: String) : IParam {

   private val paramValueParsers: MutableList<IValueParamParser<*>> = mutableListOf()
   private var matchedValueParamParser: IValueParamParser<*>? = null

   private var singleRawValue: String? = null
   private val nameless: Boolean = name == ""

   fun addParser(parser: IValueParamParser<*>): ValueParam {
      paramValueParsers.add(parser)
      return this
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>): Boolean {
      fun noArgsFollowing(): Boolean {
         if (idx == allArguments.lastIndex) return true
         for (i in (idx + 1)..allArguments.lastIndex) {
            if (allArguments[i].value.startsWith("--")) {
               return false
            }
         }
         return true
      }

      return (!nameless && arg == "--$name") ||
             (!nameless && arg.startsWith("--$name:")) ||
             (nameless && paramValueParsers.size == 1 && paramValueParsers[0].seperateValueArgs() != null && noArgsFollowing())
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {
      if (arg == "--$name") {
         singleRawValue = ""
      } else if (arg.startsWith("--$name:")) {
         singleRawValue = arg.substring(name.length + 3)
      } else {
         singleRawValue = arg
      }

      for (paramValueParser in paramValueParsers) {
         if (paramValueParser.seperateValueArgs() != null) {
            val seperateValueArgs = paramValueParser.seperateValueArgs()!!
            var assigned = 0
            val offset = if (nameless) 0 else 1
            for (i in 1..seperateValueArgs.last) {
               if (idx + i - 1 + offset > allArguments.lastIndex) {
                  if (matchedValueParamParser == null && seperateValueArgs.first == 0) {
                     matchedValueParamParser = paramValueParser
                  }
                  break
               }
               val arg1 = allArguments[idx + i - 1 + offset]
               if (arg1.value.startsWith("--")) {
                  if (matchedValueParamParser == null && seperateValueArgs.first == 0) {
                     matchedValueParamParser = paramValueParser
                  }
                  break
               }
               if (paramValueParser.matches(arg1.value)) {
                  paramValueParser.assign(arg1.value)
                  matchedValueParamParser = paramValueParser
                  arg1.matched = true
                  assigned++
               } else break
            }
            if (assigned < seperateValueArgs.first && matchedValueParamParser != null) {
               throw RuntimeException("Anzahl an Parameterwerten ($assigned) ist zu wenig für Parameter $name. Erwartet werden $seperateValueArgs Parameterwerte.")
            }
            if (matchedValueParamParser != null) {
               break
            }
         } else if (paramValueParser.matches(singleRawValue!!)) {
            paramValueParser.assign(singleRawValue!!)
            matchedValueParamParser = paramValueParser
            break
         }
      }

      if (paramValueParsers.isNotEmpty() && matchedValueParamParser == null) {
         throw RuntimeException("Parameterwert konnte nicht verarbeitet werden: $singleRawValue")
      }
   }

   override fun deferrExec(): Boolean {
      return false
   }

   override fun exec() {
      matchedValueParamParser?.exec()
   }

}