package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ValueParam(private val name: String = "", private val description: String? = null, private val required: Boolean = false) : IParam {

   init {
      if (name != "" && !name.matches(Regex("[a-zA-Z]+[0-9a-zA-Z_-]*")))
         throw IllegalArgumentException("Name of the parameter contains not allowed characters: '$name'")
   }

   private val paramValueParsers: MutableList<IValueParamParser<*>> = mutableListOf()
   private var matchedValueParamParser: IValueParamParser<*>? = null

   private val nameless: Boolean = name == ""

   fun addParser(parser: IValueParamParser<*>): ValueParam {
      paramValueParsers.add(parser)
      return this
   }

   override fun configure(ignoreCase: Boolean) {
      //
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>, ignoreCase: Boolean): Boolean {
      fun noArgsFollowing(): Boolean {
         for (i in idx..allArguments.lastIndex) {
            if (allArguments[i].value.startsWith("--")) {
               return false
            }
         }
         return true
      }

      return (!nameless && arg.equals("--$name", ignoreCase)) ||
            (!nameless && arg.startsWith("--$name:", ignoreCase)) ||
            (nameless && paramValueParsers.size == 1 && paramValueParsers[0].seperateValueArgs() != null && noArgsFollowing())
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {
      val singleRawValue = when {
         arg == "--$name" -> ""
         arg.startsWith("--$name:") -> arg.substring(name.length + 3)
         else -> arg
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
            if (assigned < seperateValueArgs.first && (paramValueParsers.size == 1 || matchedValueParamParser != null)) {
               var msg = "Number of parameter values ($assigned) is too few for parameter $name. "
               msg += when {
                  seperateValueArgs.first == seperateValueArgs.last -> "${seperateValueArgs.first} parameter values are expected."
                  seperateValueArgs.last == Int.MAX_VALUE -> "At least ${seperateValueArgs.first} parameter values are expected."
                  else -> "${seperateValueArgs.first} to ${seperateValueArgs.last} parameter values are expected."
               }
               throw ArgParseException(msg)
            }
            if (matchedValueParamParser != null) {
               break
            }
         } else if (paramValueParser.matches(singleRawValue)) {
            paramValueParser.assign(singleRawValue)
            matchedValueParamParser = paramValueParser
            break
         }
      }

      if (paramValueParsers.isNotEmpty() && matchedValueParamParser == null) {
         throw ArgParseException("Parameter value could not be processed: $singleRawValue")
      }
   }

   override fun checkRequired() {
      if (required && matchedValueParamParser == null) {
         throw ArgParseException("Required parameter '$name' is not given")
      }
   }

   override fun deferrExec(): Boolean {
      return false
   }

   override fun exec() {
      matchedValueParamParser?.exec()
   }

   override fun printout(): String {
      return paramValueParsers.joinToString("\n") { parser ->
         val parserPrintout = parser.printout()
         (if (nameless) "" else "--$name" +
               (if (parser.seperateValueArgs() != null) " " else (if (parserPrintout.startsWith("[")) "" else ":"))) +
               parserPrintout +
               (if (required) " (required)" else "") +
               (if (description != null) "${ArgParser.descriptionMarker}$description" else "")
      }
   }
}