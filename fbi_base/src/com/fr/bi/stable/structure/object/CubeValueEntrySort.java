package com.fr.bi.stable.structure.object;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by loy on 16/6/21.
 */
public class CubeValueEntrySort {

    private CubeValueEntry[] entrys = null;

    private int sortedEntryCount = 0;

    public CubeValueEntrySort() {
    }

    /**
     *
     * @param cves
     * @param maxIndexLength CubeValueEntry里index在 0 - maxIndexLength 之间,不包括maxIndexLength
     */
    public CubeValueEntrySort(CubeValueEntry[] cves, int maxIndexLength) {
        entrys = new CubeValueEntry[maxIndexLength];
        sortedEntryCount = 0;
        for(int i = 0, len = cves.length; i < len; i++){
            CubeValueEntry e = cves[i];
            if(entrys[e.getIndex()] == null){
                sortedEntryCount++;
            }
            entrys[e.getIndex()] = e;
        }
        removeNullItem();
    }

    /**
     *
     * @param it
     * @param maxIndexLength CubeValueEntry里index在 0 - maxIndexLength 之间,不包括maxIndexLength
     */
    public CubeValueEntrySort(Iterator<CubeValueEntry> it, int maxIndexLength) {
        entrys = new CubeValueEntry[maxIndexLength];
        sortedEntryCount = 0;
        while(it.hasNext()){
            CubeValueEntry e = it.next();
            if(entrys[e.getIndex()] == null){
                sortedEntryCount++;
            }
            entrys[e.getIndex()] = e;
        }
        removeNullItem();
    }

    public static CubeValueEntrySortBuilder getBuilder(int maxIndexLength){
        return new CubeValueEntrySortBuilder(maxIndexLength);
    }

    public Iterator<CubeValueEntry> iteratorASC(){
        return new CubeValueEntryIterator(entrys, true);
    }

    public Iterator<CubeValueEntry> iteratorDESC(){
        return new CubeValueEntryIterator(entrys, false);
    }

    public CubeValueEntry[] getSortedASC(){
        return entrys;
    }

    public CubeValueEntry[] getSortedDESC(){
        CubeValueEntry[] desc = new CubeValueEntry[entrys.length];
        for (int i = 0; i < entrys.length; i++) {
            desc[entrys.length - 1 - i] = entrys[i];
        }
        return desc;
    }

    private void removeNullItem(){
        CubeValueEntry[] entrysWithoutNull = new CubeValueEntry[sortedEntryCount];
        int index = 0;
        for (int i = 0; i < entrys.length; i++) {
            if(entrys[i] != null){
                entrysWithoutNull[index++] = entrys[i];
            }
        }
        entrys = entrysWithoutNull;
    }

    private static class CubeValueEntryIterator implements Iterator<CubeValueEntry>{

        private int currentIndex = -1;
        private CubeValueEntry[] cves;
        private boolean asc;

        public CubeValueEntryIterator(CubeValueEntry[] cves, boolean asc) {
            this.cves = cves;
            this.asc = asc;
            if(!asc){
                currentIndex = cves.length;
            }
        }

        @Override
        public boolean hasNext() {
            if(asc){
                return currentIndex < cves.length - 1;
            }
            else{
                return currentIndex > 0;
            }
        }

        @Override
        public CubeValueEntry next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if(asc){
                return cves[++currentIndex];
            }
            else {
                return cves[--currentIndex];
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    public static class CubeValueEntrySortBuilder{

        private CubeValueEntrySort sort;

        public CubeValueEntrySortBuilder(int maxIndexLength) {
            sort = new CubeValueEntrySort();
            sort.entrys = new CubeValueEntry[maxIndexLength];
            sort.sortedEntryCount = 0;
        }

        public void putSortItem(CubeValueEntry e){
            if(sort.entrys[e.getIndex()] == null){
                sort.sortedEntryCount++;
            }
            sort.entrys[e.getIndex()] = e;
        }

        public CubeValueEntrySort build(){
            sort.removeNullItem();
            return sort;
        }
    }

}
