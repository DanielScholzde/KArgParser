package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException
import de.danielscholz.kargparser.ArgParser
import de.danielscholz.kargparser.IValueParamParser
import java.io.File

class FilesValueParamParser(private val numberOfFiles: IntRange = 1..Int.MAX_VALUE,
                            private val checkIsDir: Boolean = false,
                            private val checkIsFile: Boolean = false,
                            callback: ((List<File>) -> Unit)? = null) : IValueParamParser<List<File>> {

   private var argParser: ArgParser<*>? = null

   override var callback: ((List<File>) -> Unit)? = null
   private var value: MutableList<File> = mutableListOf()

   init {
      this.callback = callback
   }

   override fun configure(parentArgParser: ArgParser<*>) {
      argParser = parentArgParser
   }

   override fun seperateValueArgs(): IntRange? {
      return numberOfFiles
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue != ""
   }

   override fun assign(rawValue: String) {
      val file = File(rawValue)
      if (checkIsFile && !file.isFile) throw ArgParseException("$file is no file!", argParser!!)
      if (checkIsDir && !file.isDirectory) throw ArgParseException("$file is no directory!", argParser!!)
      value.add(file)
   }

   override fun exec() {
      callback?.invoke(value) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "file1 file2 ..."
   }
}