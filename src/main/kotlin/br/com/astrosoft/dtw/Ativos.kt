package br.com.astrosoft.dtw

fun main(args: Array<String>) {
  val arquvioTXT = "/home/ivaneyvieira/Dropbox/tablepdf/tabelaKadja.txt"
  val arquvioZeroCSV = "/home/ivaneyvieira/Dropbox/tablepdf/ativoZero.csv"
  val arquvioNoZeroCSV = "/home/ivaneyvieira/Dropbox/tablepdf/ativoNoZero.csv"
  val processador = Processamento(arquvioTXT)
  val seqDados = processador.execute()
  val gerador = GeraArquivo(arquvioZeroCSV, arquvioNoZeroCSV)
  gerador.execute(seqDados)
}