package com.opensignal.protocol.gb20999.server;

import com.opensignal.protocol.gb20999.core.constant.DataClassId;
import com.opensignal.protocol.gb20999.core.constant.RunMode;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory mock state for the simulated signal controller.
 */
public class MockSignalData {

    private final ConcurrentHashMap<String, byte[]> storedValues = new ConcurrentHashMap<String, byte[]>();

    public final String deviceManufacturer = "OpenSignal Simulator";
    public final String deviceVersion = "V1.0.0";
    public final String deviceSerial = "SIM-20999-001";

    public final int phaseCount = 8;
    public final int stageCount = 4;
    public final int planCount = 3;
    public final int lightGroupCount = 16;
    public final int detectorCount = 8;

    private volatile int currentRunMode = RunMode.LOCAL_FIX_CYCLE.code();
    private volatile int currentPlan = 1;
    private volatile int currentStage = 1;

    private static String storeKey(int dataClassId, int objectId) {
        return dataClassId + "." + objectId;
    }

    private static byte[] asciiFixed(String s, int length) {
        byte[] out = new byte[length];
        Arrays.fill(out, (byte) ' ');
        if (s == null) {
            return out;
        }
        byte[] src = s.getBytes(StandardCharsets.US_ASCII);
        int n = Math.min(src.length, length);
        System.arraycopy(src, 0, out, 0, n);
        return out;
    }

    private static void putU16Be(byte[] buf, int offset, int v) {
        buf[offset] = (byte) ((v >> 8) & 0xFF);
        buf[offset + 1] = (byte) (v & 0xFF);
    }

    private byte[] getOrDefault(int dataClassId, int objectId, byte[] defaults) {
        String key = storeKey(dataClassId, objectId);
        byte[] stored = storedValues.get(key);
        if (stored != null) {
            return Arrays.copyOf(stored, stored.length);
        }
        return defaults;
    }

    public byte[] getDeviceInfoData(int objectId) {
        switch (objectId) {
            case 1:
                return getOrDefault(DataClassId.DEVICE, objectId, asciiFixed(deviceManufacturer, 32));
            case 2:
                return getOrDefault(DataClassId.DEVICE, objectId, asciiFixed(deviceVersion, 16));
            case 3:
                return getOrDefault(DataClassId.DEVICE, objectId, asciiFixed(deviceSerial, 32));
            case 4:
                return getOrDefault(DataClassId.DEVICE, objectId, asciiFixed("2024-01-01", 16));
            case 5:
                return getOrDefault(DataClassId.DEVICE, objectId, asciiFixed("2024-06-01", 16));
            default:
                return getOrDefault(DataClassId.DEVICE, objectId, new byte[]{0x00});
        }
    }

    public byte[] getBasicInfoData(int objectId) {
        switch (objectId) {
            case 1:
                return getOrDefault(DataClassId.PHASE, objectId, asciiFixed("Mock Cross 01", 32));
            case 2: {
                byte[] ip = new byte[48];
                System.arraycopy(asciiFixed("192.168.1.100", 16), 0, ip, 0, 16);
                System.arraycopy(asciiFixed("255.255.255.0", 16), 0, ip, 16, 16);
                System.arraycopy(asciiFixed("192.168.1.1", 16), 0, ip, 32, 16);
                return getOrDefault(DataClassId.PHASE, objectId, ip);
            }
            case 3: {
                byte[] upper = new byte[24];
                System.arraycopy(asciiFixed("192.168.1.200", 16), 0, upper, 0, 16);
                putU16Be(upper, 16, 9000);
                upper[18] = 0x01;
                return getOrDefault(DataClassId.PHASE, objectId, upper);
            }
            case 4:
                return getOrDefault(DataClassId.PHASE, objectId, new byte[]{0x00, 0x20});
            case 5: {
                byte[] sid = new byte[2];
                putU16Be(sid, 0, 0x0001);
                return getOrDefault(DataClassId.PHASE, objectId, sid);
            }
            case 6:
                return getOrDefault(DataClassId.PHASE, objectId, new byte[]{0x01});
            case 7:
                return getOrDefault(DataClassId.PHASE, objectId, new byte[]{0x00});
            default:
                return getOrDefault(DataClassId.PHASE, objectId, new byte[]{0x00});
        }
    }

    /**
     * Mock payload for data class 13 ({@link RunStatus} in model javadoc).
     */
    public byte[] getRunStatusData(int objectId, int attributeId) {
        int cls = Gb20999ServerConfig.RUN_STATUS_DATA_CLASS_ID;
        if (objectId == 1) {
            byte[] status = new byte[20];
            status[0] = 0x00;
            status[1] = 0x00;
            status[2] = 0x00;
            status[3] = 0x00;
            putU16Be(status, 4, 220);
            putU16Be(status, 6, 500);
            status[8] = 25;
            status[9] = 50;
            status[10] = 0;
            status[11] = 0;
            Arrays.fill(status, 12, 20, (byte) 0);
            return getOrDefault(cls, objectId, status);
        }
        if (objectId == 2) {
            byte[] row = new byte[5];
            row[0] = 0x01;
            row[1] = (byte) (currentRunMode & 0xFF);
            putU16Be(row, 2, currentPlan);
            row[4] = (byte) (currentStage & 0xFF);
            return getOrDefault(cls, objectId, row);
        }
        byte[] ignored = new byte[1];
        ignored[0] = (byte) (attributeId & 0xFF);
        return getOrDefault(cls, objectId, ignored);
    }

    /**
     * Persists SET payload. Returns {@code null} on success.
     */
    public byte[] handleSet(int dataClassId, int objectId, byte[] data) {
        byte[] copy = data == null ? new byte[0] : Arrays.copyOf(data, data.length);
        storedValues.put(storeKey(dataClassId, objectId), copy);

        if (dataClassId == Gb20999ServerConfig.RUN_STATUS_DATA_CLASS_ID) {
            applyRunStatusSet(objectId, copy);
        }
        return null;
    }

    private void applyRunStatusSet(int objectId, byte[] copy) {
        if (objectId == 2 && copy.length >= 5) {
            currentRunMode = copy[1] & 0xFF;
            currentPlan = ((copy[2] & 0xFF) << 8) | (copy[3] & 0xFF);
            currentStage = copy[4] & 0xFF;
        }
    }

    public int getCurrentRunMode() {
        return currentRunMode;
    }

    public void setCurrentRunMode(int currentRunMode) {
        this.currentRunMode = currentRunMode;
    }

    public int getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(int currentPlan) {
        this.currentPlan = currentPlan;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }
}
