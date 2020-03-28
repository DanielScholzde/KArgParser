package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException
import de.danielscholz.kargparser.IValueParamParser
import java.io.File

class FileValueParamParser(private val checkIsDir: Boolean = false,
                           private val checkIsFile: Boolean = false,
                           callback: ((File) -> Unit)? = null) : IValueParamParser<File> {

   override var callback: ((File) -> Unit)? = null
   private var value: File? = null

   init {
      this.callback = callback
   }

   override fun seperateValueArgs(): IntRange? {
      return 1..1
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue != ""
   }

   override fun assign(rawValue: String) {
      val file = File(rawValue)
      if (checkIsFile && !file.isFile) throw ArgParseException("$file is no file!")
      if (checkIsDir && !file.isDirectory) throw ArgParseException("$file is no directory!")
      value = file
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw ArgParseException("callback must be specified!")
   }

   override fun printout(): String {
      return "file"
   }
}