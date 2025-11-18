# Release Notes - Event Library v3.0.0

**Release Date:** November 18, 2025

---

## 🎉 Major Release: Modern Java Event Management Library

Event Library v3.0.0 represents a complete modernization of the event management system, bringing enterprise-grade features, improved thread safety, and modern Java 17 capabilities.

---

## 🚀 Highlights

### **Modern Java 17**
Upgraded from Java 8 to Java 17, taking advantage of:
- Modern language features
- Improved performance
- Enhanced security
- Better garbage collection

### **New Features**
1. **Listener Priorities** - Control execution order with numeric priorities
2. **Async Execution** - CompletableFuture-based asynchronous event calling
3. **Builder Pattern** - Fluent API for event configuration
4. **Exception Handling** - Configurable strategies for resilient execution
5. **Custom Exceptions** - 5 specific exception types for better error handling

### **Thread Safety Improvements**
- Fixed singleton pattern with double-checked locking
- Thread-safe `breakLoop()` using ThreadLocal
- Optimized synchronization with ReentrantLock
- Comprehensive null checking

### **Quality Enhancements**
- 63 comprehensive JUnit 5 tests
- Static analysis tools (SpotBugs, Checkstyle, PMD)
- JaCoCo code coverage reporting
- Extensive documentation and examples

---

## ⚠️ Breaking Changes

### **1. Java Version Requirement**
- **Before:** Java 8+
- **Now:** Java 17+
- **Migration:** Update your project to Java 17 or higher

### **2. API Returns Optional**
- **Before:** Methods returned nullable Event/EventGroup
- **Now:** Methods return `Optional<Event>` and `Optional<EventGroup>`
- **Migration Example:**
  ```java
  // Old code (v2.x)
  Event event = manager.getEvent("myEvent");
  if (event != null) {
      event.call(payload);
  }

  // New code (v3.0)
  manager.getEvent("myEvent").ifPresent(event -> {
      try {
          event.call(payload);
      } catch (Throwable e) {
          // Handle exception
      }
  });
  ```

### **3. Logging Dependency**
- **Before:** Custom `org.nishat.util:log` library
- **Now:** SLF4J API (industry standard)
- **Migration:** Add SLF4J implementation to your project:
  ```xml
  <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-simple</artifactId>
      <version>2.0.16</version>
  </dependency>
  ```

---

## 📦 Installation

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

---

## 🆕 New Features Guide

### **1. Listener Priorities**
Control the execution order of listeners:
```java
Event event = new Event("myEvent");

// High priority - executes first
event.addListener(criticalListener, 10);

// Normal priority
event.addListener(normalListener, 5);

// Low priority - executes last
event.addListener(loggingListener, 1);
```

### **2. Asynchronous Execution**
Execute events without blocking:
```java
Event event = new Event("asyncEvent");
event.addListener(heavyProcessingListener);

CompletableFuture<Void> future = event.callAsync(payload);

// Do other work...

// Wait if needed
future.get();
```

### **3. Builder Pattern**
Configure events with a fluent API:
```java
Event event = Event.builder("resilientEvent")
    .withExceptionHandling(true)  // Continue on failures
    .withExecutor(customExecutor)  // Custom thread pool
    .build();
```

### **4. Exception Handling**
Create resilient events that continue on failures:
```java
Event event = Event.builder("api")
    .withExceptionHandling(true)
    .build();

event.addListener(mayFailListener);
event.addListener(mustRunListener);

// Both listeners execute, failures are logged
event.call(data);
```

### **5. Constants for Group Names**
Use type-safe constants:
```java
// Old way
manager.registerEvent("default", event);

// New way
manager.registerEvent(EventManager.DEFAULT_GROUP, event);
manager.registerEvent(EventManager.SYSTEM_GROUP, event);
manager.registerEvent(EventManager.TEMP_GROUP, event);
```

---

## 🔧 Bug Fixes

1. **Thread Safety** - Fixed race conditions in EventManager singleton
2. **Concurrency** - Fixed breakLoop thread safety with ThreadLocal
3. **NPE Prevention** - Added comprehensive null checking
4. **Memory Leaks** - Fixed synchronized list wrapper issue
5. **Performance** - Optimized synchronization with ReentrantLock
6. **Equality** - Fixed equals() for anonymous inner classes

---

## 📊 Performance Improvements

- **Synchronization:** Switched from broad synchronized blocks to ReentrantLock
- **Memory:** Removed unnecessary synchronized wrappers
- **Concurrency:** Better thread-local state management
- **Startup:** Lazy initialization where appropriate

---

## 📚 Documentation

- **Comprehensive README** with 10+ usage examples
- **Detailed CHANGELOG** with full version history
- **Inline Javadoc** for all public APIs
- **Migration guide** for v2.x users

---

## 🧪 Testing

- **63 JUnit 5 tests** covering all functionality
- **~85% code coverage** with JaCoCo
- **Concurrency tests** for thread safety
- **Edge case testing** for robustness

---

## 🔗 Resources

- **Repository:** https://github.com/MasumNishat/event
- **Maven Central:** https://repo1.maven.org/maven2/org/nishat/util/event/
- **Documentation:** See README.md
- **Issues:** https://github.com/MasumNishat/event/issues

---

## 👥 Contributors

- **Al Masum Nishat** - Lead Developer (masum.nishat21@gmail.com)

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🙏 Acknowledgments

Thank you to all users who have provided feedback and contributed to making this library better!

---

## 📈 Version Comparison

| Feature | v2.0.0 | v3.0.0 |
|---------|--------|--------|
| Java Version | 8 | 17 |
| Thread Safety | Partial | Complete |
| Async Support | ❌ | ✅ CompletableFuture |
| Priorities | ❌ | ✅ Numeric priorities |
| Builder Pattern | ❌ | ✅ Fluent API |
| Exception Handling | Basic | Configurable |
| Test Coverage | 0% | ~85% |
| Custom Exceptions | 1 | 5 |
| Optional Returns | ❌ | ✅ Type-safe |
| Logging | Custom | SLF4J |

---

## 🎯 What's Next?

Looking ahead to future releases:
- Java 21 LTS support
- Reactive Streams integration
- Spring Framework auto-configuration
- Event metrics and monitoring
- Dead letter queue support

---

**Ready to upgrade?** Check out the [Migration Guide](#-breaking-changes) and [Installation](#-installation) sections above!

**Questions or issues?** Open an issue on GitHub: https://github.com/MasumNishat/event/issues
