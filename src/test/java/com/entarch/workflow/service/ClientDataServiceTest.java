package com.entarch.workflow.service;

import com.entarch.workflow.model.ClientData;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ClientDataServiceTest {

    @Autowired
    private ClientDataService service;

    private final Faker faker = new Faker(new Random());

    @EnabledIfEnvironmentVariable(named = "AWS_ACCESS_KEY_ID", matches=".*")
    @Test
    public void testRoundTrip() {
        String email = faker.bothify("????##@gmail.com");
        int beforeCount = service.getAllClientData().size();
        assertThat(service.getClientData(email)).isEmpty();

        service.createClientData(buildClientData(email));
        Optional<ClientData> result = service.getClientData(email);
        assertThat(result).isNotEmpty();
        assertThat(result.get().getEmail()).isEqualTo(email);
        int afterCount = service.getAllClientData().size();
        assertThat(afterCount).isEqualTo(beforeCount + 1);

        service.deleteClientData(email);
        assertThat(service.getClientData(email)).isEmpty();
    }

    @EnabledIfEnvironmentVariable(named = "AWS_ACCESS_KEY_ID", matches=".*")
    @Test
    public void testUpdateOwnerAndType() {
        String email = faker.bothify("????##@gmail.com");
        String owner = faker.bothify("????##@gmail.com");

        service.createClientData(buildClientData(email));
        service.setClientDataOwnerAndType(email, owner, "Client");
        Optional<ClientData> clientData = service.getClientData(email);
        assertThat(clientData).isNotEmpty();
        assertThat(clientData.get().getOwner()).isEqualTo(owner);
        assertThat(clientData.get().getType()).isEqualTo("Client");

        service.deleteClientData(email);
    }

    @EnabledIfEnvironmentVariable(named = "AWS_ACCESS_KEY_ID", matches=".*")
    @Test
    public void testSetExecutionArn() {
        String email = faker.bothify("????##@gmail.com");
        String executionArn = "test";
        service.createClientData(buildClientData(email));
        service.setExecutionArn(email, executionArn);
        Optional<ClientData> clientData = service.getClientData(email);
        assertThat(clientData).isNotEmpty();
        assertThat(clientData.get().getExecutionArn()).isEqualTo(executionArn);
        service.deleteClientData(email);
    }

    @Test
    @Disabled("Run manually")
    public void testBulkAddClientData() {
        int count = 15;
        for(int i = 0; i < count; i++ ) {
            String email = faker.bothify("????##@gmail.com");
            service.createClientData(buildClientData(email));
        }
    }

    private ClientData buildClientData(String email) {
        return ClientData.builder()
                .email(email)
                .name(faker.name().firstName() + " " + faker.name().lastName())
                .owner("None")
                .type("Prospect")
                .executionArn("None")
                .build();
    }
}
