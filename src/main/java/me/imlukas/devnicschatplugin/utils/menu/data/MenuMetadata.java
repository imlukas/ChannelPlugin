package me.imlukas.devnicschatplugin.utils.menu.data;

import java.util.HashMap;

public class MenuMetadata extends HashMap<String, MenuMetadata.Holder<Object>> {

    public <T> T get(String key) {
        return (T) super.get(key).value;
    }

    public <T> T getOrDefault(String key, T def) {
        return (T) super.getOrDefault(key, new Holder<>(def));
    }

    public <T> T remove(String key) {
        return (T) super.remove(key);
    }

    public <T> T computeIfAbsent(String key, T def) {
        return (T) super.computeIfAbsent(key, k -> new Holder<>(def));
    }

    public Object put(String key, Object value) {
        Holder<Object> holder = super.put(key, new Holder<>(value));

        return holder == null ? null : holder.value;
    }

    public Object putTransient(String key, Object value) {
        Holder<Object> holder = super.put(key, new TransientHolder<>(value));

        return holder == null ? null : holder.value;
    }

    public void wipeTransient() {
        for (Entry<String, Holder<Object>> entry : entrySet()) {
            if (entry.getValue() instanceof TransientHolder)
                remove(entry.getKey());
        }
    }

    public void copyFrom(MenuMetadata other) {
        this.putAll(other);
    }

    public static class Holder<T> {
        private T value;

        public Holder(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    private static class TransientHolder<T> extends Holder<T> {
        public TransientHolder(T value) {
            super(value);
        }
    }

}
