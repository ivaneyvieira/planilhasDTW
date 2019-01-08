import java.io.File

object DeParaAtivos {
  val arquivo = "/home/ivaneyvieira/Dropbox/tablepdf/deParaContaAtivo.txt"
  val file = File(arquivo)
  val listAtivos = ArrayList<ContasAtivo>()

  init {
    file.forEachLine { linha ->
      val parte = linha.split("\t")
      if (parte.size == 3) {
        val codigo = parte[0]
        val descricao = parte[1]
        val conta = parte[2]
        listAtivos.add(ContasAtivo(codigo, descricao, conta))
      }
    }
  }

  fun getByConta(codigoConta: String): String? {
    val conta = codigoConta.subSequence(0, endIndex = 10)
    if(conta == "1.3.2.7.03.00001")
      println("Conta 1.3.2.7.03.00001 ")
    val contaAtivo = listAtivos.find { it.conta == conta }
    if(contaAtivo == null)
      println("Conta não encontrada")
    return contaAtivo?.codigo?.let {
      if (it == "") return null
      it
    }
  }
}

data class ContasAtivo(
  val codigo: String,
  val descricao: String,
  val conta: String
                      )