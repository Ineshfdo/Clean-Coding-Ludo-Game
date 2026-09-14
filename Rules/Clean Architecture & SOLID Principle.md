# Clean Architecture  & SOLID Principles

This document summarizes the key Clean Architecture and SOLID principles that should be considered when designing and implementing the Ludo assignment.

The main focus is not only to make the system work correctly, but also to ensure that the code is maintainable, extendable, understandable, and easy to modify when requirements change.


## 1 . Purpose of Software Architecture

The main purpose of software architecture is to reduce the effort and cost required to:

* Build software
* Maintain software
* Modify software
* Extend software

A system should not only work correctly now. It should also remain easy to change when requirements change.

### Key Points

* Requirements will inevitably change.
* A program that works perfectly but is extremely difficult to modify can eventually become useless.
* A program that is easy to modify can continue adapting to new requirements.
* Poor architecture causes development and maintenance costs to increase over time.
* As releases increase, developers may spend more time understanding existing code than writing new code.
* Good architecture should make the system extendable and maintainable rather than heavily hard-coded.
* Architecture should be considered from the beginning, not added after completing the application.

### Ludo Assignment Application

For the Ludo assignment, the code should be designed so that it can reasonably be extended rather than being written only for one fixed scenario.

For example, the architecture should make it easier to introduce new:

* Rules
* Movement behaviours
* Player behaviours
* Mystery-cell effects
* Strategies
* Game components

without requiring large changes throughout the existing codebase.

  

## 2 . Functionality vs Architecture

There can be a conflict between:

* Delivering functionality quickly
* Maintaining good software architecture

Managers may prioritize deadlines and working features, while developers and software architects must also consider:

* Maintainability
* Extensibility
* Structure
* Code quality
* Long-term development cost

Therefore, architectural decisions often involve **trade-offs**.

### Important Assignment Point

You are **not expected to achieve 100% compliance with every principle in every situation**.

Sometimes:

* A design pattern may conflict with a SOLID principle.
* A requirement may force you to compromise another architectural goal.
* A design decision may improve one area while creating a limitation in another.

When this happens:

1. Identify the conflict.
2. Consider the possible approaches.
3. Select the most suitable approach for the application.
4. Explain the trade-off.
5. Justify the final decision in the report.

The analysis and justification of these decisions are important parts of the assignment report.

   

# 3 . SOLID Principles

The assignment should consider all five SOLID principles:

|Letter|Principle|
|-|-|
|**S**|Single Responsibility Principle|
|**O**|Open/Closed Principle|
|**L**|Liskov Substitution Principle|
|**I**|Interface Segregation Principle|
|**D**|Dependency Inversion Principle|

  

## 3.1 Single Responsibility Principle — SRP

### Main Idea

A class or module should have **one responsibility** and therefore **one main reason to change**.

SRP is closely related to **cohesion**.

A class should not contain several unrelated responsibilities belonging to different actors or stakeholders.

### Example

An `Employee` class should not be responsible for all of the following:

* Calculating employee pay
* Reporting employee hours
* Saving employee information

These responsibilities may belong to different areas of the system.

A better structure could be:

```text
EmployeeData
    |
    +-- PayCalculator
    |
    +-- HoursReporter
    |
    +-- EmployeeSaver
```

A **Facade** can be placed in front of these classes when a common interface is required.

### Assignment Guidance

Avoid:

* One class doing many unrelated jobs
* Duplicated functionality
* Large classes containing unrelated operations

A useful question is:

> What is the reason this class would need to change?

If there are several unrelated reasons, the class may be violating SRP.

  

## 3.2 Open/Closed Principle — OCP

### Main Idea

A software component should be:

>   *  *Open for extension but closed for modification.  *  *

New functionality should be introduced without repeatedly changing stable existing code.

A useful question is:

> When a new requirement is introduced, how much existing code must be changed?

The goal is to minimize modifications to existing stable code.

### Poor Approach

```java
if (type == A) {
    // behaviour A
} else if (type == B) {
    // behaviour B
} else if (type == C) {
    // behaviour C
}
```

If similar conditional logic is spread throughout the system, every new type may require several existing classes to be modified.

### Better Approach

Use techniques such as:

* Interfaces
* Abstract classes
* Polymorphism
* Strategy implementations
* Separate rule implementations

### Ludo Assignment Application

When adding a new:

* Movement behaviour
* Rule
* Player behaviour
* Mystery-cell effect
* Game component
* Strategy

the system should preferably be **extended** rather than requiring continuous modification of existing classes.

  

## 3.3 Liskov Substitution Principle — LSP

### Main Idea

An object of a subclass should be usable in place of its superclass without breaking or changing the expected behaviour of the program.

A simple check is:

> Can the subclass genuinely replace the superclass?

### Important Rule

A subclass should not remove behaviour that the superclass promises.

Overriding behaviour can be valid, but the subclass should still satisfy the expectations created by the superclass.

### Example

```java
Parent p = new Child();
```

The rest of the program should continue to work according to the behaviour expected from `Parent`.

### Warning Sign

If the program repeatedly requires logic such as:

```java
if (object instanceof ChildA) {
    // special handling
}

if (object instanceof ChildB) {
    // different special handling
}
```

because the subclasses cannot be used consistently through the parent type, the inheritance hierarchy should be reconsidered.

