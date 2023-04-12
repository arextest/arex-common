package com.arextest.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the result of invoking a method (or all methods
 * in a class) can be mocked.
 *
 * <p>Each time an advised method is invoked, mocking behavior will be applied,
 * checking whether the method has been already invoked for the given arguments.
 * A sensible default simply uses the method parameters to compute the key, but
 * a SpEL expression can be provided via the {@link #key} attribute.
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ArexMock {

    /**
     * <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions">Spring Expression Language (SpEL)</a> expression for computing the key dynamically.
     * <p>Default is {@code ""}, meaning all method parameters are considered as a key.
     * <p>The SpEL expression evaluates against a dedicated context that provides the
     * following meta-data:
     * <ul>
     * <li>Method arguments can be accessed by index. For instance the second argument
     * can be accessed via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
     * can also be accessed by name if that information is available.</li>
     * </ul>
     * <p>An example is as follows
     * <pre>{@code
     *     @ArexMock(key = "#name + #id")
     *     FooModel foo(String name, int id) {
     *         FooModel model = biz codes;
     *         return model;
     *     }
     *}
     * </pre>
     */
    String key() default "";

    /**
     * AREX will serialize and save the method returns on record,
     * and will deserialize the method returns into object on replay.
     * For methods with custom generic return types, AREX cannot get the actual return Type on record,
     * so the method returns cannot be deserialized correctly on replay.
     * <p>Here need to configure the {@code acutalType}, default is {@code Object.class}, An example is as follows
     * <pre>{@code
     *     Class BaseModel<T> {
     *         final T data;
     *         BaseModel(T data) {
     *             this.data = data;
     *         }
     *         T getData() {
     *             return data;
     *         }
     *     }
     *     @ArexMock(actualType = FooModel.class)
     *     BaseModel<FooModel> foo(int id) {
     *         FooModel model = biz codes;
     *         return new BaseModel<>(model);
     *     }
     *}
     * </pre>
     */
    Class<?> actualType() default Object.class;

}
