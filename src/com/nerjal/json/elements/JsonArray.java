package com.nerjal.json.elements;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class JsonArray extends JsonElement implements Iterable<JsonElement> {
    private final List<JsonElement> list;
    protected transient int modCount = 0;

    public JsonArray() {
        this.list = new ArrayList<>();
    }
    public JsonArray(Collection<JsonElement> elements) {
        this.list = List.copyOf(elements);
    }

    public JsonElement get(int index) {
        return this.list.get(index);
    }
    public JsonElement[] getAll(int from, int to) {
        return (JsonElement[]) Arrays.copyOfRange(this.list.toArray(), from, to);
    }
    public boolean remove(JsonElement element) {
        boolean b = this.list.remove(element);
        modCount++;
        return b;
    }
    public JsonElement remove(int index) {
        JsonElement e = this.list.remove(index);
        modCount++;
        return e;
    }
    public Collection<JsonElement> removeAll(Collection<JsonElement> elements) {
        List<JsonElement> returnList = new ArrayList<>();
        elements.forEach(e -> {
            if (list.remove(e)) returnList.add(e);
        });
        modCount++;
        return returnList;
    }
    public void add(JsonElement element) {
        this.list.add(element);
        modCount++;
    }
    public void add(int index, JsonElement element) {
        this.list.add(index, element);
        modCount++;
    }
    public void addAll(Collection<JsonElement> elements) {
        this.list.addAll(elements);
        modCount++;
    }
    public void addAll(JsonElement[] elements) {
        this.addAll(List.of(elements));
        modCount++;
    }
    public void replaceAll(UnaryOperator<JsonElement> operator) {
        this.list.replaceAll(operator);
        modCount++;
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isJsonArray() {
        return true;
    }
    @Override
    public String typeToString() {
        return "Array";
    }
    @Override
    public JsonArray getAsJsonArray() {
        return this;
    }


    public void forAll(Consumer<JsonElement> action) {
        this.list.forEach(action);
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return new SimpleJArrayIterator();
    }

    @Override
    public void forEach(Consumer<? super JsonElement> action) {
        Iterable.super.forEach(action);
    }

    public class SimpleJArrayIterator implements Iterator<JsonElement> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        int lastRet = -1;

        /**
         * The modCount value that the iterator believes that the backing
         * List should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        int expectedModCount = modCount;


        @Override
        public boolean hasNext() {
            if (cursor == size()) return false;
            for (int i = cursor; i < size(); i++) {
                if (!list.get(i).isComment()) return true;
            }
            return false;
        }

        @Override
        public JsonElement next() {
            checkForComodification();
            try {
                JsonElement element = get(cursor);
                lastRet = cursor;
                cursor++;
                if (element.isComment()) return next();
                return element;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException(e);
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super JsonElement> action) {
            Iterator.super.forEachRemaining(action);
        }


        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                JsonArray.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
}
