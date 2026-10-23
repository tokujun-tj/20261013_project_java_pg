import com.sun.net.httpserver.HttpServer; // Java標準の小さなWebサーバーを使います
import java.net.InetSocketAddress; // 待ち受ける住所とポート番号を指定します
import java.util.ArrayList; // Todoを入れるリストを用意します
import java.util.List; // 文字列のリストを使います
public class App { // Appという名前のプログラムを定義します
    public static void main(String[] args) throws Exception { // プログラムの実行開始地点です
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0); // 8080番ポートでサーバーを用意します
        server.createContext("/", exchange -> { // "/"へのアクセスを受け取る処理を登録します
            String path = exchange.getRequestURI().getPath(); // アクセスされたパスを取り出します
            String message;
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8"); // 通常の応答はUTF-8のテキストとして返します
            if (path.equals("/hello")) { // パスが「/hello」か比べます
                String query = exchange.getRequestURI().getRawQuery(); // URLのクエリを取り出します
                String name = query.substring(5); // name=の後ろを切り出します
                message = "こんにちは、" + name + "さん！"; // 名前を応答に入れます
            } else if (path.equals("/todos")) { // /todosへのアクセスを処理します
                List<String> todos = new ArrayList<>(); // Todoの文字列を入れるリストを作ります
                todos.add("牛乳を買う"); // 1件目のTodoを追加します
                todos.add("卵を買う"); // 2件目のTodoを追加します
                todos.add("パンを買う"); // 3件目のTodoを追加します
                todos.add("自分の今日のTodo");
                String html = "<ul>"; // HTMLのリストを始めます
                for (String todo : todos) { // リストのTodoを1件ずつ取り出します
                    html += "<li>" + todo + "</li>"; // TodoをHTMLの項目として追加します
                } // Todoをすべて追加し終えます
                html += "</ul>"; // HTMLのリストを閉じます
                message = html; // 組み立てたHTMLを応答にします
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8"); // /todosだけHTMLとして返します
            } else if (path.equals("/bye")) {
                message = "さようなら！";
            } else if (path.equals("/menu")) { // パスが「/menu」か比べます
                message = "今日のお昼はカレー";
            } else {
                message = "ページが見つかりません";
            }
            byte[] body = message.getBytes("UTF-8"); // 返す文字をUTF-8のデータに変換します
            exchange.sendResponseHeaders(200, body.length); // 正常な応答とデータの長さを送ります
            exchange.getResponseBody().write(body); // 変換したデータをブラウザに送ります
            exchange.getResponseBody().close(); // 送信を終えて応答を閉じます
        }); // "/"への処理の登録はここまでです
            server.start(); // サーバーの待ち受けを開始します
        System.out.println("サーバー起動: http://localhost:8080 （止めるときは Ctrl+C）"); // 起動したことをターミナルに表示します
    } // mainメソッドはここまでです
} // Appクラスはここまでです
