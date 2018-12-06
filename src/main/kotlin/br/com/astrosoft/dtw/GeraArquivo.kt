package br.com.astrosoft.dtw

import java.io.File

class GeraArquivo(val arquvioZeroCSV: String, val arquvioNoZeroCSV: String) {
  fun execute(seqDados: List<DadosAtivoPDF>) {
    val chaveZerada = seqDados.filter { it.mesAno == "DEZ/2017" && it.valorSaldoResidual == 0.00 }
      .map { ChaveAtivoPDF(it.codigoConta, it.codigoItem) }
      .distinct()
    val dadosZerado = seqDados.filter { chaveZerada.contains(ChaveAtivoPDF(it.codigoConta, it.codigoItem)) }
    val dadosNoZerado = seqDados.filter { !chaveZerada.contains(ChaveAtivoPDF(it.codigoConta, it.codigoItem)) }
    produzArquivos(arquvioZeroCSV, dadosZerado)
    produzArquivos(arquvioNoZeroCSV, dadosNoZerado)
  }

  private fun produzArquivos(arquvioCSV: String, dados: List<DadosAtivoPDF>) {
    File(arquvioCSV).bufferedWriter().use { out ->
      dados.sortedWith(compareBy({it.codigoConta}, {it.codigoItem}, {it.anoMes}))
        .forEach {registro ->
          out.write(registro.linhaDados())
          out.newLine()
        }
    }
  }
}

private fun DadosAtivoPDF.linhaDados(): String {
  return listOf(mesAno, codigoConta, descricaoConta)
    .joinToString(separator = ";")
}
