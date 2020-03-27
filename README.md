# KArgParser
Programm Argument Parser written in Kotlin and designed for Kotlin applications

The main features are: very easy to use, small code size and type safety.

Other features are: Subtree-Parsing, provision of a DSL, and the possibility to register different parsers for one parameter value. 

Examples:

    var foo = 0
    
    ArgParserBuilderSimple()
        .add("foo", IntValueParamParser { foo = it })
        .build()
        .parseArgs(arrayOf("--foo:5"))
    
    // foo == 5


Complex example:

    class MainParams {
        var ignoreCase = false
    }

    class CompareFilesParams {
        var files: List<File> = listOf()
    }

    ArgParserBuilder(MainParams()).buildWith {
        val mainParams = data // is necessary to access its data for compareFiles methodcall
        add(data::ignoreCase, BooleanValueParamParser())
        addActionParser("compareFiles", ArgParserBuilder(CompareFilesParams()).buildWith {
            addNamelessLast(data::files, FilesValueParamParser(2..2, checkIsFile = true))
        }) {
          compareFiles(data.files[0], data.files[1], mainParams.ignoreCase)
        }
    }
    .parseArgs(arrayOf("--ignoreCase", "compareFiles", "file1.txt", "file2.txt"))
    
    fun compareFiles(file1: File, file2: File, ignoreCase: Boolean) {
        //
    }