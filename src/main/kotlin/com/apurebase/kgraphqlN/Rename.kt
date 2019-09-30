package com.apurebase.kgraphqlN

import com.apurebase.kgraphql.RequestException


/**
 * 2 Language
 * 2.1 Source Text
 */
internal object SourceText {
    const val unicodeBOM = '\uFEFF'
    const val comment = '#'
    const val comma = ','
    val sourceText = '\u0020'..'\uFFFF'
    val whiteSpaces = listOf('\u0009', '\u0020')
    val lineTerminators = listOf('\u000A', '\u000D')

    val ignoredTokens = listOf(
        unicodeBOM,
        *whiteSpaces.toTypedArray(),
        *lineTerminators.toTypedArray(),
        comment,
        comma
    )

    val punctuators = listOf("!", "$", "(", ")", "...", ":", "=", "@", "[", "]", "{", "|", "}")

    val nameRegex = Regex("[_A-Za-z][_0-9A-Za-z]*")
}


internal fun validateAndFilterRequest(request : String) : String{
    return request
        .validateCharacters()
        .dropUnicodeBOM()
        .filterComments()
}

private fun String.dropUnicodeBOM() : String {
    return if(startsWith('\uFEFF')){
        this.drop(1)
    } else {
        this
    }
}

private fun String.filterComments() = replace(Regex("${SourceText.comment}.*\n"), "")

//validate characters, will be moved because according to spec restrictions are only on documents tokens
private fun String.validateCharacters(): String {
    forEach { char ->
        when (char) {
            in SourceText.whiteSpaces, in SourceText.lineTerminators, in SourceText.sourceText -> { }
            else -> throw RequestException("Illegal character: ${String.format("\\u%04x", char.toInt())}")
        }
    }
    return this
}
