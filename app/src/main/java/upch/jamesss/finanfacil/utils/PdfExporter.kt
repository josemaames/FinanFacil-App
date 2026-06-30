package upch.jamesss.finanfacil.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun exportReport(
        context: Context,
        usuario: String,
        total: Double,
        presupuesto: Double,
        disponible: Double,
        promedio: Double,
        categoria: String,
        porcentaje: Int,
        ranking: List<Pair<String, Double>>,
        comparacion: String
    ): File {

        val pdf = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(
            595,
            1000,
            1
        ).create()

        val page = pdf.startPage(pageInfo)

        val canvas = page.canvas

        val titulo = Paint().apply {
            color = Color.rgb(33, 75, 170)
            textSize = 30f
            isFakeBoldText = true
        }

        val subtitulo = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isFakeBoldText = true
        }

        val texto = Paint().apply {
            color = Color.DKGRAY
            textSize = 16f
        }

        val negrita = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            isFakeBoldText = true
        }

        var y = 50f

        fun espacio(px: Float = 28f) {
            y += px
        }

        fun linea(
            contenido: String,
            paint: Paint = texto
        ) {
            canvas.drawText(
                contenido,
                40f,
                y,
                paint
            )
            y += 32f
        }

        // Encabezado

        canvas.drawRect(
            0f,
            0f,
            595f,
            85f,
            Paint().apply {
                color = Color.rgb(37,99,235)
            }
        )

        val tituloBlanco = Paint().apply{
            color = Color.WHITE
            textSize = 30f
            isFakeBoldText = true
        }

        canvas.drawText(
            "FinanFácil",
            40f,
            45f,
            tituloBlanco
        )

        canvas.drawText(
            "Reporte Financiero",
            40f,
            75f,
            Paint().apply{
                color = Color.WHITE
                textSize = 17f
            }
        )

        y = 120f

        espacio()

        linea(
            "Fecha: ${
                SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date())
            }"
        )

        linea("Usuario: $usuario")

        espacio()

        canvas.drawLine(
            40f,
            y,
            555f,
            y,
            Paint().apply{
                color = Color.rgb(37,99,235)
                strokeWidth = 3f
            }
        )

        y += 10f

        espacio()

        linea("RESUMEN GENERAL", subtitulo)

        espacio(10f)

        linea("Total gastado: S/ %.2f".format(total))

        linea("Presupuesto: S/ %.2f".format(presupuesto))

        linea("Disponible: S/ %.2f".format(disponible))

        linea("Promedio por gasto: S/ %.2f".format(promedio))

        espacio()

        canvas.drawLine(
            40f,
            y,
            555f,
            y,
            negrita
        )

        espacio()

        linea("CATEGORÍA PRINCIPAL", subtitulo)

        espacio(10f)

        linea(categoria, negrita)

        linea("Representa el $porcentaje% del total")

        espacio()

        canvas.drawLine(
            40f,
            y,
            555f,
            y,
            negrita
        )

        espacio()

        linea("RANKING DE CATEGORÍAS", subtitulo)

        espacio(10f)

        ranking.forEachIndexed { index, item ->

            val emoji =
                when (index) {
                    0 -> "🥇"
                    1 -> "🥈"
                    2 -> "🥉"
                    else -> "•"
                }

            linea(
                "$emoji ${item.first}    S/ %.2f"
                    .format(item.second)
            )
        }

        espacio()

        canvas.drawLine(
            40f,
            y,
            555f,
            y,
            negrita
        )

        espacio()

        linea("COMPARACIÓN MENSUAL", subtitulo)

        espacio(10f)

        comparacion
            .split("\n")
            .forEach {

                linea(it)

            }

        espacio()

        canvas.drawLine(
            40f,
            y,
            555f,
            y,
            negrita
        )

        espacio()

        linea("Generado automáticamente por", texto)
        linea("FinanFácil v1.0", negrita)

        pdf.finishPage(page)

        val downloads =
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )

        if (!downloads.exists()) {
            downloads.mkdirs()
        }

        val file = File(
            downloads,
            "FinanFacil_Reporte.pdf"
        )

        pdf.writeTo(file.outputStream())

        pdf.close()

        return file
    }
}