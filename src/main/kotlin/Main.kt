import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.switch
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class AsciiConverter : CliktCommand(help="A program to convert a source image to an ascii image") {

    //files
    private val source by argument(help="Source file").file(mustExist = true)
    private val destinationDir by argument(help="output dir").file(canBeFile = false, canBeSymlink = false)

    //options
    private val outputName by option("-o", "--output-name", help="Name the outputted file will use.")
    private val resolution by option("-r", "--resolution", help="Factor to reduce input file resolution by.").int().default(3)
    private val useColor by option("-c", "--color", help="Outputted image will include color.").switch(Pair("-c", "c"), Pair("--color", "c"))
    private val asciiValue by option("-a", "--ascii", help="Overrides the default ascii characters used, first to last is darkest to lightest.")
        .default(""" .'`^"\,:;Il!i><~+_-?][}{1)(|\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$""")
    private val asciiResolution by option("-t", "--text-resolution", help="Sets the resolution the ascii characters will be outputted with.")
        .int().default(20)

    override fun run() {

        //read image from file
        val sourceImage = ImageIO.read(source)


        val baseResolutionX = sourceImage.width
        val baseResolutionY = sourceImage.height
        val filteredResolutionX = baseResolutionX / resolution
        val filteredResolutionY = baseResolutionY / resolution

        //get int representation of bitwise stored color values from source
        //val rawRGB = sourceImage.getRGB(0,0, baseResolutionX, baseResolutionY, null, 0, baseResolutionX)
        val rawRGB2D = MutableList(baseResolutionY) {y ->  MutableList(baseResolutionX) {x -> sourceImage.getRGB(x, y)} } //initialize


        //filter by resolution to reduce size of picture
        //filter lists first
        val rawFilteredRGB = rawRGB2D
            .filterIndexed {index, _ ->
                index % resolution == 0
            }.toMutableList()

        //then individual pixels
        rawFilteredRGB.forEachIndexed { index, list ->
            rawFilteredRGB[index] = list.filterIndexed { innerIndex, _ ->
                innerIndex % resolution == 0
            }.toMutableList()
        }

        //map to color objects
        val rgb2D = rawFilteredRGB.map { line -> line.map{ Color(it) }}

        //take average for value
        val values2D = rgb2D.map {line -> line.map { (it.red + it.blue + it.green) / 3}}

        //normalize the values to fit string indexing
        val normalizedValues2D = values2D.map {lines -> lines.map {((asciiValue.length - 1) * it) / 255}}

        //convert to ascii
        val ascii2D = normalizedValues2D.map {lines -> lines.map {asciiValue[it]}}

        //Create Image to write to
        val writeImage = BufferedImage(filteredResolutionX * asciiResolution, filteredResolutionY * asciiResolution, BufferedImage.TYPE_INT_RGB)

        //convert to graphics object
        val graphics = writeImage.createGraphics()
        graphics.background = Color.BLACK
        graphics.font = Font(graphics.font.fontName, graphics.font.style, asciiResolution)

        //print characters onto image
        ascii2D.forEachIndexed {rawY, line -> line.forEachIndexed {rawX, char ->
            val x = rawX * asciiResolution
            val y = rawY * asciiResolution

            graphics.color = if (useColor.isNullOrEmpty()) Color.WHITE else rgb2D[rawY][rawX]
            graphics.drawString(char.toString(), x, y)
        }}
        graphics.dispose()

        //create file to output to
        //name will either be source-ascii or from option string
        val outputFile = File("$destinationDir/${if (outputName.isNullOrEmpty()) "${source.nameWithoutExtension}-ascii.png" else "$outputName.png"}")
        ImageIO.write(writeImage, "png", outputFile)
    }
}

fun main(args: Array<String>) = AsciiConverter().main(args)