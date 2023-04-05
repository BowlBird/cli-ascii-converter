import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

fun convertToAsciiImage(
    source: BufferedImage,
    resolution: Int,
    asciiValue: String,
    asciiResolution: Int,
    useColor: Boolean
): BufferedImage {

    val baseResolution = Pair(source.width, source.height)
    val filteredResolution = Pair(
        resolution,
        (resolution * (baseResolution.second.toDouble() / baseResolution.first)).roundToInt()
    )
    //used to accurately index rgb from source
    val resolutionRatio = Pair(
        baseResolution.first.toDouble() / filteredResolution.first,
        baseResolution.second.toDouble() / filteredResolution.second,
    )

    //get int representation of bitwise stored color values from source
    val rawFilteredRGB2D = MutableList(filteredResolution.second) {y ->
        MutableList(filteredResolution.first) {x ->
            //if statement filters when to call getRGB as it is quite intensive
            source.getRGB((x * resolutionRatio.first).roundToInt(), (y * resolutionRatio.second).roundToInt()) //initialize
        }
    }

    //map to value
    val values2D = rawFilteredRGB2D.map { line -> line.map{

        //taken from java library to avoid constructing Color objects
        val bitRGB = -16777216 or it
        val rgb = Triple(
            bitRGB shr 16 and 255,
            bitRGB shr 8 and 255,
            bitRGB shr 0 and 255
        )

        val value = (rgb.first + rgb.second + rgb.third) / 3
        value
    }}

    //normalize the values to fit string indexing
    val normalizedValues2D = values2D.map {lines -> lines.map {((asciiValue.length - 1) * it) / 255}}

    //convert to ascii
    val ascii2D = normalizedValues2D.map {lines -> lines.map {asciiValue[it]}}

    //Create Image to write to
    val writeImage = BufferedImage(filteredResolution.first * asciiResolution, filteredResolution.second * asciiResolution, BufferedImage.TYPE_INT_RGB)

    //convert to graphics object
    val graphics = writeImage.createGraphics()
    graphics.font = Font(graphics.font.fontName, graphics.font.style, asciiResolution)

    //print characters onto image
    ascii2D.forEachIndexed {rawY, line -> line.forEachIndexed {rawX, char ->
        val x = rawX * asciiResolution
        val y = rawY * asciiResolution

        graphics.color = if (useColor) Color(rawFilteredRGB2D[rawY][rawX]) else Color.WHITE
        graphics.drawString(char.toString(), x, y + asciiResolution) //offset to ensure all strings are on screen
    }}
    graphics.dispose()

    return writeImage
}

fun convertToAsciiText(
    source: BufferedImage,
    resolution: Int,
    asciiValue: String,
): String {
    val baseResolution = Pair(source.width, source.height)
    val filteredResolution = Pair(
        resolution,
        (resolution * (baseResolution.second.toDouble() / baseResolution.first)).roundToInt()
    )
    //used to accurately index rgb from source
    val resolutionRatio = Pair(
        baseResolution.first.toDouble() / filteredResolution.first,
        baseResolution.second.toDouble() / filteredResolution.second,
    )

    //get int representation of bitwise stored color values from source
    val rawFilteredRGB2D = MutableList(filteredResolution.second) {y ->
        MutableList(filteredResolution.first) {x ->
            //if statement filters when to call getRGB as it is quite intensive
            source.getRGB((x * resolutionRatio.first).roundToInt(), (y * resolutionRatio.second).roundToInt()) //initialize
        }
    }

    //map to value
    val values2D = rawFilteredRGB2D.map { line -> line.map{

        //taken from java library to avoid constructing Color objects
        val bitRGB = -16777216 or it
        val rgb = Triple(
            bitRGB shr 16 and 255,
            bitRGB shr 8 and 255,
            bitRGB shr 0 and 255
        )

        val value = (rgb.first + rgb.second + rgb.third) / 3
        value
    }}

    //normalize the values to fit string indexing
    val normalizedValues2D = values2D.map {lines -> lines.map {((asciiValue.length - 1) * it) / 255}}

    //convert to ascii
    val ascii2D = normalizedValues2D.map {lines -> lines.map {asciiValue[it]}}

    //coverts 2d array into string with newlines at the end of each line.
    return ascii2D.joinToString(separator = "") { "${it.joinToString(separator = "")}\n" }
}
