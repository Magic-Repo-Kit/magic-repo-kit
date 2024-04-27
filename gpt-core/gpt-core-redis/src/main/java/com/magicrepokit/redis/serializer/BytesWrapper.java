package com.magicrepokit.redis.serializer;

public class BytesWrapper<T> implements Cloneable {
    private T value;

    public BytesWrapper() {
    }

    public BytesWrapper(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BytesWrapper<T> clone() {
        try {
            return (BytesWrapper) super.clone();
        } catch (CloneNotSupportedException e) {
            return new BytesWrapper<>();
        }
    }
}
