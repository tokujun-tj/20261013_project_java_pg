import java.util.ArrayList; // 複数のデータを順番に入れる入れ物を使うために読み込みます
import java.util.List; // Todoの一覧を扱う型を使うために読み込みます
public class Main { // publicは他の場所からも使えるという意味で、Mainというクラスを作ります
    public static void main(String[] args) { // プログラムを実行したとき最初に動く場所です
        List<Todo> todos = new ArrayList<>(); // Todoを入れる一覧（List）を作ります
        todos.add(new Todo("牛乳を買う", false)); // まだ済んでいないTodoを1件追加します
        todos.add(new Todo("ゴミを出す", true)); // 済んだTodoを1件追加します
        todos.add(new Todo("お昼ご飯を買う",true));
        for (Todo todo : todos) { // 一覧からTodoを1件ずつ取り出して繰り返します
            System.out.println(todo.toItem()); // Todoを<li>の形にして1行表示します
        } // forの繰り返しはここまでです
    } // mainメソッド（実行の入口）はここまでです
} // Mainクラスはここまでです
class Todo { // Todoの情報をまとめるクラス（設計図）を作ります
    private final String title; // Todoのタイトル（文字列）を保存します
    private final boolean done; // 済んだかどうか（trueまたはfalse）を保存します
    Todo(String title, boolean done) { // タイトルと済んだかどうかを受け取る初期化処理です
        this.title = title; // 受け取ったタイトルをこのTodoに保存します
        this.done = done; // 受け取った状態をこのTodoに保存します
    } // 初期化処理はここまでです
    String toItem() { // Todoを<li>で囲んだ1行の文字列に変換します
        String mark = done ? "[完了] " : ""; // 済んでいる場合だけタイトルの前に印を付けます
        return "<li>" + mark + title + "</li>"; // 完成した1行を呼び出し元に返します
    } // toItemメソッドはここまでです
} // Todoクラスはここまでです