### Assignment Guidance

Check inheritance relationships carefully.

A subclass should behave as a valid replacement for its parent class.

  

## 3.4 Interface Segregation Principle — ISP

### Main Idea

Classes should **not be forced to implement methods they do not need**.

Avoid creating one large interface containing functionality that only some implementations require.

### Poor Example

```text
Shape

getLength()
getWidth()
getRadius()
getArea()
```

This creates problems because:

* A rectangle does not require `getRadius()`.
* A circle does not require `getLength()` and `getWidth()`.

### Better Approach

Split the large interface into smaller focused interfaces:

```text
Length
Width
Radius
Area
```

Then each class implements only the interfaces it needs.

Example:

```text
Rectangle
implements Length, Width, Area

Circle
implements Radius, Area
```

### Ludo Assignment Application

Do not force game classes to implement methods that do not logically apply to them.

Prefer:

* Small interfaces
* Focused responsibilities
* Interfaces based on actual behaviours

  

## 3.5 Dependency Inversion Principle — DIP

### Main Idea

Code should generally depend on:

>   *  *Abstractions rather than concrete implementations.  *  *

Higher-level classes should avoid directly depending on specific concrete classes when those implementations may change.

### Preferred Structure

```text
High-Level Class
       |
       v
Interface / Abstract Class
       |
       -
   |           |
Concrete A  Concrete B
```

### Why?

Direct dependency:

```text
A -> ConcreteB
```

If `ConcreteB` changes, class `A` may also need to change.

Using an abstraction:

```text
A -> Abstraction <- ConcreteB
```

New implementations can often be added without modifying `A`.

### Important Clarification

Not everything needs to be abstract.

Stable Java or library classes can be depended upon directly when they are unlikely to change.

The main concern is **volatile classes**, meaning classes that are likely to change.

For components likely to change:

* Prefer interfaces
* Prefer abstract classes where appropriate
* Avoid unnecessary coupling to concrete implementations

  

# 4 . SOLID Must Be Designed From the Beginning

A poor development approach is:

```text
Write Entire Program
        ↓
Finish Application
        ↓
Try to Add SOLID
```

SOLID principles should influence the software architecture while the system is being designed and developed.

Trying to apply SOLID only after finishing the program may require major restructuring.

A better approach is:

```text
Understand Requirements
        ↓
Design Architecture
        ↓
Apply SOLID Principles
        ↓
Implement Code
        ↓
Refactor and Improve
```

  

# 5 . Design Patterns and SOLID Can Conflict

The system does not need to achieve:

```text
100% SOLID
+
100% Design Pattern Compliance
```

in every situation.

Sometimes preserving a design pattern may require a compromise with another principle.

The responsibility of the developer or architect is to make a justified design decision.

### Recommended Analysis Structure

```text
Problem
   ↓
Possible Approaches
   ↓
Trade-Off
   ↓
Chosen Approach
   ↓
Why It Is Appropriate for the Ludo System
```

Do not hide reasonable limitations.

Instead:

* Identify them
* Explain them
* Justify why the chosen design was still appropriate

 

# 6. The Code Must Match the Explanation

The analysis in the report must match the actual implementation.

Do not claim:

> "The system follows OCP."

if adding one new feature requires modifying several existing classes.

Instead, provide implementation-specific explanations.

For example:

```text
Class X applies OCP because...
Interface Y allows...
New implementations can be added by...
Existing classes do not need to be modified because...
```

The justification should refer directly to the actual Ludo code.

The following should remain consistent with each other:

* Source code
* UML diagrams
* Design pattern discussion
* SOLID analysis
* Assignment report
* Viva explanation

 

# 7. Understand Every Line of the Code

Every important part of the project should be understood before the viva.

If AI tools were used to assist with development, you are still responsible for understanding the produced code.

You should understand:

* Every class
* Every important method
* Important variables
* Method calls
* Java syntax
* Inheritance
* Interfaces
* Abstractions
* Relationships between classes
* Interactions between files
* Why each design pattern was selected
* Why each SOLID principle applies
* How the overall architecture works

It is not enough to say:

> "This method moves the player."

You should be able to explain:

* How the method moves the player
* Which other classes or methods it calls
* What rules are checked
* Why the method belongs in that class
* Why the implementation was designed that way
* Which design principles or patterns are involved

 

# Final Checklist

Before submitting the Ludo assignment, check the following:

* [ ] The architecture is reasonably extendable.
* [ ] The code is not unnecessarily hard-coded.
* [ ] SRP has been considered.
* [ ] OCP has been considered.
* [ ] LSP has been considered.
* [ ] ISP has been considered.
* [ ] DIP has been considered.
* [ ] SOLID principles were considered during development, not only afterwards.
* [ ] Any SOLID/design-pattern conflicts are explained.
* [ ] Architectural trade-offs are justified.
* [ ] Reasonable limitations are documented.
* [ ] The report matches the actual implementation.
* [ ] UML diagrams match the actual code.
* [ ] Design-pattern explanations match the actual implementation.
* [ ] Every important class and method can be explained during the viva.
* [ ] AI-assisted code has been fully reviewed and understood.

 

## Key Principle

>  * *The goal is not to claim that the system is perfect. The goal is to show that the architectural decisions are appropriate, understandable, maintainable, and properly justified for the Ludo application. * *

