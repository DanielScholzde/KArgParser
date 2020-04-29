## KArgParser

#### Program argument parser written in Kotlin and designed for Kotlin applications

The main features are: very easy to use, small code size and type safety.

Other features are:
- subtree parsing
- provision of a DSL
- support for own parameter parser
- possibility to register different parsers for one parameter value
- program arguments are recognizable in any order (especially when using subparsers)
- no dependencies other than Kotlin

Example:

    class Parameter(var foo: Int = 0, var bar: String? = null)
    
    val parameter = Parameter()
    
    try {
    
        ArgParserBuilder(parameter).buildWith {
            add(parameter::foo, IntParam(), "Description for foo", required = true)
            add(parameter::bar, StringParam(), "Description for bar")
        }
        .parseArgs(arrayOf("--bar", "Penny", "--foo", "42"))
    
    } catch (e: ArgParseException) {
        println(parser.printout(e))
        return
    }
    
    // parameter.foo == 42
    // parameter.bar == "Penny"
    


Complex example:

    class MainParams {
        var ignoreCase = false
    }

    class CompareFilesParams {
        var sourceFile: File? = null
        var targetFile: File? = null
    }
    
    class FindDuplicatesParams {
        var directories: List<File> = listOf()
    }

    val parser = ArgParserBuilder(MainParams()).buildWith {
        val mainParamValues = paramValues // is necessary to access its data for compareFiles/findDuplicates methodcall
        
        add(mainParamValues::ignoreCase, BooleanParam(), "Ignore case when comparing file contents")
        
        addActionParser("compareFiles", ArgParserBuilder(CompareFilesParams()).buildWith {
            addNamelessLast(paramValues::sourceFile, FileParam(checkIsFile = true), required = true)
            addNamelessLast(paramValues::targetFile, FileParam(checkIsFile = true), required = true)
        }) {
            compareFiles(data.sourceFile, data.targetFile, mainParamValues.ignoreCase)
        }
        
        addActionParser("findDuplicates", ArgParserBuilder(FindDuplicatesParams()).buildWith {
            addNamelessLast(paramValues::directories, FilesParam(1..Int.MAX_VALUE, checkIsDir = true), required = true)
        }) {
            findDuplicates(data.directories, mainParamValues.ignoreCase)
        }
    }
    
    try {
        parser.parseArgs(arrayOf("--compareFiles", "--ignoreCase", "file1.txt", "file2.txt"))
    } catch (e: ArgParseException) {
        println(parser.printout(e))
    }
    
    fun compareFiles(file1: File, file2: File, ignoreCase: Boolean) {
        //
    }
    
    fun findDuplicates(directories: List<File>, ignoreCase: Boolean) {
        //
    }