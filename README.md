# Event - Lightweight Java Event Management Library

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nishat.util/event/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nishat.util/event)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://openjdk.org/)

A modern, thread-safe event listener library for Java 17+ that provides flexible event-driven programming with support for listener priorities, asynchronous execution, and robust exception handling.

## Features

✨ **Modern Java 17+** - Takes advantage of modern Java features
🔒 **Thread-Safe** - Built with concurrency in mind using locks and atomic operations
🎯 **Listener Priorities** - Control execution order of listeners
⚡ **Async Support** - CompletableFuture-based asynchronous event execution
🛡️ **Exception Handling** - Configurable exception handling strategies
📦 **Event Grouping** - Organize events into logical groups
🏗️ **Builder Pattern** - Fluent API for event configuration
🧪 **Well-Tested** - Comprehensive unit test coverage
📝 **SLF4J Logging** - Standard logging integration
🎨 **Clean API** - Intuitive and easy to use

## Requirements

- Java 17 or higher
- Maven 3.6.3 or higher (for building)

## Installation

### Maven

```xml
<dependency>
    <groupId>org.nishat.util</groupId>
    <artifactId>event</artifactId>
    <version>3.0.0</version>
</dependency>

<!-- SLF4J implementation (choose one) -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>2.0.16</version>
</dependency>
```

### Gradle

```gradle
implementation 'org.nishat.util:event:3.0.0'
implementation 'org.slf4j:slf4j-simple:2.0.16'
```

## Quick Start

### Basic Event Usage

```java
import org.nishat.util.event.*;

// Create an event
Event event = new Event("userLogin");

// Add listeners
event.addListener(new Listener() {
    @Override
    public void exec(Object payload) {
        System.out.println("User logged in: " + payload);
    }
});

// Trigger the event
event.call("user@example.com");
```

### Using Event Manager

```java
EventManager manager = EventManager.getInstance();

// Register an event
Event loginEvent = new Event("login");
manager.registerEvent(loginEvent);

// Retrieve and use the event
manager.getEvent("login").ifPresent(event -> {
    try {
        event.call("user@example.com");
    } catch (Throwable e) {
        e.printStackTrace();
    }
});
```

### Listener Priorities

Higher priority listeners execute first:

```java
Event event = new Event("priorityExample");

// Low priority (executes last)
event.addListener(new Listener("low") {
    @Override
    public void exec(Object payload) {
        System.out.println("Low priority listener");
    }
}, 1);

// High priority (executes first)
event.addListener(new Listener("high") {
    @Override
    public void exec(Object payload) {
        System.out.println("High priority listener");
    }
}, 10);

event.call(null);
// Output:
// High priority listener
// Low priority listener
```

### Asynchronous Events

```java
Event event = new Event("asyncEvent");

event.addListener(new Listener() {
    @Override
    public void exec(Object payload) {
        // Long-running task
        Thread.sleep(1000);
        System.out.println("Async task completed");
    }
});

// Call asynchronously
CompletableFuture<Void> future = event.callAsync(null);

// Do other work...
System.out.println("Continuing without waiting");

// Wait for completion if needed
future.join();
```

### Exception Handling

```java
// Event that continues on listener failures
Event resilientEvent = Event.builder("resilient")
    .withExceptionHandling(true)
    .build();

resilientEvent.addListener(new Listener() {
    @Override
    public void exec(Object payload) throws Exception {
        throw new RuntimeException("This listener fails");
    }
});

resilientEvent.addListener(new Listener() {
    @Override
    public void exec(Object payload) {
        System.out.println("This listener still executes");
    }
});

// Both listeners execute, exceptions are logged
resilientEvent.call(null);
```

### Breaking Listener Chain

```java
Event event = new Event("chainBreak");

event.addListener(new Listener() {
    @Override
    public void exec(Object payload) {
        System.out.println("First listener");
    }
});

event.addListener(new Listener() {
    @Override
    public void exec(Object payload) {
        System.out.println("Second listener - breaking chain");
        event.breakLoop();
    }
});

event.addListener(new Listener() {
    @Override
    public void exec(Object payload) {
        System.out.println("This won't execute");
    }
});

event.call(null);
// Output:
// First listener
// Second listener - breaking chain
```

