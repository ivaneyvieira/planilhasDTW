package br.com.astrosoft.dtw

import java.io.File
import java.lang.Thread.yield
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Processamento(val arquvioTXT: String) {
  val linhas = File(arquvioTXT).readLines()
  private var index = 0
  private var pagina: Int = 0
  private var codigoConta: String = ""
  private var descricaoConta: String = ""
  private var codigoItem: String = ""
  private var descricaoItem: String = ""
  private var dataEntrada: LocalDate = LocalDate.MIN
  private var valorOriginal: Double = 0.00
  private var quantIndice: Double = 0.00
  private var valorTaxa: Double = 0.00
  private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  fun execute(): List<DadosAtivoPDF> {
    index = 0
    val lista = ArrayList<DadosAtivoPDF>()
    linhas.forEach { linha ->
      processaLinha(linha).forEach { dados ->
        lista.add(dados)
      }
    }
    return lista
  }

  private fun processaLinha(linha: String): List<DadosAtivoPDF> {
    return when {
      linha.isPagina()      -> {
        processaLinhaPagina(linha)
        emptyList()
      }
      linha.isConta()       -> {
        processaLinhaConta(linha)
        emptyList()
      }
      linha.isItem()        -> {
        processaLinhaItem(linha)
        emptyList()
      }
      linha.isEntrada()     -> {
        processaLinhaEntrada(linha)
        emptyList()
      }
      linha.isValoresGrid() -> {
        processaLinhaValoresGrid(linha)
      }
      else                  -> emptyList()
    }
  }

  private fun processaLinhaPagina(linha: String) {
    pagina += 1
  }

  private fun processaLinhaValoresGrid(linha: String): List<DadosAtivoPDF> {
    val partes = linha.split(" +".toRegex())
    val ativo1 = DadosAtivoPDF(
      index++,
      pagina = pagina,
      codigoConta = codigoConta,
      descricaoConta = descricaoConta,
      codigoItem = codigoItem,
      descricaoItem = descricaoItem,
      dataEntrada = dataEntrada,
      valorOriginal = valorOriginal,
      quantIndice = quantIndice,
      valorTaxa = valorTaxa,
      mesAno = partes.getOrNull(0) ?: "",
      valorCorrigido = partes.getOrNull(1).toValor(),
      valorDepAcum = partes.getOrNull(2).toValor(),
      valorSaldoResidual = partes.getOrNull(3).toValor()
                              )
    val ativo2 = DadosAtivoPDF(
      index++,
      pagina = pagina,
      codigoConta = codigoConta,
      descricaoConta = descricaoConta,
      codigoItem = codigoItem,
      descricaoItem = descricaoItem,
      dataEntrada = dataEntrada,
      valorOriginal = valorOriginal,
      quantIndice = quantIndice,
      valorTaxa = valorTaxa,
      mesAno = partes.getOrNull(4),
      valorCorrigido = partes.getOrNull(5)?.toValor(),
      valorDepAcum = partes.getOrNull(6)?.toValor(),
      valorSaldoResidual = partes.getOrNull(7)?.toValor()
                              )
    return listOf(ativo1, ativo2).filter { it.mesAno != null }
  }

  private fun processaLinhaEntrada(linha: String) {
    val partes = linha.split(" +".toRegex())
    dataEntrada = partes.getOrNull(1).toData()
    valorOriginal = partes.getOrNull(4).toValor()
    quantIndice = partes.getOrNull(7).toValor()
    valorTaxa = partes.getOrNull(9).toValor()
  }

  private fun processaLinhaItem(linha: String) {
    val partes = linha.split(" +".toRegex()).toList()
    codigoItem = partes.first()
    descricaoItem = partes.drop(1).joinToString(" ")
  }

  private fun processaLinhaConta(linha: String) {
    val partes = linha.split(" +".toRegex()).toList()
    codigoConta = partes.first()
    descricaoConta = partes.drop(1).joinToString(" ")
  }

  private fun String.isConta() = "^[0-9]\\.[0-9]".toRegex().find(this) != null
  private fun String.isItem() = "^[0-9][0-9]+\\.[0-9][0-9]+".toRegex().find(this) != null
  private fun String.isEntrada() = "^Entrada".toRegex().find(this) != null
  private fun String.isValoresGrid() = "^[A-Z][A-Z][A-Z]/[0-9][0-9][0-9][0-9] ".toRegex().find(this) != null
  private fun String.isPagina() = "Pagina: [0-9]+".toRegex().find(this) != null

  private fun String?.toValor(): Double {
    return this?.replace(".", "")?.replace(",", ".")?.toDoubleOrNull() ?: 0.00
  }

  private fun String?.toData(): LocalDate {
    return LocalDate.parse(this, formatter)
  }
}


