package br.com.astrosoft.dtw

import com.opencsv.CSVWriter
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.FileWriter

fun main(args: Array<String>) {
  val arquvioTXT = "/home/ivaneyvieira/Dropbox/tablepdf/tabelaKadja.txt"
  val arquvioZeroExcel = "/home/ivaneyvieira/Dropbox/tablepdf/ativoZero.xlsx"
  val arquvioNoZeroExcel = "/home/ivaneyvieira/Dropbox/tablepdf/ativoNoZero.xlsx"
  val arquvioExcel = "/home/ivaneyvieira/Dropbox/tablepdf/ativo.xlsx"
  val processador = Processamento(arquvioTXT)
  val seqDados = processador.execute()
  val gerador = GeraArquivo(arquvioZeroExcel, arquvioNoZeroExcel, arquvioExcel)
  gerador.execute(seqDados)
  val fileWriter = FileWriter("/home/ivaneyvieira/Dropbox/tablepdf/dados.csv")
  val mappingStrategy = ColumnPositionMappingStrategy<DadosAtivoPDF>()
  mappingStrategy.type = DadosAtivoPDF::class.java
  val listCol = listOf("pagina", "codigoConta", "descricaoConta", "codigoItem", "descricaoItem", "dataEntrada",
                       "valorOriginal", "quantIndice", "valorTaxa", "mesAno", "valorCorrigido", "valorDepAcum",
                       "valorSaldoResidual", "sequencial", "anoMes", "quantVidaUtil", "quantVidaMeses")
  val pairs = listCol.map { Pair(it, it) }
  val mapCol = mapOf(* pairs.toTypedArray() )
  mappingStrategy.setColumnMapping(* listCol.toTypedArray())
  val beanToCsv = StatefulBeanToCsvBuilder<DadosAtivoPDF>(fileWriter)
    .withMappingStrategy(mappingStrategy)
    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
    .withThrowExceptions(true)
    .build()
//"pagina", "codigoConta", "descricaoConta", "codigoItem", "descricaoItem", "dataEntrada", "valorOriginal", "quantIndice", "valorTaxa", "mesAno", "valorCorrigido", "valorDepAcum", "valorSaldoResidual", "sequencial", "anoMes", "quantVidaUtil", "quantVidaMeses"
  beanToCsv.write(seqDados)
}