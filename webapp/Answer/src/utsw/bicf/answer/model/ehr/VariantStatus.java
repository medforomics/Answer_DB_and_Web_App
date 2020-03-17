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
 * <p>Java class for VariantStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VariantStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ambiguous"/>
 *     &lt;enumeration value="known"/>
 *     &lt;enumeration value="likely"/>
 *     &lt;enumeration value="unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VariantStatus")
@XmlEnum
public enum VariantStatus {

    @XmlEnumValue("ambiguous")
    AMBIGUOUS("ambiguous"),
    @XmlEnumValue("known")
    KNOWN("known"),
    @XmlEnumValue("likely")
    LIKELY("likely"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");
    private final String value;

    VariantStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VariantStatus fromValue(String v) {
        for (VariantStatus c: VariantStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
