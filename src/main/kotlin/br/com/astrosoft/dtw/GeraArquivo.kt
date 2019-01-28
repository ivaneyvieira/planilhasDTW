package br.com.astrosoft.dtw

import br.com.astrosoft.dtw.ETipo.NUMERO
import br.com.astrosoft.dtw.ETipo.STRING
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GeraArquivo(val arquvioExcel: String) {
  fun execute(seqDados: List<DadosAtivoPDF>) {
    val chaveNoZerada = seqDados.filter { it.mesAno == "DEZ/2017" && it.valorSaldoResidual != 0.00 }
      .map { ChaveAtivoPDF(it.codigoConta, it.codigoItem) }
      .distinct()
    //produzArquivos(arquvioZeroExcel, dadosZerado)
    //produzArquivos(arquvioNoZeroExcel, dadosNoZerado)
    //produzArquivos(arquvioExcel, seqDados)
    var i = 0
    val dadosArquivo = seqDados
      .filter { !it.codigoConta.startsWith("1.3.2.1.") }
      .groupBy { ChaveAtivoPDF(it.codigoConta, it.codigoItem) }
      .entries.mapNotNull { it.value.ordena().lastOrNull() }
      .sortedWith(compareBy({ it.valorSaldoResidual == 0.00 }, { it.codigo }))
      .sequencial()
/*
    dadosArquivo
      .map { it.codigo }.sorted().distinct().forEach { codigo ->
        val dados = dadosArquivo.filter { it.codigo == codigo }.sortedBy { it.sequencial }
        val dadosZerado = dados.filter { !chaveNoZerada.contains(ChaveAtivoPDF(it.codigoConta, it.codigoItem)) }
        val dadosNoZerado = dados.filter { chaveNoZerada.contains(ChaveAtivoPDF(it.codigoConta, it.codigoItem)) }
        produzArquivosExcel("${arquvioExcel}-${i++.toString().padStart(3, '0')}-Zero-$codigo.xlsx", dadosZerado)
        produzArquivosExcel("${arquvioExcel}-${i++.toString().padStart(3, '0')}-NOZero-$codigo.xlsx", dadosNoZerado)
      }
      */
    val dadosZerado = dadosArquivo.filter { !chaveNoZerada.contains(ChaveAtivoPDF(it.codigoConta, it.codigoItem)) }
    val dadosNoZerado = dadosArquivo.filter { chaveNoZerada.contains(ChaveAtivoPDF(it.codigoConta, it.codigoItem)) }
    produzArquivosExcel("${arquvioExcel}-Zero.xlsx", dadosZerado)
    produzArquivosExcel("${arquvioExcel}-NOZero.xlsx", dadosNoZerado)
    produzArquivosExcel("${arquvioExcel}-Tudo.xlsx", dadosArquivo)
  }

  private fun produzArquivosExcel(arquvioExcel: String, dados: List<DadosAtivoPDF>) {
    if (dados.isEmpty()) return
    val colunas = listOf(
      Coluna("Nº do item",
             "Nº do ativo",
             STRING) { "AT" + "${it.sequencial + 15}"
        .padStart(5, '0') },
      /*      Coluna("Itens já cadastrados no SAP",
                   "Itens já cadastrados no SAP",
                   STRING) { "" },*/
      Coluna("Descrição do item",
             "Descrição do item",
             STRING) { it.descricaoItem },
      Coluna("Descrição em língua estrangeira",
             "Descrição do ativo",
             STRING) { it.descricaoItem },
      Coluna("Código da Classe do ativo",
             "Veículos",
             STRING) { it.codigo },
      Coluna("Data de incorporação",
             "Aquisição",
             STRING) { it.dataEntrada.toStr() },
      Coluna("Código Área de depreciação",
             "Cód. do plano de depreciação",
             STRING) { "MÁQUINAS E ACES" },
      Coluna("Data de início da depreciação",
             "Geralmente é a data de aquisição",
             STRING) { it.dataEntrada.toStr() },
      Coluna("Vida útil",
             "Vida útil total",
             STRING) { it.quantVidaMeses.toString() },
      //     Coluna("Vida útil",
      //          "em meses",
      //       STRING) { it.quantVidaMeses.toString() },
      Coluna("Vida útil restante",
             "Vida útil restante",
             STRING) { it.quantVidaUtil.toString() }, //formula
      Coluna("Tipo de depreciação",
             "Geralmente é linear",
             STRING) { "0002" },
      Coluna("CAP",
             "Custo de aquisição",
             NUMERO) { it.valorOriginal.toStr() },
      Coluna("Depreciação acumulada normal",
             "Depreciação acumulada antes do exercício",
             NUMERO) { it.valorDepAcum?.toStr() ?: "0" } //,
      //Coluna("Valor Saldo Residual",
      //     "Ultimo saldo residual",
      //   NUMERO) { it.valorSaldoResidual?.toStr() ?: "0" },
      /*
    Coluna("Ultimo Mes",
           "Ultimo mes/ano",
           STRING) { it.mesAno ?: "" },
    Coluna("taxa",
           "taxa",
           NUMERO) { it.valorTaxa.toStr() }*/
                        )
    val dadosArquivo = dados
      .groupBy { ChaveAtivoPDF(it.codigoConta, it.codigoItem) }
      .entries.mapNotNull { it.value.ordena().lastOrNull() }
      .sortedBy { it.sequencial }

    val workbook = XSSFWorkbook()
    val createHelper = workbook.creationHelper
    val sheet = workbook.createSheet("Ativos")
    val headerFont = workbook.createFont()
    headerFont.bold = true
    val headerCellStyle = workbook.createCellStyle()
    headerCellStyle.setFont(headerFont)
    val numberStyle = workbook.createCellStyle()
    numberStyle.dataFormat = createHelper.createDataFormat().getFormat("#,##0.00")
    val headerRow = sheet.createRow(0)
    val headerRow2 = sheet.createRow(1)

    colunas.forEachIndexed { index, coluna ->
      val cell = headerRow.createCell(index)
      cell.setCellValue(coluna.titulo)
      cell.cellStyle = headerCellStyle
      val cell2 = headerRow2.createCell(index)
      cell2.setCellValue(coluna.subTitulo)
      cell2.cellStyle = headerCellStyle
    }

    dadosArquivo.forEachIndexed { indexRow, dadosAtivoPDF ->
      val row = sheet.createRow(2 + indexRow)
      colunas.forEachIndexed { indexCol, coluna ->
        val cell = row.createCell(indexCol)
        if (coluna.tipo == NUMERO)
          cell.setCellValue(coluna.valor(dadosAtivoPDF).toDoubleOrNull() ?: 0.00)
        else
          cell.setCellValue(coluna.valor(dadosAtivoPDF))
        if (coluna.tipo == NUMERO)
          cell.cellStyle = numberStyle
      }
    }

    colunas.forEachIndexed { index, _ ->
      sheet.autoSizeColumn(index)
    }
    val fileOut = FileOutputStream(arquvioExcel)
    workbook.write(fileOut)
    fileOut.close()
    workbook.close()
  }

  private fun produzArquivosTxt(arquivoTexto: String, dados: List<DadosAtivoPDF>) {
    val colunas = listOf(
      Coluna("Nº do item",
             "Nº do ativo",
             STRING) { "AT" + "${it.sequencial + 15}".padStart(5, '0') },
      /*      Coluna("Itens já cadastrados no SAP",
                   "Itens já cadastrados no SAP",
                   STRING) { "" },*/
      Coluna("Descrição do item",
             "Descrição do item",
             STRING) { it.descricaoItem },
      Coluna("Descrição em língua estrangeira",
             "Descrição do ativo",
             STRING) { it.descricaoItem },
      Coluna("Código da Classe do ativo",
             "Veículos",
             STRING) { it.codigo },
      Coluna("Data de incorporação",
             "Aquisição",
             STRING) { it.dataEntrada.toStr() },
      Coluna("Código Área de depreciação",
             "Cód. do plano de depreciação",
             STRING) { "MÁQUINAS E ACES" },
      Coluna("Data de início da depreciação",
             "Geralmente é a data de aquisição",
             STRING) { it.dataEntrada.toStr() },
      Coluna("Vida útil",
             "Vida útil total",
             STRING) { it.quantVidaMeses.toString() },
      //     Coluna("Vida útil",
      //          "em meses",
      //       STRING) { it.quantVidaMeses.toString() },
      Coluna("Vida útil restante",
             "Vida útil restante",
             STRING) { it.quantVidaUtil.toString() }, //formula
      Coluna("Tipo de depreciação",
             "Geralmente é linear",
             NUMERO) { "0002" },
      Coluna("CAP",
             "Custo de aquisição",
             NUMERO) { it.valorOriginal.toStr() },
      Coluna("Depreciação acumulada normal",
             "Depreciação acumulada antes do exercício",
             NUMERO) { it.valorDepAcum?.toStr() ?: "0" } //,
      //Coluna("Valor Saldo Residual",
      //     "Ultimo saldo residual",
      //   NUMERO) { it.valorSaldoResidual?.toStr() ?: "0" },
      /*
    Coluna("Ultimo Mes",
           "Ultimo mes/ano",
           STRING) { it.mesAno ?: "" },
    Coluna("taxa",
           "taxa",
           NUMERO) { it.valorTaxa.toStr() }*/
                        )

    colunas.forEachIndexed { index, coluna ->
    }
    val linhas = dados.map { dado ->
      colunas.joinToString(separator = "\t") { coluna ->
        coluna.valor(dado)
      }
    }
    val myfile = File(arquivoTexto)

    myfile.printWriter().use { out ->
      linhas.forEach { linha ->
        out.println(linha)
      }
    }
  }
}

private fun Double.toStr(): String {
  //val df = DecimalFormat("0.00")
  //return df.format(this)
  return this.toString()
}

private fun LocalDate.toStr(): String {
  val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
  return this.format(formatter)
}

private fun List<DadosAtivoPDF>.sequencial(): List<DadosAtivoPDF> {
  forEachIndexed { i, d ->
    d.sequencial = i
  }
  return this
}

private fun List<DadosAtivoPDF>.ordena(): List<DadosAtivoPDF> {
  return this.sortedWith(compareBy({ it.codigoItem }, { it.codigoConta }, { it.anoMes }))
}

private fun DadosAtivoPDF.linhaDados(): String {
  return listOf(mesAno, codigoConta, descricaoConta)
    .joinToString(separator = ";")
}

enum class ETipo {
  STRING, NUMERO
}

data class Coluna(val titulo: String, val subTitulo: String, val tipo: ETipo, val valor: (DadosAtivoPDF) -> String)
