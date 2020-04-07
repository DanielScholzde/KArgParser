package de.danielscholz.kargparser

class Config(var ignoreCase: Boolean = false,
             var prefixStr: String = "--",
             var noPrefixForActionParams: Boolean = false,
             var onlyFilesAsSeperateArgs: Boolean = false)