//Program dziala w oparciu o magiczne piatki. Nie stosuje dodatkowych tablic dla median: po prostu przezuca mediany
//(z kazdej piatki) na przod tablicy i wywolywany jest Select() aby znalezc mediane median. Wyznaczam jedynie dwa
//zbiory S1 i S2 - zakladam, ze zbior z liczbami rownymi pivotowi jest jednoelementowy (nie psuje zlozonosci)
import java.util.Scanner;
public class Source {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int sets = scanner.nextInt();
        while (sets-- > 0) {
            //wczytywanie danych do klasy
            int numberOfSongs = scanner.nextInt();
            int[] songs = new int[numberOfSongs];
            for(int i=0; i<numberOfSongs; i++){
                songs[i] = scanner.nextInt();
            }
            int numberOfQueries = scanner.nextInt();
            int[] queries = new int[numberOfQueries];
            for(int i=0;i<numberOfQueries; i++){
                queries[i] = scanner.nextInt();
            }
            Set set = new Set(numberOfSongs, songs, numberOfQueries, queries);
            //wypisanie wyniku
            set.printResult();
        }
    }
}

class Set{
    int numberOfSongs;
    int numberOfQueries;
    int[] songs;
    int[] queries;
    int p = 10; //przy takiej dlugosci wychodzimy z rekurencji i robimy selection sorta

    public Set(int numberOfSongs, int[] songs, int numberOfQueries, int[] queries){
        this.numberOfSongs = numberOfSongs;
        this.songs = songs;
        this.numberOfQueries = numberOfQueries;
        this.queries = queries;
    }
    public void printResult(){
        for(int i=0; i<numberOfQueries; i++){
            //gdy dany numer liczby wychodzi poza tablice, wypisz "brak"
            if(queries[i] < 1 || queries[i] > numberOfSongs){
                System.out.println(queries[i]+" brak");
            }
            else {
                //zwracamy k-ty element
                int kthElement = Select(0, numberOfSongs - 1, queries[i] - 1); //-1 bo mam zaimplementowanego Selecta dla indeksu
                System.out.println(queries[i] + " " + kthElement);
            }
        }
    }
    private int Select(int low, int high, int k){ // k jako indeks
        //jezeli podzbior jest mniejszy od parametru p, sortujemy niemalejaca i wyznaczamy k-ty element (baza rekurencji)
        if(high - low < p){
            int length = high-low+1;
            insertionSort(low,length);
            return songs[k];
        }
        else {
            //wyznacz mediane median jako pivota
            int pivot = getMedianPivot(low, high);
            int CardinalityS1 = partition(low, high, pivot); //moc zbioru liczb mniejszych (lub rownych) od pivota
            if (k == CardinalityS1) { //jezeli moc zbioru jest rowna k to zworc k (bo k jako indeks podajemy)
                return songs[k];
            } else if (k < CardinalityS1) return Select(low, CardinalityS1-1, k);
            return Select(CardinalityS1+1, high, k);
        }
    }
    //funkcja dzieli wybrany kawalek tablicy na dwia podatblice (el <= od pivot i > od pivot)
    public int partition(int left,  int right, int pivot) {
        int i = left - 1;
        int j = left;
        int tmp = 0;
        while (j<=right) {
            if(songs[j] <= pivot){
                i++;
                swap(i,j);
                if(songs[i] == pivot) tmp = i;
            }
            j++;
        }
        swap(tmp,i); //na koniec zamieniamy tak, aby na koncu tablicy z el. <= pivot byla wartosc rowna pivotovi
        // (przydaje sie do if w Select())
        return i;
    }
    private int getMedianPivot(int low, int high){
        int numberOfFives = 0; //liczba piatek czyli median
        int index = low; //indeks na ktory wstawiamy mediane danej piatki
        for(int i=low; i<=high; i+=5){
            numberOfFives++;
            if(i+5 <= high) insertionSortFives(i,5, index);
            else insertionSortFives(i, high+1-i, index);
            index++;
        }
        // szukamy na początku tablicy gdzie umiescilismy mediany wszystkich 5 elementowych podzbiorow
        // szukamy medainy a wiec wartosci srodkowej dlatego k = low+numberOfFives/2
        return Select(low,low+numberOfFives-1,low+numberOfFives/2);
    }

    private void insertionSortFives(int id, int length, int index){ //dwa razy wywolywany??????????????????
        insertionSort(id,length); //sortujemy piatke
        swap(index, id+length/2); //i umieszczamy jej mediane na odpowiednim miejscu w tablicy wejsciowej
    }
    //sortowanie wybranego fragmeentu tablicy
    public void insertionSort(int id, int length) {
        for (int i = id + 1; i < id + length; i++) {
            int key = songs[i];
            int j = i - 1;
            while (j >= id && songs[j] > key) {
                swap(j + 1, j);
                j = j - 1;
            }
        }
    }
    private void swap(int a, int b){
        int tmp = songs[a];
        songs[a] = songs[b];
        songs[b] = tmp;
    }
}

//INPUT:
//        2
//        34
//        1 4 2 5 8 5 8 9 4 8 1 5 9 7 8 2 1 5 2 6 8 3 11 23 4 1 4 3 21 6 33 2 1 6
//        8
//        1 5 6 17 15 20 34 35
//        9
//        1 4 2 5 8  5 8 9 4
//        3
//        1 9 20
//OUTPUT:
//        1 1
//        5 1
//        6 2
//        17 5
//        15 4
//        20 6
//        34 33
//        35 brak
//        1 1
//        9 9
//        20 brak
