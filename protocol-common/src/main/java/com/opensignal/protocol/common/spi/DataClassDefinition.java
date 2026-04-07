package com.opensignal.protocol.common.spi;

import java.util.Collections;
import java.util.List;

/**
 * Definition of a data class, including its attributes.
 * Used both for standard GB/T 20999 data classes and vendor extensions.
 */
public class DataClassDefinition {

    private final int dataClassId;
    private final String name;
    private final List<AttributeDefinition> attributes;

    public DataClassDefinition(int dataClassId, String name, List<AttributeDefinition> attributes) {
        this.dataClassId = dataClassId;
        this.name = name;
        this.attributes = attributes == null ? Collections.emptyList() : Collections.unmodifiableList(attributes);
    }

    public int getDataClassId() { return dataClassId; }
    public String getName() { return name; }
    public List<AttributeDefinition> getAttributes() { return attributes; }

    public static class AttributeDefinition {

        private final int attributeId;
        private final String name;
        private final DataType dataType;
        private final int byteLength;

        public AttributeDefinition(int attributeId, String name, DataType dataType, int byteLength) {
            this.attributeId = attributeId;
            this.name = name;
            this.dataType = dataType;
            this.byteLength = byteLength;
        }

        public int getAttributeId() { return attributeId; }
        public String getName() { return name; }
        public DataType getDataType() { return dataType; }
        public int getByteLength() { return byteLength; }
    }

    public enum DataType {
        UINT8, UINT16, UINT32, BYTE_ARRAY, STRING, BOOL
    }
}
