package com.experiment.SpringBeanThreadSafety.integration;

import com.experiment.SpringBeanThreadSafety.entities.Product;
import com.experiment.SpringBeanThreadSafety.entities.PurchaseProductDto;
import com.experiment.SpringBeanThreadSafety.entities.UserDto;
import com.experiment.SpringBeanThreadSafety.services.MessageBroker;
import com.experiment.SpringBeanThreadSafety.services.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ThreadSafetyApplicationTests {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    private UserService userService;

    RestTemplate restTemplate = new RestTemplate();

    /**
     * The point of this integration test is to run two parallel post requests
     * at the same time and see if they execute in a thread safe fashion or not. This test might pass
     * sometimes, but you can notice a race condition sometimes between the threads where both the threads
     * update the totalUsersCount to 1 and the assertion fails. But the point is: making singletons stateful is not a good choice,
     * but if we do that we should guarantee race condition would not happen.
     */
    @Test
    void testSingleton() throws Exception{
        final String baseUrl = "http://localhost:"+randomServerPort+"/users/create";
        URI url = new URI(baseUrl);
        UserDto user1 = new UserDto("user1", "user1@howdy.com");
        UserDto user2 = new UserDto("user2", "user2@howdy.com");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<UserDto> request1 = new HttpEntity<>(user1, headers);
        HttpEntity<UserDto> request2 = new HttpEntity<>(user2, headers);

        Thread thread1 = new Thread(() -> makePostCall(url, request1));
        Thread thread2 = new Thread(() -> makePostCall(url, request2));
        thread1.start();
        thread2.start();

        /* Join is a synchronization method that blocks the calling thread (that is, the thread that calls the method)
         until the thread whose Join method is called has completed. Use this method to ensure that a thread has
         been terminated. The caller will block indefinitely if the thread does not terminate. */

        thread1.join();
        thread2.join();

        System.out.println("number of users created: " + userService.totalCreatedUsers);

        assertEquals(2, userService.totalCreatedUsers,
                "number of users created: " + userService.totalCreatedUsers);
    }

    @Test
    void testDefaultBehaviourOfPrototypeWithSingleton() throws Exception{
        final String baseUrl = "http://localhost:"+randomServerPort+"/purchase";
        URI url = new URI(baseUrl);
        PurchaseProductDto purchaseProductDto1 = new PurchaseProductDto(Product.builder().id(1).name("laptop").build(), "Buyer One");
        PurchaseProductDto purchaseProductDto2 = new PurchaseProductDto(Product.builder().id(2).name("television").build(), "Buyer Two");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<PurchaseProductDto> request1 = new HttpEntity<>(purchaseProductDto1, headers);
        HttpEntity<PurchaseProductDto> request2 = new HttpEntity<>(purchaseProductDto2, headers);

        Thread thread1 = new Thread(() -> makePostCall(url, request1));
        Thread thread2 = new Thread(() -> makePostCall(url, request2));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();


        System.out.println(MessageBroker.messages);
        assertEquals(2, MessageBroker.eventsCount.get(),
                "number of ProductPurchasedEvent instances: " + MessageBroker.eventsCount.get());
        assertNotEquals(MessageBroker.messages.get(0).getEntityId(), MessageBroker.messages.get(1).getEntityId());
    }

    @Test
    void testProxiedPrototypeWithSingleton() throws Exception {
        final String baseUrl = "http://localhost:"+randomServerPort+"/purchase/with-proxy";
        URI url = new URI(baseUrl);
        PurchaseProductDto purchaseProductDto1 = new PurchaseProductDto(Product.builder().id(1).name("laptop").build(), "Buyer One");
        PurchaseProductDto purchaseProductDto2 = new PurchaseProductDto(Product.builder().id(2).name("television").build(), "Buyer Two");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<PurchaseProductDto> request1 = new HttpEntity<>(purchaseProductDto1, headers);
        HttpEntity<PurchaseProductDto> request2 = new HttpEntity<>(purchaseProductDto2, headers);

        Thread thread1 = new Thread(() -> makePostCall(url, request1));
        Thread thread2 = new Thread(() -> makePostCall(url, request2));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();


        System.out.println("messages list: " + MessageBroker.messages);
        System.out.println("eventId list: " + MessageBroker.createdEventsIdList);
        assertEquals(2, MessageBroker.eventsCount.get(),
                "number of ProductPurchasedEvent instances: " + MessageBroker.eventsCount.get());
        assertNotEquals(MessageBroker.messages.get(0).getEntityId(), MessageBroker.messages.get(1).getEntityId());

    }

    @Test
    void testPrototypeObjectFactoryWithSingleton() throws Exception{
        final String baseUrl = "http://localhost:"+randomServerPort+"/purchase/with-object-factory";
        URI url = new URI(baseUrl);
        PurchaseProductDto purchaseProductDto1 = new PurchaseProductDto(Product.builder().id(1).name("laptop").build(), "Buyer One");
        PurchaseProductDto purchaseProductDto2 = new PurchaseProductDto(Product.builder().id(2).name("television").build(), "Buyer Two");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<PurchaseProductDto> request1 = new HttpEntity<>(purchaseProductDto1, headers);
        HttpEntity<PurchaseProductDto> request2 = new HttpEntity<>(purchaseProductDto2, headers);

        Thread.sleep(5000);
        // getting initial eventId value before sending http requests
        int initEventId = MessageBroker.createdEventsIdList.get(0);
        System.out.println("####################### DELAY ##########################" + initEventId);

        Thread thread1 = new Thread(() -> makePostCall(url, request1));
        Thread thread2 = new Thread(() -> makePostCall(url, request2));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();


        System.out.println("messages list: " + MessageBroker.messages);
        System.out.println("eventId list: " + MessageBroker.createdEventsIdList);
        System.out.println("messages list: " + MessageBroker.messages);
        System.out.println("eventId list: " + MessageBroker.createdEventsIdList);

        // we could ignore first created instance so 3 is ok.
        assertEquals(3, MessageBroker.eventsCount.get(),
                "number of ProductPurchasedEvent instances: " + MessageBroker.eventsCount.get());
        // if event instances are 3 the first one should be equal to initial eventId set at application startup
        assertEquals(initEventId, MessageBroker.createdEventsIdList.get(0));

        // finally messages delivered to message broker should not have same event id
        assertNotEquals(MessageBroker.messages.get(0).getEntityId(), MessageBroker.messages.get(1).getEntityId());
    }

    private void makePostCall(URI url, HttpEntity request) {
        ResponseEntity<Integer> response =
                restTemplate.exchange(url,
                        HttpMethod.POST,
                        request,
                        Integer.class);
    }

}