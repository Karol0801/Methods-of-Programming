// Program opiera sie na wykorzystaniu stosu, ktory uzywany jest do obu konwersji. Poza stosem wykorzystywana jest
// klasa 'Element', ktora przechowuje wszystkie metody potrzebne do wykonania zadania
import java.util.Scanner;

// Implementacji stosu - analogicznie jak pokazano na wykladzie z tym, ze w tym przypadku stos rosnie w dol
class Stack {
    private final int maxSize;
    private final String[] elements;
    private int t;

    public Stack(int size){
        maxSize = size;
        elements = new String[maxSize];
        t = -1;                                 //wierzcholek stosu - stos rosnie w dol
    }

    public void push(String c){
        if(!isFull()) elements[++t] = c;
    }
    public String pop(){
        if(isEmpty()) return "";
        else return elements[t--];
    }
    public String top(){
        if(isEmpty()) return "";
        else return elements[t];
    }
    public boolean isFull(){    //zwraca true, gdy stos pelny
        return (t == maxSize-1);
    }
    public boolean isEmpty() {
        return (t == -1);
    }
}
//Ponizsza klasa przechowuje jeden zestaw danych czyli linie z typem wyrazenia i jego zawartoscia
class Expression{
    public String expression;           //przechowuje wyrazenie
    public String notation;             //przechowuje notacje
    public Expression(String input, String not) {
        expression = input;
        notation = not;
    }
    //Metoda usuwa wszystkie znaki ktore nie powinny znalezc sie w prawidlowo podanym wyrazeniu
    public void deleteWrongCharacters(){
        String result = "";
        StringBuilder sb = new StringBuilder(result);    //stringBuilder w celu utworzenia stringa ktorego mozna modyfikowac(klasyczny String jest niemutowalny)
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            //Jezeli jest to INF, sprawdz czy dany znak moze wystapic w tej notacji.
            //Jezeli tak, dodaj go do zmiennej sb (znaki ktore nie moga wystapic zwroca -1)
            if(notation.equals("INF") && getPriority(c) != -1) sb.append(c);
                //Ponizej przypadek dla ONP - podobny, lecz wykluczamy nawiasy ktore zwracaja wartosc 10
            else if(getPriority(c) != -1 && getPriority(c) != 10) sb.append(c);
        }
        //Zwroc zmienna (zlozona jedynie z prawidlowych znakow) jako Stringa
        expression = sb.toString();
    }
    //Ponizsza metoda sprawdza, czy po usunieciu zbednych znakow, wyrazenie ONP jest poprawne
    public boolean checkONPAccuracy() {
        int stackSize = 0;     //tworzymy zmienna rozmiaru "wyimaginowanego" stosu, bo tak naprawde nie potrzebujemy go tutaj calego wykorzystywac
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            //jezeli symbol jest litera, dodaj go na stos(a tak naprawde interesuje nas tylko liczba liter na stosie)
            if ((c >= 'a' && c <= 'z')) stackSize += 1;
            else {
                //wchodzimy gdy znak jest operatorem, dla 2-wartosciowych wyrazen sciagamy ze stosu 2 wartosci i wkladamy
                //nowopowstale wyrazenie tak wiec +1 - 2 (dla jednoargumentowych operatorow nie zmienia sie liczba)
                stackSize = stackSize + 1 - valence(c);
                if(stackSize <= 0) return false;        //liczba elementow nie moze byc < 0
            }
        }
        //gdy na stosie pozostanie nam jeden element(bedzie to obliczona wartosc wyrazenia), wtedy wyrazenie jest
        //obliczalne tak wiec jest prawidlowo zapisane w postaci ONP
        return stackSize == 1;
    }
    //Ponizsza metoda zwraca wartosciowosc operatora, ale tez liczb i nawiasow (dla liczb i nawiasow wartosc jest
    //okreslona przeze mnie, zaimplementowane poniewaz w innej funkcji, valence() informuje nas z jakim znakiem mamy do
    //czynienia (literka/nawias/operator jednoarg/dwuarg)
    private int valence(char operator){
        if(operator == '(' || operator == ')') return -1; //to nie operator, ale przyda nam sie ten przypadek w sprawdzaniu INF
        else if(operator == '~' || operator == '!') return 1;
        else if(operator >= 'a' && operator <= 'z') return 0;
        else return 2;
    }
    //Ponizsza metoda sprawdza czy wyrazenie w postaci INF zapisane jest w prawidlowy sposob
    //Wykorzystuje w tym celu automat skonczony opisany w poleceniu zadania
    public boolean checkINFAccuracy(){
        int state = 0;
        byte unpaired = 0;       //zmienna przechowujaca ilosc otwartych (niesparowanych) nawiasow
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if(c == '(') unpaired++;
            else if (c == ')'){
                unpaired--;
                if(unpaired < 0) return false;
            }
            if(state == 0){     //stan 0 automatu skonczoneg opisany w tresci zadania
                if(valence(c) == 2 || c == ')') return false;
                else if(valence(c) == 1){
                    state = 2;
                }
                else if ((c >= 'a' && c <= 'z')){
                    state = 1;
                }
            }
            else if(state == 1){
                if(valence(c) == 1 || (c >= 'a' && c <= 'z') || c == '(') return false;
                else if(valence(c) == 2){
                    state = 0;
                }
            }
            else{                                           // else if(state == 2)
                if(valence(c) == 2 || c == ')') return false;
                else if(c == '(') state = 0;
                else if((c >= 'a' && c <= 'z')){
                    state = 1;
                }
            }
        }
        if(unpaired != 0) return false;
        return state == 1;
    }
    //Ponizsza metoda dodaje spacje przed kazdym znakiem outputu
    private void addSpaces(){
        StringBuilder tmp = new StringBuilder();
        for(int i=0;i<expression.length();i++){
            tmp.append(' ');
            tmp.append(expression.charAt(i));
        }
        expression = tmp.toString();
    }
    //metoda konwersji wyraznenia z ONP do INF. Uzywam tutaj dwoch stosow - jeden przechowuje wyrazenia (String),
    //a drugi priorytety tych wyrazen (a tak wlasciwie operatorow, ktore tworza to wyrazenie). Na podstawie wartosci
    //priorytetow podejmowane sa decyzje, czy stosujemy nawiasy, czy tez nie
    public void convertToINF() {
        Stack stack = new Stack(expression.length());
        Stack priorityStack = new Stack(expression.length());
        String tmp;
        for(int i=0; i<expression.length();i++){
            char c = expression.charAt(i);
            //jezeli mamy litere, to ma ona najwyzszy priorytet, kladziemy ja na stos wraz z ustalonym priorytetem
            if(c >= 'a' && c <= 'z') {
                stack.push(String.valueOf(c));
                priorityStack.push(String.valueOf(getPriority(c)));
            }
            else {
                //jezeli znak jest operatorem dwuargumentowym
                if(c != '~' && c != '!'){
                    //sprawdzamy, czy priorytet na szczycie stosu jest <= od priorytetu operatora (na szycie stosu
                    //znajduje sie prawy potomek poddrzewa). Jezeli tak, dodajemy nawias (dla potegowania nie sprawdzamy
                    //rownosci bo jest operatorem prawostronnym
                    if(c != '^' && c != '=' && Integer.parseInt(priorityStack.top()) <= getPriority(c)) tmp = "(" + stack.pop() + ")";
                    else if ((c == '^' || c== '=' ) && Integer.parseInt(priorityStack.top()) < getPriority(c)) tmp = "(" + stack.pop() + ")"; //szczegolny przypadek, operator prwostronny
                    else tmp = stack.pop();
                    priorityStack.pop();
                    //ponizej sprawdzamy wyrazenie i jego priorytet z lewego poddrzewa. W tym przypadku kwestia
                    //uwzgledniania rownosci jest odwrotna dla operatorow prawostronnych
                    if((c != '^' && c!= '=' ) && Integer.parseInt(priorityStack.top()) < getPriority(c)) tmp = "(" + stack.pop() + ")"+ c + tmp;
                    else if ((c == '^' || c == '=' ) && Integer.parseInt(priorityStack.top()) <= getPriority(c)) tmp = "(" + stack.pop() + ")" + c + tmp;
                    else tmp = stack.pop()+c+tmp;
                    priorityStack.pop();
                }
                //jezeli znak jest operatorem jednoargumentowym
                else {
                    if(Integer.parseInt(priorityStack.top()) < getPriority(c)) tmp=c+"("+stack.pop()+")";
                    else tmp = c + stack.pop();
                    priorityStack.pop();
                }
                stack.push(tmp);                            //dodajemy na stos nowo powstale wyrazenie
                priorityStack.push(Integer.toString(getPriority(c)));         //... oraz jego priorytet
            }
        }
        expression = stack.pop();
        addSpaces();
    }
    //ponizsza metoda zwraca priorytet operatora (ale tez operanda, on jest najwiekszy)
    private int getPriority(char c){
        int priority = -1;
        if(c == '(' || c == ')') priority = 10;
        else if (c == '!' || c == '~') priority = 9;
        else if (c == '^') priority = 8;
        else if (c == '*' || c == '/' || c=='%') priority = 7;
        else if (c == '+' || c == '-') priority = 6;
        else if (c == '<' || c == '>') priority = 5;
        else if (c == '?') priority = 4;
        else if (c == '&') priority = 3;
        else if (c == '|') priority = 2;
        else if (c == '=') priority = 1;
        else if (c >= 'a' && c <= 'z') priority = 11; // literka
        return priority;
    }
    //ponizsza metoda dokonuje konwersji z wyrazenia INF do ONP
    public void convertToONP(){
        StringBuilder ONP = new StringBuilder();
        Stack stack = new Stack(expression.length());
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if(c >= 'a' && c <= 'z') ONP.append(c);             //literki dodajemy prosto na wyjscie
            else {
                if(c == '(') stack.push(Character.toString(c)); //nawias kladziemy na stos
                else if(c == '|') {
                    if (stack.top().equals("(")) stack.push(Character.toString(c)); //jezeli na stosie jest
                        //nawias to poloz na stos, jak nie to sprawdz co jest na stosie i zdejmij operatory o priorytecie nizszym
                    else {
                        while (!stack.isEmpty()
                                && !stack.top().equals("(")
                                && !stack.top().equals("=")) ONP.append(stack.pop());
                        stack.push(Character.toString(c));
                    }
                }
                else if(c == '&') { //analogicznie jak wyzej, z tym ze do operatorow o priorytecie nizszym ktore zdejmujemy
                    //dokladamy kolejne
                    if (stack.top().equals("(")) stack.push(Character.toString(c));
                    else {
                        while (!stack.isEmpty()
                                && !stack.top().equals("(")
                                && !stack.top().equals("|")
                                && !stack.top().equals("=")) ONP.append(stack.pop());
                        stack.push(Character.toString(c));
                    }
                }
                else if(c == '?') {
                    if (stack.top().equals("(")) stack.push(Character.toString(c));
                    else {
                        while (!stack.isEmpty()
                                && !stack.top().equals("(")
                                && !stack.top().equals("|")
                                && !stack.top().equals("&")
                                && !stack.top().equals("=")) ONP.append(stack.pop());
                        stack.push(Character.toString(c));
                    }
                }
                else if(c == '<' || c == '>') {
                    if (stack.top().equals("(")) stack.push(Character.toString(c));
                    else {
                        while (!stack.isEmpty()
                                && !stack.top().equals("(")
                                && !stack.top().equals("|")
                                && !stack.top().equals("&")
                                && !stack.top().equals("?")
                                && !stack.top().equals("=")) ONP.append(stack.pop());
                        stack.push(Character.toString(c));
                    }
                }
                else if(c == '+' || c == '-') {
                    if (stack.top().equals("(")) stack.push(Character.toString(c));
                    else {
                        while (!stack.isEmpty()
                                && !stack.top().equals("(")
                                && !stack.top().equals("|")
                                && !stack.top().equals("&")
                                && !stack.top().equals("?")
                                && !stack.top().equals("<")
                                && !stack.top().equals(">")
                                && !stack.top().equals("=")) ONP.append(stack.pop());
                        stack.push(Character.toString(c));
                    }
                }
                else if(c == '*' || c == '/' || c == '%') {
                    if (stack.top().equals("(")) stack.push(Character.toString(c));
                    else {
                        while (!stack.isEmpty()
                                && !stack.top().equals("(")
                                && !stack.top().equals("|")
                                && !stack.top().equals("&")
                                && !stack.top().equals("?")
                                && !stack.top().equals("<")
                                && !stack.top().equals(">")
                                && !stack.top().equals("+")
                                && !stack.top().equals("-")
                                && !stack.top().equals("=")) ONP.append(stack.pop());
                        stack.push(Character.toString(c));
                    }
                }
                else if(c == '!' || c == '~') stack.push(Character.toString(c));
                else if(c == '^') {
                    if (stack.top().equals("(")) stack.push(Character.toString(c));
                    else {
                        while (stack.top().equals("~") || stack.top().equals("!")) ONP.append(stack.pop());
                        stack.push(Character.toString(c));
                    }
                }
                else if(c == ')') {
                    while (!stack.top().equals("(")) ONP.append(stack.pop());
                    stack.pop();
                }
                else if(c == '=') {
                    while (!stack.isEmpty() && !stack.top().equals("(") && !stack.top().equals("=")) ONP.append(stack.pop());
                    stack.push(Character.toString(c));
                }
            }
        }
        while(!stack.isEmpty()) ONP.append(stack.pop()); //na wyjscie dodajemy wszystko co zostalo na stosie na koniec
        expression = ONP.toString();
        addSpaces();
    }
}
class Source {
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        int sets = scanner.nextInt();                     //liczba zestawow danych

