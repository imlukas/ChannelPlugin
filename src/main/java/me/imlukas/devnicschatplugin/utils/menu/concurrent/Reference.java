package me.imlukas.devnicschatplugin.utils.menu.concurrent;

public class Reference<T> {

    private T value;

    public Reference(T value) {
        this.value = value;
    }

    public Reference() {
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