### Event Groups

Organize events into logical groups:

```java
EventManager manager = EventManager.getInstance();

// Create custom group
EventGroup authGroup = new EventGroup() {
    @Override
    public String name() {
        return "authentication";
    }
};

// Register events in the group
Event loginEvent = new Event("login");
Event logoutEvent = new Event("logout");

manager.registerEvent(authGroup, loginEvent);
manager.registerEvent(authGroup, logoutEvent);

// Access events from the group
manager.getEvent("authentication", "login").ifPresent(event -> {
    // Use the event
});

// Default groups available
manager.getGroup(EventManager.DEFAULT_GROUP);  // "default"
manager.getGroup(EventManager.SYSTEM_GROUP);   // "system" (protected)
manager.getGroup(EventManager.TEMP_GROUP);     // "temp"
```

### Lifecycle Hooks

```java
Event event = new Event("hooks");

// Before/after all listeners
event.doBeforeEachCall(e -> System.out.println("Starting event: " + e.name()));
event.doAfterEachCall(e -> System.out.println("Finished event: " + e.name()));

// Before/after each listener
event.doBeforeEachListenerCall(l -> System.out.println("Calling listener: " + l.getId()));
event.doAfterEachListenerCall(l -> System.out.println("Listener done: " + l.getId()));

event.addListener(new Listener() {
    @Override
    public void exec(Object payload) {
        System.out.println("Listener executing");
    }
});

event.call(null);
```

### Advanced Builder Pattern

```java
ExecutorService customExecutor = Executors.newFixedThreadPool(4);

Event event = Event.builder("advancedEvent")
    .withExecutor(customExecutor)
    .withExceptionHandling(true)
    .build();

// Use the event...
```

## API Documentation

### Core Classes

- **Event** - Represents an event that can have multiple listeners
- **EventManager** - Singleton manager for organizing events and groups
- **EventGroup** - Logical grouping of related events
- **Listener** - Abstract class for implementing event listeners

### Exception Types

- **EventException** - Base exception for all event-related errors
- **EventNotFoundException** - Thrown when an event is not found
- **EventGroupNotFoundException** - Thrown when a group is not found
- **ProtectedGroupException** - Thrown when modifying protected groups
- **ListenerExecutionException** - Wraps exceptions from listener execution

## Thread Safety

This library is designed to be thread-safe:

- `EventManager` uses double-checked locking for singleton instance
- `Event` uses `ReentrantLock` for call synchronization
- `breakLoop()` uses `ThreadLocal<AtomicBoolean>` for thread-safe operation
- All collections use `ConcurrentHashMap` for thread-safe access

## Best Practices

1. **Use Event Groups** - Organize related events together
2. **Set Listener Priorities** - Control execution order when it matters
3. **Handle Exceptions** - Use `.withExceptionHandling(true)` for resilient events
4. **Custom IDs** - Give listeners meaningful IDs for debugging
5. **Cleanup** - Remove listeners when no longer needed to prevent memory leaks
6. **Async for Long Tasks** - Use `callAsync()` for time-consuming operations

## Migration from v1.x

Key changes in v2.x:

- **Java 17 Required** - Upgraded from Java 8
- **Optional Returns** - `getEvent()` and `getGroup()` now return `Optional<T>`
- **Custom Exceptions** - More specific exception types
- **SLF4J Logging** - Replaced custom logging with SLF4J
- **New Features** - Listener priorities, async support, builder pattern

## Resources

- **Maven Repository**: https://repo1.maven.org/maven2/org/nishat/util/event/
- **Snapshots**: https://s01.oss.sonatype.org/content/repositories/snapshots/org/nishat/util/event/
- **Issues**: https://github.com/MasumNishat/event/issues

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

**Al Masum Nishat**
Email: masum.nishat21@gmail.com
Organization: org.nishat

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and release notes.
