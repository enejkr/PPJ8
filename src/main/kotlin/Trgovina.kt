import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.w3c.dom.Element

class Trgovina(
    val izdelki: MutableList<Izdelek> = mutableListOf<Izdelek>()
) {
    fun parsexml(file: String) {
        val document = parse(file)

        document.documentElement.normalize()
        val root = document.documentElement
        println(root.nodeName)

        val nodeList = root.childNodes

        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i)

            if (node.nodeType == Document.ELEMENT_NODE) {
                val element = node as Element
                println("Tag: ${element.tagName}")

//                for (j in 0 until element.childNodes.length) {
//                    val childNode = element.childNodes.item(j)
//                    if (childNode.nodeType == Document.ELEMENT_NODE) {
//                        println("Child node: ${childNode.nodeName} = ${childNode.textContent}")
//                    }
//                }

                val id = element.getElementsByTagName("ID").item(0)?.textContent?.toIntOrNull() ?: 0
                val ime = element.getElementsByTagName("ime").item(0)?.textContent ?: ""
                val osvezitev = element.getElementsByTagName("osvezitev").item(0)?.textContent ?: ""
                val url = element.getElementsByTagName("url").item(0)?.textContent ?: ""
                val opis = element.getElementsByTagName("opis").item(0)?.textContent ?: ""
                val slika = element.getElementsByTagName("slike").item(0)?.textContent ?: ""
                val cena = element.getElementsByTagName("cena").item(0)?.textContent?.toDoubleOrNull() ?: 0.0
                val ocena = element.getElementsByTagName("ocena").item(0)?.textContent?.toDoubleOrNull() ?: 0.0
                val teza = element.getElementsByTagName("taza").item(0)?.textContent?.toDoubleOrNull() ?: 0.0

                val izdelek = Izdelek(id, ime, osvezitev, url, opis, slika, cena, ocena, teza)
                addIzdelek(izdelek)


            }
        }
    }

    fun addIzdelek(izdelek: Izdelek) {
        val izdelekToAdd = izdelki.find { it.id == izdelek.id }
        if (izdelekToAdd == null) {
            izdelki.add(izdelek)
            log(izdelek)
            //println("Izdelek dodan: $izdelek")
        } else {
            println("izdelek with id ${izdelek.id} already exists.")
        }
    }

    fun saveToXML(filePath: String, izdelki: List<Izdelek>) {
        try {
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder: DocumentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document: Document = documentBuilder.newDocument()

            val rootElement: Element = document.createElement("products")
            document.appendChild(rootElement)

            for (izdelek in izdelki) {
                val izdelekElement: Element = document.createElement("izdelek")

                fun addElement(name: String, value: String) {
                    val element: Element = document.createElement(name)
                    element.appendChild(document.createTextNode(value))
                    izdelekElement.appendChild(element)
                }

                addElement("id", izdelek.id.toString())
                addElement("ime", izdelek.ime)
                addElement("osvezitev", izdelek.osvezitev)
                addElement("url", izdelek.url)
                addElement("opis", izdelek.opis)
                addElement("slika", izdelek.slika)
                addElement("cena", izdelek.cena.toString())
                addElement("ocena", izdelek.ocena.toString())
                addElement("teza", izdelek.teza.toString())

                rootElement.appendChild(izdelekElement)
            }

            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            val source = DOMSource(document)
            val result = StreamResult(File(filePath))

            transformer.transform(source, result)

            println("Podatki so bili uspešno zapisani v datoteko: $filePath")
        } catch (e: Exception) {
            println("Napaka pri zapisovanju XML: ${e.message}")
        }
    }

    fun removeIzdelek(value: Int) {
        val izdelekToRemove = izdelki.find { it.id == value }

        if (izdelekToRemove != null) {
            izdelki.remove(izdelekToRemove)
            log(izdelekToRemove)
        } else {
            println("Izdelek with id $value not found.")
        }
    }

    private fun log(izdelek: Izdelek) {
        val logFile = File("log.txt")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = dateFormat.format(Date())
        val logEntry = "[$timestamp] Removed Izdelek - ID: ${izdelek.id}, Name: ${izdelek.ime}, Cena: ${izdelek.cena}\n"
        logFile.appendText(logEntry)

    }

    fun parse(path: String): Document {
        try {
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = documentBuilderFactory.newDocumentBuilder()
            return docBuilder.parse(File(path))
        } catch (e: Exception) {
            println("Napaka pri nalaganju XML: ${e.message}")
            throw e
        }
    }

}
