package de.danielscholz.kargparser

import de.danielscholz.kargparser.ArgParser.Argument

class ValueParam(internal val name: String? = null, internal val description: String? = null, internal val required: Boolean = false) : IParam {

   init {
      if (name != null && !name.matches(Regex("[a-zA-Z]+[0-9a-zA-Z_-]*"))) {
         throw IllegalArgumentException("Name of the parameter contains not allowed characters: '$name'")
      }
   }

   private var argParser: ArgParser<*>? = null
   private var config: Config = ArgParser.defaultConfig

   private val paramValueParsers: MutableList<IValueParamParser<*>> = mutableListOf()
   private var matchedValueParamParser: IValueParamParser<*>? = null

   fun addParser(parser: IValueParamParser<*>): ValueParam {
      paramValueParsers.add(parser)
      return this
   }

   override fun init(argParser: ArgParser<*>, config: Config) {
      this.argParser = argParser
      this.config = config
      paramValueParsers.forEach {
         it.init(argParser, config)
         it.numberOfSeperateValueArgsToAccept()?.let { range ->
            if (range.first < 1) throw Exception("Minimum argument value count of '${range.first}' is smaller than 1")
            if (range.first > range.last) throw Exception("Minimum argument value count '${range.first}' is bigger than maximum argument count '${range.last}'")
         }
      }
   }

   override fun matches(arg: String, idx: Int, allArguments: List<Argument>): Boolean {
      fun noParameterFollowing(): Boolean {
         for (i in idx..allArguments.lastIndex) {
            if (allArguments[i].value.startsWith(config.prefixStr)) {
               return false
            }
         }
         return true
      }

      if (matchedValueParamParser != null) return false // this parameter already matched an argument

      return (!nameless() && arg.equals("${config.prefixStr}$name", config.ignoreCase)) ||
            (!nameless() && arg.startsWith("${config.prefixStr}$name:", config.ignoreCase)) ||
            (nameless() && paramValueParsers.size == 1 && paramValueParsers[0].numberOfSeperateValueArgsToAccept() != null && noParameterFollowing())
   }

   override fun assign(arg: String, idx: Int, allArguments: List<Argument>) {
      val singleRawValue = when {
         !nameless() && arg.equals("${config.prefixStr}$name", config.ignoreCase) -> ""
         !nameless() && arg.startsWith("${config.prefixStr}$name:", config.ignoreCase) -> arg.substring(name!!.length + config.prefixStr.length + 1).trim()
         else -> arg
      }

      for (paramValueParser in paramValueParsers) {
         if (paramValueParser.numberOfSeperateValueArgsToAccept() != null) {
            val seperateValueArgs = paramValueParser.numberOfSeperateValueArgsToAccept()!!
            var assigned = 0
            val offset = if (nameless()) 0 else 1
            for (i in 1..seperateValueArgs.last) {
               if (idx + i - 1 + offset > allArguments.lastIndex) {
                  if (matchedValueParamParser == null && seperateValueArgs.first == 0) {
                     matchedValueParamParser = paramValueParser
                  }
                  break
               }
               val arg1 = allArguments[idx + i - 1 + offset]
               if (arg1.value.startsWith(config.prefixStr)) {
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
         (if (nameless()) "" else "${config.prefixStr}$name" +
               (if (parser.numberOfSeperateValueArgsToAccept() != null) " " else (if (parserPrintout.startsWith("[")) "" else ":"))) +
               parserPrintout +
               (if (required) " (required)" else "") +
               (if (description != null) "${ArgParser.descriptionMarker}$description" else "")
      }
   }

   internal fun nameless() = name == null

}