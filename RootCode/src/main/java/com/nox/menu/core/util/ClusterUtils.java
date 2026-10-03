package com.nox.menu.core.util;

import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public class ClusterUtils {

    public static <T> List<List<T>> clusterByProximity(List<T> items, double linkDist, int minSz, Function<T, Vec3> posExtractor) {
        int n = items.size();
        if (n == 0) {
            return Collections.emptyList();
        }
        int[] parent = new int[n];
        for (int i = 0; i < n; ++i) {
            parent[i] = i;
        }
        double sq = linkDist * linkDist;
        for (int i = 0; i < n; ++i) {
            Vec3 a = posExtractor.apply(items.get(i));
            for (int j = i + 1; j < n; ++j) {
                Vec3 b = posExtractor.apply(items.get(j));
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                double dz = a.z - b.z;
                if (dx * dx + dy * dy + dz * dz <= sq) {
                    union(parent, i, j);
                }
            }
        }
        LinkedHashMap<Integer, List<T>> groups = new LinkedHashMap<>();
        for (int i = 0; i < n; ++i) {
            groups.computeIfAbsent(find(parent, i), k -> new ArrayList<>()).add(items.get(i));
        }
        ArrayList<List<T>> result = new ArrayList<>();
        for (List<T> g : groups.values()) {
            if (g.size() >= minSz) {
                result.add(g);
            }
        }
        return result;
    }

    private static int find(int[] parent, int i) {
        while (parent[i] != i) {
            parent[i] = parent[parent[i]];
            i = parent[i];
        }
        return i;
    }

    private static void union(int[] parent, int a, int b) {
        int ra = find(parent, a);
        int rb = find(parent, b);
        if (ra != rb) {
            parent[ra] = rb;
        }
    }
}
