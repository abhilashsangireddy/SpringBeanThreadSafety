package com.experiment.SpringBeanThreadSafety.services;

import com.experiment.SpringBeanThreadSafety.entities.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageBroker {
    public static final AtomicInteger eventsCount = new AtomicInteger(0);
    public static final List<Integer> createdEventsIdList = Collections.synchronizedList(new ArrayList<>());
    public static List<Message> messages = Collections.synchronizedList(new ArrayList<>());
}
