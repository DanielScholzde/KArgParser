# KArgParser
Program Argument Parser written in Kotlin and designed for Kotlin applications

The main features are: very easy to use, small code size and type safety.

Other features are: Subtree-Parsing, provision of a DSL, and the possibility to register different parsers for one parameter value. 

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
        println("An error has occurred while processing the parameters: " + e.message)
        println("All supported parameters are:")
        println(parser.printout())
    }
    


Complex example:

    class MainParams {
        var ignoreCase = false
    }

    class CompareFilesParams {
        var files: List<File> = listOf()
    }

    val parser = ArgParserBuilder(MainParams()).buildWith {
        val mainParams = data // is necessary to access its data for compareFiles methodcall
        add(data::ignoreCase, BooleanValueParamParser(), "Ignore case when comparing file contents")
        addActionParser("compareFiles", ArgParserBuilder(CompareFilesParams()).buildWith {
            addNamelessLast(data::files, FilesValueParamParser(2..2, checkIsFile = true), required = true)
        }) {
          compareFiles(data.files[0], data.files[1], mainParams.ignoreCase)
        }
    }
    
    try {
       parser.parseArgs(arrayOf("--ignoreCase", "compareFiles", "file1.txt", "file2.txt"))
    } catch (e: ArgParseException) {
       println("An error has occurred while processing the parameters: " + e.message)
       println("All supported parameters are:")
       println(parser.printout())
    }
    
    fun compareFiles(file1: File, file2: File, ignoreCase: Boolean) {
        //
    }