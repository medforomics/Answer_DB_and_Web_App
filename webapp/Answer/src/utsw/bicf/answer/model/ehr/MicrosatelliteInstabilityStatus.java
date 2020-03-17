//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.28 at 02:28:17 PM CST 
//


package utsw.bicf.answer.model.ehr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MicrosatelliteInstabilityStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MicrosatelliteInstabilityStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MSI-H"/>
 *     &lt;enumeration value="MSI-L"/>
 *     &lt;enumeration value="MSS"/>
 *     &lt;enumeration value="unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MicrosatelliteInstabilityStatus")
@XmlEnum
public enum MicrosatelliteInstabilityStatus {


    /**
     * MSI high
     * 
     */
    @XmlEnumValue("MSI-H")
    MSI_H("MSI-H"),

    /**
     * MSI low
     * 
     */
    @XmlEnumValue("MSI-L")
    MSI_L("MSI-L"),

    /**
     * MSI stable
     * 
     */
    MSS("MSS"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");
    private final String value;

    MicrosatelliteInstabilityStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MicrosatelliteInstabilityStatus fromValue(String v) {
        for (MicrosatelliteInstabilityStatus c: MicrosatelliteInstabilityStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
