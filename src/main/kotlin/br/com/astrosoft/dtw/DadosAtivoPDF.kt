package br.com.astrosoft.dtw

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

val mapMes =
  mapOf("JAN" to "01", "FEV" to "02", "MAR" to "03",
        "ABR" to "04", "MAI" to "05", "JUN" to "06",
        "JUL" to "07", "AGO" to "08", "SET" to "09",
        "OUT" to "10", "NOV" to "11", "DEZ" to "12")

data class DadosAtivoPDF(
  val indice: Int,
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
  var sequencial: Int = 0
  val anoMes: String
    get() {
      mesAno ?: return ""
      val partes = mesAno.split("/".toRegex())
      return "${partes.getOrNull(1)}${mapMes[partes.getOrNull(0)]}"
    }
  val quantVidaUtil: Int
    get() {
      val dataFinal = dataEntrada.plusMonths(quantVidaUtilTotalMeses.toLong())
      val dataRef = LocalDate.of(2017, 12, 31)
      val day = dataEntrada.dayOfMonth
      val dif = ChronoUnit.MONTHS.between(dataRef, dataFinal).toInt() - if (day == 31) 1 else 0
      return if (dif < 0) 0 else dif
    }
  /*
      if (valorSaldoResidual == null || valorOriginal == 0.00 || valorTaxa == 0.00) 0
      else
        (12 * ((valorSaldoResidual / valorOriginal) / (valorTaxa / 100))).roundToInt()
        */
  val quantVidaUtilTotalMeses: Int =
    if (valorTaxa == 0.00) 0
    else (12 / (valorTaxa / 100)).roundToInt()
  val codigo: String
    get() {
      return DeParaAtivos.getByConta(codigoConta)?.padStart(4, '0') ?: ""
    }
}

data class ChaveAtivoPDF(
  val codigoConta: String,
  val codigoItem: String
                        )