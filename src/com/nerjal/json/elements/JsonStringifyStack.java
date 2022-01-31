package com.nerjal.json.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>An object used to store the
 * stringification stack of a JSON
 * structure, in order to avoid
 * parsing a circular structure.
 * </p>
 * Stack works on the elements'
 * hash.
 * @author Nerjal Nosk
 */
class JsonStringifyStack {
    private final List<Integer> stack = new ArrayList<>();

    /**
     * Instantiates a new stack with the
     * specified element's hash.
     * @param e the first element to add
     *          to the stack.
     */
    public JsonStringifyStack(JsonElement e) {
        this.stack.add(e.hashCode());
    }

    /**
     * Either returns if the stack already
     * has the specified element's hash
     * in it, or adds it.
     * @param i the element's hash to search
     *          for in the stack
     * @return whether the given hash was
     *         already in the stack or not.
     */
    private int findOrAdd(int i) {
        int l = 0;
        int h = stack.size();
        if (stack.get(l) == i || stack.get(h-1) == i) return -1;
        if (stack.get(l) > i) {
            stack.add(0,i);
            return 0;
        }
        if (stack.get(h-1) < i) {
            stack.add(i);
            return h;
        }
        int k;
        while (h-l > 1) {
            k = (l + h)/2;
            if (stack.get(k) == i) return -1;
            if (stack.get(k) > i) l = k;
            else h = k;
        }
        stack.add(h,i);
        return h;
    }

    /**
     * Either tells if the specified
     * JsonElement is already in the stack,
     * or adds it.
     * @return true if the element is already
     *         in the stack (error case),
     *         false otherwise.
     */
    public final boolean hasOrAdd(JsonElement e) {
        return findOrAdd(e.hashCode()) < 0;
    }

    /**
     * Removes the specified element's
     * hash from the stack.<br>
     * Used when closing an element while
     * parsing it to string.
     * @param e the element to remove
     *          from the stack
     */
    public final void rem(JsonElement e) {
        int i = e.hashCode();
        int up = stack.size();
        if (stack.get(up-1) < i) return;
        int down = 0;
        if (stack.get(down) > i) return;
        int k = 0;
        while (up - down > 1) {
            k = (up+down)/2;
            if (stack.get(k) == i) {
                stack.remove(k);
                return;
            }
            if (stack.get(k) > i) down = k;
            else up = k;
        }
        if (stack.get(k) == i) stack.remove(i);
    }
}
