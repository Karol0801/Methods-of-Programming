// Program dla kazdego zestawu danych tworzy obiekt zawierajacy tablice wartosci i dla tablicy kazdego obiektu
// wyszukuje liczbe elementow znajdujacych sie w podanym przedziale za pomoca metody SearchBinFirst() oraz SearchBinLast().
// Obydwie metody sa zlozonosci log2(n). Dodatkowo metoda UniqueValues() zlicza unikalne wartosci w tablicy, jest ona zlozonosci O(n)
// Wszystkie metody korzystaja z faktu, ze tablica jest uporzadkowana niemalejaco
import java.util.Scanner;

//ponizsza klasa zawiera glowne metody wyliczajace indeks startowy przedzialu(SearchBinFirst()) oraz indeks koncowy(SearchBinLast())
class Array {
    public long[] values;
    public Array(long[] values){
        this.values = values;
    }
    public int SearchBinFirst(int x){
        int low = 0, high = values.length-1;                  //ograniczenie dolne i gorne rozpatrywanego fragmentu tablicy
        int index = -1;                                       //najnizszy indeks szukanej wartosci

        //petle wykonjemy do momentu, w ktorym upewnimy sie ze nie ma juz wiecej
        //takiej samej wartosci o nizszym indeksie jak ta, ktora znalezlismy
        while(low <= high || (low-1) == high && index == -1){
            int mid = (high - low) / 2 + low;   //wybieramy srodek przedzialu
            //wchodzimy w przypadku, gdy dojdziemy do srodka tablicy lub przejrzymy wszystkie wartosci i nie znajdziemy jej.
            //Potrzebne do nastepujacego przypadku: szukamy 4 w tablicy: 3 5 5. Ten przypadek rozpatruje ostatnia
            //mozliwa wartosc
            if((low == high || (low-1) == high) && index == -1) {
                //jezeli wartosc jest wieksza badz rowna x, zapisz jej indeks
                if(values[mid] >= x) {
                    index = mid;
                    break;
                }
                //jezeli wartosc jest mniejsza od x oraz przedzial nie bedzie pusty, zapisz jej indeks jako mid+1
                else if(values[mid] < x) {
                    index = mid + 1;
                    break;
                }
            }
            else if(values[mid] > x) high = mid - 1;
            else if(values[mid] < x) low = mid + 1;
                //jezeli srodkowa wartosc rowna jest startValue, zapisz jej indeks ale szukaj dalej w przedziale o indeksach
                //nizszych, gdyz chcemy uzyskac najnizszy indeks sposrod wielu tych samych wartosci.
                //(nie przestajemy szukac po znaleznieniu pierwszej wartosci x, moze byc ich wiecej)
            else {
                index = mid;
                high = mid - 1;
            }
        }
        return index;
    }
    //ponizsza funkcja dziala analogicznie do poprzedniej, z drobnymi zmianami powodujacymi, ze znajdujemy indeks
    //dla podanej wartosci, ktory jest najwiekszy (najwiekszy, jezeli tych wartosci jest wiele)
    public int SearchBinLast(int x){
        int low = 0, high = values.length-1;
        int index= - 1;
        while (low <= high || (low-1) == high && index == -1) {
            int mid = (high - low) / 2 + low;
            if((low == high || (low-1) == high) && index == -1){
                if(values[mid] <= x) {
                    index = mid;
                    break;
                }
                else if(values[mid] > x) {
                    index = mid-1;
                    break;
                }
            }
            else if (values[mid] > x) high = mid-1;
            else if (values[mid] < x) low = mid+1;
            else {
                index = mid;
                low = mid + 1;
            }
        }
        return index;
    }
    public int UniqueValues() {
        int numberOfUnique = 1;                     //zawsze minimum jedna wartosc
        for(int i=0; i< values.length-1; i++){
            //gdy wartosc jest rozna od poprzedniej, zwieksz liczbe wartosci unikalnych
            if(values[i+1] != values [i]){
                numberOfUnique += 1;
            }
        }
        return numberOfUnique;
    }
}


class Source {
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        byte sets = scanner.nextByte();                     //liczba zestawow danych
        while (sets-- > 0) {                                //kazda iteracja obsluguje jeden zestaw danych
            int setLength = scanner.nextInt();              //liczba danych w zestawie
            long[] values = new long[setLength];            //zbior liczb danego zestawu
            for (int i = 0; i < setLength; i++) {           //wczytywanie wartosci
                values[i] = scanner.nextLong();
            }
            Array set = new Array(values);                  //utworzenie obiektu i przekazanie wartosci, dla ktorych wyliczamy dluosc przedzialu
            int queryNumber = scanner.nextInt();            //liczba zapytan o dochody
            for (int i = 0; i < queryNumber; i++) {
                int startValue = scanner.nextInt();         //wczytywanie wartosci dla ktorych szukam indeksow
                int endValue = scanner.nextInt();
                if (startValue > endValue) {                  //jezeli przedzial jest nieprawidlowy, wypisz 0
                    System.out.println(0);
                } else {
                    //aby wyliczyc ilosc wartosci pomiedzy podanymi liczbami, odejmujemy indeksy i dodajemy 1 (przedzial jest domkniety)
                    System.out.println(set.SearchBinLast(endValue) - set.SearchBinFirst(startValue) + 1);
                }
            }
            System.out.println(set.UniqueValues());         //wypisuje unikalne wartosci
        }
    }
}
//PRZYKLADOWE DANE
//        3
//        1
//        5
//        3
//        1 2
//        0 5
//        6 10
//        10
//        -2 4 10 33 34 34 34 50 51 51
//        5
//        -1 9
//        -2 50
//        15 0
//        34 34
//        33 33
//        4
//        5 5 9 12
//        3
//        0 2
//        5 11
//        5 12
// OUTPUT
//        0
//        1
//        0
//        1
//        1
//        8
//        0
//        3
//        1
//        7
//        0
//        3
//        4
//        3
