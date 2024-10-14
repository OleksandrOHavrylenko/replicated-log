package com.distributed.commons;

import java.util.Objects;

public class LogItem {

    private final Long id;

    private final String message;

    public LogItem(final Long id, final String message) {
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DataItem{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogItem logItem = (LogItem) o;
        return Objects.equals(id, logItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
