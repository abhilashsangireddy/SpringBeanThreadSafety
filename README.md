# SpringBeanThreadSafety

### The discussion for beans being thread safe arises because of the following factors:

1. Is the object being used capable of holding state for itself?
2. Does object method that is being called change any state related information of the object?
3. Is the object immutable? (immutable ≠ stateless)

The state of the object can be decided by the attributes of the object as well as the instantiation strategy
of the spring bean corresponding to the object.

#### Four commonly used scopes of spring beans:
* Singleton
* Prototype
* Session
* Request

Singletons and their dependencies (including prototypes) are created and injected at the application startup and persists till the application stops.
Prototypes are injected on demand. Every method call on a prototype bean a new one is instantiated.
Lifecycle of session and request beans, are bound to the lifecycle of a user session and HTTP request respectively.

To make a web controller threadsafe, we have the choice of making all the  controllers, services and repositories stateless and make them all singleton.
To ensure that we have to make sure that no method changes the state of these objects.

#### ThreadSafetyApplicationTests

##### Test 1

`@Test
void testSingleton() throws Exception`
* The point of this integration test is to run two parallel post requests
* at the same time and see if they execute in a thread safe fashion or not. This test might pass
* sometimes, but you can notice a race condition sometimes between the threads where both the threads
* update the totalUsersCount to 1 and the assertion fails. But the point is: making singletons stateful is not a good choice,
* but if we do that we should guarantee race condition would not happen.

The simplest way of solving the above-mentioned race condition is to make saveUserToDb() method synchronous
But, it is not a right way to do it and is an anti-pattern.

##### Test 2

` @Test
void testDefaultBehaviourOfPrototypeWithSingleton() throws Exception{`

This test is expected to fail. Also we know that the constructor has been called just one time, which eventsCount value (=1) proves it.
This is what happened: spring created only one instance of ProductPurchasedEvent. This is the default behavior of spring when a singleton has a prototype dependency (unless we customize it). It creates and injects all dependent objects at the application startup level.
Look at the mess now! Two different events published to the message broker but with same id which we could anticipate that the message broker will ignore the second one.

##### Test 3
`@Test
void testProxiedPrototypeWithSingleton() throws Exception {`

This test is also expected to fail. After running the test it can be seen that we now have 7 distinct instances and one Message in MessageBroker with wrong data. even worse! It’s because every time we call a method of a proxied prototype, spring creates a new instance. So each time one of the setters is called the newly created bean will be in a state that just the value of the related setter is set and other fields values remain null (calling fire methods after two setters in this case).

##### Test 4



## Learnings:

1. @SneakyThrows can be used to sneakily throw checked exceptions without actually declaring this in your method's throws clause.
2. @SpringBootTest annotation is used for loading application beans into text context and can be controlled by specifying the claases we want to load.
3. @SpringBootTest is not usually required for unit testing.
4. Various spring bean scopes and their role in thread safe applications.
5. AtomicInteger is thread safe (in fact, all classes from java.util.concurrent.atomic package are thread safe), 
   while normal integers are NOT threadsafe.
6. Very often, we don’t need to use prototype and default singletons are good enough. 
7. So don’t use prototype (also session and request) unless you really need it.
8. If you design your singleton beans stateless you are guaranteed that won’t face thread safety issues.
   but if you decide to use prototype, be aware of the way you use it matches your scenario and expectations.


