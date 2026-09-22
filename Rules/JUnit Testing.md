# JUnit Testing Requirements

This document contains the testing requirements stated by the lecturer in the transcript for the Ludo assignment.

---

## 1. Unit Testing Is Required

Unit testing is a required part of the assignment.

The lecturer stated that the test cases should be written using **JUnit**.

> **Required testing framework: JUnit**

The lecturer also said that unit testing had already been covered previously, so it would not be taught again in detail. However, the testing work will still be evaluated.

---

## 2. What Must Be Shown

The lecturer identified two main things that must be shown for testing:

1. **Number of test cases**
2. **Test coverage**

These are separate measurements.

A large number of test cases does not automatically mean that the system has been tested properly.

---

## 3. Number of Test Cases

The report should state the number of test cases implemented.

However, the lecturer emphasized that simply increasing the number of tests is not useful if the same small part of the program is being tested repeatedly.

For example:

```text
100 tests for the same function
```

does not represent good testing if other important functions are not tested.

The tests should cover the important behaviours and functions of the application.

---

## 4. Test Granularity

The lecturer said that test cases should be **granular**.

This means that important functions should normally be tested separately rather than putting many unrelated checks into one very large test.

### Lecturer's Example

If a class contains seven important/public functions, the lecturer explained that ideally the functions should be covered by separate tests rather than one large test case for all seven.

Example:

```text
Class with 7 functions

Test 1 -> Function 1
Test 2 -> Function 2
Test 3 -> Function 3
Test 4 -> Function 4
Test 5 -> Function 5
Test 6 -> Function 6
Test 7 -> Function 7
```

This makes each test easier to understand and makes it clearer which behaviour is being verified.

---

## 5. Test Coverage

Test coverage should show how much of the codebase is exercised by the test cases.

Coverage is not the same as the number of tests.

For example:

```text
Many tests
+
Only one small area of the program tested
=
Poor overall coverage
```

The lecturer referred to **85% code coverage** when explaining the expected coverage and also stated that **100% coverage is not always possible**.

Therefore, the important point is to achieve strong coverage and report the real coverage produced by the project.

> **Do not invent a coverage percentage.**

The coverage stated in the report must match the coverage that can actually be produced from the project.

---

## 6. Coverage Tools

The lecturer stated that **any suitable tool can be used for coverage**.

The transcript does not require one specific coverage framework or tool.

The selected tool should allow you to demonstrate how much of the codebase is covered by the JUnit tests.

---

## 7. Mocks and Stubs

Mocks and stubs may be used where necessary.

The lecturer specifically mentioned **Mockito** as a suitable option.

Therefore, the testing setup may use:

```text
JUnit
  +
Mockito
```

where Mockito is needed to create mocks or stubs.

Mocks and stubs should be used only where they are useful for isolating the code being tested.

---

## 8. What the Lecturer Will Evaluate

The lecturer specifically said that testing will be evaluated based on:

- **Test granularity**
- **Test coverage**
- **Quality of the test cases**

Therefore, the goal is not simply to create a large number of tests.

The test cases should be meaningful and should properly verify the important functionality of the system.

---

## 9. Evidence Is Required

The lecturer said that evidence should be provided for the testing work.

The report should provide evidence for:

- The implemented test cases
- The test results
- The coverage result

A simple summary can be included in the report:

```text
Total Test Cases: XX
Passed: XX
Failed: XX
Code Coverage: XX%
```

Use the actual values produced by the project.

---

## 10. Viva Requirement

The testing must be demonstrable during the viva.

You should be able to:

1. Run the JUnit test suite.
2. Show that the tests execute successfully.
3. Show the code coverage result.
4. Demonstrate that the coverage matches the value reported in the assignment report.

Therefore:

```text
Coverage in Report
        =
Coverage Demonstrated in Viva
```

The report should not contain testing results that cannot be reproduced from the submitted project.

---

## 11. Recommended Testing Section in the Assignment Report

A clear testing section could be organized as follows:

```text
Testing

1. Unit Testing Approach
2. JUnit Test Cases
3. Number of Test Cases
4. Test Granularity
5. Mocks and Stubs
6. Mockito Usage
7. Code Coverage
8. Test Results
9. Testing Evidence
```

---

## 12. Example Test Summary

The report can include a summary such as:

| Item | Result |
|---|---|
| Testing Framework | JUnit |
| Total Test Cases | XX |
| Passed Tests | XX |
| Failed Tests | XX |
| Coverage | XX% |
| Mocking Tool | Mockito, where required |

Replace `XX` with the actual values from the project.

---

## 13. Important Requirements

- Use **JUnit** for the unit tests.
- State the **number of test cases**.
- Measure and report **test coverage**.
- Avoid repeatedly testing only the same function.
- Keep tests reasonably **granular**.
- Test important functions separately where appropriate.
- Focus on the **quality** of the tests, not only the quantity.
- Use **Mockito** for mocks and stubs where necessary.
- A suitable coverage tool may be used.
- Provide evidence of the tests and coverage.
- Be able to run the tests during the viva.
- The coverage demonstrated during the viva must match the report.
- Do not claim a coverage value that the project cannot reproduce.
- 100% coverage is not necessarily expected or always achievable.

---

## 14. What the Transcript Does NOT State

The following restrictions are **not stated in the lecturer's transcript**:

- "Use minimal frameworks only."
- "No automated test generation is allowed."

Therefore, these should not be presented as lecturer requirements unless they are stated separately in the official assignment specification or another lecturer instruction.

The transcript actually says that tools may be used in relation to testing and coverage, and it specifically allows Mockito for mocks and stubs.

---

## Key Point

The lecturer's main expectation is:

> **Use JUnit to create meaningful, granular tests with good coverage, provide evidence of the results, and be able to reproduce those results during the viva.**
