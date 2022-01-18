package com.nerjal.json.elements;

import java.util.ArrayList;
import java.util.List;

class JsonStringifyStack {
    private final List<Integer> stack = new ArrayList<>();

    public JsonStringifyStack(JsonElement e) {
        this.stack.add(e.hashCode());
    }

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
     * Either tells if a {@link JsonElement} is already in the stack, or
     * adds it.
     * @return true if the element is already in the stack
     * (error case), false otherwise.
     */
    public boolean hasOrAdd(JsonElement e) {
        return findOrAdd(e.hashCode()) < 0;
    }

    public void rem(JsonElement e) {
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
