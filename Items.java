public class Items { // Itemsという名前のクラス（プログラムのまとまり）を作ります
    public static void main(String[] args) { // プログラムを実行したとき最初に動く場所です
        String[] todos = { // Todoの文字列を入れる配列（複数のデータを並べた入れ物）です
                "牛乳を買う", // 1件目のTodoです
                "", // 2件目のTodoです
                "パンを買う", // 3件目のTodoです
                "掃除をする"
        }; // 配列の内容はここまでです
        boolean[] done = { true, false, false, false }; // 各Todoが済んだかどうかを同じ順番で記録します
        for (int i = 0; i < todos.length; i++) { // iを0から始め、配列の件数分だけ繰り返します
            if (!todos[i].isEmpty()) { // 文字列が空でないときだけ中の処理を行います
                String mark = done[i] ? "[済]" : ""; // 済んでいれば印を入れ、まだなら空にします
                System.out.println("<li>" + mark + todos[i] + "</li>"); // Todoと必要な印を<li>と</li>で囲んで出力します
            } // 空でないTodoの処理はここまでです
        } // for文の繰り返しはここまでです
    } // mainメソッド（実行の入口）はここまでです
} // Itemsクラスはここまでです
