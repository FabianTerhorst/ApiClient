package io.fabianterhorst.apiclient.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    /**
     * Creating a string from a list
     * For example new List(){1,2,3} will output "1,2,3"
     * Is sometimes needed for a url query parameter
     *
     * @param list List
     * @return string
     */
    public static String joinList(List list) {
        return list.toString().replaceAll(",", ",").replaceAll("[\\[.\\].\\s+]", "");
    }

    /**
     * Creating a list from a string
     * For example "1,2,3" will output new List(){1,2,3}
     *
     * @param list List
     * @return string
     */
    public static List<Integer> getListFromString(String list) {
        List<String> stringList = Arrays.asList(list.split("\\s*,\\s*"));
        List<Integer> integerList = new ArrayList<>();
        for (String string : stringList) {
            integerList.add(Integer.parseInt(string));
        }
        return integerList;
    }
}
