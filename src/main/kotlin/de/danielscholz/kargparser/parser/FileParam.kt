package de.danielscholz.kargparser.parser

import de.danielscholz.kargparser.ArgParseException
import java.io.File

class FileParam(private val checkIsDir: Boolean = false,
                private val checkIsFile: Boolean = false,
                callback: ((File) -> Unit)? = null) : ParamParserBase<File, File?>() {

   override var callback: ((File) -> Unit)? = null
   private var value: File? = null

   init {
      this.callback = callback
   }

   override fun numberOfSeparateValueArgsToAccept(): IntRange? {
      return 1..1
   }

   override fun matches(rawValue: String): Boolean {
      return rawValue != ""
   }

   override fun assign(rawValue: String) {
      val file = File(rawValue)
      if (checkIsFile && !file.isFile) throw ArgParseException("$file is no file!", argParser!!)
      if (checkIsDir && !file.isDirectory) throw ArgParseException("$file is no directory!", argParser!!)
      value = file
   }

   override fun exec() {
      callback?.invoke(value!!) ?: throw ArgParseException("callback must be specified!", argParser!!)
   }

   override fun printout(): String {
      return "file"
   }
}