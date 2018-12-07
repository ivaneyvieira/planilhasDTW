package br.com.astrosoft.dtw

import java.time.LocalDate
import kotlin.math.roundToInt

val mapMes =
  mapOf("JAN" to "01", "FEV" to "02", "MAR" to "03",
        "ABR" to "04", "MAI" to "05", "JUN" to "06",
        "JUL" to "07", "AGO" to "08", "SET" to "09",
        "OUT" to "10", "NOV" to "11", "DEZ" to "12")

data class DadosAtivoPDF(
  val pagina: Int,
  val codigoConta: String,
  val descricaoConta: String,
  val codigoItem: String,
  val descricaoItem: String,
  val dataEntrada: LocalDate,
  val valorOriginal: Double,
  val quantIndice: Double,
  val valorTaxa: Double,
  val mesAno: String?,
  val valorCorrigido: Double?,
  val valorDepAcum: Double?,
  val valorSaldoResidual: Double?
                        ) {
  val anoMes: String = anoMes()
  var sequencial: Int = 0

  fun anoMes(): String {
    mesAno ?: return ""
    val partes = mesAno.split("/".toRegex())
    return "${partes.getOrNull(1)}${mapMes[partes.getOrNull(0)]}"
  }

  val quantVidaUtil: Int =
    if (valorSaldoResidual == null || valorOriginal == 0.00 || valorTaxa == 0.00) 0
    else
      (12 * ((valorSaldoResidual / valorOriginal) / (valorTaxa / 100))).roundToInt()
}

data class ChaveAtivoPDF(
  val codigoConta: String,
  val codigoItem: String
                        )