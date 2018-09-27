package net.sf.launch4j.config;

import java.util.Comparator;

public interface Describable {

    String getDescription();

    int getIndex();

    class DescribableComparator implements Comparator<Describable> {

        @Override
        public int compare(Describable o1, Describable o2) {
            return o1.getDescription().compareTo(o2.getDescription());
        }
    }
}