        while (sets-- > 0) {                                //kazda iteracja obsluguje jeden zestaw danych
            String input = scanner.nextLine();              //liczba danych w zestawie
            if(input.length() > 5){                         //poprawny input ma zawsze min 6 znakow a nie wczytaja nam sie jakies pojedyncze spacje
                char firstCharacter = input.charAt(0);
                String notation;
                if (firstCharacter == 'I') notation = "INF";
                else notation = "ONP";
                Expression exp = new Expression(input, notation);
                exp.deleteWrongCharacters();                //usuwamy zbedne znaki
                if(notation.equals("INF")) {
                    if (exp.checkINFAccuracy()) {           //gdy wyrazenie jest poprawne, zmien notacje i wypisz wynik
                        exp.convertToONP();
                        System.out.println("ONP:" + exp.expression);
                    } else System.out.println("ONP: error");
                }
                else {
                    if (exp.checkONPAccuracy()) {           //gdy wyrazenie jest poprawne, zmien notacje i wypisz wynik
                        exp.convertToINF();
                        System.out.println("INF:" + exp.expression);
                    } else System.out.println("INF: error");
                }
            }
            else {
                sets++;      //czasami jedna linijka sie gubila, przez co rozwazamy tylko te, ktorych dlugosc > 5
            }
        }
    }
}
//INPUT:
//16
//        INF: x=a+((b-c+d))
//        INF: x=a+(((a-b)+c))
//        INF: a)+(b
//        INF: (((!b)))
//        INF: (!b))
//        INF: (!)!b
//        INF: x=~((~a+b*))c
//        INF: a^ b ^ c
//        INF: a * ( b * c )
//        INF: ( a ^ b ) ^ c
//        INF: a * b * c
//        ONP: abc^^
//        ONP: abc**
//        ONP: ab^c^
//        ONP: ab*c*
//        ONP: aabc|=+
//OUTPUT:
//        ONP: x a b c - d + + =
//        ONP: x a a b - c + + =
//        ONP: error
//        ONP: b !
//        ONP: error
//        ONP: error
//        ONP: error
//        ONP: a b c ^ ^
//        ONP: a b c * *
//        ONP: a b ^ c ^
//        ONP: a b * c *
//        INF: a ^ b ^ c
//        INF: a * ( b * c )
//        INF: ( a ^ b ) ^ c
//        INF: a * b * c
//        INF: a + ( a = b | c )
