import com.sun.net.httpserver.HttpServer; // Java標準の小さなWebサーバーを使います
import java.net.InetSocketAddress; // 待ち受ける住所とポート番号を指定します
import java.net.URLDecoder; // フォームで送られた日本語を元に戻します
import java.nio.charset.StandardCharsets; // 文字コードを指定します
import java.util.ArrayList; // Todoを入れるリストを用意します
import java.util.List; // 文字列のリストを使います
public class App { // Appという名前のプログラムを定義します
    public static void main(String[] args) throws Exception { // プログラムの実行開始地点です
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0); // 8080番ポートでサーバーを用意します
        List<String> todos = new ArrayList<>();
         // 追加されたTodoを保存する空のリストです
        server.createContext("/", exchange -> { // "/"へのアクセスを受け取る処理を登録します
            String path = exchange.getRequestURI().getPath(); // アクセスされたパスを取り出します
            String method = exchange.getRequestMethod(); // GETやPOSTなどの方法を取り出します
            String message;
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8"); // 通常の応答はUTF-8のテキストとして返します
            if (path.equals("/add") && method.equals("POST")) { // フォームから送られたTodoを追加します
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8); // 送信された内容をUTF-8で読み取ります
                String value = body.substring(5); // todo=の後ろを取り出します
                String todo = URLDecoder.decode(value, StandardCharsets.UTF_8); // 日本語を元の文字に戻します
                if (!todo.isEmpty()) { // 空の入力でなければリストに追加します
                    todos.add(todo); // Todoをリストに追加します
                } // 空の入力を追加しない処理はここまでです
                exchange.getResponseHeaders().set("Location", "/"); // 戻り先をトップページにします
                exchange.sendResponseHeaders(303, -1); // トップページへ移動する応答を返します
                exchange.close(); // 応答を閉じます
                return; // この分岐の処理を終えます
            } else if (path.equals("/")) { // トップページにフォームとTodo一覧を表示します
                String html = "<form method='post' action='/add'><input name='todo'><button>追加</button></form><ul>"; // Todo追加フォームと一覧を始めます
                for (String todo : todos) { // リストのTodoを1件ずつ取り出します
                    html += "<li>" + todo + "</li>"; // TodoをHTMLの項目として追加します
                } // Todoをすべて追加し終えます
                html += "</ul>"; // HTMLのリストを閉じます
                message = html; // 組み立てたHTMLを応答にします
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8"); // トップページをHTMLとして返します
            } else if (path.equals("/hello")) { // パスが「/hello」か比べます
                String query = exchange.getRequestURI().getRawQuery(); // URLのクエリを取り出します
                String name = query.substring(5); // name=の後ろを切り出します
                message = "こんにちは、" + name + "さん！"; // 名前を応答に入れます
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
