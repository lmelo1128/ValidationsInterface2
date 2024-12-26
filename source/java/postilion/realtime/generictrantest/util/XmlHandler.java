package postilion.realtime.generictrantest.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class XmlHandler {

	public static String processXmlRequest(String xmlInput, String tagName, boolean enableLog) {

		String tagValue = null;

		try {
			// Parsear el XML y extraer el tag solicitado
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream inputStream = new ByteArrayInputStream(xmlInput.getBytes("UTF-8"));
			Document doc = builder.parse(inputStream);

			// Normalizar el documento
			doc.getDocumentElement().normalize();

			// Extraer el tag solicitado
			NodeList nodeList = doc.getElementsByTagName(tagName);
			if (nodeList.getLength() > 0) {
				tagValue = nodeList.item(0).getTextContent();
			} else {
				Logger.logLine("Tag '" + tagName + "' no encontrado.", enableLog);
			}

		} catch (Exception e) {

			e.printStackTrace(); // Manejo de errores
		}

		return tagValue;
	}

	public static String buildSuccessfulResponse() {
		StringBuilder xmlBuilder = new StringBuilder();

		xmlBuilder.append("<AutraPinInqRs>").append("<IFX>").append("<SignonRs>").append("<Status>")
				.append("<StatusCode>0</StatusCode>").append("<ServerStatusCode>A</ServerStatusCode>")
				.append("<Severity>Info</Severity>").append("</Status>").append("<CustId>")
				.append("<SPName>SMP</SPName>").append("<PersonalIdent>").append("<AccountHolder>1</AccountHolder>")
				.append("<PersonInfo>").append("<NameAddrType>Customer</NameAddrType>").append("<GovIssueIdent>")
				.append("<GovIssueIdentType>0</GovIssueIdentType>").append("<IdentSerialNum>81813439</IdentSerialNum>")
				.append("<GovOrg>Country</GovOrg>").append("</GovIssueIdent>").append("</PersonInfo>")
				.append("</PersonalIdent>").append("</CustId>")
				.append("<ClientDt>2024-10-10T08:46:41.811079-05:00</ClientDt>")
				.append("<CustLangPref>es-CO</CustLangPref>").append("<ClientApp>").append("<Org>Banco de Bogota</Org>")
				.append("<Name>ESB</Name>").append("<Version>1.0</Version>").append("</ClientApp>")
				.append("<ServerDt>2024-10-17T21:45:34.000000-05:00</ServerDt>").append("<Language>es-CO</Language>")
				.append("</SignonRs>").append("</IFX>").append("</AutraPinInqRs>");

		return xmlBuilder.toString();
	}

	public static String buildFailedResponse() {
		StringBuilder xmlBuilder = new StringBuilder();

		xmlBuilder.append("<AutraPinInqRs>").append("<IFX>").append("<SignonRs>").append("<Status>")
				.append("<StatusCode>25</StatusCode>").append("<ServerStatusCode>E</ServerStatusCode>")
				.append("<Severity>Error</Severity>").append("<StatusDesc>Estado no permite transacciones</StatusDesc>")
				.append("</Status>").append("</SignonRs>").append("</IFX>").append("</AutraPinInqRs>");

		return xmlBuilder.toString();
	}
}
