package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParser
import de.danielscholz.kargparser.IValueParamParser

abstract class BaseParser<T> : IValueParamParser<T> {

   protected var argParser: ArgParser<*>? = null
   protected var config: ArgParser.Config = ArgParser.defaultConfig

   protected val intRange1to1 = 1..1

   override fun init(parentArgParser: ArgParser<*>, config: ArgParser.Config) {
      argParser = parentArgParser
      this.config = config
   }

   override fun numberOfSeperateValueArgsToAccept(): IntRange? {
      return if (config.onlyFilesAsSeperateArgs) null else intRange1to1
   }

}