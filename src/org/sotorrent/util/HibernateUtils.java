package org.sotorrent.util;

import org.hibernate.StatelessSession;

import java.util.List;
import java.util.Set;

public class HibernateUtils {
    public static void insertList(StatelessSession session, List list) {
        for (Object element : list) {
            session.insert(element);
        }
    }

    public static void updateList(StatelessSession session, List list) {
        for (Object element : list) {
            session.update(element);
        }
    }

    public static String setToQueryString(Set<Byte> valueSet) {
        // convert set into array
        Byte[] values = new Byte[valueSet.size()];
        valueSet.toArray(values);

        // concatenate values into string for SQL query
        StringBuilder valuesString = new StringBuilder();
        for (int i=0; i<values.length-1; i++) {
            valuesString.append(values[i]).append(",");
        }
        valuesString.append(values[values.length - 1]);

        // return final string
        return valuesString.toString();
    }
}
