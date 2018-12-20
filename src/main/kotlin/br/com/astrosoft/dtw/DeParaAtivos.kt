import java.io.File

object DeParaAtivos {
  val arquivo = "/home/ivaneyvieira/Dropbox/tablepdf/deparaClasseAtivos.txt"
  val file = File(arquivo)
  val mapAtivo = HashMap<String, String>()

  init {
    file.forEachLine { linha ->
      val parte = linha.split("\t")
      if (parte.size == 2) {
        val codigo = parte[0]
        val descricao = parte[1]
        mapAtivo.put(descricao, codigo)
      }
    }
  }

  fun getCodigo(codigo: String): String? {
    return mapAtivo[codigo]
  }
}