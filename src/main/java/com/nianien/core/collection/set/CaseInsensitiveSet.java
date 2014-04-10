package com.nianien.core.collection.set;

import java.util.*;

/**
 * 当元素类型为String类型时,不区分大小写,但仍保持原有数据不变<br/>
 *
 * @author skyfalling
 */
public class CaseInsensitiveSet<E> extends HashSet<E> {
    private static final long serialVersionUID = 1L;
    private Set<E> keySet = new HashSet<E>();

    /**
     * 构造方法
     */
    public CaseInsensitiveSet() {
    }

    /**
     * 构造方法
     *
     * @param c
     */
    public CaseInsensitiveSet(Collection<E> c) {
        for (E e : c) {
            add(e);
        }
    }

    /**
     * 构造方法
     *
     * @param a
     */
    public CaseInsensitiveSet(E[] a) {
        for (E e : a) {
            add(e);
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o != null && o instanceof String) {
            return keySet.contains(o.toString());
        }
        return super.contains(o);
    }

    @Override
    public boolean add(E e) {
        if (e != null && e instanceof String) {
            boolean changed = keySet.add((E) e.toString().toLowerCase());
            if (changed) {
                super.add(e);
            }
            return changed;
        }
        return super.add(e);
    }

    @Override
    public boolean remove(Object o) {
        if (o != null && o instanceof String) {
            boolean changed = keySet.remove(o.toString().toLowerCase());
            if (changed) {
                super.remove(o);
            }
            return changed;
        }
        return super.remove(o);
    }

}