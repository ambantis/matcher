package com.ambantis.matcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileOpener {

    private String fileName;
    private int n;

    public FileOpener(String fileName, int n) {
        if (fileName == null || fileName.isEmpty())
            throw new IllegalArgumentException("file name must not be non-empty");
        if (n < 1)
            throw new IllegalArgumentException("`n` must be greater than zero");
        this.fileName = fileName;
        this.n = n;
    }

    private boolean isReady(char[] a) {
        for (char c : a)
            if (c == 0)
                return false;
        return a.length == n;
    }

    private void clear(char[] a) {
        for (int i = 0; i < a.length; i++)
            a[i] = 0;
    }

    private boolean goodData(String name, char[] head, LinkedList<Character> tail) {
        return !name.isEmpty() && isReady(head) && tail.size() == n;
    }

    private Strand makeStrand(String name, char[] head, LinkedList<Character> tail) {
        return new Strand(name, Arrays.toString(head), Arrays.toString(tail.toArray()));
    }

    public List<Strand> getStrands() throws IOException {
        Path file = Paths.get(".", fileName);
        ArrayList<Strand> result = new ArrayList<>();
        try (BufferedReader buf = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String name = "";
            char[] head = new char[n];
            LinkedList<Character> tail = new LinkedList<>();
            int _c;
            char c;
            while (true) {

                // read a char, if it is EOF, possibly adding last strand, then return
                _c = buf.read();
                if (_c == -1) {
                    if (goodData(name, head, tail)) result.add(makeStrand(name, head, tail));
                    return result;
                }

                // we're not done, so let's evaluate the character we just read
                c = (char) _c;
                switch (c) {
                    case '>':
                        // create a new Strand because we've got to the end of the dna
                        if (goodData(name, head, tail))
                            result.add(makeStrand(name, head, tail));

                        // clear all variables
                        clear(head);
                        tail.clear();

                        // read the name and then the first `n` letters of the DNA
                        name = buf.readLine();
                        if (buf.read(head, 0, n) != n)
                            return result;
                        break;

                    default:
                        if (tail.size() == n) {
                            tail.pop();
                            tail.add(c);
                        }
                        break;
                }
            }
        }
    }
}
