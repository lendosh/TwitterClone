package com.vlad.TwiterClone;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Sets.*;

public class GuavaTest {

    public static void print(Object ... args){
        Arrays.stream(args).forEach(System.out::println);
    }

    @Test
    public void test(){
        ArrayList<Object> list = new ArrayList<>();
        list.add("first");
        list.add("second");
        list.add("third");

        ArrayList<String> strings = new ArrayList<String>() {{
            add("first");
            add("second");
            add("third");
        }};

        List<String> list1 =  ImmutableList.of("first", "second", "third");

        list.addAll(list);

        ImmutableMap<String, Integer> map = ImmutableMap.of("first", 1, "second", 2, "third", 3);

        print(list, list1, map);
    }
    @Test
    public void test2(){
        print(Strings.commonSuffix("Mike", "Coke"));

        print((Strings.padEnd("some one string", 20, '_')));

        print((Strings.repeat("_", 20)));
        ImmutableList.of("John", "Jeremy", "Mike", "Sudharsan")
                .stream()
                .map(name -> Strings.padStart(name, 15, '.'))
                .forEach(GuavaTest::print);
    }

    @Test
    public void test3(){
        String str = "long text, just long text, nothing but long text";

        Iterable<String> strings = Splitter.on("text").split(str);

        String result = Joiner.on("video").join(strings);

        print(result);
    }

    @Test
    public void test4(){
        ArrayList<String> list =  Lists.newArrayList("First", "Second", "Third");
        HashSet<String> set = newHashSet("fourth", "fifth", "sixth");

       Iterable<String> concat = concat(list,set);

        Iterable<String> skip = skip(concat, 2);
        Iterable<String> limit = limit(skip, 2);
        print(concat, limit);
    }

    @Test
    public void sets(){
        HashSet<Integer> sets1 = newHashSet(1, 2, 3, 4);
        HashSet<Integer> sets2 = newHashSet(3, 4, 5, 6, 7);

        Sets.SetView<Integer> difference = difference(sets1, sets2);
        SetView<Integer> difference1 = difference(sets2, sets1);

        SetView<Integer> inter = intersection(sets1, sets2);

        print(difference, difference1, inter);

    }

    @Test
    public void test5(){
        HashMultiset<Object> multiset = HashMultiset.create();
        multiset.add("Let's code");
        multiset.add("razbor poletov",  3);
        multiset.add("radioT");
        multiset.add("radioT");

        print(multiset);
    }

    @Test
    public void test6(){
        HashMultimap<Object, Object> multimap = HashMultimap.create();

        multimap.put("letsCode", "Dru");
        multimap.put("razbor poletov", "Dima");
        multimap.put("razbor poletov", "Gamov");
        multimap.putAll("Google", Lists.newArrayList("Page", "Brinn"));

        print(multimap);
    }

    @Test
    public void test7(){
        HashBiMap<Object, Object> biMap = HashBiMap.create();

        biMap.put("letsCode", 1);
        biMap.put("Google", 2);
        biMap.put("razbor poletov", 3);

        print(biMap, biMap.inverse());
        print(biMap.get("Google"), biMap.inverse().get(2));

    }

    @Test
    public void test8(){
        HashBasedTable<String, String, Double> table = HashBasedTable.create();

        table.put("Mike", "2017-11-11", 2000.0);
        table.put("Mike", "2017-10-11", 1000.0);
        table.put("John", "2017-11-11", 3000.0);
        table.put("John", "2017-10-11", 2000.0);
        table.put("Jeremy", "2017-11-11", 500.0);
        table.put("Jeremy", "2017-10-11", 1500.0);

        print(
                table,
                table.get("Mike", "2017-11-11"),
                table.row("Mike"),
                table.column("2017-11-11"),
                Tables.transpose(table)
        );
    }
}
