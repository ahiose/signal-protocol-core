package com.opensignal.sample;

import com.opensignal.protocol.common.model.ControlMode;
import com.opensignal.protocol.common.spi.VendorProfile;
import com.opensignal.protocol.gb20999.client.Gb20999Client;
import com.opensignal.protocol.gb20999.client.Gb20999ClientConfig;
import com.opensignal.protocol.gb20999.core.constant.CommandCode;
import com.opensignal.protocol.gb20999.core.constant.DataClassId;
import com.opensignal.protocol.gb20999.core.frame.DataValue;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.server.Gb20999Server;
import com.opensignal.protocol.gb20999.server.Gb20999ServerConfig;

import java.net.InetSocketAddress;

public class BasicQueryExample {

    public static void main(String[] args) throws Exception {
        // 1. Start a mock server for testing
        // (In real usage, you'd connect to an actual signal controller)
        Gb20999ServerConfig serverConfig = Gb20999ServerConfig.builder()
                .bindPort(9000)
                .build();
        Gb20999Server server = Gb20999Server.create(serverConfig);
        server.start();
        System.out.println("Mock signal controller started on port 9000");

        // 2. Create client with KeLi vendor profile
        Gb20999ClientConfig clientConfig = Gb20999ClientConfig.builder()
                .remoteAddress(new InetSocketAddress("127.0.0.1", 9000))
                .vendorId("keli")
                .build();
        Gb20999Client client = Gb20999Client.create(clientConfig);
        client.connect();
        System.out.println("Connected to signal controller");
        System.out.println("Vendor: " + client.getVendorProfile().vendorName());

        // 3. Query device info (data class 1, object 1 = manufacturer)
        try {
            Frame response = client.query(DataClassId.DEVICE, 1);
            System.out.println("Device query response: type=" + response.getFrameType()
                    + ", dataValues=" + response.dataValueCount());
            // Print response data values
            for (DataValue dv : response.getDataValues()) {
                System.out.println("  DataValue: class=" + dv.getDataClassId()
                        + " obj=" + dv.getObjectId()
                        + " data=" + (dv.hasData() ? dv.dataLength() + " bytes" : "empty"));
            }
        } catch (Exception e) {
            System.out.println("Query failed: " + e.getMessage());
        }

        // 4. Query run status (DataClassId.CURRENT_STATUS)
        try {
            Frame status = client.query(DataClassId.CURRENT_STATUS, 2);
            System.out.println("Run status response: type=" + status.getFrameType());
        } catch (Exception e) {
            System.out.println("Status query failed: " + e.getMessage());
        }

        // 5. Send a command (yellow flash)
        try {
            client.command(CommandCode.FLASH.code());
            System.out.println("Yellow flash command sent successfully");
        } catch (Exception e) {
            System.out.println("Command failed: " + e.getMessage());
        }

        // 6. Demonstrate vendor mode mapping
        VendorProfile profile = client.getVendorProfile();
        int deviceMode = profile.toDeviceControlMode(ControlMode.CENTER_LOCK_SCHEME.getCode());
        System.out.println("Standard mode CENTER_LOCK_SCHEME(53) -> KeLi device mode: " + deviceMode);

        // 7. Cleanup
        client.close();
        server.close();
        System.out.println("Done.");
    }
}
