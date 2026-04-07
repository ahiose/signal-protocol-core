package com.opensignal.protocol.gb20999.core.frame;

import java.util.Arrays;

/**
 * A single data value within a GB/T 20999 frame, identified by the
 * four-tuple (dataClassId, objectId, attributeId, elementId).
 */
public class DataValue {

    private final int index;
    private final int length;
    private final int dataClassId;
    private final int objectId;
    private final int attributeId;
    private final int elementId;
    private final byte[] data;

    public DataValue(int index, int length, int dataClassId, int objectId,
                     int attributeId, int elementId, byte[] data) {
        this.index = index;
        this.length = length;
        this.dataClassId = dataClassId;
        this.objectId = objectId;
        this.attributeId = attributeId;
        this.elementId = elementId;
        this.data = data;
    }

    /**
     * Create a query-style DataValue with no payload.
     */
    public static DataValue queryOf(int index, int dataClassId, int objectId,
                                    int attributeId, int elementId) {
        return new DataValue(index, 4, dataClassId, objectId, attributeId, elementId, null);
    }

    /**
     * Create a DataValue with payload data.
     */
    public static DataValue of(int index, int dataClassId, int objectId,
                               int attributeId, int elementId, byte[] data) {
        int len = 4 + (data == null ? 0 : data.length);
        return new DataValue(index, len, dataClassId, objectId, attributeId, elementId, data);
    }

    public int getIndex() { return index; }
    public int getLength() { return length; }
    public int getDataClassId() { return dataClassId; }
    public int getObjectId() { return objectId; }
    public int getAttributeId() { return attributeId; }
    public int getElementId() { return elementId; }
    public byte[] getData() { return data; }

    public boolean hasData() {
        return data != null && data.length > 0;
    }

    public int dataLength() {
        return data == null ? 0 : data.length;
    }

    /**
     * Total wire length: 4 bytes (classId+objectId+attrId+elemId) + data
     */
    public int wireLength() {
        return 4 + dataLength();
    }

    @Override
    public String toString() {
        return String.format("DataValue[%d.%d.%d.%d len=%d]",
                dataClassId, objectId, attributeId, elementId, dataLength());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataValue that = (DataValue) o;
        return index == that.index
                && dataClassId == that.dataClassId
                && objectId == that.objectId
                && attributeId == that.attributeId
                && elementId == that.elementId
                && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(dataClassId);
        result = 31 * result + Integer.hashCode(objectId);
        result = 31 * result + Integer.hashCode(attributeId);
        result = 31 * result + Integer.hashCode(elementId);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
