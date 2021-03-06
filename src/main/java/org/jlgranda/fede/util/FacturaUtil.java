/*
 * Copyright (C) 2015 jlgranda
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jlgranda.fede.util;

import java.io.Serializable;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.jlgranda.fede.sri.jaxb.factura.v110.Factura;
import org.jlgranda.fede.sri.jaxb.factura.v110.ObjectFactory;


/**
 *
 * @author jlgranda
 */
public class FacturaUtil implements Serializable {
    
    public static final String TEXTO_VERIFICACION_FACTURA = "<factura id="; 
    public static final String INICIO_TAG_COMPROBANTE = "<comprobante><![CDATA["; 
    public static final String FIN_TAG_COMPROBANTE = "]]></comprobante>"; 
    public static final String INICIO_TAG_COMPROBANTE_SIN_CDATA = "<comprobante>"; 
    public static final String FIN_TAG_COMPROBANTE_SIN_CDATA = "</comprobante>"; 
    private static final long serialVersionUID = -2866847851945931341L;
    
       
    /**
     * Lee el contenido XML de la factura electrónica a un objeto factura JAXB
     * @param xml contenido XML de la factura electrónica
     * @return la instancia factura representada por el XML
     */
    public static Factura read(String xml){
        Factura factura = null;
        xml = corregir(xml);
        if (! xml.contains(TEXTO_VERIFICACION_FACTURA)){
            System.err.println("No contiene una factura!");
            return factura;
        }
        
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            String comprobante = extraerComprobante(xml);
            if (comprobante != null){
                StringReader reader = new StringReader(comprobante);
                factura = (Factura) unmarshaller.unmarshal(reader);
            } else {
                Logger.getLogger(FacturaUtil.class.getName()).log(Level.INFO, "No se pudo obtener el contendio del comprobante. El contenido XML es: ");
                Logger.getLogger(FacturaUtil.class.getName()).log(Level.INFO, xml);
            }
        } catch (JAXBException ex) {
            Logger.getLogger(FacturaUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return factura;
    }
    
    public static String read(String xml, String tag){
        String tagInicio = tag.split("><")[0] + ">";
        String tagFinal = "<" + tag.split("><")[1];
        int indiceInicioTag = xml.indexOf(tagInicio) + tagInicio.length();
        int indiceFinTag = xml.indexOf(tagFinal);
        
        return xml.substring(indiceInicioTag, indiceFinTag);
        
    }

    public static String extraerComprobante(String xml) {
        int indiceInicioTagComprobante = xml.indexOf(INICIO_TAG_COMPROBANTE);
        int indiceFinTagComprobante = -1;
        if (indiceInicioTagComprobante > -1){
            indiceInicioTagComprobante = indiceInicioTagComprobante + INICIO_TAG_COMPROBANTE.length();
            indiceFinTagComprobante = xml.indexOf(FIN_TAG_COMPROBANTE);
        } else {
            indiceInicioTagComprobante =xml.indexOf(INICIO_TAG_COMPROBANTE_SIN_CDATA);
            if (indiceInicioTagComprobante > -1){
                indiceInicioTagComprobante = indiceInicioTagComprobante + INICIO_TAG_COMPROBANTE_SIN_CDATA.length();
                indiceFinTagComprobante = xml.indexOf(FIN_TAG_COMPROBANTE_SIN_CDATA);
            }
        } 
        return (indiceInicioTagComprobante == -1 || indiceFinTagComprobante == -1) ? xml : xml.substring(indiceInicioTagComprobante, indiceFinTagComprobante);
    }
    
    public static String extraerURL(String xml, String inicioTagURL, String finTagURL) {
        int indiceInicioTagComprobante = xml.lastIndexOf(inicioTagURL);
        int indiceFinTagComprobante = -1;
        if (indiceInicioTagComprobante > -1){
            indiceInicioTagComprobante = indiceInicioTagComprobante + inicioTagURL.length();
            indiceFinTagComprobante = xml.indexOf(finTagURL);
        } 
        return (indiceInicioTagComprobante == -1 || indiceFinTagComprobante == -1) ? null : xml.substring(indiceInicioTagComprobante, indiceFinTagComprobante);
    }
    
    private static String corregir(String xml) {
        if (xml.contains("&lt;")){
            xml = xml.replace("&lt;", "<");
        }
        if (xml.contains("&gt;")){
            xml = xml.replace("&gt;", ">");
        }
        
        return xml;
    }
    
    public static void main(String args[]){
//        System.err.println(read("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:autorizacionComprobanteResponse xmlns:ns2=\"http://ec.gob.sri.ws.autorizacion\"><RespuestaAutorizacionComprobante><claveAccesoConsultada>3105201501119000221300120010260000866211234567814</claveAccesoConsultada><numeroComprobantes>1</numeroComprobantes><autorizaciones><autorizacion><estado>AUTORIZADO</estado><numeroAutorizacion>0706201512424111900022130016097370826</numeroAutorizacion><fechaAutorizacion>2015-06-07T12:42:41.700-05:00</fechaAutorizacion><ambiente>PRODUCCION</ambiente><comprobante>&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;\n" +
//"&lt;factura id=\"comprobante\" version=\"1.1.0\"&gt; &lt;infoTributaria&gt; &lt;ambiente&gt;2&lt;/ambiente&gt; &lt;tipoEmision&gt;1&lt;/tipoEmision&gt; &lt;razonSocial&gt;BANCO DE LOJA S.A.&lt;/razonSocial&gt; &lt;nombreComercial&gt;BANCO DE LOJA S.A.&lt;/nombreComercial&gt; &lt;ruc&gt;1190002213001&lt;/ruc&gt; &lt;claveAcceso&gt;3105201501119000221300120010260000866211234567814&lt;/claveAcceso&gt; &lt;codDoc&gt;01&lt;/codDoc&gt; &lt;estab&gt;001&lt;/estab&gt; &lt;ptoEmi&gt;026&lt;/ptoEmi&gt; &lt;secuencial&gt;000086621&lt;/secuencial&gt; &lt;dirMatriz&gt;BOLIVAR Y ROCAFUERTE&lt;/dirMatriz&gt; &lt;/infoTributaria&gt; &lt;infoFactura&gt; &lt;fechaEmision&gt;31/05/2015&lt;/fechaEmision&gt; &lt;dirEstablecimiento&gt;Bolivar s/n y Rocafuerte&lt;/dirEstablecimiento&gt; &lt;contribuyenteEspecial&gt;3997&lt;/contribuyenteEspecial&gt; &lt;obligadoContabilidad&gt;SI&lt;/obligadoContabilidad&gt; &lt;tipoIdentificacionComprador&gt;05&lt;/tipoIdentificacionComprador&gt; &lt;razonSocialComprador&gt;GRANDA SIVISAPA, JOSE LUIS&lt;/razonSocialComprador&gt; &lt;identificacionComprador&gt;1103826960&lt;/identificacionComprador&gt; &lt;totalSinImpuestos&gt;2.24&lt;/totalSinImpuestos&gt; &lt;totalDescuento&gt;0.00&lt;/totalDescuento&gt; &lt;totalConImpuestos&gt; &lt;totalImpuesto&gt; &lt;codigo&gt;2&lt;/codigo&gt; &lt;codigoPorcentaje&gt;2&lt;/codigoPorcentaje&gt; &lt;baseImponible&gt;2.24&lt;/baseImponible&gt; &lt;valor&gt;0.27&lt;/valor&gt; &lt;/totalImpuesto&gt; &lt;/totalConImpuestos&gt; &lt;propina&gt;00.00&lt;/propina&gt; &lt;importeTotal&gt;2.51&lt;/importeTotal&gt; &lt;moneda&gt;DOLAR&lt;/moneda&gt; &lt;/infoFactura&gt; &lt;detalles&gt; &lt;detalle&gt; &lt;codigoPrincipal&gt;4053013A1&lt;/codigoPrincipal&gt; &lt;descripcion&gt;COSTO POR RETIRO EN CAJEROS DE OTRA ENTIDAD&lt;/descripcion&gt; &lt;cantidad&gt;1&lt;/cantidad&gt; &lt;precioUnitario&gt;0.45&lt;/precioUnitario&gt; &lt;descuento&gt;00.00&lt;/descuento&gt; &lt;precioTotalSinImpuesto&gt;0.45&lt;/precioTotalSinImpuesto&gt; &lt;impuestos&gt; &lt;impuesto&gt; &lt;codigo&gt;2&lt;/codigo&gt; &lt;codigoPorcentaje&gt;2&lt;/codigoPorcentaje&gt; &lt;tarifa&gt;12&lt;/tarifa&gt; &lt;baseImponible&gt;0.45&lt;/baseImponible&gt; &lt;valor&gt;0.05&lt;/valor&gt; &lt;/impuesto&gt; &lt;/impuestos&gt; &lt;/detalle&gt; &lt;detalle&gt; &lt;codigoPrincipal&gt;4053069B2&lt;/codigoPrincipal&gt; &lt;descripcion&gt;COSTO TRANSFERENCIA WEB&lt;/descripcion&gt; &lt;cantidad&gt;1&lt;/cantidad&gt; &lt;precioUnitario&gt;0.45&lt;/precioUnitario&gt; &lt;descuento&gt;00.00&lt;/descuento&gt; &lt;precioTotalSinImpuesto&gt;0.45&lt;/precioTotalSinImpuesto&gt; &lt;impuestos&gt; &lt;impuesto&gt; &lt;codigo&gt;2&lt;/codigo&gt; &lt;codigoPorcentaje&gt;2&lt;/codigoPorcentaje&gt; &lt;tarifa&gt;12&lt;/tarifa&gt; &lt;baseImponible&gt;0.45&lt;/baseImponible&gt; &lt;valor&gt;0.05&lt;/valor&gt; &lt;/impuesto&gt; &lt;/impuestos&gt; &lt;/detalle&gt; &lt;detalle&gt; &lt;codigoPrincipal&gt;4053084C3&lt;/codigoPrincipal&gt; &lt;descripcion&gt;N/D COSTO RECAUDACION CNT WEB&lt;/descripcion&gt; &lt;cantidad&gt;2&lt;/cantidad&gt; &lt;precioUnitario&gt;0.31&lt;/precioUnitario&gt; &lt;descuento&gt;00.00&lt;/descuento&gt; &lt;precioTotalSinImpuesto&gt;0.62&lt;/precioTotalSinImpuesto&gt; &lt;impuestos&gt; &lt;impuesto&gt; &lt;codigo&gt;2&lt;/codigo&gt; &lt;codigoPorcentaje&gt;2&lt;/codigoPorcentaje&gt; &lt;tarifa&gt;12&lt;/tarifa&gt; &lt;baseImponible&gt;0.62&lt;/baseImponible&gt; &lt;valor&gt;0.07&lt;/valor&gt; &lt;/impuesto&gt; &lt;/impuestos&gt; &lt;/detalle&gt; &lt;detalle&gt; &lt;codigoPrincipal&gt;4244056D4&lt;/codigoPrincipal&gt; &lt;descripcion&gt;SRVP TRANSFERNCIA BCE&lt;/descripcion&gt; &lt;cantidad&gt;1&lt;/cantidad&gt; &lt;precioUnitario&gt;0.27&lt;/precioUnitario&gt; &lt;descuento&gt;00.00&lt;/descuento&gt; &lt;precioTotalSinImpuesto&gt;0.27&lt;/precioTotalSinImpuesto&gt; &lt;impuestos&gt; &lt;impuesto&gt; &lt;codigo&gt;2&lt;/codigo&gt; &lt;codigoPorcentaje&gt;2&lt;/codigoPorcentaje&gt; &lt;tarifa&gt;12&lt;/tarifa&gt; &lt;baseImponible&gt;0.27&lt;/baseImponible&gt; &lt;valor&gt;0.03&lt;/valor&gt; &lt;/impuesto&gt; &lt;/impuestos&gt; &lt;/detalle&gt; &lt;detalle&gt; &lt;codigoPrincipal&gt;4244067E5&lt;/codigoPrincipal&gt; &lt;descripcion&gt;SRVP TRAN INTER WEB&lt;/descripcion&gt; &lt;cantidad&gt;1&lt;/cantidad&gt; &lt;precioUnitario&gt;0.45&lt;/precioUnitario&gt; &lt;descuento&gt;00.00&lt;/descuento&gt; &lt;precioTotalSinImpuesto&gt;0.45&lt;/precioTotalSinImpuesto&gt; &lt;impuestos&gt; &lt;impuesto&gt; &lt;codigo&gt;2&lt;/codigo&gt; &lt;codigoPorcentaje&gt;2&lt;/codigoPorcentaje&gt; &lt;tarifa&gt;12&lt;/tarifa&gt; &lt;baseImponible&gt;0.45&lt;/baseImponible&gt; &lt;valor&gt;0.05&lt;/valor&gt; &lt;/impuesto&gt; &lt;/impuestos&gt; &lt;/detalle&gt; &lt;/detalles&gt; &lt;ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:etsi=\"http://uri.etsi.org/01903/v1.3.2#\" Id=\"Signature89175\"&gt;\n" +
//"&lt;ds:SignedInfo Id=\"Signature-SignedInfo702093\"&gt;\n" +
//"&lt;ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"&gt;&lt;/ds:CanonicalizationMethod&gt;\n" +
//"&lt;ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"&gt;&lt;/ds:SignatureMethod&gt;\n" +
//"&lt;ds:Reference Id=\"SignedPropertiesID814922\" Type=\"http://uri.etsi.org/01903#SignedProperties\" URI=\"#Signature89175-SignedProperties346146\"&gt;\n" +
//"&lt;ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"&gt;&lt;/ds:DigestMethod&gt;\n" +
//"&lt;ds:DigestValue&gt;XnJhAhdDPMMTFERrxoDGdZi9r0A=&lt;/ds:DigestValue&gt;\n" +
//"&lt;/ds:Reference&gt;\n" +
//"&lt;ds:Reference URI=\"#Certificate1235323\"&gt;\n" +
//"&lt;ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"&gt;&lt;/ds:DigestMethod&gt;\n" +
//"&lt;ds:DigestValue&gt;GLa6Zkhh3pgvq7KYJrnbmoDgSZw=&lt;/ds:DigestValue&gt;\n" +
//"&lt;/ds:Reference&gt;\n" +
//"&lt;ds:Reference Id=\"Reference-ID-998763\" URI=\"#comprobante\"&gt;\n" +
//"&lt;ds:Transforms&gt;\n" +
//"&lt;ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"&gt;&lt;/ds:Transform&gt;\n" +
//"&lt;/ds:Transforms&gt;\n" +
//"&lt;ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"&gt;&lt;/ds:DigestMethod&gt;\n" +
//"&lt;ds:DigestValue&gt;mCypBx4N+4WZqzKmPFaXLCX0J8s=&lt;/ds:DigestValue&gt;\n" +
//"&lt;/ds:Reference&gt;\n" +
//"&lt;/ds:SignedInfo&gt;\n" +
//"&lt;ds:SignatureValue Id=\"SignatureValue97757\"&gt;\n" +
//"iRt7WaumuKdHEH1GZlqm0PZQVo7utiWSQ3Ghp1CseWhQvmcszSdhGyf6s7ZG8nZLEhgJuPoCsykR\n" +
//"fiBzJEm38vV3+aL6ryao0A9jT5uCDblPfSRBRVLwnzI5xgSsDhNHSLcMssTrgFoj5KfSluDomd97\n" +
//"gYl1kBHFf+4R3oHlIsB3vn9JVpPLM8FSq76TUmY9NkjhuH+M47/6YxUhlcg8aZ04+i7hAWzB4RKG\n" +
//"2x4qpwtCcssREKwy7pUIP1knD2mOwCzcJ+zrwRT7G8lybs5PLIrEyZN7V1Pe1T8oZKBumf9U251d\n" +
//"7vDx1Sw5QY0EUiyh0G8/xnYseE4thilKCsZvTg==\n" +
//"&lt;/ds:SignatureValue&gt;\n" +
//"&lt;ds:KeyInfo Id=\"Certificate1235323\"&gt;\n" +
//"&lt;ds:X509Data&gt;\n" +
//"&lt;ds:X509Certificate&gt;\n" +
//"MIIJVDCCBzygAwIBAgIETkQp/TANBgkqhkiG9w0BAQsFADCBoTELMAkGA1UEBhMCRUMxIjAgBgNV\n" +
//"BAoTGUJBTkNPIENFTlRSQUwgREVMIEVDVUFET1IxNzA1BgNVBAsTLkVOVElEQUQgREUgQ0VSVElG\n" +
//"SUNBQ0lPTiBERSBJTkZPUk1BQ0lPTi1FQ0lCQ0UxDjAMBgNVBAcTBVFVSVRPMSUwIwYDVQQDExxB\n" +
//"QyBCQU5DTyBDRU5UUkFMIERFTCBFQ1VBRE9SMB4XDTE1MDIyMDIwMDEzN1oXDTE2MDIyMDIwMzEz\n" +
//"N1owgbMxCzAJBgNVBAYTAkVDMSIwIAYDVQQKExlCQU5DTyBDRU5UUkFMIERFTCBFQ1VBRE9SMTcw\n" +
//"NQYDVQQLEy5FTlRJREFEIERFIENFUlRJRklDQUNJT04gREUgSU5GT1JNQUNJT04tRUNJQkNFMQ4w\n" +
//"DAYDVQQHEwVRVUlUTzE3MBEGA1UEBRMKMDAwMDA0MjgyODAiBgNVBAMTG0VOQSBNQVJJQU5FTEEg\n" +
//"R1VFUlJFUk8gTU9SQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKV3Xwfz/JgsTCMB\n" +
//"GTE8wdVhoAx9uU7Xn4Udub3eSJImf26jTvydtkfZvYYhiRQEV7pgyA01HpcF6jtPu8tQaCtEeAOx\n" +
//"mr819tSpH996dPCEUoT1nBAzfm3jRw4wxEcT6BceHGFbj/SgyReSvxhYPz0vcdOMMCJ65SwdkhEh\n" +
//"vNBgnWJI9u4yIXLANf807sRr//1mAgGyolQ6j2ZM70ioJb4lGcFCQFRqyhB4BoSI8MnDnTsS4nzi\n" +
//"8GEyfGq0g547shxNAVsFyPSzXdJKBIcp1Qt5F3RuI3RUgx0OEMwx0PIEpAlv2mb4jVxEJkMqXlEQ\n" +
//"7MxjYkwjfLFiGWfq4WWGDG0CAwEAAaOCBH4wggR6MIGRBggrBgEFBQcBAQSBhDCBgTA+BggrBgEF\n" +
//"BQcwAYYyaHR0cDovL29jc3AuZWNpLmJjZS5lYy9lamJjYS9wdWJsaWN3ZWIvc3RhdHVzL29jc3Aw\n" +
//"PwYIKwYBBQUHMAGGM2h0dHA6Ly9vY3NwMS5lY2kuYmNlLmVjL2VqYmNhL3B1YmxpY3dlYi9zdGF0\n" +
//"dXMvb2NzcDAgBgorBgEEAYKoOwMKBBITEEJBTkNPIERFIExPSkEgU0EwHQYKKwYBBAGCqDsDCwQP\n" +
//"Ew0xMTkwMDAyMjEzMDAxMBoGCisGAQQBgqg7AwEEDBMKMTEwMzU5NjUyMjAdBgorBgEEAYKoOwMC\n" +
//"BA8TDUVOQSBNQVJJQU5FTEEwGAYKKwYBBAGCqDsDAwQKEwhHVUVSUkVSTzAUBgorBgEEAYKoOwME\n" +
//"BAYTBE1PUkEwJAYKKwYBBAGCqDsDBQQWExRKRUZFIERFIENPTlRBQklMSURBRDAkBgorBgEEAYKo\n" +
//"OwMHBBYTFEJPTElWQVIgWSBST0NBRlVFUlRFMBkGCisGAQQBgqg7AwgECxMJMDczNzAxNjAwMBQG\n" +
//"CisGAQQBgqg7AwkEBhMETG9qYTAXBgorBgEEAYKoOwMMBAkTB0VDVUFET1IwIAYKKwYBBAGCqDsD\n" +
//"MwQSExBTT0ZUV0FSRS1BUkNISVZPMCoGA1UdEQQjMCGBH2VuYV9ndWVycmVyb0BiYW5jb2RlbG9q\n" +
//"YS5maW4uZWMwggHfBgNVHR8EggHWMIIB0jCCAc6gggHKoIIBxoaB1WxkYXA6Ly9iY2VxbGRhcHN1\n" +
//"YnAxLmJjZS5lYy9jbj1DUkwyMDQsY249QUMlMjBCQU5DTyUyMENFTlRSQUwlMjBERUwlMjBFQ1VB\n" +
//"RE9SLGw9UVVJVE8sb3U9RU5USURBRCUyMERFJTIwQ0VSVElGSUNBQ0lPTiUyMERFJTIwSU5GT1JN\n" +
//"QUNJT04tRUNJQkNFLG89QkFOQ08lMjBDRU5UUkFMJTIwREVMJTIwRUNVQURPUixjPUVDP2NlcnRp\n" +
//"ZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZYY0aHR0cDovL3d3dy5lY2kuYmNlLmVjL0NSTC9lY2lf\n" +
//"YmNlX2VjX2NybGZpbGVjb21iLmNybKSBtTCBsjELMAkGA1UEBhMCRUMxIjAgBgNVBAoTGUJBTkNP\n" +
//"IENFTlRSQUwgREVMIEVDVUFET1IxNzA1BgNVBAsTLkVOVElEQUQgREUgQ0VSVElGSUNBQ0lPTiBE\n" +
//"RSBJTkZPUk1BQ0lPTi1FQ0lCQ0UxDjAMBgNVBAcTBVFVSVRPMSUwIwYDVQQDExxBQyBCQU5DTyBD\n" +
//"RU5UUkFMIERFTCBFQ1VBRE9SMQ8wDQYDVQQDEwZDUkwyMDQwCwYDVR0PBAQDAgUgMB8GA1UdIwQY\n" +
//"MBaAFBj58PvmMhyZZjkqyouyaX1JJ7/OMB0GA1UdDgQWBBRUB9wgmpPagGTT9cLSQ+1y/fpZmTAJ\n" +
//"BgNVHRMEAjAAMBkGCSqGSIb2fQdBAAQMMAobBFY4LjEDAgSQMA0GCSqGSIb3DQEBCwUAA4ICAQBz\n" +
//"tttB1ojXNhoBIedLIk5DzhuQkjuIZaUG0xNhnuvjmZhl3ocfM/a2Ccic87FzZAr9DypY3deE9R01\n" +
//"4QJnCppoKai8HG0tFYeCecJ0yLbFZZVW8OYw0GjeqycqLEX1duERQYk8HO+KbWMSnJlHZL6w+Q54\n" +
//"6euOkLu7rZFjWph4GUWHWqODKThoQs1Upg61htg2ziemrAmUjpSixVhu+wsJ/N6B+po1gmR5uUP5\n" +
//"RAlBd9MkcVqFgUg8Zv1U0ctm48F30wkVFHRogAcyJNQSg2R/a+sTfF9Y2qKRMTdoq+1OzfdKbFvh\n" +
//"36QvgNDbDOcmnoUhkjPXFwoONYSxxcQIbpsLX0Ki/Z4uMEnYeGF2hrRAgbNSUKWZCajA3KOavK89\n" +
//"H1hGmz1uVixdmhxr9YEKdUqp+ESUqP/fbVXtKrrXqOAkbEAH86nFMTDq9HzsPhRtCnlbkVPIfZX/\n" +
//"gwmMQcfuWnNmNb+8y9v5ael9+DUahGYLKqIrRjpxidEBaOenmuZqfxCbmhMF2jHEUd09/K7WM+Xa\n" +
//"u643F1rJtY4AgHZrdP9Cx9KRAzXC2+lrg8Q9TET6NWYey1+5xXg4SMQFOSukDUS65tKBL81a40Ie\n" +
//"UGJcu8q8Ow1DVkB452/ROMATbWT+Rp+JiWe9bCWnz42rCN/B+T3FfZ9gLkNreuHCLKZnFV2RqA==\n" +
//"&lt;/ds:X509Certificate&gt;\n" +
//"&lt;/ds:X509Data&gt;\n" +
//"&lt;ds:KeyValue&gt;\n" +
//"&lt;ds:RSAKeyValue&gt;\n" +
//"&lt;ds:Modulus&gt;\n" +
//"pXdfB/P8mCxMIwEZMTzB1WGgDH25TtefhR25vd5IkiZ/bqNO/J22R9m9hiGJFARXumDIDTUelwXq\n" +
//"O0+7y1BoK0R4A7GavzX21Kkf33p08IRShPWcEDN+beNHDjDERxPoFx4cYVuP9KDJF5K/GFg/PS9x\n" +
//"04wwInrlLB2SESG80GCdYkj27jIhcsA1/zTuxGv//WYCAbKiVDqPZkzvSKglviUZwUJAVGrKEHgG\n" +
//"hIjwycOdOxLifOLwYTJ8arSDnjuyHE0BWwXI9LNd0koEhynVC3kXdG4jdFSDHQ4QzDHQ8gSkCW/a\n" +
//"ZviNXEQmQypeURDszGNiTCN8sWIZZ+rhZYYMbQ==\n" +
//"&lt;/ds:Modulus&gt;\n" +
//"&lt;ds:Exponent&gt;AQAB&lt;/ds:Exponent&gt;\n" +
//"&lt;/ds:RSAKeyValue&gt;\n" +
//"&lt;/ds:KeyValue&gt;\n" +
//"&lt;/ds:KeyInfo&gt;\n" +
//"&lt;ds:Object Id=\"Signature89175-Object734576\"&gt;&lt;etsi:QualifyingProperties Target=\"#Signature89175\"&gt;&lt;etsi:SignedProperties Id=\"Signature89175-SignedProperties346146\"&gt;&lt;etsi:SignedSignatureProperties&gt;&lt;etsi:SigningTime&gt;2015-06-07T12:42:34-05:00&lt;/etsi:SigningTime&gt;&lt;etsi:SigningCertificate&gt;&lt;etsi:Cert&gt;&lt;etsi:CertDigest&gt;&lt;ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"&gt;&lt;/ds:DigestMethod&gt;&lt;ds:DigestValue&gt;jryjvMvaZuC8v5yowhrUJFaaZys=&lt;/ds:DigestValue&gt;&lt;/etsi:CertDigest&gt;&lt;etsi:IssuerSerial&gt;&lt;ds:X509IssuerName&gt;CN=AC BANCO CENTRAL DEL ECUADOR,L=QUITO,OU=ENTIDAD DE CERTIFICACION DE INFORMACION-ECIBCE,O=BANCO CENTRAL DEL ECUADOR,C=EC&lt;/ds:X509IssuerName&gt;&lt;ds:X509SerialNumber&gt;1313090045&lt;/ds:X509SerialNumber&gt;&lt;/etsi:IssuerSerial&gt;&lt;/etsi:Cert&gt;&lt;/etsi:SigningCertificate&gt;&lt;etsi:SignerRole&gt;&lt;etsi:ClaimedRoles&gt;&lt;etsi:ClaimedRole&gt;Rol de firma&lt;/etsi:ClaimedRole&gt;&lt;/etsi:ClaimedRoles&gt;&lt;/etsi:SignerRole&gt;&lt;/etsi:SignedSignatureProperties&gt;&lt;etsi:SignedDataObjectProperties&gt;&lt;etsi:DataObjectFormat ObjectReference=\"#Reference-ID-998763\"&gt;&lt;etsi:Description&gt;contenido firma&lt;/etsi:Description&gt;&lt;etsi:MimeType&gt;text/xml&lt;/etsi:MimeType&gt;&lt;/etsi:DataObjectFormat&gt;&lt;/etsi:SignedDataObjectProperties&gt;&lt;/etsi:SignedProperties&gt;&lt;/etsi:QualifyingProperties&gt;&lt;/ds:Object&gt;&lt;/ds:Signature&gt;&lt;/factura&gt;</comprobante><mensajes/></autorizacion></autorizaciones></RespuestaAutorizacionComprobante></ns2:autorizacionComprobanteResponse></soap:Body></soap:Envelope>", "<numeroAutorizacion></numeroAutorizacion>"));
//    }

    
    String url = FacturaUtil.extraerURL("<div dir=\"ltr\"><br><div class=\"gmail_quote\">---------- Mensaje reenviado ----------<br>De: <b class=\"gmail_sendername\">DISTRIBUIDORA ROMAR - Facturacion Electronica</b> <span dir=\"ltr\">&lt;<a href=\"mailto:facturacion1@romar.com.ec\">facturacion1@romar.com.ec</a>&gt;</span><br>Fecha: 23 de julio de 2015, 19:28<br>Asunto: Ghost - Doc. Electrónico: 2307201501180145025300120010320000195431234567811<br>Para: <a href=\"mailto:rcmacas@gmail.com\">rcmacas@gmail.com</a><br><br><br>\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"<u></u>\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"<div>\n" +
"\n" +
"<table width=\"691\" border=\"0\">\n" +
"  <tbody><tr>\n" +
"    <td> </td>\n" +
"    <td> </td>\n" +
"    <td align=\"center\"> </td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td height=\"130\" width=\"125\"><img src=\"http://logos/1801450253001.png\" height=\"124\" width=\"131\"></td>\n" +
"    <td width=\"427\">\n" +
"	\n" +
"	<span>ROMAR IMPORTADORA &amp; DISTRIBUIDORA<br></span>\n" +
"    \n" +
"    <span>CELI CORONEL HUMBERTO RODRIGO</span>\n" +
"      <br>\n" +
"     <br>\n" +
"        RUC: 1801450253001        <br>\n" +
"        Direcc.: AV. SALVADOR BUSTAMANTE CELI SN Y NUEVA YORK        <br>\n" +
"        <span>\n" +
"        OBLIGADO A LLEVAR CONTABILIDAD        </span>\n" +
"        \n" +
"        <br>\n" +
"        \n" +
"        \n" +
"    <span>Contribuyente Especial Nro. 00209</span>\n" +
"    \n" +
"   </td>\n" +
"    <td align=\"center\" width=\"125\"><img src=\"http://temp/2307201501180145025300120010320000195431234567811.png\" height=\"108\" width=\"104\"></td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td colspan=\"3\" align=\"right\"></td>\n" +
"  </tr>\n" +
"</tbody></table>\n" +
"\n" +
"\n" +
"\n" +
"<table width=\"690\" bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
"  <tbody><tr bgcolor=\"#C5FCE8\">\n" +
"    <td colspan=\"4\" align=\"right\"> </td>\n" +
"  </tr>\n" +
"  <tr bgcolor=\"#C5FCE8\">\n" +
"    <td colspan=\"2\"><span>FACTURA Nro. 001-032-000019543</span></td>\n" +
"    <td colspan=\"2\"></td>\n" +
"  </tr>\n" +
"  <tr bgcolor=\"#C5FCE8\">\n" +
"    <td colspan=\"2\" align=\"left\"><span>Aut. Nro. <u></u>2307201519280418014502530017500121291<u></u></span></td>\n" +
"    <td colspan=\"2\" align=\"left\"><span><span>Ambiente: PRODUCCION</span></span></td>\n" +
"  </tr>\n" +
"  <tr bgcolor=\"#C5FCE8\">\n" +
"    <td colspan=\"2\" align=\"left\"><span>Fecha: <u></u>2015-07-23T19:28:04.415-05:00<u></u></span></td>\n" +
"    <td colspan=\"2\" align=\"left\"><span>Tipo Emisión: NORMAL</span></td>\n" +
"  </tr>\n" +
"  <tr bgcolor=\"#C5FCE8\">\n" +
"    <td colspan=\"2\" align=\"left\"> </td>\n" +
"    <td colspan=\"2\" align=\"left\"> </td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td align=\"left\"> </td>\n" +
"    <td> </td>\n" +
"    <td> </td>\n" +
"    <td> </td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td align=\"left\" width=\"80\"><span>\n" +
"      \n" +
"      RUC/CI:\n" +
"\n" +
"    </span></td>\n" +
"    <td width=\"321\">\n" +
"      <span>1104499049001    </span></td>\n" +
"    <td width=\"53\"><span>FECHA:</span></td>\n" +
"    <td width=\"236\"><span>23/07/2015 </span></td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td><span>\n" +
"      \n" +
"      \n" +
"      CLIENTE:\n" +
"      \n" +
"      \n" +
"    </span></td>\n" +
"    <td colspan=\"3\">\n" +
"	\n" +
"      <span>MACAS GONZALEZ RITA CECIBEL    \n" +
"    </span></td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td><span>\n" +
"      \n" +
"      DIRECCION:\n" +
"      \n" +
"    </span></td>\n" +
"    <td colspan=\"3\">\n" +
"	\n" +
"      <span>10 DE AGOSTO 16-35 Y AV. UNIVERSITARIA Y 18 DE NOVIEMBRE FTE A ALMACEN PAULIS    \n" +
"    \n" +
"    </span></td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td><span>TELEFONO: </span></td>\n" +
"    <td>\n" +
"	\n" +
"    <span><span>585965</span></span></td>\n" +
"    <td><span>E-MAIL.:</span></td>\n" +
"    <td>\n" +
"	\n" +
"      <span><a href=\"mailto:rcmacas@gmail.com\" target=\"_blank\">rcmacas@gmail.com</a>    </span></td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td><span>CIUDAD:</span></td>\n" +
"    \n" +
"    <td><span>LOJA</span></td>\n" +
"    <td> </td>\n" +
"    <td> </td>\n" +
"  </tr>\n" +
"</tbody></table>\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"<table width=\"689\" bgcolor=\"#FFFFFF\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\">\n" +
"  \n" +
"  <tbody><tr align=\"center\" bgcolor=\"#CCCCCC\">\n" +
"    \n" +
" \n" +
"    <td align=\"left\" height=\"30\" width=\"91\">\n" +
"    \n" +
"    Código</td>\n" +
"    <td align=\"left\" width=\"378\">Descripción</td>\n" +
"    <td width=\"48\">Cant.</td>\n" +
"    <td width=\"60\">Valor Unitario</td>\n" +
"    <td width=\"43\">Desc.</td>\n" +
"    <td width=\"50\">Valor Total</td>\n" +
"\n" +
"    \n" +
"  </tr>\n" +
"</tbody></table>\n" +
"\n" +
"  \n" +
"  \n" +
" \n" +
" \n" +
"\n" +
" \n" +
" <table><tbody><tr><td width=\"600\">\n" +
"    <table width=\"689\" bgcolor=\"#FFFFFF\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
"              <tbody><tr>\n" +
"            <td width=\"94\">\n" +
"              11009            </td><td align=\"left\" width=\"377\">   <label>ACEITE PALMA ORO LT X12 </label>\n" +
"            </td><td align=\"right\" width=\"47\">   <label>24 </label>\n" +
"            </td><td align=\"right\" width=\"69\">   <label>1.376 </label>\n" +
"            </td><td align=\"right\" width=\"42\">   <label>    0.00</label>\n" +
"          </td><td align=\"right\" width=\"60\">  <label>   33.02 </label>            </td></tr>\n" +
"	          \n" +
"   </tbody></table>\n" +
"   \n" +
"</td>\n" +
"  \n" +
"\n" +
"\n" +
"\n" +
"</tr></tbody></table>\n" +
"\n" +
"\n" +
"\n" +
"<table width=\"689\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\">\n" +
"  <tbody><tr>\n" +
"    <td rowspan=\"7\" width=\"445\"><label></label>\n" +
"      <br>\n" +
"    </td>\n" +
"    <td width=\"163\" bgcolor=\"#CCCCCC\">Subtotal 12%</td>\n" +
"    <td align=\"right\" width=\"73\" bgcolor=\"#CCCCCC\"><span>        0.00</span></td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td bgcolor=\"#CCCCCC\">Subtotal 0%</td>\n" +
"    <td align=\"right\" bgcolor=\"#CCCCCC\">       33.02</td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td bgcolor=\"#CCCCCC\">Subtotal no objeto de IVA</td>\n" +
"    <td align=\"right\" bgcolor=\"#CCCCCC\">        0.00</td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td bgcolor=\"#CCCCCC\">Subtotal sin Impuestos</td>\n" +
"    <td align=\"right\" bgcolor=\"#CCCCCC\">       33.02</td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td bgcolor=\"#CCCCCC\">Descuento</td>\n" +
"    <td align=\"right\" bgcolor=\"#CCCCCC\">        0.00</td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td bgcolor=\"#CCCCCC\">ICE</td>\n" +
"    <td align=\"right\" bgcolor=\"#CCCCCC\">        0.00</td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td width=\"163\" bgcolor=\"#CCCCCC\">IVA 12%</td>\n" +
"    <td align=\"right\" width=\"73\" bgcolor=\"#CCCCCC\">        0.00</td>\n" +
"  </tr>\n" +
"  <tr>\n" +
"    <td width=\"445\"><span>Clave de Acceso: <br>2307201501180145025300120010320000195431234567811</span></td>\n" +
"    <td height=\"23\" width=\"163\" bgcolor=\"#CCCCCC\">TOTAL $<br></td>\n" +
"    <td align=\"right\" width=\"73\" bgcolor=\"#CCCCCC\">       33.02</td>\n" +
"  </tr>\n" +
"</tbody></table>\n" +
"\n" +
"</div>\n" +
"\n" +
"\n" +
"<p>\n" +
"\n" +
"Sirvase encontrar su documento electronico adjunto: <br> \n" +
"\n" +
"<b> <a href=\"http://www.tecsicom.net/ghost.php?ca=2307201501180145025300120010320000195431234567811\" target=\"_blank\">Impresión en formato PDF</a> </b>\n" +
"\n" +
"<br><br>\n" +
"\n" +
"<b> <a href=\"http://www.tecsicom.net/fet/docs/2307201501180145025300120010320000195431234567811.zip\" target=\"_blank\">Descarga formato XML</a> </b>\n" +
"\n" +
"<br><br>\n" +
"\n" +
"Ghost - Facturación Electrónica\n" +
"\n" +
"<br>\n" +
"\n" +
"\n" +
"\n" +
"</p>\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"\n" +
"</div><br><br clear=\"all\"><br>-- <br><div class=\"gmail_signature\">fede</div>\n" +
"</div>", "<b> <a href=\"", "\" target=\"_blank\">Descarga formato XML</a>");
    
    //System.err.println(url);
    
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<factura id=\"comprobante\" version=\"1.1.0\">   <infoTributaria>     <ambiente>2</ambiente>     <tipoEmision>1</tipoEmision>     <razonSocial>BANCO DE LOJA S.A.</razonSocial>     <nombreComercial>BANCO DE LOJA S.A.</nombreComercial>     <ruc>1190002213001</ruc>     <claveAcceso>3011202001119000221300120010260005871611234567810</claveAcceso>     <codDoc>01</codDoc>     <estab>001</estab>     <ptoEmi>026</ptoEmi>     <secuencial>000587161</secuencial>     <dirMatriz>BOLIVAR Y ROCAFUERTE</dirMatriz>   </infoTributaria>   <infoFactura>     <fechaEmision>30/11/2020</fechaEmision>     <dirEstablecimiento>Bolivar 15814 y Rocafuerte</dirEstablecimiento>     <contribuyenteEspecial>3997</contribuyenteEspecial>     <obligadoContabilidad>SI</obligadoContabilidad>     <tipoIdentificacionComprador>05</tipoIdentificacionComprador>     <razonSocialComprador>GRANDA SIVISAPA JOSE LUIS</razonSocialComprador>     <identificacionComprador>1103826960</identificacionComprador>     <totalSinImpuestos>4</totalSinImpuestos>     <totalDescuento>0.00</totalDescuento>     <totalConImpuestos>       <totalImpuesto>         <codigo>2</codigo>         <codigoPorcentaje>2</codigoPorcentaje>         <baseImponible>4.00</baseImponible>         <valor>0.48</valor>       </totalImpuesto>     </totalConImpuestos>     <propina>00.00</propina>     <importeTotal>4.48</importeTotal>     <moneda>DOLAR</moneda>     <pagos>       <pago>         <formaPago>20</formaPago>         <total>4.48</total>       </pago>     </pagos>   </infoFactura>   <detalles>     <detalle>       <codigoPrincipal>4053069A1</codigoPrincipal>       <descripcion>COSTO TRANSFERENCIA WEB</descripcion>       <cantidad>3</cantidad>       <precioUnitario>0.36</precioUnitario>       <descuento>00.00</descuento>       <precioTotalSinImpuesto>1.08</precioTotalSinImpuesto>       <impuestos>         <impuesto>           <codigo>2</codigo>           <codigoPorcentaje>2</codigoPorcentaje>           <tarifa>12</tarifa>           <baseImponible>1.08</baseImponible>           <valor>0.13</valor>         </impuesto>       </impuestos>     </detalle>     <detalle>       <codigoPrincipal>4053099B2</codigoPrincipal>       <descripcion>COSTO PAGO EERSSA WEB</descripcion>       <cantidad>1</cantidad>       <precioUnitario>0.27</precioUnitario>       <descuento>00.00</descuento>       <precioTotalSinImpuesto>0.27</precioTotalSinImpuesto>       <impuestos>         <impuesto>           <codigo>2</codigo>           <codigoPorcentaje>2</codigoPorcentaje>           <tarifa>12</tarifa>           <baseImponible>0.27</baseImponible>           <valor>0.03</valor>         </impuesto>       </impuestos>     </detalle>     <detalle>       <codigoPrincipal>4244067C3</codigoPrincipal>       <descripcion>SRVP TRAN INTER WEB</descripcion>       <cantidad>4</cantidad>       <precioUnitario>0.36</precioUnitario>       <descuento>00.00</descuento>       <precioTotalSinImpuesto>1.44</precioTotalSinImpuesto>       <impuestos>         <impuesto>           <codigo>2</codigo>           <codigoPorcentaje>2</codigoPorcentaje>           <tarifa>12</tarifa>           <baseImponible>1.44</baseImponible>           <valor>0.17</valor>         </impuesto>       </impuestos>     </detalle>     <detalle>       <codigoPrincipal>4053092D4</codigoPrincipal>       <descripcion>COSTO TRANSFERENCIA PAGO DIRECTO</descripcion>       <cantidad>1</cantidad>       <precioUnitario>0.20</precioUnitario>       <descuento>00.00</descuento>       <precioTotalSinImpuesto>0.20</precioTotalSinImpuesto>       <impuestos>         <impuesto>           <codigo>2</codigo>           <codigoPorcentaje>2</codigoPorcentaje>           <tarifa>12</tarifa>           <baseImponible>0.20</baseImponible>           <valor>0.02</valor>         </impuesto>       </impuestos>     </detalle>     <detalle>       <codigoPrincipal>4285086E5</codigoPrincipal>       <descripcion>TRANSFERENCIAS SCI RECIBIDAS</descripcion>       <cantidad>1</cantidad>       <precioUnitario>0.20</precioUnitario>       <descuento>00.00</descuento>       <precioTotalSinImpuesto>0.20</precioTotalSinImpuesto>       <impuestos>         <impuesto>           <codigo>2</codigo>           <codigoPorcentaje>2</codigoPorcentaje>           <tarifa>12</tarifa>           <baseImponible>0.20</baseImponible>           <valor>0.02</valor>         </impuesto>       </impuestos>     </detalle>     <detalle>       <codigoPrincipal>4031095F6</codigoPrincipal>       <descripcion>COSTO PAGO EMAALEP</descripcion>       <cantidad>3</cantidad>       <precioUnitario>0.27</precioUnitario>       <descuento>00.00</descuento>       <precioTotalSinImpuesto>0.81</precioTotalSinImpuesto>       <impuestos>         <impuesto>           <codigo>2</codigo>           <codigoPorcentaje>2</codigoPorcentaje>           <tarifa>12</tarifa>           <baseImponible>0.81</baseImponible>           <valor>0.10</valor>         </impuesto>       </impuestos>     </detalle>   </detalles> <ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:etsi=\"http://uri.etsi.org/01903/v1.3.2#\" Id=\"Signature995092\">\n" +
"<ds:SignedInfo Id=\"Signature-SignedInfo363246\">\n" +
"<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></ds:CanonicalizationMethod>\n" +
"<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></ds:SignatureMethod>\n" +
"<ds:Reference Id=\"SignedPropertiesID503724\" Type=\"http://uri.etsi.org/01903#SignedProperties\" URI=\"#Signature995092-SignedProperties478702\">\n" +
"<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod>\n" +
"<ds:DigestValue>hCX/TNOOuNqyVio5E5EvFRplJBE=</ds:DigestValue>\n" +
"</ds:Reference>\n" +
"<ds:Reference URI=\"#Certificate1181313\">\n" +
"<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod>\n" +
"<ds:DigestValue>jeR86RfD0aIoRk8chrIa8c35fcs=</ds:DigestValue>\n" +
"</ds:Reference>\n" +
"<ds:Reference Id=\"Reference-ID-395235\" URI=\"#comprobante\">\n" +
"<ds:Transforms>\n" +
"<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></ds:Transform>\n" +
"</ds:Transforms>\n" +
"<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod>\n" +
"<ds:DigestValue>FLu3dGzEOGbsrwqIjINpqIzXo2Q=</ds:DigestValue>\n" +
"</ds:Reference>\n" +
"</ds:SignedInfo>\n" +
"<ds:SignatureValue Id=\"SignatureValue935596\">\n" +
"LM+kSoTbui/p9QegYoNbnO2grTFw0Yy8n/t/uSpVlWUC+v7TGqCnl2YXS0EIb50U3bCuGbn2LpJg\n" +
"zSDn8l4Id4FlZrNckuaFy7gREGfcq7vDXjh+zjOM/JT2EtSEGMPz/uTSmmozQh6BaXib4lHJm9S0\n" +
"UWPooUtcO8jZ+smh2pqguR60OH/1t9E7kj7rIJYfeopN7dAzQF/MwAWNVBvwEmaYuN661UirsW6y\n" +
"qFfvYx0X2GAG/6TKrIt87VTlOfGy02QmO1lQpeCMMxcXdxrxAd9FR71SZPjVHMt0kiwHPAzBvSBa\n" +
"amouHXyW+xLKZqiRLAEI33d/7IJE9F9Ac9/YUw==\n" +
"</ds:SignatureValue>\n" +
"<ds:KeyInfo Id=\"Certificate1181313\">\n" +
"<ds:X509Data>\n" +
"<ds:X509Certificate>\n" +
"MIIJ6jCCB9KgAwIBAgIEW2I/4TANBgkqhkiG9w0BAQsFADCBoTELMAkGA1UEBhMCRUMxIjAgBgNV\n" +
"BAoTGUJBTkNPIENFTlRSQUwgREVMIEVDVUFET1IxNzA1BgNVBAsTLkVOVElEQUQgREUgQ0VSVElG\n" +
"SUNBQ0lPTiBERSBJTkZPUk1BQ0lPTi1FQ0lCQ0UxDjAMBgNVBAcTBVFVSVRPMSUwIwYDVQQDExxB\n" +
"QyBCQU5DTyBDRU5UUkFMIERFTCBFQ1VBRE9SMB4XDTIwMDEwOTE3MTM1MFoXDTIyMDEwOTE3NDM1\n" +
"MFowgbMxCzAJBgNVBAYTAkVDMSIwIAYDVQQKExlCQU5DTyBDRU5UUkFMIERFTCBFQ1VBRE9SMTcw\n" +
"NQYDVQQLEy5FTlRJREFEIERFIENFUlRJRklDQUNJT04gREUgSU5GT1JNQUNJT04tRUNJQkNFMQ4w\n" +
"DAYDVQQHEwVRVUlUTzE3MBEGA1UEBRMKMDAwMDA0MjgyODAiBgNVBAMTG0VOQSBNQVJJQU5FTEEg\n" +
"R1VFUlJFUk8gTU9SQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAM9+P1PVJjQHUswD\n" +
"LkdB0CuD032+WfO/4d+bhBOf2u0WzTEQk/7yzL3plB7N71vorFWvWPa5gPG3G7spCpWO7XIWPDPE\n" +
"2NsHMqoKUw4nvsZJKM5iuDJk6n982yZTMH+3l+gseV/CjRfaq+kiOCZUqBy+gepiapVQJACZSjDP\n" +
"RXF43gISJYIzWYkFpJobA05YPUajkrD+U3OyCbMU/Pqdy9Oe6PiaTwCtczrmnJuJyfxGEXdS/ksT\n" +
"t3U5pwUJgk1DvB7d+jlosCVHiOrY2hv8DxMpOhebDF9/A8Y+hvCOVLcUa9XGisYI3sTdXezlPrMr\n" +
"CgYGWjZ6Z7Vpd1b6I/pSBDUCAwEAAaOCBRQwggUQMAsGA1UdDwQEAwIHgDBnBgNVHSAEYDBeMFwG\n" +
"CysGAQQBgqg7AgIBME0wSwYIKwYBBQUHAgEWP2h0dHA6Ly93d3cuZWNpLmJjZS5lYy9wb2xpdGlj\n" +
"YS1jZXJ0aWZpY2Fkby9wZXJzb25hLWp1cmlkaWNhLnBkZjCBkQYIKwYBBQUHAQEEgYQwgYEwPgYI\n" +
"KwYBBQUHMAGGMmh0dHA6Ly9vY3NwLmVjaS5iY2UuZWMvZWpiY2EvcHVibGljd2ViL3N0YXR1cy9v\n" +
"Y3NwMD8GCCsGAQUFBzABhjNodHRwOi8vb2NzcDEuZWNpLmJjZS5lYy9lamJjYS9wdWJsaWN3ZWIv\n" +
"c3RhdHVzL29jc3AwIAYKKwYBBAGCqDsDCgQSExBCQU5DTyBERSBMT0pBIFNBMB0GCisGAQQBgqg7\n" +
"AwsEDxMNMTE5MDAwMjIxMzAwMTAaBgorBgEEAYKoOwMBBAwTCjExMDM1OTY1MjIwHQYKKwYBBAGC\n" +
"qDsDAgQPEw1FTkEgTUFSSUFORUxBMBgGCisGAQQBgqg7AwMEChMIR1VFUlJFUk8wFAYKKwYBBAGC\n" +
"qDsDBAQGEwRNT1JBMCQGCisGAQQBgqg7AwUEFhMUSkVGRSBERSBDT05UQUJJTElEQUQwJAYKKwYB\n" +
"BAGCqDsDBwQWExRCT0xJVkFSIFkgUk9DQUZVRVJURTAZBgorBgEEAYKoOwMIBAsTCTA3MzcwMTYw\n" +
"MDAUBgorBgEEAYKoOwMJBAYTBExvamEwFwYKKwYBBAGCqDsDDAQJEwdFQ1VBRE9SMCAGCisGAQQB\n" +
"gqg7AzMEEhMQU09GVFdBUkUtQVJDSElWTzAqBgNVHREEIzAhgR9lbmFfZ3VlcnJlcm9AYmFuY29k\n" +
"ZWxvamEuZmluLmVjMIIB3wYDVR0fBIIB1jCCAdIwggHOoIIByqCCAcaGgdVsZGFwOi8vYmNlcWxk\n" +
"YXBzdWJwMS5iY2UuZWMvY249Q1JMOTAyLGNuPUFDJTIwQkFOQ08lMjBDRU5UUkFMJTIwREVMJTIw\n" +
"RUNVQURPUixsPVFVSVRPLG91PUVOVElEQUQlMjBERSUyMENFUlRJRklDQUNJT04lMjBERSUyMElO\n" +
"Rk9STUFDSU9OLUVDSUJDRSxvPUJBTkNPJTIwQ0VOVFJBTCUyMERFTCUyMEVDVUFET1IsYz1FQz9j\n" +
"ZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2WGNGh0dHA6Ly93d3cuZWNpLmJjZS5lYy9DUkwv\n" +
"ZWNpX2JjZV9lY19jcmxmaWxlY29tYi5jcmykgbUwgbIxCzAJBgNVBAYTAkVDMSIwIAYDVQQKExlC\n" +
"QU5DTyBDRU5UUkFMIERFTCBFQ1VBRE9SMTcwNQYDVQQLEy5FTlRJREFEIERFIENFUlRJRklDQUNJ\n" +
"T04gREUgSU5GT1JNQUNJT04tRUNJQkNFMQ4wDAYDVQQHEwVRVUlUTzElMCMGA1UEAxMcQUMgQkFO\n" +
"Q08gQ0VOVFJBTCBERUwgRUNVQURPUjEPMA0GA1UEAxMGQ1JMOTAyMCsGA1UdEAQkMCKADzIwMjAw\n" +
"MTA5MTcxMzUwWoEPMjAyMjAxMDkxNzQzNTBaMB8GA1UdIwQYMBaAFEii3yMfHfgsUXqMA81JMqUJ\n" +
"wZSrMB0GA1UdDgQWBBSz9fYgiLUXWQkg2f51tuzs8P58tTAJBgNVHRMEAjAAMBkGCSqGSIb2fQdB\n" +
"AAQMMAobBFY4LjEDAgSwMA0GCSqGSIb3DQEBCwUAA4ICAQDHNAwJaoa53orefCnzszxBs1ZVssSk\n" +
"Gc+bXqr2mBW7pe2oiwqEwEBJ1B/XRsuTnBL+CEZgcr6cJy2kNd3DGeUfk7+I8kpB25TdPjGRV/Oo\n" +
"bqFigbBh+zPo42NOFX9XhWxmM/O5mGItZ2FtRFEtDpQSKTMKbdAaSSxz9pO/f3PhsjJfKZjvoqhI\n" +
"8aHWnpmC5qQRtb7ycPycmSCOsUdm/dF2zvaJWt40RnKmCTfXFDreyUgWmWIMlzDd0NsvoqACmuLY\n" +
"62ttF1QlPb5hdXw6K/IbI9t+8hrPMf5/KC0Gpmd84USXmL7HErWKnNqAcqJrBCZxORvQ6g7a+RCz\n" +
"6Bh+GYxWYpedBE2fyzRczzdlCVa3TfBGZ41TnVCqNM37Zgo71zrJw43OvfR9AJSW9QCyoETCcQ92\n" +
"09v6+Xj9rRawq0tw9OvbRxFW9B8grz38CluP47dWIlkkX4/xLQ41I5bSXT6X8P41deImeQlR5CVS\n" +
"KrHP3UiO8dLnR6SJHuONnPrAlLDA7YPv10O2DArR+OPSFRjrwAktjP1NWsGFS6+qlFXKVmgx44mv\n" +
"lnP6ddZl8sr7Ue21PsL6NYrB327i0CJuaLtA16qjPcf7D0Eme6tJya5JFNT9aUgJ/kb+NQam6/8h\n" +
"JtQUIYKzRIky9MlvDnZ27J9xPflQZLO0vtY3/YPQiKkKJQ==\n" +
"</ds:X509Certificate>\n" +
"</ds:X509Data>\n" +
"<ds:KeyValue>\n" +
"<ds:RSAKeyValue>\n" +
"<ds:Modulus>\n" +
"z34/U9UmNAdSzAMuR0HQK4PTfb5Z87/h35uEE5/a7RbNMRCT/vLMvemUHs3vW+isVa9Y9rmA8bcb\n" +
"uykKlY7tchY8M8TY2wcyqgpTDie+xkkozmK4MmTqf3zbJlMwf7eX6Cx5X8KNF9qr6SI4JlSoHL6B\n" +
"6mJqlVAkAJlKMM9FcXjeAhIlgjNZiQWkmhsDTlg9RqOSsP5Tc7IJsxT8+p3L057o+JpPAK1zOuac\n" +
"m4nJ/EYRd1L+SxO3dTmnBQmCTUO8Ht36OWiwJUeI6tjaG/wPEyk6F5sMX38Dxj6G8I5UtxRr1caK\n" +
"xgjexN1d7OU+sysKBgZaNnpntWl3Vvoj+lIENQ==\n" +
"</ds:Modulus>\n" +
"<ds:Exponent>AQAB</ds:Exponent>\n" +
"</ds:RSAKeyValue>\n" +
"</ds:KeyValue>\n" +
"</ds:KeyInfo>\n" +
"<ds:Object Id=\"Signature995092-Object378421\"><etsi:QualifyingProperties Target=\"#Signature995092\"><etsi:SignedProperties Id=\"Signature995092-SignedProperties478702\"><etsi:SignedSignatureProperties><etsi:SigningTime>2020-12-01T19:38:49-05:00</etsi:SigningTime><etsi:SigningCertificate><etsi:Cert><etsi:CertDigest><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod><ds:DigestValue>JnO16qgWnMpPs+Ux4Si9ewq/zVY=</ds:DigestValue></etsi:CertDigest><etsi:IssuerSerial><ds:X509IssuerName>CN=AC BANCO CENTRAL DEL ECUADOR,L=QUITO,OU=ENTIDAD DE CERTIFICACION DE INFORMACION-ECIBCE,O=BANCO CENTRAL DEL ECUADOR,C=EC</ds:X509IssuerName><ds:X509SerialNumber>1533165537</ds:X509SerialNumber></etsi:IssuerSerial></etsi:Cert></etsi:SigningCertificate><etsi:SignerRole><etsi:ClaimedRoles><etsi:ClaimedRole>Rol de firma</etsi:ClaimedRole></etsi:ClaimedRoles></etsi:SignerRole></etsi:SignedSignatureProperties><etsi:SignedDataObjectProperties><etsi:DataObjectFormat ObjectReference=\"#Reference-ID-395235\"><etsi:Description>contenido firma</etsi:Description><etsi:MimeType>text/xml</etsi:MimeType></etsi:DataObjectFormat></etsi:SignedDataObjectProperties></etsi:SignedProperties></etsi:QualifyingProperties></ds:Object></ds:Signature></factura>";
        System.out.println(FacturaUtil.read(xml).getId());
    }
}
