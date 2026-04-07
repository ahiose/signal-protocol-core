package com.opensignal.sample.spring;

import com.opensignal.protocol.gb20999.client.Gb20999Client;
import com.opensignal.protocol.gb20999.core.constant.DataClassId;
import com.opensignal.protocol.gb20999.core.frame.Frame;
import com.opensignal.protocol.gb20999.spring.EnableGb20999Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableGb20999Client
public class Gb20999SpringBootSampleApp implements CommandLineRunner {

    @Autowired
    private Gb20999Client client;

    public static void main(String[] args) {
        SpringApplication.run(Gb20999SpringBootSampleApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== GB/T 20999 Spring Boot Sample ===");
        System.out.println("Client connected: " + client.isConnected());
        System.out.println("Vendor: " + client.getVendorProfile().vendorName());

        if (client.isConnected()) {
            try {
                Frame response = client.query(DataClassId.DEVICE, 1);
                System.out.println("Device info response: " + response.getFrameType());
            } catch (Exception e) {
                System.out.println("Query failed (expected if no device): " + e.getMessage());
            }
        }
        System.out.println("=== Sample complete ===");
    }
}
