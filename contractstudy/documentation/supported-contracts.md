# Supported Contracts and Categories

Neither Java nor Kotlin offers a native standardized approach for contract specification. Still, practitioners can make use of many language features and libraries to make use of Design-by-Contract in their applications.

This analysis tool investigates four different categories of contracts - conditional runtime exceptions, assertions, APIs, annotations, and others - providing insights into how popular each category is. 

- [Supported Contracts and Categories](#supported-contracts-and-categories)
  - [Examples of Contracts](#examples-of-contracts)
    - [Conditional Runtime Exception](#conditional-runtime-exception)
    - [APIs](#apis)
    - [Assertions](#assertions)
    - [Annotations](#annotations)
    - [Others](#others)
  - [Supported Constructs and Features](#supported-constructs-and-features)
    - [Conditional Runtime Exception](#conditional-runtime-exception-1)
    - [APIs](#apis-1)
    - [Assertions](#assertions-1)
    - [Annotations](#annotations-1)
    - [Others](#others-1)

## Examples of Contracts

### Conditional Runtime Exception

In the next example, a precondition is specified through an `IllegalArgumentException` associated to an `if-statement`.

```java
public void proceedWithCheckout(List<Item> shoppingCart)  {
    if (shoppingCart.isEmpty()) {
        throw new IllegalArgumentException();
    }
    ...
}
```

### APIs

In the next example, a precondition is specified through the `notEmpty` method from the `org.apache.commons.lang3.Validate` package.

```kotlin
import org.apache.commons.lang3.Validate
    
fun addToShoppingCart(items: List<Item>): List<Item>  {
    Validate.notEmpty(items)
    shoppingCart.addAll(items)
    return shoppingCart
}
```

### Assertions

In the next example, two preconditions - `!items.isEmpty()` and `items.size() <= 10` - and a postcondition - `shoppingCartItems.containsAll(items)` are specified through native `assertions`.

```java
public List<Item> addToShoppingCart(List<Item> items){
    assert !items.isEmpty();
    assert items.size() <= 10;
    shoppingCartItems.addAll(items);
    assert shoppingCartItems.containsAll(items);
    return shoppingCartItems;
}
```

```kotlin
fun addToShoppingCart(items: List<Item>): List<Item> {
    assert(items.isNotEmpty())
    require(items.size <= 10)
    shoppingCartItems.addAll(items)
    check(shoppingCartItems.containsAll(items))
    return shoppingCartItems
}
```

### Annotations

In the next example, a precondition - `@NotNull` -, a post-condition - `@Size(min=1)` -, and a class invariant - `@Size(max=10)` - are specified through annotations from the `javax.validation.constraints` package.

```kotlin
import javax.validation.constraints.*
    
class ShoppingCart {
    @Size(max=10)
    private val items: List<Item> = mutableListOf()

    @Size(min=1) fun addItem(@NotNull itemUUID: String, @Min(1) quantity: Int): List<Item> {
        ...
    }
}
```

### Others

The next example uses Kotlin's Experimental Contracts to specify a contract between the programmer and the compiler to allow a situation that otherwise would result in a compile error.

```kotlin
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@ExperimentalContracts
fun sendBirthdayMessage(birthdate: String?) {
    isBirthdateValidOrElseThrow(birthdate)
    val birthdaySplit = birthdate.split("/")
    ...
}

@ExperimentalContracts
private fun isBirthdateValidOrElseThrow(birthdate: String?) {
    contract { returns() implies (birthdate != null) }
    if (birthdate == null) {
        throw IllegalArgumentException()
    }
    ...
}
```

## Supported Constructs and Features

### Conditional Runtime Exception

|-------------------------------------|----------------------------------|
| AndroidRuntimeException             | MissingResourceException         |
| ArithmeticException                 | NegativeArraySizeException       |
| ArrayStoreException                 | NoSuchElementException           |
| ArrayStoreException                 | NullPointerException             |
| BufferOverflowException             | ParcelFormatException            |
| BufferUnderflowException            | ParseException                   |
| ClassCastException                  | ProviderException                |
| CompletionException                 | ProviderNotFoundException        |
| ConcurrentModificationException     | RejectedExecutionException       |
| DOMException                        | SQLException                     |
| DateTimeException                   | SecurityException                |
| EmptyStackException                 | TypeNotPresentException          |
| EnumConstantNotPresentException     | UncheckedIOException             |
| FileSystemAlreadyExistsException    | UndeclaredThrowableException     |
| FileSystemNotFoundException         | UnsupportedOperationException    |
| IllegalArgumentException            | WrongMethodTypeException         |
| IllegalMonitorStateException        | AcceptPendingException           |
| IllegalStateException               | AccessControlException           |
| IncompleteAnnotationException       | AlreadyBoundException            |
| IndexOutOfBoundsException           | AlreadyConnectedException        |
| LSException                         | ArrayIndexOutOfBoundsException   |
| MalformedParameterizedTypeException | BadParceableException            |
| MalformedParametersException        | CancellationException            |
| UnsupportedAddressTypeException     | UnsupportedCharsetException      |
| WritePendingException               | ZoneRulesException               |
| CancelledKeyException               | PatternSyntaxException           |
| ClosedDirectoryStreamException      | StringIndexOutOfBoundsException  |
| ClosedFileSystemException           | ReadOnlyBufferException          |
| ClosedFileSystemException           | ReadOnlyFileSystemException      |
| ClosedSelectorException             | ReadPendingException             |
| ClosedWatchServiceException         | ShutdownChannelGroupException    |
| ConnectionPendingException          | StringIndexOutOfBoundsException  |
| NonReadableChannelException         | UnknownFormatConversionException |
| NonWritableChannelException         | UnknownFormatFlagsException      |
| NotYetBoundException                | UnresolvedAddressException       |
| NotYetConnectedException            | UnsupportedTemporalTypeException |
| NumberFormatException               | OverlappingFileLockException     |
|-------------------------------------|----------------------------------|

### APIs

|-----------------------------------------|------------------------|
| Apache lang2 Validate                   |                        |
|                                         | allElementsOfType()    |
|                                         | isTrue()               |
|                                         | noNullElements()       |
|                                         | notEmpty()             |
|                                         | notNull()              |
|                                         |                        |
| Apacha lang3 Validate                   |                        |
|                                         | allElementsOfType()    |
|                                         | exclusiveBetween()     |
|                                         | inclusiveBetween()     |
|                                         | assignableFrom()       |
|                                         | isInstanceOf()         |
|                                         | matchesPattern()       |
|                                         | notBlank()             |
|                                         | validIndex()           |
|                                         | validState()           |
|                                         |                        |
| Guava Preconditions                     |                        |
|                                         | checkArgument()        |
|                                         | checkState()           |
|                                         | checkElementIndex()    |
|                                         | checkPositionIndex()   |
|                                         | checkNotNull()         |
|                                         | checkPositionIndexes() |
|                                         |                        |
| Spring Assert                           |                        |
|                                         | doesNotContain()       |
|                                         | hasLength()            |
|                                         | hasText()              |
|                                         | notEmpty()             |
|                                         | noNullElements()       |
|                                         | isInstanceOf()         |
|                                         | isAssignable()         |
|                                         | state()                |
|                                         | isNull()               |
|                                         | isTrue()               |
|                                         | notNull()              |
|                                         |                        |
|-----------------------------------------|------------------------|

### Assertions

|-----------------------------------------|------------------------|
| Java                                    |                        |
|                                         | assert()               |
|                                         |                        |
| Kotlin                                  |                        |
|                                         | assert()               |
|                                         | require()              |
|                                         | requireNotNull()       |
|                                         | check()                |
|                                         | checkNotNull()         |
|-----------------------------------------|------------------------|

### Annotations

|---------------------------------|--------------------------------------|----------------------------------|
| JSR305                          |                                      |                                  |
|                                 | @CheckForNull                        | @CheckForSigned                  |
|                                 | @MatchesPattern                      | @Nonnegative                     |
|                                 | @Nonnul                              | @Nullable                        |
|                                 | @OverridingMethodsMustInvokeSupper   | @ParametersAreNonnullByDefault   |
|                                 | @RegEx                               | @Signed                          |
|                                 | @Syntax                              | @Syntax                          |
|                                 | @Tainted                             | @Untainted                       |
|                                 | @WillClose                           | @WillCloseWhenClosed             |
|                                 | @WillNotClose                        | @Guardedby                       |
|                                 | @Immutable                           | @NotThreadSafe                   |
|                                 | @ThreadSafe                          |                                  |
|                                 |                                      |                                  |
| JSR303, JSR349                  |                                      |                                  |
|                                 | @Null                                | @DecimalMin                      |
|                                 | @NotNull                             | @Size                            |
|                                 | @AssertTrue                          | @Digits                          |
|                                 | @AssertFalse                         | @Past                            |
|                                 | @Min                                 | @Future                          |
|                                 | @Max                                 | @Pattern                         |
|                                 | @DecimalMax                          |                                  |
|                                 |                                      |                                  |
| JetBrain                        |                                      |                                  |
|                                 | @Contract                            | @NotNull                         |
|                                 | @Nullable                            | @PropertyKey                     |
|                                 | @TestOnly                            |                                  |
|                                 |                                      |                                  |
| IntelliJ                        |                                      |                                  |
|                                 | @BoxLayoutAxis                       | @CalendarMonth                   |
|                                 | @CursorType                          | @FlowLayoutAlignment             |
|                                 | @FontStyle                           | @HorizontalAlignment             |
|                                 | @InputEventMask                      | @ListSelectionMode               |
|                                 | @PatternFlags                        | @TabLayoutPolicy                 |
|                                 | @AdjustableOrientation               | @Flow                            |
|                                 | @Identifier                          | @TabPlacement                    |
|                                 | @TitledBorderJustification           | @TitledBorderTitlePosition       |
|                                 | @Language                            | @MagicConstant                   |
|                                 | @Pattern                             | PrintFormat                      |
|                                 | @PrintFormat                         | @RexExp                          |
|                                 | @Subst                               |                                  |
|                                 |                                      |                                  |
| FindBugs                        |                                      |                                  |
|                                 | @CheckForNull                        | @NonNull                         |
|                                 | @Nullable                            | @PossiblyNull                    |
|                                 | @FontStyle                           | @HorizontalAlignment             |
|                                 | @UnkownNullness                      | @CreateObligation                |
|                                 | @DischargesObligation                | @CleanupObligation               |
|                                 |                                      |                                  |
| Android                         |                                      |                                  |
|                                 | @AndroidSupressLint                  | @AndroidTargetApi                |
|                                 |                                      |                                  |
| Androidx                        |                                      |                                  |
|                                 | @AnimatorRes                         | @AnimRes                         |
|                                 | @AnyRes                              | @AnyThread                       |
|                                 | @AnyThread                           | @ArrayRes                        |
|                                 | @AttrRes                             | @BinderThread                    |
|                                 | @BinderThread                        | @BoolRes                         |
|                                 | @CallSuper                           | @CheckResult                     |
|                                 | @ChecksSdkIntAtLeast                 | @ColorInt                        |
|                                 | @ColorLong                           | @ColorRes                        |
|                                 | @ContentView                         | @DimenRes                        |
|                                 | @Dimension                           | @NotInline                       |
|                                 | @DrawableRes                         | @FloatRange                      |
|                                 | @FloatRange                          | @FontRes                         |
|                                 | @FontRes                             | @FractionRes                     |
|                                 | @FractionRes                         | @GuardedBy                       |
|                                 | @GuardedBy                           | @HalfFloat                       |
|                                 | @IdRes                               | @InspectableProperty             |
|                                 | @IntDef                              | @IntegerRes                      |
|                                 | @InterpolatorRes                     | @IntRange                        |
|                                 | @Keep                                | @LayoutRes                       |
|                                 | @LongDef                             | @MainThread                      |
|                                 | @MainThread                          | @MenuRes                         |
|                                 | @NavigationRes                       | @NonNull                         |
|                                 | @Nullable                            | @PluralsRec                      |
|                                 | @Px                                  | @RawRes                          |
|                                 | @RequiresApi                         | @RequiresFeature                 |
|                                 | @RequiresPermission                  | @RestrictTo                      |
|                                 | @Size                                | @StringDef                       |
|                                 | @StringRes                           | @StyleableRes                    |
|                                 | @StyleRes                            | @TransitionRes                   |
|                                 | @UiThread                            | @VisibleForTesting               |
|                                 | @WorkerThread                        | @XmlRes                          |
|---------------------------------|--------------------------------------|----------------------------------|

### Others
 
|-----------------------------------------|---------------------------------|
| Kotlin                                  |                                 |
|                                         | @ExperimentalContracts()        |
|-----------------------------------------|---------------------------------|