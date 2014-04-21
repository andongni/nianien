package com.nianien.core.function;


/**
 * 布尔函数式,支持与或非运算
 *
 * @author skyfalling
 */
public class BooleanPredicate<T> implements Predicate<T> {

    private Predicate<T> predicate;

    /**
     * 默认函数式
     *
     * @param predicate
     */
    public BooleanPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }


    /**
     * AND操作,支持短路
     *
     * @param predicates
     * @return a new BooleanPredicate
     */
    public BooleanPredicate and(final Predicate... predicates) {
        return new BooleanPredicate(new Predicate<T>() {
            @Override
            public boolean call(T t) {
                boolean bl = BooleanPredicate.this.call(t);
                for (Predicate predicate : predicates) {
                    bl &= predicate.call(t);
                    if (!bl)
                        return bl;
                }
                return bl;
            }
        });
    }


    /**
     * OR操作,支持短路
     *
     * @param predicates
     * @return a new BooleanPredicate
     */
    public BooleanPredicate or(final Predicate... predicates) {
        return new BooleanPredicate(new Predicate<T>() {
            @Override
            public boolean call(T t) {
                boolean bl = BooleanPredicate.this.call(t);
                for (Predicate predicate : predicates) {
                    bl |= predicate.call(t);
                    if (bl)
                        return bl;
                }
                return bl;
            }
        });
    }

    /**
     * 非操作
     *
     * @return a new BooleanPredicate
     */
    public BooleanPredicate not() {
        return new BooleanPredicate(new Predicate<T>() {
            @Override
            public boolean call(T t) {
                return !BooleanPredicate.this.call(t);
            }
        });
    }

    @Override
    public boolean call(T t) {
        return predicate.call(t);
    }
}