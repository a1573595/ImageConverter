import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Android
 * 48x48（1.0x 基準）- 中密度 (mdpi)
 * 72x72 (1.5x) - 高密度 (hdpi)
 * 96x96 (2.0x) - 超高密度 (xhdpi)
 * 144x144 (3.0x) - 超超高密度 (xxhdpi)
 * 192x192 (4.0x) - 超超超高密度 (xxxhdpi)
 */
const val mdpi = 48
const val hdpi = 72
const val xdpi = 96
const val xxdpi = 144
const val xxxdpi = 192

/**
 * iOS
 * 60x60（@1x）
 * 120x120 (@2x)
 * 180x180 (@3x)
 */
const val dpi1x = 60
const val dpi2x = 120
const val dpi3x = 180

fun main() {
    println("Image converter for Android/iOS icon")

    val root = System.getProperty("user.dir")
    val androidPath = "${root}/Android"
    val iOSPath = "${root}/iOS"

    val jpgRegex = Regex("jpe?g")
    val pngRegex = Regex("png")

    var total = 0
    var success = 0
    var failed = 0

    File("${androidPath}/mdpi").mkdirs()
    File("${androidPath}/hdpi").mkdirs()
    File("${androidPath}/xdpi").mkdirs()
    File("${androidPath}/xxdpi").mkdirs()
    File("${androidPath}/xxxdpi").mkdirs()

    File("${iOSPath}/dpi1x").mkdirs()
    File("${iOSPath}/dpi2x").mkdirs()
    File("${iOSPath}/dpi3x").mkdirs()

    File(root).listFiles()?.forEach {
        if (it.isFile) {
            val isJPG = jpgRegex.matches(it.name.substringAfter("."))
            val isPng = pngRegex.matches(it.name.substringAfter("."))

            if (isJPG || isPng) {
                print("$it\t")

                runCatching {
                    total++

                    val img = ImageIO.read(it)

                    val maxValue = img.width.coerceAtLeast(img.height).toFloat()
                    val mScale = maxValue / mdpi
                    val hScale = maxValue / hdpi
                    val xScale = maxValue / xdpi
                    val xxScale = maxValue / xxdpi
                    val xxxScale = maxValue / xxxdpi

                    val dpi1xScale = maxValue / dpi1x
                    val dpi2xScale = maxValue / dpi2x
                    val dpi3xScale = maxValue / dpi3x

                    val mdpiImg = scaleImage(img, mScale)
                    val hdpiImg = scaleImage(img, hScale)
                    val xdpiImg = scaleImage(img, xScale)
                    val xxdpiImg = scaleImage(img, xxScale)
                    val xxxdpiImg = scaleImage(img, xxxScale)

                    val dpi1xImg = scaleImage(img, dpi1xScale)
                    val dpi2xImg = scaleImage(img, dpi2xScale)
                    val dpi3xImg = scaleImage(img, dpi3xScale)

                    ImageIO.write(
                        mdpiImg.toBufferedImage(isJPG),
                        if (isJPG) "jpeg" else "png",
                        File("${androidPath}/mdpi/${it.name}")
                    )
                    ImageIO.write(
                        hdpiImg.toBufferedImage(isJPG),
                        if (isJPG) "jpeg" else "png",
                        File("${androidPath}/hdpi/${it.name}")
                    )
                    ImageIO.write(
                        xdpiImg.toBufferedImage(isJPG),
                        if (isJPG) "jpeg" else "png",
                        File("${androidPath}/xdpi/${it.name}")
                    )
                    ImageIO.write(
                        xxdpiImg.toBufferedImage(isJPG),
                        if (isJPG) "jpeg" else "png",
                        File("${androidPath}/xxdpi/${it.name}")
                    )
                    ImageIO.write(
                        xxxdpiImg.toBufferedImage(isJPG),
                        if (isJPG) "jpeg" else "png",
                        File("${androidPath}/xxxdpi/${it.name}")
                    )

                    ImageIO.write(
                        dpi1xImg.toBufferedImage(isJPG),
                        if (isJPG) "jpeg" else "png",
                        File("$iOSPath/dpi1x/${it.name}")
                    )
                    ImageIO.write(
                        dpi2xImg.toBufferedImage(isJPG),
                        if (isJPG) "jpeg" else "png",
                        File("$iOSPath/dpi2x/${it.name}")
                    )
                    ImageIO.write(
                        dpi3xImg.toBufferedImage(isJPG),
                        if (isJPG) "jpeg" else "png",
                        File("$iOSPath/dpi3x/${it.name}")
                    )
                }
                    .onSuccess {
                        println(" Success.")
                        success++
                    }
                    .onFailure {
                        println(" Failed.")
                        failed++
                    }
            }
        }
    }

    println("Total:$total, Success:$success, Failed:$failed.")
}

fun scaleImage(img: BufferedImage, scale: Float): Image =
    img.getScaledInstance((img.width / scale).toInt(), (img.height / scale).toInt(), Image.SCALE_SMOOTH)

/**
 * Converts a given Image into a BufferedImage
 *
 * @param img The Image to be converted
 * @return The converted BufferedImage
 */
fun Image.toBufferedImage(isJPG: Boolean): BufferedImage {
    if (this is BufferedImage) {
        return this
    }

    val bufferedImg = BufferedImage(
        this.getWidth(null), this.getHeight(null),
        if (isJPG) {
            BufferedImage.TYPE_INT_RGB
        } else {
            BufferedImage.TYPE_INT_ARGB
        }
    )

    val bGr = bufferedImg.createGraphics()
    bGr.drawImage(this, 0, 0, null)
    bGr.dispose()

    return bufferedImg
}