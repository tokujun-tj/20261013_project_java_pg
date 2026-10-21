public class Items { // Itemsという名前のクラス（プログラムのまとまり）を作ります
    public static void main(String[] args) { // プログラムを実行したとき最初に動く場所です
        String[] todos = { // Todoの文字列を入れる配列（複数のデータを並べた入れ物）です
                "牛乳を買う", // 1件目のTodoです
                "卵を買う", // 2件目のTodoです
                "パンを買う", // 3件目のTodoです
                "掃除をする"
        }; // 配列の内容はここまでです
        for (int i = 0; i < todos.length; i++) { // iを0から始め、配列の件数分だけ繰り返します
            System.out.println("<li>" + todos[i] + "</li>"); // i番目のTodoを<li>と</li>で囲んで1行出力します
        } // for文の繰り返しはここまでです
    } // mainメソッド（実行の入口）はここまでです
} // Itemsクラスはここまでです
