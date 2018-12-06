package br.com.astrosoft.dtw

import java.time.LocalDate

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

  fun anoMes(): String {
    mesAno ?: return ""
    val partes = mesAno.split("/".toRegex())
    return "${partes.getOrNull(1)}${mapMes[partes.getOrNull(0)]}"
  }
}

data class ChaveAtivoPDF(
  val codigoConta: String,
  val codigoItem: String
                        )