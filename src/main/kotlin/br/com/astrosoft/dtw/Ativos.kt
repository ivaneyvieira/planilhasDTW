package br.com.astrosoft.dtw

fun main(args: Array<String>) {
  val arquvioTXT = "/home/ivaneyvieira/Dropbox/tablepdf/tabelaKadja.txt"
  val arquvioZeroExcel = "/home/ivaneyvieira/Dropbox/tablepdf/ativoZero.xlsx"
  val arquvioNoZeroExcel = "/home/ivaneyvieira/Dropbox/tablepdf/ativoNoZero.xlsx"
  val processador = Processamento(arquvioTXT)
  val seqDados = processador.execute()
  val gerador = GeraArquivo(arquvioZeroExcel, arquvioNoZeroExcel)
  gerador.execute(seqDados)
}