import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.switch
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis



class AsciiConverter : CliktCommand(help="A program to convert a source image to an ascii image") {
    //files
    private val source by argument(help="Source file").file(mustExist = true)
    private val destinationDir by argument(help="output dir").file(canBeFile = false, canBeSymlink = false)

    //options
    private val outputName by option("-o", "--output-name", help="Name the outputted file will use.")
    private val resolution by option("-r", "--resolution", help="Factor to reduce input file resolution by.").int().default(200).check { it > 0 }
    private val useColor by option("-c", "--color", help="Outputted image will include color.").switch(Pair("-c", "c"), Pair("--color", "c"))
    private val asciiValue by option("-a", "--ascii", help="Overrides the default ascii characters used, first to last is darkest to lightest.")
        .default(""" .'`^"\,:;Il!i><~+_-?][}{1)(|\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$""")
    private val asciiResolution by option("-t", "--text-resolution", help="Sets the resolution the ascii characters will be outputted with.")
        .int().default(20)

    override fun run() {

        //read image from file
        val sourceImage = ImageIO.read(source)

        //process
        val asciiImage = convertToAscii(sourceImage, resolution, asciiValue, asciiResolution, !useColor.isNullOrEmpty())

        //create file to output to
        //name will either be source-ascii or from option string
        val outputFile = File("$destinationDir/${if (outputName.isNullOrEmpty()) "${source.nameWithoutExtension}-ascii.png" else "$outputName.png"}")
        ImageIO.write(asciiImage, "png", outputFile)
    }
}

fun main(args: Array<String>)  {
    AsciiConverter().main(args)
}