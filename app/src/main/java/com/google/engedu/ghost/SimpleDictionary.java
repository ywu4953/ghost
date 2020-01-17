/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if (prefix=="") {
            Random random = new Random();
            String randomWord = words.get(random.nextInt(words.size()));
            return randomWord;
        }
        else{
            int i = 0;
            int j = words.size()-1;
            while(i<=j){
                int mid = i + (j-i)/2;
                if (words.get(mid).substring(0, prefix.length()).equals(prefix)){
                    return words.get(mid);
                }

                else if (prefix.compareTo(words.get(mid).substring(0, prefix.length()))<0){
                    j = mid-1;
                }

                else {
                    i = mid+1;
                }
            }
        }
        return null;

    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String target = getAnyWordStartingWith(prefix);
        int i = words.indexOf(target);
        while(words.get(i).substring(0, prefix.length()).equals(prefix) && i>=0){
            i--;
        }
        int start = i+1;
        i = words.indexOf(target);
        while((words.get(i)).substring(0, prefix.length()).equals(prefix) && i<= words.size()){
            i++;
        }
        int end = i-1;

        ArrayList<String> odd = new ArrayList<>();
        ArrayList<String> even = new ArrayList<>();

        for (int k = start; k<=end; k++){
            if (words.get(k).length()%2==0){
                even.add(words.get(k));
            }
            else{
                odd.add(words.get(k));
            }
        }

        Random random = new Random();

        if (prefix == ""){
            return even.get(random.nextInt(even.size()));
        }
        else if (prefix.length()/2==0){
            String randomWord = even.get(random.nextInt(even.size()));
            return randomWord;
        }
        else{
            String randomWord = odd.get(random.nextInt(odd.size()));
            return randomWord;
        }
    }
}
