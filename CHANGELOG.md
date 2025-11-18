# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [3.0.0] - 2025-11-18

### Added
- Java 17 support with modern language features
- Listener priority system for controlling execution order
- Asynchronous event execution using CompletableFuture
- Builder pattern for Event configuration
- Custom exception types:
  - `EventException` - Base exception
  - `EventNotFoundException` - When event is not found
  - `EventGroupNotFoundException` - When group is not found
  - `ProtectedGroupException` - When modifying protected groups
  - `ListenerExecutionException` - When listener execution fails
- Configurable exception handling to continue execution on failures
- `Optional<T>` return types for safer null handling
- Constants for default group names (`DEFAULT_GROUP`, `SYSTEM_GROUP`, `TEMP_GROUP`)
- Thread-safe `breakLoop()` using ThreadLocal<AtomicBoolean>
- `updateListenerPriority()` method to change listener priority
- `getListenerPriority()` method to query listener priority
- `toString()`, `equals()`, and `hashCode()` implementations for all core classes
- Comprehensive JUnit 5 test suite with 50+ tests
- Static analysis tools (SpotBugs, Checkstyle, PMD, JaCoCo)
- Maven Enforcer plugin for build consistency
- Support for custom ExecutorService in builder pattern

### Changed
- **BREAKING**: Minimum Java version upgraded from Java 8 to Java 17
- **BREAKING**: `getEvent()` now returns `Optional<Event>` instead of nullable Event
- **BREAKING**: `getGroup()` now returns `Optional<EventGroup>` instead of nullable EventGroup
- **BREAKING**: Replaced custom `org.nishat.util:log` with SLF4J API
- Improved thread safety with proper double-checked locking in EventManager singleton
- Optimized synchronization using ReentrantLock instead of synchronized blocks
- Enhanced null checking with descriptive error messages
- Better exception messages for debugging
- Updated all Maven plugins to latest versions
- Improved Javadoc documentation with examples

### Fixed
- Thread safety race condition in EventManager singleton initialization
- Thread safety issue with `breakLoop` field (now ThreadLocal)
- NullPointerException risks in EventGroup.remove()
- NullPointerException risks in EventManager operations
- Memory leak from unnecessary synchronized list wrapper in getGroupNames()
- Performance bottleneck from overly broad synchronized blocks
- Listener ID validation (now throws exception on null/empty)
- Event name validation (now throws exception on null/empty)

### Removed
- Dependency on custom `org.nishat.util:log` library
- Empty `EventUtil.java` class
- Double-brace initialization anti-pattern
- Generic RuntimeException usage in favor of specific exceptions
- TODO comments from source code

### Security
- Added input validation for all public API methods
- Improved thread safety across all concurrent operations
- Added Maven Enforcer plugin to ensure consistent build environment

## [2.0.0] - 2022-12-14

### Added
- Documentation improvements
- Function simplification

### Changed
- Updated major version
- Simplified some functions

## [1.0.2] - Previous Release

### Fixed
- Bug fixes and improvements

## [1.0.1] - Previous Release

### Fixed
- Minor bug fixes

## [1.0.0] - Initial Release

### Added
- Basic event management system
- EventManager singleton
- Event class with listener support
- EventGroup for organizing events
- Listener abstract class
- Support for multiple listeners per event
- Loop breaking capability
- Lifecycle hooks (doBeforeCall, doAfterCall)
- Event group management
- Java 8 compatibility

[Unreleased]: https://github.com/MasumNishat/event/compare/v3.0.0...HEAD
[3.0.0]: https://github.com/MasumNishat/event/compare/v2.0.0...v3.0.0
[2.0.0]: https://github.com/MasumNishat/event/compare/v1.0.2...v2.0.0
[1.0.2]: https://github.com/MasumNishat/event/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/MasumNishat/event/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/MasumNishat/event/releases/tag/v1.0.0
