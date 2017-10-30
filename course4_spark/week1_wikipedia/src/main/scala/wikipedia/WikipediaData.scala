package wikipedia

import java.io.{BufferedReader, File, FileReader}
import java.nio.file.{Files, Paths}

object WikipediaData {

  private[wikipedia] def filePath = {
    val resource = this.getClass.getClassLoader.getResource("wikipedia/wikipedia.dat")
    if (resource == null) sys.error("Please download the dataset as explained in the assignment instructions")
    new File(resource.toURI).getPath
  }

  private[wikipedia] def parse(line: String): WikipediaArticle = {
    val subs = "</title><text>"
    val i = line.indexOf(subs)
    val title = line.substring(14, i)
    val text  = line.substring(i + subs.length, line.length-16)
    WikipediaArticle(title, text)
  }


  // this method could be replaced by sc.textFile(WikipediaData.filePath)
  def getArticles = {
    val fr = new FileReader(filePath)
    val br = new BufferedReader(fr)
    var list: Seq[WikipediaArticle] = IndexedSeq()

    var line:String = null
    do{
      line = br.readLine
      if(line != null) {
        list = list :+ parse(line)
      }
    }while(line != null)

    br.close()
    fr.close()
    list
  }
}
