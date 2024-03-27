//Program w celu zliczenia inwersji wykorzystuje algorytm bazujacy na MergeSort przy wykorzystaniu jednej,
//pomocniczej tablicy. Liczba inwersji zwieksza sie za kazdym razem, gdy zamieniamy kolejnosc liczb w tablicy

import java.util.Scanner;
public class Source {
    public static Scanner scanner = new Scanner(System.in);
    public static long inv = 0;
    public static void main(String[] args) {
        int sets = scanner.nextInt();                   //liczba zestawow danych
        while (sets-- > 0) {                            //kazda iteracja obsluguje jeden zestaw danych
            int size = scanner.nextInt();               //liczba komend w zestawie
            inv = 0;
            long[] values = new long[size];
            for (int i = 0; i < size; i++) {
                values[i] = scanner.nextInt();
            }
            System.out.println(Inversion(values,0, values.length-1));
        }
    }
    static long Inversion(long[] array, int start, int end) {
        if(start < end) {
            int mid = (start + end) / 2;
            //rekurencyjnie wywolujemy funkcje na dwoch czesciach tablicy wejsciowej
            Inversion(array, start, mid);
            Inversion(array, mid + 1, end);
            //scalamy w celu zliczenia inwersji
            merge(array, start, end);
        }
        return inv;
    }

    static long merge(long[] array, int start, int end) {
        int mid = (start + end) / 2; //ustalamy srodek podanej tablicy
        int tempSize = mid-start+1;
        long [] temp = new long[tempSize];   //tworzymy tablice pomocnicza o dlugosci n/2 + 1
        //uzupelniamy tablice pomocnicza lewa strona tablicy wejsciowej
        for(int i = start; i <= mid; i++) {
            temp[i-start] = array[i];
        }
        int left = start;
        int right = mid + 1;    //indeks od ktorej zaczyna sie prawa strona tablicy, ktora nie przepisywana jest do temp
        int remainNumbers;
        int k=0;                //zmienna trzymajaca aktualny indeks w tablicy temp, ktory porownujemy
        while((k < tempSize) && (right <= end)) {
            //gdy liczba z tablicy pomocniczej jest mniejsza od liczby aktualnie rozpatrywanej z prawej strony tablicy,
            //wpisz ja na odpowiednie miejsce i przejdz do nastepnej liczby z temp
            if (temp[k] <= array[right]) {
                array[left++] = temp[k++];
            } else {
                //gdy liczba z prawej strony jest mniejsza, wtedy dochodzi do zamiany liczb miejscami tak wiec
                //zliczamy inwersje powiekszona o tyle liczb, ile zostalo do konca tablicy temp (jezeli w temp sa 3 elementy
                //od ktorych wartosc jest mniejsza, dodajemy +3 do inwersji)
                array[left++] = array[right++];
                remainNumbers = tempSize - k;
                inv += remainNumbers;
            }
        }
        //gdy zostaja jakies elementy w tablicy temp, dopisujemy je na koniec tablicy wejsiciowej
        while(k < tempSize) {
            array[left++] = temp[k++];
        }
        //(nie musimy robic drugiego while tak jak przy standardowym mergeSort, gdyz liczby z prawej strony
        //tablicy sa juz wpisane prawidlowo, gdy dojdziemy do konca tablicy temp)
        return inv;
    }
}
//INPUT
//        4
//        5
//        1 3 2 4 7
//        8
//        0 0 4 0 0 5 2 1
//        6
//        2 2 6 2 7 1
//        7
//        12 0 3 10000 2 0 0
//OUTPUT
//        1
//        7
//        6
//        13
