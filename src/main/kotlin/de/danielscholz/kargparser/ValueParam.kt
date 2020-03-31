package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ValueParam(internal val name: String? = null, private val description: String? = null, private val required: Boolean = false) : IParam {

   init {
      if (name != null && !name.matches(Regex("[a-zA-Z]+[0-9a-zA-Z_-]*"))) {
         throw IllegalArgumentException("Name of the parameter contains not allowed characters: '$name'")
      }
   }

   private var argParser: ArgParser<*>? = null

   private val paramValueParsers: MutableList<IValueParamParser<*>> = mutableListOf()
   private var matchedValueParamParser: IValueParamParser<*>? = null

   fun addParser(parser: IValueParamParser<*>): ValueParam {
      paramValueParsers.add(parser)
      return this
   }

   override fun init(parentArgParser: ArgParser<*>) {
      argParser = parentArgParser
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

      if (matchedValueParamParser != null) return false

      return (name != null && arg.equals("--$name", ignoreCase)) ||
            (name != null && arg.startsWith("--$name:", ignoreCase)) ||
            (name == null && paramValueParsers.size == 1 && paramValueParsers[0].seperateValueArgs() != null && noArgsFollowing())
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {
      val singleRawValue = when {
         name != null && arg == "--$name" -> ""
         name != null && arg.startsWith("--$name:") -> arg.substring(name.length + 3)
         else -> arg
      }

      for (paramValueParser in paramValueParsers) {
         if (paramValueParser.seperateValueArgs() != null) {
            val seperateValueArgs = paramValueParser.seperateValueArgs()!!
            var assigned = 0
            val offset = if (name == null) 0 else 1
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
               var msg = "Number of parameter values ($assigned) is too few for parameter '${name ?: description}'. "
               msg += when {
                  seperateValueArgs.first == seperateValueArgs.last -> "${seperateValueArgs.first} parameter values are expected."
                  seperateValueArgs.last == Int.MAX_VALUE -> "At least ${seperateValueArgs.first} parameter values are expected."
                  else -> "${seperateValueArgs.first} to ${seperateValueArgs.last} parameter values are expected."
               }
               throw ArgParseException(msg, argParser!!)
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
         throw ArgParseException("Value for parameter '${name ?: description}' could not be processed: $singleRawValue", argParser!!)
      }
   }

   override fun checkRequired() {
      if (required && matchedValueParamParser == null) {
         throw ArgParseException("Required parameter '${name ?: description}' is not given", argParser!!)
      }
   }

   override fun deferrExec(): Boolean {
      return false
   }

   override fun exec() {
      matchedValueParamParser?.exec()
   }

   override fun reset() {
      matchedValueParamParser = null
   }

   override fun printout(e: ArgParseException?): String {
      return paramValueParsers.joinToString("\n") { parser ->
         val parserPrintout = parser.printout()
         (if (name == null) "" else "--$name" +
               (if (parser.seperateValueArgs() != null) " " else (if (parserPrintout.startsWith("[")) "" else ":"))) +
               parserPrintout +
               (if (required) " (required)" else "") +
               (if (description != null) "${ArgParser.descriptionMarker}$description" else "")
      }
   }

   internal fun nameless() = name == null

}