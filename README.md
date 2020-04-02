# KArgParser
Program Argument Parser written in Kotlin and designed for Kotlin applications

The main features are: very easy to use, small code size and type safety.

Other features are: Subtree-Parsing, provision of a DSL, and the possibility to register different parsers for one parameter value. 
The parser is designed so that the parameters can be recognized in any order, especially when using subparsers.

Example:

    var foo = 0
    try {
        ArgParserBuilderSimple()
            .add("foo", IntValueParamParser { foo = it }, "Description for foo", required = true)
            .build()
            .parseArgs(arrayOf("--foo:5"))
        
        // foo == 5
        ...
    } catch (e: ArgParseException) {
        println(parser.printout(e))
    }
    


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
        
        add(paramValues::ignoreCase, BooleanValueParamParser(), "Ignore case when comparing file contents")
        
        addActionParser("compareFiles", ArgParserBuilder(CompareFilesParams()).buildWith {
            addNamelessLast(paramValues::sourceFile, FileValueParamParser(checkIsFile = true), required = true)
            addNamelessLast(paramValues::targetFile, FileValueParamParser(checkIsFile = true), required = true)
        }) {
            compareFiles(data.sourceFile, data.targetFile, mainParamValues.ignoreCase)
        }
        
        addActionParser("findDuplicates", ArgParserBuilder(FindDuplicatesParams()).buildWith {
            addNamelessLast(paramValues::directories, FilesValueParamParser(1..Int.MAX_VALUE, checkIsDir = true), required = true)
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