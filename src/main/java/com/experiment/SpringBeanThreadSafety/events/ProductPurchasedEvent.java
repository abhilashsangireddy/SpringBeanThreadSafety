package com.experiment.SpringBeanThreadSafety.events;


import com.experiment.SpringBeanThreadSafety.entities.Message;
import com.experiment.SpringBeanThreadSafety.entities.Product;
import com.experiment.SpringBeanThreadSafety.services.MessageBroker;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Random;

@Data
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductPurchasedEvent {
    private Product product;
    private String buyer;
    private int eventId;

    Random rand = new Random();

    ProductPurchasedEvent() {
        this.eventId = rand.nextInt(1000);
        MessageBroker.eventsCount.incrementAndGet();
        MessageBroker.createdEventsIdList.add(eventId);
    }

    public int fire() {
        String message = buyer + " purchased " + product;
        System.out.println("fire method called _ Thread : " + Thread.currentThread().getName() + " " + message + " eventId: " + eventId);
        MessageBroker.messages.add(new Message(eventId, message));
        return eventId;
    }
}
