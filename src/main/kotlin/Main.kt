import java.io.File
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import javax.xml.xpath.*
import javax.xml.parsers.DocumentBuilderFactory

fun validateXML(xmlFile: File, xsdFile: File): Boolean {
    return try {
        val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schema = schemaFactory.newSchema(xsdFile)
        val validator = schema.newValidator()
        validator.validate(StreamSource(xmlFile))
        println("XML je veljaven.")
        true
    } catch (e: Exception) {
        println("Napaka pri validaciji: ${e.message}")
        false
    }
}

fun main() {
    val inputFile = File("test.xml")
    val xsdFile = File("trgovina.xsd")
    val isValid = validateXML(inputFile, xsdFile)
    println("Rezultat validacije: $isValid")
    var trgovina = Trgovina()
    trgovina.parsexml("test.xml")
    trgovina.izdelki.add(Izdelek(4, "Primer Izdelka1", "osvezitev", "url", "opis", "slike", 20.0, 4.5, 10.0))
    trgovina.removeIzdelek(1)
    trgovina.saveToXML("izdelki.xml", trgovina.izdelki)

    val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val document = documentBuilder.parse(inputFile)

    val xpathFactory = XPathFactory.newInstance()
    val xpath = xpathFactory.newXPath()

    val expression = "//izdelek[ocena > 4.5]"
    val nodeList = xpath.evaluate(expression, document, XPathConstants.NODESET) as org.w3c.dom.NodeList

    val expression1 = "//izdelek[cena < 20]"
    val nodeList1 = xpath.evaluate(expression1, document, XPathConstants.NODESET) as org.w3c.dom.NodeList

    val expression2 = "//izdelek[contains(ime, 'Primer Izdelka1')]"
    val nodeList2 = xpath.evaluate(expression2, document, XPathConstants.NODESET) as org.w3c.dom.NodeList

    println("idelki ki imajo oceno vec kot 4.5")
    for (i in 0 until nodeList.length) {
        val node = nodeList.item(i)
        println("Najden izdelek: ${node.textContent}")
    }

    println("idelki ki imajo ceno manj kot 20")
    for (i in 0 until nodeList1.length) {
        val node1 = nodeList1.item(i)
        println("Najden izdelek: ${node1.textContent}")
    }

    println("idelki ki imajo v imenu niz \" Primer Izdelka1 \"")
    for (i in 0 until nodeList2.length) {
        val node2 = nodeList2.item(i)
        println("Najden izdelek: ${node2.textContent}")
    }
}

